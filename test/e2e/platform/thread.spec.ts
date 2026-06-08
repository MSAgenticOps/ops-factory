/**
 * E2E Tests: Assistant (Thread) Page (PR9) — Real Operations
 *
 * Covers (after the framework-reuse refactor):
 *   - Page renders (conversation shell or empty state)
 *   - Sidebar exposes the Assistant entry
 *   - When an assistant exists: the shared conversation shell (column B), the always-on copilot dropdown,
 *     and the proactive-push panel mounted into the shared RightPanelHost (narrow `thread` mode)
 *   - The push panel can be collapsed / reopened from the header toggle
 *   - Clicking a push card opens the read-only run modal; closing returns to the page
 *
 * Assertions are guarded by data presence (mirrors inbox.spec.ts) so the suite is robust whether or not the
 * logged-in user has bound IM conversations.
 */
import { test, expect, type Page } from '@playwright/test'

const USER = 'admin'

async function loginAs(page: Page, username: string) {
  await page.goto('/#/')
  await page.evaluate((userId) => {
    localStorage.setItem('opsfactory:userId', userId)
  }, username)
  await page.reload({ waitUntil: 'domcontentloaded' })
  await page.waitForTimeout(500)
}

test.describe('Assistant — rendering', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, USER)
  })

  test('renders the conversation shell or the empty state', async ({ page }) => {
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const hasConversation = await page.locator('.conversation-shell').isVisible().catch(() => false)
    const hasEmpty = await page.locator('.thread-empty-card').isVisible().catch(() => false)
    expect(hasConversation || hasEmpty).toBeTruthy()
  })

  test('sidebar exposes the Assistant entry', async ({ page }) => {
    await page.goto('/#/thread')
    const entry = page.locator('.nav-link:has-text("Assistant"), .nav-link:has-text("助理")')
    await expect(entry.first()).toBeVisible({ timeout: 5000 })
  })
})

test.describe('Assistant — workbench', () => {
  test('shows the conversation, copilot dropdown, and the push panel in the right-panel framework', async ({ page }) => {
    await loginAs(page, USER)
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const hasConversation = await page.locator('.conversation-shell').isVisible().catch(() => false)
    if (!hasConversation) {
      test.skip(true, 'no bound assistant for this user')
      return
    }

    // Always-on copilot dropdown (knowledge-base-style select control) + the conversation composer (shared shell).
    await expect(page.locator('.thread-switcher-trigger')).toBeVisible()
    await expect(page.locator('.chat-input-area-bottom')).toBeVisible()
    // The push timeline lives in the shared RightPanelHost as the narrow `thread` mode.
    await expect(page.locator('.right-panel-host.open.thread')).toBeVisible()
    await expect(page.locator('.right-panel-title')).toBeVisible()

    // Column B is a faithful replica of the chat composer: the full toolbar renders, while the in-composer
    // agent picker is suppressed (the header switcher owns assistant identity).
    await expect(page.locator('.conversation-shell .chat-input-toolbar')).toBeVisible()
    await expect(page.locator('.conversation-shell .chat-input-toolbar .agent-selector')).toHaveCount(0)
  })

  test('the push panel closes from its × and reopens from the header icon', async ({ page }) => {
    await loginAs(page, USER)
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const closeBtn = page.locator('.right-panel-close')
    if (!(await closeBtn.isVisible().catch(() => false))) {
      test.skip(true, 'no bound assistant for this user')
      return
    }

    await expect(page.locator('.right-panel-host.open.thread')).toBeVisible()
    await closeBtn.click()
    await expect(page.locator('.right-panel-host.open.thread')).toHaveCount(0)
    await page.locator('.thread-panel-show').click()
    await expect(page.locator('.right-panel-host.open.thread')).toBeVisible()
  })
})

test.describe('Assistant — read-only run modal', () => {
  test('clicking a push card opens the run modal and it can be closed', async ({ page }) => {
    await loginAs(page, USER)
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const cards = page.locator('.thread-push-card')
    if ((await cards.count()) === 0) {
      test.skip(true, 'no proactive pushes for this user')
      return
    }

    await cards.first().click()
    const modal = page.locator('.modal-overlay')
    await expect(modal).toBeVisible({ timeout: 10_000 })
    // The read-only modal has no composer.
    await expect(modal.locator('textarea')).toHaveCount(0)

    await modal.locator('.modal-close').click()
    await expect(modal).toHaveCount(0)
  })
})
