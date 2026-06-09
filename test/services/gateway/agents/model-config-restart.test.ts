/**
 * Model config restart E2E — proves the full save → restart → take-effect chain.
 *
 * Model/provider config reaches goosed only as spawn-time environment variables
 * (env wins over config.yaml inside goose), so a running instance keeps serving
 * the old model after a save. This test drives the real gateway + goosed + a
 * mock OpenAI-compatible LLM server and asserts:
 *   1. a running instance keeps using the OLD model after model-config is saved
 *   2. POST /agents/:id/instances/restart stops the instance
 *   3. the lazily respawned instance uses the NEW model
 */
import { createServer, type Server } from 'node:http'
import { rm } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { startJavaGateway, freePort, type GatewayHandle } from '../../../platform/shared/helpers.js'
import { makeUserMessage } from '../../../platform/shared/journey-helpers.js'

const PROJECT_ROOT = resolve(import.meta.dirname, '..', '..', '..', '..')
const USER_SYS = 'admin'
const USER_CHAT = 'test-restart-user'
const PROVIDER_NAME = 'custom_e2e_restart'
const MODEL_ONE = 'e2e-model-one'
const MODEL_TWO = 'e2e-model-two'

const agentId = `test-model-restart-${Date.now()}`

let gw: GatewayHandle
let mockLlm: Server
let mockPort: number

/** Models requested by goosed against the mock LLM, in arrival order. */
const requestedModels: string[] = []

/**
 * Minimal OpenAI-compatible chat completions server. Records the `model` of
 * every request; answers streaming requests with SSE chunks and non-streaming
 * ones (e.g. background session naming) with a plain completion object.
 */
function startMockLlm(port: number): Promise<Server> {
  const server = createServer((req, res) => {
    let raw = ''
    req.on('data', (chunk) => { raw += chunk })
    req.on('end', () => {
      let body: any = {}
      try { body = JSON.parse(raw) } catch { /* keep empty */ }
      const model = String(body.model || 'unknown')
      requestedModels.push(model)

      const usage = { prompt_tokens: 1, completion_tokens: 1, total_tokens: 2 }
      if (body.stream) {
        res.writeHead(200, { 'Content-Type': 'text/event-stream' })
        const chunk = (payload: object) => `data: ${JSON.stringify(payload)}\n\n`
        res.write(chunk({
          id: 'chatcmpl-e2e', object: 'chat.completion.chunk', created: 1, model,
          choices: [{ index: 0, delta: { role: 'assistant', content: 'ok' }, finish_reason: null }],
        }))
        res.write(chunk({
          id: 'chatcmpl-e2e', object: 'chat.completion.chunk', created: 1, model,
          choices: [{ index: 0, delta: {}, finish_reason: 'stop' }], usage,
        }))
        res.write('data: [DONE]\n\n')
        res.end()
      } else {
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify({
          id: 'chatcmpl-e2e', object: 'chat.completion', created: 1, model,
          choices: [{ index: 0, message: { role: 'assistant', content: 'ok' }, finish_reason: 'stop' }],
          usage,
        }))
      }
    })
  })
  return new Promise((resolveStart, reject) => {
    server.listen(port, '127.0.0.1', () => resolveStart(server))
    server.on('error', reject)
  })
}

/** Start a session and send one message so goosed performs a real LLM call. */
async function chatOnce(text: string): Promise<void> {
  const startRes = await gw.fetchAs(USER_CHAT, `/agents/${agentId}/agent/start`, {
    method: 'POST', body: JSON.stringify({}),
  })
  if (!startRes.ok) {
    throw new Error(`agent/start failed (${startRes.status}): ${await startRes.text()}`)
  }
  const sessionId = (await startRes.json()).id as string

  const resumeRes = await gw.fetchAs(USER_CHAT, `/agents/${agentId}/agent/resume`, {
    method: 'POST',
    body: JSON.stringify({ session_id: sessionId, load_model_and_extensions: true }),
  })
  expect(resumeRes.ok).toBe(true)

  const replyRes = await gw.fetchAs(
    USER_CHAT,
    `/agents/${agentId}/sessions/${encodeURIComponent(sessionId)}/reply`,
    {
      method: 'POST',
      body: JSON.stringify({ request_id: crypto.randomUUID(), user_message: makeUserMessage(text) }),
    },
  )
  expect(replyRes.ok).toBe(true)
}

