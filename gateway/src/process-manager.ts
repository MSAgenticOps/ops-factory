import { ChildProcess, spawn } from 'node:child_process'
import { existsSync, readdirSync, readFileSync, writeFileSync } from 'node:fs'
import { mkdir } from 'node:fs/promises'
import { relative, join } from 'node:path'
import { parse as parseYaml, stringify as stringifyYaml } from 'yaml'
import type { AgentConfig, GatewayConfig } from './config.js'

interface ManagedProcess {
  config: AgentConfig
  child: ChildProcess | null
  status: 'starting' | 'running' | 'stopped' | 'error'
}

export class ProcessManager {
  private processes = new Map<string, ManagedProcess>()
  private config: GatewayConfig

  constructor(config: GatewayConfig) {
    this.config = config
  }

  async startAll(): Promise<void> {
    const results = await Promise.allSettled(
      this.config.agents.map(agent => this.startAgent(agent))
    )
    for (const r of results) {
      if (r.status === 'rejected') {
        console.error('Agent start failed:', r.reason)
      }
    }
  }

  private getArtifactsPath(agentId: string): string {
    return `${this.config.agentsDir}/${agentId}/artifacts`
  }

  private getAgentRootPath(agentId: string): string {
    return `${this.config.agentsDir}/${agentId}`
  }

  private getArtifactsPathRelative(agentId: string): string {
    const artifactsPath = this.getArtifactsPath(agentId)
    const relativePath = relative(this.config.projectRoot, artifactsPath)
    return relativePath || '.'
  }

  // Public method to get absolute artifacts path for file operations
  getArtifactsPathAbsolute(agentId: string): string | null {
    const m = this.processes.get(agentId)
    if (!m) return null
    return this.getArtifactsPath(m.config.id)
  }

  // Get per-user artifacts path: {agentsDir}/{agentId}/artifacts/users/{userId}
  getUserArtifactsPath(agentId: string, userId: string): string | null {
    const m = this.processes.get(agentId)
    if (!m) return null
    return `${this.getArtifactsPath(m.config.id)}/users/${userId}`
  }

  private getAgentSkills(agentId: string): string[] {
    const skillsDir = join(this.getAgentRootPath(agentId), 'config', 'skills')
    if (!existsSync(skillsDir)) return []

    try {
      return readdirSync(skillsDir, { withFileTypes: true })
        .filter(entry => entry.isDirectory())
        .map(entry => entry.name)
        .sort((a, b) => a.localeCompare(b))
    } catch {
      return []
    }
  }



  private async startAgent(agent: AgentConfig): Promise<void> {
    console.log(`Starting ${agent.id} on port ${agent.port}...`)

    // Ensure artifacts directory exists
    const artifactsPath = this.getArtifactsPath(agent.id)
    await mkdir(artifactsPath, { recursive: true })

    // Build environment for goosed
    // Read agent config.yaml and secrets.yaml, inject top-level string values as env vars
    // so that goosed tracing (e.g. Langfuse) can pick them up via std::env::var()
    const agentConfigEnv = this.getAgentConfigEnv(agent.id)

    const env: Record<string, string> = {
      ...(process.env as Record<string, string>),
      ...agentConfigEnv,
      GOOSE_PORT: String(agent.port),
      GOOSE_HOST: agent.host,
      GOOSE_SERVER__SECRET_KEY: agent.secret_key,
      GOOSE_PATH_ROOT: this.getAgentRootPath(agent.id),
      GOOSE_DISABLE_KEYRING: '1', // Use file-based secrets.yaml instead of keyring
    }

    const child = spawn(this.config.goosedBin, ['agent'], {
      env,
      cwd: artifactsPath,
      stdio: ['ignore', 'pipe', 'pipe'],
    })

    const managed: ManagedProcess = { config: agent, child, status: 'starting' }
    this.processes.set(agent.id, managed)

    child.stdout?.on('data', (data: Buffer) => {
      const line = data.toString().trim()
      if (line) console.log(`[${agent.id}] ${line}`)
    })

    child.stderr?.on('data', (data: Buffer) => {
      const line = data.toString().trim()
      if (line) console.error(`[${agent.id}] ${line}`)
    })

    child.on('exit', (code) => {
      console.log(`[${agent.id}] exited with code ${code}`)
      managed.status = code === 0 ? 'stopped' : 'error'
      managed.child = null
    })

    await this.waitForReady(agent)
    managed.status = 'running'
    console.log(`[${agent.id}] ready on port ${agent.port}`)
  }

