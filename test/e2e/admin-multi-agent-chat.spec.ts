/**
 * E2E Tests: Admin multi-agent chat from WebApp
 *
 * These tests intentionally drive the real WebApp UI instead of calling chat
 * APIs directly. They cover admin multi-turn chat sessions for the critical
 * agents involved in the AI chat session flow.
 */
import { test, expect, type APIRequestContext, type Page } from '@playwright/test'

const ADMIN_USER = 'admin'
const USER_STORAGE_KEY = 'opsfactory:userId'
const SESSION_LOCATOR_STORAGE_KEY = 'opsfactory:chat:session-locator'
const GATEWAY_URL = process.env.E2E_GATEWAY_URL || 'http://127.0.0.1:3000/gateway'
const GATEWAY_SECRET_KEY = process.env.GATEWAY_SECRET_KEY || 'test'

interface AgentScenario {
  id: string
  label: string
  prompts: [string, string]
}

interface StartedSession {
  agentId: string
  sessionId: string
}

type SessionBaselines = Map<string, Set<string>>

const AGENTS: AgentScenario[] = [
  {
    id: 'qa-agent',
    label: 'QA Agent',
    prompts: [
      '请从知识库检索“告警管理”，如果证据不足请明确说明，不要只打招呼。',
      '继续上一轮问题，请再用一句话说明你能否从知识库确认“告警管理”的相关内容。',
    ],
  },
  {
    id: 'qa-cli-agent',
    label: 'QA CLI Agent',
    prompts: [
      '请查找包含“告警管理”的 Markdown 文件，并基于文件证据回答，不要只打招呼。',
      '继续上一轮，请再说明你读取到的文件证据是否足以回答。',
    ],
  },
  {
    id: 'supervisor-agent',
    label: 'Supervisor Agent',
    prompts: [
      '请检查 OpsFactory 平台健康状态，并给出简短诊断摘要。',
      '继续上一轮，请再检查一次 Agent 运行状态，并说明是否有异常。',
    ],
  },
  {
    id: 'universal-agent',
    label: 'Universal Agent',
    prompts: [
      '请用一句话回应：这是 admin 多轮对话自动化测试第一轮，不要只说 hello。',
      '继续上一轮，请用一句话确认这是同一个会话的第二轮。',
    ],
  },
]

async function loginAsAdmin(page: Page) {
  await page.goto('/#/')
  await page.evaluate(([storageKey, userId]) => {
    localStorage.setItem(storageKey, userId)
  }, [USER_STORAGE_KEY, ADMIN_USER])
  await page.reload({ waitUntil: 'domcontentloaded' })
  await expect(page.locator('.chat-input')).toBeVisible({ timeout: 20_000 })
  await expect(page.locator('.new-chat-nav')).toBeVisible({ timeout: 20_000 })
}

function trackStartedSessions(page: Page, sessions: StartedSession[]) {
  page.on('response', async response => {
    if (response.request().method() !== 'POST') return
    const match = response.url().match(/\/agents\/([^/]+)\/agent\/start(?:$|\?)/)
    if (!match || response.status() !== 200) return

    try {
      const body = await response.json()
      if (typeof body?.id === 'string') {
        sessions.push({ agentId: decodeURIComponent(match[1]), sessionId: body.id })
      }
    } catch {
      // Best-effort cleanup tracking. Test assertions do not depend on this.
    }
  })
}

function matchesAgentStart(responseUrl: string, agentId: string) {
  const encodedAgentId = encodeURIComponent(agentId)
  return responseUrl.includes(`/gateway/agents/${encodedAgentId}/agent/start`)
    || responseUrl.includes(`/agents/${encodedAgentId}/agent/start`)
}

function gatewayHeaders() {
  return {
    'x-secret-key': GATEWAY_SECRET_KEY,
    'x-user-id': ADMIN_USER,
  }
}

function sessionKey(session: StartedSession) {
  return `${session.agentId}:${session.sessionId}`
}

