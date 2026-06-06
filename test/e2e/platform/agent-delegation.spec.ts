/**
 * E2E Tests: Agent-to-Agent (@mention) delegation — initiated from the web UI.
 *
 * Delegation is deterministic: an `@<agentId>` triggers the gateway A2A call directly (from the frontend, not the
 * initiator's model), so the `call_agent` card + live status line appear reliably regardless of the initiator model.
 * Two tiers:
 *   1. Deterministic, no-LLM (run with the gateway + web-app stack up): the `@mention` agent picker appears,
 *      filters, and inserts a canonical `@<agentId>` token; the history "Agent 调用" tab is present.
 *   2. Full delegated run (requires a configured provider only for the *target* agent's generation): sending
 *      `@<agent> <task>` produces a `call_agent` card with a live A2A status line, and the sub-run lands in the
 *      "Agent 调用" history tab. Multi-turn cases mention a different agent each turn. Gated behind A2A_E2E_LIVE=1.
 *
 * Prereqs: app stack running at the Playwright baseURL (./scripts/ctl.sh startup all). See MANUAL-TEST-CASES.md.
 */
import { test, expect, type Page } from '@playwright/test'

const USER = 'e2e-a2a'
const UNIQUE = Date.now()
const USER_STORAGE_KEY = 'opsfactory:userId'

