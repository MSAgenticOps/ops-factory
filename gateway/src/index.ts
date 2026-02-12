import http from 'node:http'
import { join, relative } from 'node:path'
import { mkdir } from 'node:fs/promises'
import { existsSync, readdirSync, renameSync, mkdirSync, writeFileSync } from 'node:fs'
import httpProxy from 'http-proxy'
import { loadGatewayConfig } from './config.js'
import { ProcessManager } from './process-manager.js'
import { listOutputFiles, serveFile } from './file-server.js'
import { SessionOwnerCache, extractUserFromWorkingDir } from './user-registry.js'

type JsonRecord = Record<string, unknown>

interface AgentSession extends JsonRecord {
  id: string
  working_dir?: string
  updated_at?: string
  schedule_id?: string | null
  agentId: string
}

interface SessionProbeResult {
  agentId: string
  session: JsonRecord
}

const DEFAULT_USER = 'sys'

/** Read request body as a string */
function readBody(req: http.IncomingMessage): Promise<string> {
  return new Promise((resolve, reject) => {
    let body = ''
    req.on('data', chunk => { body += chunk })
    req.on('end', () => resolve(body))
    req.on('error', reject)
  })
}

async function main() {
  const config = loadGatewayConfig()
  const manager = new ProcessManager(config)
  const ownerCache = new SessionOwnerCache()

  console.log(`Gateway starting — ${config.agents.length} agent(s) configured`)
  await manager.startAll()

  // --- One-time data migration ---
  await migrateExistingData(manager, config)

  const proxy = httpProxy.createProxyServer({
    // Increase timeout for SSE streaming (5 minutes)
    proxyTimeout: 5 * 60 * 1000,
    timeout: 5 * 60 * 1000,
  })

  proxy.on('error', (err, _req, res) => {
    console.error('Proxy error:', err.message)
    if (res instanceof http.ServerResponse && !res.headersSent) {
      res.writeHead(502, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ error: 'Bad gateway' }))
    }
  })

  const server = http.createServer(async (req, res) => {
    const url = req.url || '/'

    // CORS headers must be set before auth check (browsers send OPTIONS without custom headers)
    res.setHeader('Access-Control-Allow-Origin', '*')
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, x-secret-key, x-user-id')
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
    if (req.method === 'OPTIONS') {
      res.writeHead(204)
      res.end()
      return
    }

    // Auth check (header-based, with query param fallback for file routes)
    const headerKey = req.headers['x-secret-key']
    const urlObj = new URL(url, `http://${req.headers.host || 'localhost'}`)
    const queryKey = urlObj.searchParams.get('key')
    const isFileRoute = urlObj.pathname.match(/^\/agents\/[^/]+\/files(\/|$)/)
    const isAuthed = headerKey === config.secretKey || (isFileRoute && queryKey === config.secretKey)

    if (!isAuthed) {
      res.writeHead(401, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ error: 'Unauthorized' }))
      return
    }

    // Extract user identity from header
    const userId = (req.headers['x-user-id'] as string) || DEFAULT_USER

    const pathname = urlObj.pathname
    const upstreamHeaders = {
      'x-secret-key': config.secretKey,
      'Content-Type': 'application/json',
    }

    const runningAgentIds = () =>
      manager
        .listAgents()
        .filter(agent => agent.status === 'running')
        .map(agent => agent.id)

    const getUpstreamTarget = (agentId: string): string | null => manager.getTarget(agentId)

    const fetchJsonFromAgent = async (agentId: string, path: string, method: 'GET' | 'DELETE' = 'GET') => {
      const target = getUpstreamTarget(agentId)
      if (!target) {
        return { ok: false as const, status: 503, error: 'Agent not running' }
      }

      try {
        const response = await fetch(`${target}${path}`, {
          method,
          headers: upstreamHeaders,
          signal: AbortSignal.timeout(5000),
        })

        const text = await response.text()
        const json = text ? (JSON.parse(text) as JsonRecord) : null
        return {
          ok: response.ok,
          status: response.status,
          json,
        }
      } catch (error) {
        return {
          ok: false as const,
          status: 502,
          error: error instanceof Error ? error.message : 'Unknown upstream error',
        }
      }
    }

    /** POST JSON to agent and return parsed response */
    const postJsonToAgent = async (agentId: string, path: string, body: unknown) => {
      const target = getUpstreamTarget(agentId)
      if (!target) {
        return { ok: false as const, status: 503, error: 'Agent not running' }
      }

      try {
        const response = await fetch(`${target}${path}`, {
          method: 'POST',
          headers: upstreamHeaders,
          body: JSON.stringify(body),
          signal: AbortSignal.timeout(30000),
        })

        const text = await response.text()
        const json = text ? (JSON.parse(text) as JsonRecord) : null
        return {
          ok: response.ok,
          status: response.status,
          json,
          raw: text,
        }
      } catch (error) {
        return {
          ok: false as const,
          status: 502,
          error: error instanceof Error ? error.message : 'Unknown upstream error',
        }
      }
    }

    const parseSessions = (agentId: string, payload: JsonRecord | null): AgentSession[] => {
      const raw = payload?.sessions
      if (!Array.isArray(raw)) return []
      return raw
        .filter((item): item is JsonRecord => typeof item === 'object' && item !== null)
        .filter((item): item is JsonRecord & { id: string } => typeof item.id === 'string')
        .map(item => ({ ...item, agentId }))
    }

    const resolveSessionOwners = async (sessionId: string, preferredAgentId?: string): Promise<SessionProbeResult[]> => {
      const agentIds = preferredAgentId ? [preferredAgentId] : runningAgentIds()
      const probes = await Promise.all(agentIds.map(async agentId => {
        const result = await fetchJsonFromAgent(agentId, `/sessions/${encodeURIComponent(sessionId)}`)
        if (!result.ok) return null
        if (!result.json || typeof result.json !== 'object') return null
        return {
          agentId,
          session: result.json,
        } satisfies SessionProbeResult
      }))
      return probes.filter((probe): probe is SessionProbeResult => probe !== null)
    }

    /**
     * Check if the current user owns a session.
     * Ownership is derived from working_dir:
     *   - /users/{userId}/  → belongs to userId
     *   - no /users/ segment → shared (sys), accessible to all
     */
    const checkSessionOwnership = async (sessionId: string, session?: JsonRecord): Promise<boolean> => {
      // 1. Check in-memory cache
      let owner = ownerCache.get(sessionId)

      // 2. If cache miss but we have the session object, derive from working_dir
      if (!owner && session) {
        const workingDir = session.working_dir as string | undefined
        if (workingDir) {
          owner = extractUserFromWorkingDir(workingDir)
          ownerCache.set(sessionId, owner)
        }
      }

      // 3. If still no owner, fetch session details from goosed
      if (!owner) {
        const probes = await resolveSessionOwners(sessionId)
        if (probes.length > 0) {
          const workingDir = probes[0].session.working_dir as string | undefined
          if (workingDir) {
            owner = extractUserFromWorkingDir(workingDir)
            ownerCache.set(sessionId, owner)
          }
        }
      }

      // 4. If owner is DEFAULT_USER (sys), allow everyone
      if (!owner || owner === DEFAULT_USER) return true

      // 5. Otherwise, only the owner can access
      return owner === userId
    }

    // Helper to send 403
    const sendForbidden = () => {
      res.writeHead(403, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ error: 'Forbidden: session does not belong to this user' }))
    }

    // ===== Routes =====

    // GET /status — gateway health
    if (pathname === '/status' && req.method === 'GET') {
      res.writeHead(200, { 'Content-Type': 'text/plain' })
      res.end('ok')
      return
    }

    // GET /me — current user identity
    if (pathname === '/me' && req.method === 'GET') {
      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ userId }))
      return
    }

    // GET /config — gateway configuration (office preview, etc.)
    if (pathname === '/config' && req.method === 'GET') {
      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({
        officePreview: config.officePreview,
      }))
      return
    }

    // GET /agents — list agents
    if (pathname === '/agents' && req.method === 'GET') {
      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ agents: manager.listAgents() }))
      return
    }

    // ===== Session Routes (with user isolation) =====

    // POST /agents/:id/agent/start — intercept session creation
    const startMatch = pathname.match(/^\/agents\/([^/]+)\/agent\/start\/?$/)
    if (startMatch && req.method === 'POST') {
      const agentId = startMatch[1]
      const target = getUpstreamTarget(agentId)
      if (!target) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found or not running` }))
        return
      }

      try {
        const bodyStr = await readBody(req)
        const body = bodyStr ? JSON.parse(bodyStr) : {}

        // Rewrite working_dir to per-user path
        const agentRelPath = relative(config.projectRoot, manager.getUserArtifactsPath(agentId, userId) || '')
        body.working_dir = agentRelPath || `agents/${agentId}/artifacts/users/${userId}`

        // Ensure user directory exists
        const userArtifactsAbs = manager.getUserArtifactsPath(agentId, userId)
        if (userArtifactsAbs) {
          await mkdir(userArtifactsAbs, { recursive: true })
        }

        // Forward to goosed
        const result = await postJsonToAgent(agentId, '/agent/start', body)

        if (!result.ok) {
          res.writeHead(result.status, { 'Content-Type': 'application/json' })
          res.end(result.raw || JSON.stringify({ error: 'Failed to create session' }))
          return
        }

        // Cache session ownership
        const sessionId = (result.json as JsonRecord)?.id as string
        if (sessionId) {
          ownerCache.set(sessionId, userId)
        }

        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify(result.json))
      } catch (err) {
        console.error('Session creation interception error:', err)
        res.writeHead(500, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: 'Failed to create session' }))
      }
      return
    }

    // GET /sessions — aggregated sessions from all running agents (filtered by user)
    if (pathname === '/sessions' && req.method === 'GET') {
      const agentIds = runningAgentIds()
      const settled = await Promise.all(agentIds.map(async agentId => {
        const result = await fetchJsonFromAgent(agentId, `/sessions${urlObj.search}`)
        if (!result.ok) {
          return {
            agentId,
            sessions: [] as AgentSession[],
            error: `HTTP ${result.status}`,
          }
        }
        return {
          agentId,
          sessions: parseSessions(agentId, result.json),
          error: null as string | null,
        }
      }))

      // Collect all sessions (IDs are per-agent, so use agentId:sessionId as composite key)
      const allSessions: AgentSession[] = []
      for (const item of settled) {
        allSessions.push(...item.sessions)
      }

      // Populate ownership cache from working_dir
      ownerCache.populateFromSessions(allSessions.map(s => ({
        id: s.id,
        working_dir: s.working_dir,
      })))

      // Filter sessions by user ownership:
      //   - /users/{userId}/ in working_dir → belongs to userId
      //   - no /users/ → shared (sys), visible to all
      const sessions = allSessions
        .filter(session => {
          const owner = session.working_dir
            ? extractUserFromWorkingDir(session.working_dir)
            : DEFAULT_USER
          return owner === userId || owner === DEFAULT_USER
        })
        .sort((a, b) => {
          const aTs = typeof a.updated_at === 'string' ? Date.parse(a.updated_at) : 0
          const bTs = typeof b.updated_at === 'string' ? Date.parse(b.updated_at) : 0
          return bTs - aTs
        })

      const partialFailures = settled
        .filter(item => item.error)
        .map(item => ({ agentId: item.agentId, error: item.error }))

      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({
        sessions,
        partialFailures,
      }))
      return
    }

    // GET /sessions/:id — resolve session from one/many agents (with ownership check)
    const sessionReadMatch = pathname.match(/^\/sessions\/([^/]+)\/?$/)
    if (sessionReadMatch && req.method === 'GET') {
      const sessionId = decodeURIComponent(sessionReadMatch[1])
      const agentId = urlObj.searchParams.get('agentId') || undefined
      const owners = await resolveSessionOwners(sessionId, agentId)

      if (owners.length === 0) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: 'Session not found' }))
        return
      }

      if (!agentId && owners.length > 1) {
        res.writeHead(409, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          error: 'Session exists in multiple agents. Please provide agentId.',
          agentIds: owners.map(owner => owner.agentId),
        }))
        return
      }

      // Ownership check
      const allowed = await checkSessionOwnership(sessionId, owners[0].session)
      if (!allowed) {
        sendForbidden()
        return
      }

      const owner = owners[0]
      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ ...owner.session, agentId: owner.agentId }))
      return
    }

    // DELETE /sessions/:id — delete from resolved owner (with ownership check + cache cleanup)
    const sessionDeleteMatch = pathname.match(/^\/sessions\/([^/]+)\/?$/)
    if (sessionDeleteMatch && req.method === 'DELETE') {
      const sessionId = decodeURIComponent(sessionDeleteMatch[1])
      const agentId = urlObj.searchParams.get('agentId') || undefined
      const owners = await resolveSessionOwners(sessionId, agentId)

      if (owners.length === 0) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: 'Session not found' }))
        return
      }

      if (!agentId && owners.length > 1) {
        res.writeHead(409, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          error: 'Session exists in multiple agents. Please provide agentId.',
          agentIds: owners.map(owner => owner.agentId),
        }))
        return
      }

      // Ownership check
      const allowed = await checkSessionOwnership(sessionId, owners[0].session)
      if (!allowed) {
        sendForbidden()
        return
      }

      const owner = owners[0]
      const result = await fetchJsonFromAgent(owner.agentId, `/sessions/${encodeURIComponent(sessionId)}`, 'DELETE')

      if (!result.ok) {
        res.writeHead(result.status, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          error: `Failed to delete session on agent '${owner.agentId}'`,
        }))
        return
      }

      // Clean up cache
      ownerCache.remove(sessionId)

      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ success: true, agentId: owner.agentId }))
      return
    }

    // ===== File Routes (scoped to user) =====

    // GET /agents/:id/files — list output files (per-user)
    const fileListMatch = pathname.match(/^\/agents\/([^/]+)\/files\/?$/)
    if (fileListMatch && req.method === 'GET') {
      const agentId = fileListMatch[1]
      const userArtifactsPath = manager.getUserArtifactsPath(agentId, userId)
      if (!userArtifactsPath) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found` }))
        return
      }
      // Ensure user directory exists
      await mkdir(userArtifactsPath, { recursive: true })
      try {
        const files = await listOutputFiles(userArtifactsPath)
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ files }))
      } catch (err) {
        console.error('File listing error:', err)
        res.writeHead(500, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: 'Failed to list files' }))
      }
      return
    }

    // GET /agents/:id/files/* — serve/download a specific file (per-user)
    const fileServeMatch = pathname.match(/^\/agents\/([^/]+)\/files\/(.+)$/)
    if (fileServeMatch && req.method === 'GET') {
      const agentId = fileServeMatch[1]
      const filePath = decodeURIComponent(fileServeMatch[2])
      const userArtifactsPath = manager.getUserArtifactsPath(agentId, userId)
      if (!userArtifactsPath) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found` }))
        return
      }
      await serveFile(userArtifactsPath, filePath, req, res)
      return
    }

    // ===== Schedule Routes (agent-level, shared — no per-user ownership) =====
    // Schedules are shared config like skills and MCP. All users can CRUD.
    // Schedule runs produce sessions with the agent's default working_dir (no /users/),
    // so they appear as "sys" sessions visible to all users.
    // These routes pass through to goosed via the catch-all proxy below.

    // ===== Proxy routes that need session ownership check =====

    // POST /agents/:id/agent/reply, /agent/resume, /agent/restart, /agent/stop
    const sessionProxyMatch = pathname.match(/^\/agents\/([^/]+)\/agent\/(reply|resume|restart|stop)\/?$/)
    if (sessionProxyMatch && req.method === 'POST') {
      const agentId = sessionProxyMatch[1]
      const action = sessionProxyMatch[2]
      const target = getUpstreamTarget(agentId)

      if (!target) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found or not running` }))
        return
      }

      // Buffer body to extract session_id for ownership check
      const bodyStr = await readBody(req)
      let bodyJson: JsonRecord = {}
      try {
        bodyJson = bodyStr ? JSON.parse(bodyStr) : {}
      } catch {
        // If body isn't JSON, pass through
      }

      const sessionId = bodyJson.session_id as string
      if (sessionId) {
        const allowed = await checkSessionOwnership(sessionId)
        if (!allowed) {
          sendForbidden()
          return
        }
      }

      // For reply (SSE streaming), we need to use fetch directly since we've consumed the body
      if (action === 'reply') {
        try {
          const upstreamResponse = await fetch(`${target}/reply`, {
            method: 'POST',
            headers: upstreamHeaders,
            body: bodyStr,
          })

          // Stream the response back
          res.writeHead(upstreamResponse.status, {
            'Content-Type': upstreamResponse.headers.get('content-type') || 'text/event-stream',
            'Cache-Control': 'no-cache',
            'Connection': 'keep-alive',
          })

          if (upstreamResponse.body) {
            const reader = upstreamResponse.body.getReader()
            const pump = async () => {
              while (true) {
                const { done, value } = await reader.read()
                if (done) { res.end(); break }
                res.write(value)
              }
            }
            pump().catch(() => res.end())
          } else {
            res.end()
          }
        } catch (err) {
          console.error('SSE proxy error:', err)
          if (!res.headersSent) {
            res.writeHead(502, { 'Content-Type': 'application/json' })
          }
          res.end(JSON.stringify({ error: 'Bad gateway' }))
        }
        return
      }

      // For non-streaming routes, use fetch and return JSON
      const result = await postJsonToAgent(agentId, `/agent/${action}`, bodyJson)
      res.writeHead(result.ok ? 200 : result.status, { 'Content-Type': 'application/json' })
      res.end(result.raw || JSON.stringify(result.json || { error: `Failed to ${action} session` }))
      return
    }

    // ===== Non-user-scoped proxy routes =====

    // GET/POST /agents/:id/mcp — proxy to goosed /config/extensions (hot reload MCP config)
    const mcpMatch = pathname.match(/^\/agents\/([^/]+)\/mcp\/?$/)
    if (mcpMatch && (req.method === 'GET' || req.method === 'POST')) {
      const agentId = mcpMatch[1]
      const target = manager.getTarget(agentId)

      if (!target) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found or not running` }))
        return
      }

      req.url = '/config/extensions'
      proxy.web(req, res, { target })
      return
    }

    // DELETE /agents/:id/mcp/:name — proxy to goosed /config/extensions/{name}
    const mcpDeleteMatch = pathname.match(/^\/agents\/([^/]+)\/mcp\/(.+)$/)
    if (mcpDeleteMatch && req.method === 'DELETE') {
      const agentId = mcpDeleteMatch[1]
      const mcpName = mcpDeleteMatch[2]
      const target = manager.getTarget(agentId)

      if (!target) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found or not running` }))
        return
      }

      req.url = `/config/extensions/${mcpName}`
      proxy.web(req, res, { target })
      return
    }

    // GET/PUT /agents/:id/config — agent configuration (port, AGENTS.md)
    const configMatch = pathname.match(/^\/agents\/([^/]+)\/config\/?$/)
    if (configMatch && (req.method === 'GET' || req.method === 'PUT')) {
      const agentId = configMatch[1]

      if (req.method === 'GET') {
        const agentConfig = manager.getAgentConfig(agentId)
        if (!agentConfig) {
          res.writeHead(404, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify({ error: `Agent '${agentId}' not found` }))
          return
        }
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify(agentConfig))
        return
      }

      if (req.method === 'PUT') {
        const body = await readBody(req)
        try {
          const updates = JSON.parse(body)
          const result = manager.updateAgentConfig(agentId, updates)
          if (result.success) {
            res.writeHead(200, { 'Content-Type': 'application/json' })
            res.end(JSON.stringify(result))
          } else {
            res.writeHead(400, { 'Content-Type': 'application/json' })
            res.end(JSON.stringify(result))
          }
        } catch {
          res.writeHead(400, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify({ error: 'Invalid JSON body' }))
        }
        return
      }
    }

    // GET /agents/:id/skills — detailed skills list
    const skillsMatch = pathname.match(/^\/agents\/([^/]+)\/skills\/?$/)
    if (skillsMatch && req.method === 'GET') {
      const agentId = skillsMatch[1]
      const skills = manager.getAgentSkillsDetailed(agentId)
      res.writeHead(200, { 'Content-Type': 'application/json' })
      res.end(JSON.stringify({ skills }))
      return
    }

    // POST /agents/:id/validate-port — validate port availability
    const validatePortMatch = pathname.match(/^\/agents\/([^/]+)\/validate-port\/?$/)
    if (validatePortMatch && req.method === 'POST') {
      const agentId = validatePortMatch[1]
      const body = await readBody(req)
      try {
        const { port } = JSON.parse(body)
        if (typeof port !== 'number') {
          res.writeHead(400, { 'Content-Type': 'application/json' })
          res.end(JSON.stringify({ error: 'Port must be a number' }))
          return
        }
        const result = manager.validatePort(port, agentId)
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify(result))
      } catch {
        res.writeHead(400, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: 'Invalid JSON body' }))
      }
      return
    }

    // /agents/:id/* — catch-all proxy to goosed instance
    // This covers: schedule routes, tools, system_info, etc.
    const match = pathname.match(/^\/agents\/([^/]+)(\/.*)?$/)
    if (match) {
      const agentId = match[1]
      const path = match[2] || '/'
      const target = manager.getTarget(agentId)

      if (!target) {
        res.writeHead(404, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({ error: `Agent '${agentId}' not found or not running` }))
        return
      }

      req.url = path
      proxy.web(req, res, { target })
      return
    }

    res.writeHead(404, { 'Content-Type': 'application/json' })
    res.end(JSON.stringify({ error: 'Not found. Use /agents/:id/* to reach a goosed instance.' }))
  })

  const shutdown = async () => {
    console.log('\nGateway shutting down...')
    server.close()
    await manager.stopAll()
    process.exit(0)
  }

  process.on('SIGINT', shutdown)
  process.on('SIGTERM', shutdown)

  server.listen(config.port, config.host, () => {
    console.log(`Gateway listening on http://${config.host}:${config.port}`)
    for (const a of manager.listAgents()) {
      console.log(`  ${a.status === 'running' ? '✓' : '✗'} ${a.id} — ${a.status}`)
    }
  })
}