async function listAgentSessionIds(request: APIRequestContext, agentId: string): Promise<Set<string>> {
  const response = await request.get(
    `${GATEWAY_URL}/agents/${encodeURIComponent(agentId)}/sessions`,
    { headers: gatewayHeaders() },
  )
  if (!response.ok()) {
    const body = await response.text().catch(() => '')
    throw new Error(`Failed to list ${agentId} sessions before cleanup: HTTP ${response.status()} ${body}`)
  }

  const body = await response.json().catch(() => null)
  if (!Array.isArray(body?.sessions)) {
    throw new Error(`Failed to list ${agentId} sessions before cleanup: malformed response`)
  }

  return new Set(
    body.sessions
      .map((session: { id?: unknown }) => session.id)
      .filter((id: unknown): id is string => typeof id === 'string'),
  )
}

async function collectSessionBaselines(request: APIRequestContext): Promise<SessionBaselines> {
  const baselines: SessionBaselines = new Map()
  for (const scenario of AGENTS) {
    baselines.set(scenario.id, await listAgentSessionIds(request, scenario.id))
  }
  return baselines
}

async function deleteSession(request: APIRequestContext, session: StartedSession) {
  const response = await request.delete(
    `${GATEWAY_URL}/agents/${encodeURIComponent(session.agentId)}/sessions/${encodeURIComponent(session.sessionId)}`,
    { headers: gatewayHeaders() },
  )
  if (response.status() === 404) return
  if (!response.ok()) {
    const body = await response.text().catch(() => '')
    throw new Error(`Failed to delete ${session.agentId}/${session.sessionId}: HTTP ${response.status()} ${body}`)
  }
}

async function cleanupTestSessions(
  request: APIRequestContext,
  baselines: SessionBaselines,
  sessions: StartedSession[],
) {
  const unique = new Map<string, StartedSession>()
  for (const session of sessions) {
    unique.set(sessionKey(session), session)
  }

  for (const [agentId, baseline] of baselines) {
    const current = await listAgentSessionIds(request, agentId)
    for (const sessionId of current) {
      if (!baseline.has(sessionId)) {
        const session = { agentId, sessionId }
        unique.set(sessionKey(session), session)
      }
    }
  }

  for (const session of [...unique.values()].reverse()) {
    await deleteSession(request, session)
  }

  const leaked: StartedSession[] = []
  for (const [agentId, baseline] of baselines) {
    const current = await listAgentSessionIds(request, agentId)
    for (const sessionId of current) {
      if (!baseline.has(sessionId)) {
        leaked.push({ agentId, sessionId })
      }
    }
  }
  if (leaked.length > 0) {
    throw new Error(`E2E cleanup left sessions: ${leaked.map(sessionKey).join(', ')}`)
  }
}

async function openNewChat(page: Page) {
  await page.goto('/#/')
  await expect(page.locator('.new-chat-nav')).toBeVisible({ timeout: 20_000 })
  await page.locator('.new-chat-nav').click()
  await expect(page).toHaveURL(/\/chat/, { timeout: 20_000 })
  await expect(page.locator('.chat-input')).toBeVisible({ timeout: 20_000 })
}

async function readActiveSessionLocator(page: Page): Promise<StartedSession | null> {
  return page.evaluate(storageKey => {
    const raw = window.sessionStorage.getItem(storageKey)
    if (!raw) return null
    try {
      const parsed = JSON.parse(raw)
      if (typeof parsed?.agentId === 'string' && typeof parsed?.sessionId === 'string') {
        return { agentId: parsed.agentId, sessionId: parsed.sessionId }
      }
    } catch {
      return null
    }
    return null
  }, SESSION_LOCATOR_STORAGE_KEY)
}

function isSameSession(left: StartedSession | null, right: StartedSession | null) {
  return !!left
    && !!right
    && left.agentId === right.agentId
    && left.sessionId === right.sessionId
}