  private async waitForReady(agent: AgentConfig): Promise<void> {
    const url = `http://${agent.host}:${agent.port}/status`
    const maxAttempts = 30

    for (let i = 0; i < maxAttempts; i++) {
      try {
        const res = await fetch(url, {
          headers: { 'x-secret-key': agent.secret_key },
          signal: AbortSignal.timeout(2000),
        })
        if (res.ok) return
      } catch {
        // not ready yet
      }
      await new Promise(r => setTimeout(r, 500))
    }

    throw new Error(`${agent.id} failed to become ready on port ${agent.port}`)
  }

  getTarget(agentId: string): string | null {
    const m = this.processes.get(agentId)
    if (!m || m.status !== 'running') return null
    return `http://${m.config.host}:${m.config.port}`
  }

  listAgents(): Array<{
    id: string
    name: string
    status: string
    working_dir: string
    port: number
    provider?: string
    model?: string
    skills: string[]
  }> {
    return Array.from(this.processes.values()).map(m => {
      const gooseConfig = this.getAgentGooseConfig(m.config.id)
      return {
        id: m.config.id,
        name: m.config.name,
        status: m.status,
        working_dir: this.getArtifactsPathRelative(m.config.id),
        port: m.config.port,
        provider: gooseConfig?.GOOSE_PROVIDER,
        model: gooseConfig?.GOOSE_MODEL,
        skills: this.getAgentSkills(m.config.id),
      }
    })
  }

  /**
   * Read top-level string/number/boolean values from agent config.yaml and secrets.yaml,
   * return them as a flat key-value map suitable for process environment variables.
   * This ensures vars like LANGFUSE_* are available to goosed via std::env::var().
   */
  private getAgentConfigEnv(agentId: string): Record<string, string> {
    const result: Record<string, string> = {}
    const configDir = join(this.getAgentRootPath(agentId), 'config')

    for (const filename of ['config.yaml', 'secrets.yaml']) {
      const filePath = join(configDir, filename)
      if (!existsSync(filePath)) continue
      try {
        const parsed = parseYaml(readFileSync(filePath, 'utf-8')) as Record<string, unknown>
        if (!parsed || typeof parsed !== 'object') continue
        for (const [key, value] of Object.entries(parsed)) {
          if (typeof value === 'string') result[key] = value
          else if (typeof value === 'number' || typeof value === 'boolean') result[key] = String(value)
        }
      } catch { /* ignore parse errors */ }
    }
    return result
  }

  private getAgentGooseConfig(agentId: string): { GOOSE_PROVIDER?: string; GOOSE_MODEL?: string } | null {
    const configPath = join(this.getAgentRootPath(agentId), 'config', 'config.yaml')
    if (!existsSync(configPath)) return null
    try {
      const content = readFileSync(configPath, 'utf-8')
      return parseYaml(content) as { GOOSE_PROVIDER?: string; GOOSE_MODEL?: string }
    } catch {
      return null
    }
  }

  async stopAll(): Promise<void> {
    for (const [, managed] of this.processes) {
      if (managed.child) {
        managed.child.kill('SIGTERM')
        managed.status = 'stopped'
      }
    }
    await new Promise(r => setTimeout(r, 1000))
  }

  // ========== Agent Config Management ==========

  private getAgentConfigPath(agentId: string): string {
    return join(this.config.agentsDir, agentId, 'config.yaml')
  }

  private getAgentsMdPath(agentId: string): string {
    return join(this.config.agentsDir, agentId, 'AGENTS.md')
  }