/** Wait until the mock LLM has recorded at least one request past `fromIndex`. */
async function waitForLlmHit(fromIndex: number, timeoutMs = 60_000): Promise<string> {
  const start = Date.now()
  while (Date.now() - start < timeoutMs) {
    if (requestedModels.length > fromIndex) {
      return requestedModels[fromIndex]
    }
    await new Promise((r) => setTimeout(r, 500))
  }
  throw new Error(`mock LLM not called within ${timeoutMs}ms (recorded: ${requestedModels.join(', ')})`)
}

beforeAll(async () => {
  mockPort = await freePort()
  mockLlm = await startMockLlm(mockPort)
  gw = await startJavaGateway()

  const createRes = await gw.fetchAs(USER_SYS, '/agents', {
    method: 'POST',
    body: JSON.stringify({ id: agentId, name: `Model Restart E2E ${Date.now()}` }),
  })
  expect(createRes.status).toBe(201)

  const providerRes = await gw.fetchAs(USER_SYS, `/agents/${agentId}/providers`, {
    method: 'POST',
    body: JSON.stringify({
      name: PROVIDER_NAME,
      display_name: 'E2E Restart Provider',
      base_url: `http://127.0.0.1:${mockPort}/v1/chat/completions`,
      api_key: 'e2e-test-key',
      // goose requires context_limit on every custom provider model; parsing fails without it.
      models: [{ name: MODEL_ONE, context_limit: 32000 }, { name: MODEL_TWO, context_limit: 32000 }],
    }),
  })
  expect(providerRes.status).toBe(201)
}, 90_000)

afterAll(async () => {
  if (gw) {
    await gw.fetchAs(USER_SYS, `/agents/${agentId}`, { method: 'DELETE' }).catch(() => undefined)
    await gw.stop()
  }
  if (mockLlm) {
    await new Promise((r) => mockLlm.close(r))
  }
  // Remove the throwaway per-user runtime dir created by this test's chats.
  await rm(join(PROJECT_ROOT, 'gateway', 'users', USER_CHAT), { recursive: true, force: true })
}, 30_000)

describe('model config save → restart → take effect (E2E)', () => {
  it('save with no running instances reports requiresRestart and zero instances', async () => {
    const res = await gw.fetchAs(USER_SYS, `/agents/${agentId}/model-config`, {
      method: 'PUT',
      body: JSON.stringify({ GOOSE_PROVIDER: PROVIDER_NAME, GOOSE_MODEL: MODEL_ONE }),
    })
    expect(res.ok).toBe(true)
    const data = await res.json()
    expect(data.success).toBe(true)
    expect(data.requiresRestart).toBe(true)
    expect(data.runningInstances).toBe(0)
  }, 30_000)

  it('a running instance serves the configured model', async () => {
    const mark = requestedModels.length
    await chatOnce('hello one')
    const model = await waitForLlmHit(mark)
    expect(model).toBe(MODEL_ONE)
  }, 120_000)

  it('saving a new model reports the running instance, which keeps the OLD model', async () => {
    const saveRes = await gw.fetchAs(USER_SYS, `/agents/${agentId}/model-config`, {
      method: 'PUT',
      body: JSON.stringify({ GOOSE_PROVIDER: PROVIDER_NAME, GOOSE_MODEL: MODEL_TWO }),
    })
    expect(saveRes.ok).toBe(true)
    const saved = await saveRes.json()
    expect(saved.requiresRestart).toBe(true)
    expect(saved.runningInstances).toBe(1)

    // Same goosed process: spawn-time env still pins the old model.
    const mark = requestedModels.length
    await chatOnce('hello stale')
    const model = await waitForLlmHit(mark)
    expect(model).toBe(MODEL_ONE)
  }, 120_000)

  it('restart endpoint stops the running instance', async () => {
    const res = await gw.fetchAs(USER_SYS, `/agents/${agentId}/instances/restart`, { method: 'POST' })
    expect(res.ok).toBe(true)
    const data = await res.json()
    expect(data.success).toBe(true)
    expect(data.stoppedInstances).toBe(1)
  }, 60_000)

  it('the respawned instance serves the NEW model', async () => {
    const mark = requestedModels.length
    await chatOnce('hello two')
    const model = await waitForLlmHit(mark)
    expect(model).toBe(MODEL_TWO)
  }, 120_000)

  it('restart for an unknown agent returns 400', async () => {
    const res = await gw.fetchAs(USER_SYS, '/agents/no-such-agent-xyz/instances/restart', { method: 'POST' })
    expect(res.status).toBe(400)
  })
})