// ===== Migration Logic =====

const MIGRATION_MARKER = '.multi-user-migrated'

async function migrateExistingData(
  manager: ProcessManager,
  config: { projectRoot: string; agentsDir: string }
): Promise<void> {
  const markerPath = join(config.projectRoot, 'gateway', 'data', MIGRATION_MARKER)

  // Skip if migration already done
  if (existsSync(markerPath)) {
    return
  }

  console.log(`First run detected — migrating existing artifact files to default user "${DEFAULT_USER}"...`)

  const agents = manager.listAgents()
  let fileCount = 0

  for (const agent of agents) {
    const artifactsPath = manager.getArtifactsPathAbsolute(agent.id)
    if (!artifactsPath || !existsSync(artifactsPath)) continue

    const userDir = join(artifactsPath, 'users', DEFAULT_USER)
    if (!existsSync(userDir)) {
      mkdirSync(userDir, { recursive: true })
    }

    try {
      const entries = readdirSync(artifactsPath, { withFileTypes: true })
      for (const entry of entries) {
        // Skip the 'users' directory itself
        if (entry.name === 'users') continue
        // Skip hidden files like .DS_Store
        if (entry.name.startsWith('.')) continue

        const src = join(artifactsPath, entry.name)
        const dst = join(userDir, entry.name)
        try {
          renameSync(src, dst)
          fileCount++
        } catch (err) {
          console.warn(`  Migration: could not move ${src} → ${dst}:`, (err as Error).message)
        }
      }
    } catch (err) {
      console.warn(`  Migration: could not list artifacts for ${agent.id}:`, (err as Error).message)
    }
  }

  // Write marker file
  const markerDir = join(config.projectRoot, 'gateway', 'data')
  if (!existsSync(markerDir)) {
    mkdirSync(markerDir, { recursive: true })
  }
  writeFileSync(markerPath, new Date().toISOString(), 'utf-8')

  console.log(`  Migration complete: ${fileCount} files moved to "${DEFAULT_USER}"`)
}

main().catch(err => {
  console.error('Fatal:', err)
  process.exit(1)
})