  getAgentConfig(agentId: string): {
    id: string
    name: string
    port: number
    agentsMd: string
    workingDir: string
    provider?: string
    model?: string
  } | null {
    const m = this.processes.get(agentId)
    if (!m) return null

    // Read AGENTS.md content
    const agentsMdPath = this.getAgentsMdPath(agentId)
    let agentsMd = ''
    if (existsSync(agentsMdPath)) {
      try {
        agentsMd = readFileSync(agentsMdPath, 'utf-8')
      } catch {
        agentsMd = ''
      }
    }

    const gooseConfig = this.getAgentGooseConfig(agentId)

    return {
      id: m.config.id,
      name: m.config.name,
      port: m.config.port,
      agentsMd,
      workingDir: this.getArtifactsPathRelative(agentId),
      provider: gooseConfig?.GOOSE_PROVIDER,
      model: gooseConfig?.GOOSE_MODEL,
    }
  }

  updateAgentConfig(agentId: string, updates: { port?: number; agentsMd?: string }): {
    success: boolean
    error?: string
    requiresRestart?: boolean
  } {
    const m = this.processes.get(agentId)
    if (!m) {
      return { success: false, error: `Agent '${agentId}' not found` }
    }

    let requiresRestart = false

    try {
      // Update port in config.yaml
      if (updates.port !== undefined && updates.port !== m.config.port) {
        const configPath = this.getAgentConfigPath(agentId)
        if (existsSync(configPath)) {
          const configContent = readFileSync(configPath, 'utf-8')
          const config = parseYaml(configContent) as Record<string, unknown>
          config.port = updates.port
          writeFileSync(configPath, stringifyYaml(config), 'utf-8')
          requiresRestart = true
        } else {
          return { success: false, error: 'Config file not found' }
        }
      }

      // Update AGENTS.md
      if (updates.agentsMd !== undefined) {
        const agentsMdPath = this.getAgentsMdPath(agentId)
        writeFileSync(agentsMdPath, updates.agentsMd, 'utf-8')
      }

      return { success: true, requiresRestart }
    } catch (err) {
      return { success: false, error: err instanceof Error ? err.message : 'Unknown error' }
    }
  }

  // ========== Skills Detailed ==========

  getAgentSkillsDetailed(agentId: string): Array<{
    name: string
    description: string
    path: string
  }> {
    const skillsDir = join(this.getAgentRootPath(agentId), 'config', 'skills')
    if (!existsSync(skillsDir)) return []

    try {
      const skillDirs = readdirSync(skillsDir, { withFileTypes: true })
        .filter(entry => entry.isDirectory())
        .map(entry => entry.name)
        .sort((a, b) => a.localeCompare(b))

      return skillDirs.map(skillName => {
        const skillPath = join(skillsDir, skillName)
        const skillMdPath = join(skillPath, 'SKILL.md')

        let description = ''
        if (existsSync(skillMdPath)) {
          try {
            const content = readFileSync(skillMdPath, 'utf-8')
            // Extract description from YAML frontmatter or first paragraph
            const frontmatterMatch = content.match(/^---\s*\n([\s\S]*?)\n---/)
            if (frontmatterMatch) {
              const frontmatter = frontmatterMatch[1]
              const descMatch = frontmatter.match(/description:\s*(.+)/)
              if (descMatch) {
                description = descMatch[1].trim().replace(/^["']|["']$/g, '')
              }
            }
            // Fallback: first non-empty line after frontmatter or title
            if (!description) {
              const lines = content.split('\n')
              for (const line of lines) {
                const trimmed = line.trim()
                if (trimmed && !trimmed.startsWith('#') && !trimmed.startsWith('---')) {
                  description = trimmed
                  break
                }
              }
            }
          } catch {
            // ignore read errors
          }
        }

        return {
          name: skillName,
          description: description || 'No description available',
          path: `.claude/skills/${skillName}`,
        }
      })
    } catch {
      return []
    }
  }

  // ========== Port Validation ==========

  validatePort(port: number, excludeAgentId?: string): {
    valid: boolean
    conflictWith?: string
  } {
    // Check port range
    if (port < 1024 || port > 65535) {
      return { valid: false }
    }

    // Check for conflict with other agents
    for (const [id, managed] of this.processes) {
      if (excludeAgentId && id === excludeAgentId) continue
      if (managed.config.port === port) {
        return { valid: false, conflictWith: id }
      }
    }

    // Check gateway port
    if (port === this.config.port) {
      return { valid: false, conflictWith: 'gateway' }
    }

    return { valid: true }
  }
}
