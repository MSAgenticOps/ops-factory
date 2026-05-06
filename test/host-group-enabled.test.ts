/**
 * HostGroup enabled toggle — Integration Test
 *
 * Tests the full lifecycle of the group enabled/disabled feature:
 * 1. Create a group → enabled defaults to true
 * 2. Update group to disabled → persisted as false
 * 3. Read back → returns false
 * 4. List with enabledOnly=true → group filtered out
 * 5. Update back to enabled → persisted as true
 * 6. Read back → returns true
 * 7. List with enabledOnly=true → group shows up
 * 8. Hosts in disabled group/cluster are filtered by enabledOnly
 *
 * Prerequisites: Java gateway JAR must be built (mvn package).
 * Run: cd test && npx vitest run --config vitest.config.ts host-group-enabled.test.ts
 */
import { describe, it, expect, beforeAll, afterAll } from 'vitest'
import { startJavaGateway, type GatewayHandle } from './helpers.js'

let gw: GatewayHandle

beforeAll(async () => {
  gw = await startJavaGateway()
}, 120_000)

afterAll(async () => {
  await gw.stop()
})

async function api(method: string, path: string, body?: unknown) {
  const init: RequestInit = {
    method,
    headers: { 'Content-Type': 'application/json' },
  }
  if (body !== undefined) {
    init.body = JSON.stringify(body)
  }
  const res = await gw.fetch(path, init)
  const data = await res.json()
  return { status: res.status, data }
}

