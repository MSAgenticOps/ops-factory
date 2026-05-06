import { afterAll, beforeAll, describe, expect, it } from 'vitest'
import { existsSync } from 'node:fs'
import { mkdir, readFile, rm, writeFile } from 'node:fs/promises'
import { join, resolve } from 'node:path'
import { randomUUID } from 'node:crypto'
import { startJavaGateway, type GatewayHandle } from './helpers.js'

const PROJECT_ROOT = resolve(import.meta.dirname, '..')
const GATEWAY_ROOT = join(PROJECT_ROOT, 'gateway')
const CHANNEL_ID = `channel-layout-${randomUUID().slice(0, 8)}`
let gateway: GatewayHandle | undefined

describe('channel runtime layout', () => {
  beforeAll(async () => {
    gateway = await startJavaGateway()
    await deleteChannelIfPresent()
  }, 120_000)

  afterAll(async () => {
    await deleteChannelIfPresent()
    await gateway?.stop()
  }, 120_000)

  it('stores config under gateway/channels and runtime under gateway/users', async () => {
    const createResponse = await gateway.fetch('/channels', {
      method: 'POST',
      body: JSON.stringify({
        id: CHANNEL_ID,
        name: 'Channel Layout E2E',
        type: 'whatsapp',
        enabled: true,
        defaultAgentId: 'fo-copilot',
        config: {
          loginStatus: 'connected',
          authStateDir: 'auth',
          lastConnectedAt: 'old',
          selfPhone: '+10000000000',
        },
      }),
    })
    expect(createResponse.status).toBe(201)

    const configDir = join(GATEWAY_ROOT, 'channels', 'whatsapp', CHANNEL_ID)
    const runtimeDir = join(GATEWAY_ROOT, 'users', 'admin', 'channels', 'whatsapp', CHANNEL_ID)

    expect(existsSync(join(configDir, 'config.json'))).toBe(true)
    expect(existsSync(join(configDir, 'bindings.json'))).toBe(false)
    expect(existsSync(join(configDir, 'events.json'))).toBe(false)
    expect(existsSync(join(configDir, 'inbound-dedup.json'))).toBe(false)
    expect(existsSync(join(configDir, 'auth'))).toBe(false)
    expect(existsSync(join(configDir, 'inbox'))).toBe(false)

    expect(existsSync(join(runtimeDir, 'bindings.json'))).toBe(true)
    expect(existsSync(join(runtimeDir, 'events.json'))).toBe(true)
    expect(existsSync(join(runtimeDir, 'inbound-dedup.json'))).toBe(true)

    const config = JSON.parse(await readFile(join(configDir, 'config.json'), 'utf-8'))
    expect(config.ownerUserId).toBe('admin')
    expect(config.config).toEqual({ authStateDir: 'auth' })
  })

  it('ignores old channel-directory runtime state and reads user runtime state', async () => {
    const configDir = join(GATEWAY_ROOT, 'channels', 'whatsapp', CHANNEL_ID)
    const runtimeDir = join(GATEWAY_ROOT, 'users', 'admin', 'channels', 'whatsapp', CHANNEL_ID)
    await mkdir(configDir, { recursive: true })
    await mkdir(runtimeDir, { recursive: true })

    await writeFile(join(configDir, 'login-state.json'), JSON.stringify({
      status: 'connected',
      selfPhone: '+10000000000',
    }))

    let detailResponse = await gateway.fetch(`/channels/${CHANNEL_ID}`)
    expect(detailResponse.status).toBe(200)
    let detail = await detailResponse.json()
    expect(detail.config.loginStatus).toBe('disconnected')
    expect(detail.config.selfPhone).toBe('')

    await writeFile(join(runtimeDir, 'login-state.json'), JSON.stringify({
      status: 'connected',
      selfPhone: '+8613800000000',
      lastConnectedAt: '2026-05-06T00:00:00Z',
    }))

    detailResponse = await gateway.fetch(`/channels/${CHANNEL_ID}`)
    expect(detailResponse.status).toBe(200)
    detail = await detailResponse.json()
    expect(detail.config.loginStatus).toBe('connected')
    expect(detail.config.selfPhone).toBe('+8613800000000')
    expect(detail.config.lastConnectedAt).toBe('2026-05-06T00:00:00Z')
  })
})

async function deleteChannelIfPresent() {
  if (!gateway) return
  await gateway.fetch(`/channels/${CHANNEL_ID}`, { method: 'DELETE' }).catch(() => undefined)
  await rm(join(GATEWAY_ROOT, 'channels', 'whatsapp', CHANNEL_ID), { recursive: true, force: true })
  await rm(join(GATEWAY_ROOT, 'users', 'admin', 'channels', 'whatsapp', CHANNEL_ID), { recursive: true, force: true })
}