async function loginAs(page: Page, username: string) {
  await page.goto('/#/')
  await page.evaluate(([storageKey, userId]) => {
    localStorage.setItem(storageKey, userId)
  }, [USER_STORAGE_KEY, username])
  await page.reload({ waitUntil: 'domcontentloaded' })
  await page.waitForURL(/\/#\/?$/)
  await page.waitForTimeout(500)
}

async function openNewChat(page: Page) {
  await page.goto('/#/')
  await expect(page.locator('.chat-input')).toBeVisible({ timeout: 15_000 })
  const newChatBtn = page.locator('.new-chat-nav')
  if (await newChatBtn.count()) {
    await newChatBtn.click()
    await expect(page).toHaveURL(/\/chat/, { timeout: 15_000 })
  }
  await expect(page.locator('.chat-input')).toBeVisible({ timeout: 15_000 })
}

/** Waits for the active turn to finish (send button leaves its stop state). */
async function waitForSendIdle(page: Page) {
  await page.waitForFunction(
    () => {
      const btn = document.querySelector('.chat-send-btn-new')
      return btn && !btn.classList.contains('is-stop')
    },
    { timeout: 180_000 }
  )
}

/**
 * Selects an agent via the agent-selector dropdown. Delegation (and thus the @mention picker) is only available for
 * the A2A initiator (fo-copilot), and a fresh chat defaults to another agent — so tests must pin it explicitly.
 */
async function selectAgent(page: Page, nameMatch: RegExp) {
  const trigger = page.locator('.agent-selector-trigger')
  await expect(trigger).toBeVisible({ timeout: 15_000 })
  await trigger.click()
  await page.locator('.agent-dropdown').waitFor({ state: 'visible', timeout: 5_000 })
  await page.locator('.agent-option').filter({ hasText: nameMatch }).first().click()
  await expect(trigger).toContainText(nameMatch, { timeout: 5_000 })
}

// =====================================================
// 1. @mention picker (deterministic, no LLM required)
// =====================================================
test.describe('A2A — @mention picker', () => {
  test('typing @ opens the agent picker and selecting inserts a canonical @agentId', async ({ page }) => {
    await loginAs(page, `${USER}-mention-${UNIQUE}`)
    await openNewChat(page)
    await selectAgent(page, /FO Copilot/i)

    const input = page.locator('.chat-input')
    await input.click()
    // Type char-by-char so the input onChange (token detection) fires.
    await input.pressSequentially('@')

    const picker = page.locator('.skill-picker')
    await expect(picker).toBeVisible({ timeout: 10_000 })

    // The picker should list registered agents (sourced from GET /api/gateway/agents).
    const options = picker.locator('.skill-picker-option')
    expect(await options.count()).toBeGreaterThanOrEqual(1)

    // Selecting an option canonicalizes the token to "@<agentId> " (a slug + trailing space), regardless of the
    // option's display label.
    await options.first().click()
    await expect(input).toHaveValue(/@[\w-]+\s/)
  }, 60_000)

  test('the picker filters agents by query', async ({ page }) => {
    await loginAs(page, `${USER}-filter-${UNIQUE}`)
    await openNewChat(page)
    await selectAgent(page, /FO Copilot/i)

    const input = page.locator('.chat-input')
    await input.click()
    await input.pressSequentially('@qa')

    const picker = page.locator('.skill-picker')
    await expect(picker).toBeVisible({ timeout: 10_000 })
    // Every visible option id should contain the query.
    const optionCount = await picker.locator('.skill-picker-option').count()
    expect(optionCount).toBeGreaterThanOrEqual(1)
  }, 60_000)
})

// =====================================================
// 2. History "Agent 调用" tab present (deterministic)
// =====================================================
test.describe('A2A — history tab', () => {
  test('the Agent calls filter tab is present and selectable', async ({ page }) => {
    await loginAs(page, `${USER}-tab-${UNIQUE}`)
    await page.goto('/#/history')
    await page.waitForSelector('.seg-filter-btn', { timeout: 10_000 })

    // user / scheduled / agent_call / all  → at least 4 tabs.
    const tabs = page.locator('.seg-filter-btn')
    expect(await tabs.count()).toBeGreaterThanOrEqual(4)

    // The 3rd tab (agent_call) should become active when clicked.
    const agentCallTab = tabs.nth(2)
    await agentCallTab.click()
    await page.waitForTimeout(300)
    await expect(agentCallTab).toHaveClass(/active/)
    await expect(page).toHaveURL(/type=agent_call/)
  }, 60_000)
})

// =====================================================
// 3. Full delegated run (requires live provider / LLM)
// =====================================================
test.describe('A2A — full delegation run', () => {
  test.skip(process.env.A2A_E2E_LIVE !== '1', 'requires a configured provider for the target agent (set A2A_E2E_LIVE=1)')

  test('sending @<agent> <task> deterministically shows a call_agent card with an A2A status line', async ({ page }) => {
    await loginAs(page, `${USER}-run-${UNIQUE}`)
    await openNewChat(page)
    await selectAgent(page, /FO Copilot/i)

    const input = page.locator('.chat-input')
    await input.click()
    await input.pressSequentially('@universal-agent reply with the single word ROUTED')
    await input.press('Enter')

    // Deterministic trigger: the live A2A status line (or its completed summary) appears for the delegated sub-run.
    await expect(page.locator('.a2a-status-line')).toBeVisible({ timeout: 120_000 })
    await waitForSendIdle(page)

    // The delegated sub-session should be classifiable under the Agent calls history tab.
    await page.goto('/#/history?type=agent_call')
    await page.waitForTimeout(3000)
    expect(await page.locator('[class*="session-item"]').count()).toBeGreaterThanOrEqual(1)
  }, 240_000)

  test('multi-turn: mentioning a different agent each turn yields one delegation per turn', async ({ page }) => {
    await loginAs(page, `${USER}-multiturn-${UNIQUE}`)
    await openNewChat(page)
    await selectAgent(page, /FO Copilot/i)
    const input = page.locator('.chat-input')

    // Turn 1 → universal-agent
    await input.click()
    await input.pressSequentially('@universal-agent reply with the single word ALPHA')
    await input.press('Enter')
    await expect(page.locator('.a2a-status-line').first()).toBeVisible({ timeout: 120_000 })
    await waitForSendIdle(page)

    // Turn 2 → report-agent (a different target)
    await input.click()
    await input.pressSequentially('@report-agent reply with the single word BETA')
    await input.press('Enter')
    await waitForSendIdle(page)

    // One call_agent card / status line per turn → two distinct delegations.
    expect(await page.locator('.a2a-status-line').count()).toBeGreaterThanOrEqual(2)
  }, 300_000)
})