async function rememberNewActiveSession(
  page: Page,
  sessions: StartedSession[],
  expectedAgentId: string,
  previousLocator: StartedSession | null,
) {
  const locator = await readActiveSessionLocator(page)
  if (!locator) {
    throw new Error(`No active session locator found after starting ${expectedAgentId}`)
  }
  if (locator.agentId !== expectedAgentId) {
    throw new Error(
      `Active session belongs to ${locator.agentId}, expected ${expectedAgentId}`,
    )
  }
  if (isSameSession(locator, previousLocator)) {
    throw new Error(
      `New chat did not create a distinct ${expectedAgentId} session; refusing to delete a pre-existing admin session`,
    )
  }
  sessions.push(locator)
}

async function selectAgent(page: Page, scenario: AgentScenario) {
  const trigger = page.locator('.agent-selector-trigger')
  await expect(trigger).toBeVisible({ timeout: 20_000 })

  if ((await trigger.textContent())?.includes(scenario.label)) {
    return
  }

  await trigger.click()
  const option = page.locator('.agent-option').filter({ hasText: scenario.label }).first()
  await expect(option).toBeVisible({ timeout: 20_000 })
  const startResponsePromise = page.waitForResponse(response =>
    response.request().method() === 'POST'
      && matchesAgentStart(response.url(), scenario.id),
    { timeout: 60_000 },
  )
  await option.click()
  const startResponse = await startResponsePromise
  if (!startResponse.ok()) {
    const responseBody = await startResponse.text().catch(() => '')
    throw new Error(
      `${scenario.label} failed to start from WebApp: HTTP ${startResponse.status()} ${responseBody}`,
    )
  }
  await expect(trigger).toContainText(scenario.label, { timeout: 20_000 })
  await expect(page.locator('.chat-input')).toBeVisible({ timeout: 20_000 })
}

async function waitForTurnComplete(page: Page, previousAssistantCount: number) {
  await page.waitForFunction(
    previousCount => {
      const sendButton = document.querySelector('.chat-send-btn-new')
      const assistantMessages = document.querySelectorAll('.message.assistant').length
      return !!sendButton
        && !sendButton.classList.contains('is-stop')
        && assistantMessages > previousCount
    },
    previousAssistantCount,
    { timeout: 180_000 },
  )
}

async function sendTurn(page: Page, prompt: string) {
  const assistantMessages = page.locator('.message.assistant')
  const userMessages = page.locator('.message.user')
  const previousAssistantCount = await assistantMessages.count()
  const previousUserCount = await userMessages.count()

  const input = page.locator('.chat-input')
  await expect(input).toBeVisible({ timeout: 20_000 })
  await input.fill(prompt)
  await input.press('Enter')

  await waitForTurnComplete(page, previousAssistantCount)

  await expect(userMessages).toHaveCount(previousUserCount + 1)
  expect(await assistantMessages.count()).toBeGreaterThan(previousAssistantCount)
  await expect(page.locator('.message-error-banner')).toHaveCount(0)

  const latestAssistantText = await assistantMessages.last().textContent()
  expect((latestAssistantText || '').trim().length).toBeGreaterThan(10)
}

test.describe.configure({ mode: 'serial' })

test.describe('Admin WebApp multi-agent multi-turn chat', () => {
  for (const scenario of AGENTS) {
    test(`${scenario.label} completes a two-turn chat session`, async ({ page, request }) => {
      test.setTimeout(300_000)
      const startedSessions: StartedSession[] = []
      trackStartedSessions(page, startedSessions)
      const sessionBaselines = await collectSessionBaselines(request)

      try {
        await loginAsAdmin(page)
        const previousLocator = await readActiveSessionLocator(page)
        await openNewChat(page)
        await selectAgent(page, scenario)
        await rememberNewActiveSession(page, startedSessions, scenario.id, previousLocator)

        for (const prompt of scenario.prompts) {
          await sendTurn(page, prompt)
        }

        await expect(page.locator('.message.user')).toHaveCount(2)
        expect(await page.locator('.message.assistant').count()).toBeGreaterThanOrEqual(2)
      } finally {
        await cleanupTestSessions(request, sessionBaselines, startedSessions)
      }
    })
  }
})