describe('HostGroup enabled toggle', () => {

  const groupIds: string[] = []
  const clusterIds: string[] = []
  const hostIds: string[] = []

  afterAll(async () => {
    // Cleanup in reverse order: hosts → clusters → groups
    for (const id of hostIds) await api('DELETE', `/hosts/${id}`).catch(() => {})
    for (const id of clusterIds) await api('DELETE', `/clusters/${id}?force=true`).catch(() => {})
    for (const id of groupIds) await api('DELETE', `/host-groups/${id}?force=true`).catch(() => {})
  })

  it('create group defaults to enabled=true', async () => {
    const { status, data } = await api('POST', '/host-groups/', {
      name: 'PROD-TEST',
      code: 'PROD',
    })
    expect(status).toBe(201)
    expect(data.success).toBe(true)
    expect(data.group.enabled).toBe(true)
    expect(data.group.name).toBe('PROD-TEST')
    groupIds.push(data.group.id)
  })

  it('create group with enabled=false', async () => {
    const { status, data } = await api('POST', '/host-groups/', {
      name: 'DISABLED-TEST',
      code: 'OFF',
      enabled: false,
    })
    expect(status).toBe(201)
    expect(data.success).toBe(true)
    expect(data.group.enabled).toBe(false)
    groupIds.push(data.group.id)
  })

  it('get group returns correct enabled state', async () => {
    const disabledGroup = groupIds[1]
    const { status, data } = await api('GET', `/host-groups/${disabledGroup}`)
    expect(status).toBe(200)
    expect(data.group.enabled).toBe(false)
  })

  it('update group to disabled persists enabled=false', async () => {
    const groupId = groupIds[0] // PROD-TEST, originally enabled=true

    // Step 1: Disable
    const { status, data } = await api('PUT', `/host-groups/${groupId}`, {
      name: 'PROD-TEST',
      code: 'PROD',
      description: '',
      enabled: false,
    })
    expect(status).toBe(200)
    expect(data.success).toBe(true)
    expect(data.group.enabled).toBe(false)
  })

  it('read back disabled group returns enabled=false', async () => {
    const groupId = groupIds[0]
    const { status, data } = await api('GET', `/host-groups/${groupId}`)
    expect(status).toBe(200)
    expect(data.group.enabled).toBe(false)
  })

  it('list with enabledOnly=true excludes disabled groups', async () => {
    const { status, data } = await api('GET', '/host-groups/?enabledOnly=true')
    expect(status).toBe(200)
    const ids = (data.groups as any[]).map((g: any) => g.id)
    // groupIds[0] was disabled, groupIds[1] was disabled — neither should appear
    expect(ids).not.toContain(groupIds[0])
    expect(ids).not.toContain(groupIds[1])
  })

  it('list without enabledOnly returns all groups', async () => {
    const { status, data } = await api('GET', '/host-groups/')
    expect(status).toBe(200)
    const ids = (data.groups as any[]).map((g: any) => g.id)
    expect(ids).toContain(groupIds[0])
    expect(ids).toContain(groupIds[1])
  })

  it('update group back to enabled persists enabled=true', async () => {
    const groupId = groupIds[0]

    const { status, data } = await api('PUT', `/host-groups/${groupId}`, {
      name: 'PROD-TEST',
      code: 'PROD',
      description: '',
      enabled: true,
    })
    expect(status).toBe(200)
    expect(data.success).toBe(true)
    expect(data.group.enabled).toBe(true)
  })

  it('read back re-enabled group returns enabled=true', async () => {
    const groupId = groupIds[0]
    const { status, data } = await api('GET', `/host-groups/${groupId}`)
    expect(status).toBe(200)
    expect(data.group.enabled).toBe(true)
  })

  it('list with enabledOnly=true now includes re-enabled group', async () => {
    const { status, data } = await api('GET', '/host-groups/?enabledOnly=true')
    expect(status).toBe(200)
    const ids = (data.groups as any[]).map((g: any) => g.id)
    expect(ids).toContain(groupIds[0]) // re-enabled
    expect(ids).not.toContain(groupIds[1]) // still disabled
  })

  it('partial update (no enabled field) preserves existing enabled state', async () => {
    const groupId = groupIds[1] // DISABLED-TEST, currently enabled=false

    const { status, data } = await api('PUT', `/host-groups/${groupId}`, {
      description: 'updated description only',
    })
    expect(status).toBe(200)
    expect(data.success).toBe(true)
    // enabled should still be false — not overwritten
    expect(data.group.enabled).toBe(false)
    expect(data.group.description).toBe('updated description only')
  })

  it('tree with enabledOnly=true filters out disabled groups and children', async () => {
    const { status, data } = await api('GET', '/host-groups/tree?enabledOnly=true')
    expect(status).toBe(200)
    const tree = data.tree as any[]
    const treeIds = tree.map((n: any) => n.id)
    expect(treeIds).not.toContain(groupIds[1]) // disabled group
  })

  // ── Host filtering by disabled group/cluster ──────────────────

  describe('hosts in disabled groups are filtered', () => {

    it('creates cluster + host in the disabled group', async () => {
      const disabledGroupId = groupIds[1] // DISABLED-TEST

      // Create cluster in disabled group
      const cl = await api('POST', '/clusters/', {
        name: 'DISABLED-CLUSTER',
        type: 'NSLB',
        purpose: 'test',
        groupId: disabledGroupId,
      })
      expect(cl.status).toBe(201)
      expect(cl.data.success).toBe(true)
      clusterIds.push(cl.data.cluster.id)

      // Create host in that cluster
      const h = await api('POST', '/hosts/', {
        name: 'DISABLED-HOST',
        ip: '10.99.99.1',
        port: 22,
        username: 'root',
        authType: 'password',
        credential: 'test',
        clusterId: cl.data.cluster.id,
        tags: ['NSLB'],
      })
      expect(h.status).toBe(201)
      expect(h.data.success).toBe(true)
      hostIds.push(h.data.host.id)
    })

    it('GET /hosts without enabledOnly returns the host', async () => {
      const { status, data } = await api('GET', '/hosts/')
      expect(status).toBe(200)
      const ids = (data.hosts as any[]).map((h: any) => h.id)
      expect(ids).toContain(hostIds[0])
    })

    it('GET /hosts?enabledOnly=true excludes hosts in disabled group', async () => {
      const { status, data } = await api('GET', '/hosts/?enabledOnly=true')
      expect(status).toBe(200)
      const ids = (data.hosts as any[]).map((h: any) => h.id)
      expect(ids).not.toContain(hostIds[0])
    })

    it('GET /hosts?clusterId=X&enabledOnly=true returns empty for disabled group cluster', async () => {
      const { status, data } = await api('GET', `/hosts/?clusterId=${clusterIds[0]}&enabledOnly=true`)
      expect(status).toBe(200)
      expect((data.hosts as any[]).length).toBe(0)
    })

    it('GET /hosts?groupId=X&enabledOnly=true returns empty for disabled group', async () => {
      const { status, data } = await api('GET', `/hosts/?groupId=${groupIds[1]}&enabledOnly=true`)
      expect(status).toBe(200)
      expect((data.hosts as any[]).length).toBe(0)
    })

    it('GET /clusters?enabledOnly=true excludes clusters in disabled group', async () => {
      const { status, data } = await api('GET', '/clusters/?enabledOnly=true')
      expect(status).toBe(200)
      const ids = (data.clusters as any[]).map((c: any) => c.id)
      expect(ids).not.toContain(clusterIds[0])
    })

    it('re-enable the group makes hosts visible again', async () => {
      // Re-enable the disabled group
      await api('PUT', `/host-groups/${groupIds[1]}`, { enabled: true })

      const { status, data } = await api('GET', '/hosts/?enabledOnly=true')
      expect(status).toBe(200)
      const ids = (data.hosts as any[]).map((h: any) => h.id)
      expect(ids).toContain(hostIds[0])

      // Disable it again for cleanup consistency
      await api('PUT', `/host-groups/${groupIds[1]}`, { enabled: false })
    })
  })
})
