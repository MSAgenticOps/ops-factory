/**
 * E2E Tests: Thread Page (PR9) — Real Operations
 *
 * Covers:
 *   - Page renders (threads workbench or empty state)
 *   - Sidebar exposes the Thread entry
 *   - When a thread exists: column B (main conversation) + column C (proactive push timeline) render
 *   - Clicking a push card opens the read-only run modal; closing returns to the timeline
 *   - The push timeline can be collapsed / shown
 *
 * Assertions are guarded by data presence (mirrors inbox.spec.ts) so the suite is robust whether or not the
 * logged-in user has bound IM conversations.
 */
import { test, expect, type Page } from '@playwright/test'

// Use admin so the test can exercise the rich path when bindings/follow-ups exist; all rich assertions are guarded.
const USER = 'admin'

async function loginAs(page: Page, username: string) {
  await page.goto('/#/')
  await page.evaluate((userId) => {
    localStorage.setItem('opsfactory:userId', userId)
  }, username)
  await page.reload({ waitUntil: 'domcontentloaded' })
  await page.waitForURL(/\/#\/?$/)
  await page.waitForTimeout(500)
}

test.describe('Thread — rendering', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, USER)
  })

  test('renders the workbench or the empty state', async ({ page }) => {
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const hasWorkbench = await page.locator('.thread-header').isVisible().catch(() => false)
    const hasEmpty = await page.locator('.thread-empty-card').isVisible().catch(() => false)
    expect(hasWorkbench || hasEmpty).toBeTruthy()
  })

  test('sidebar exposes the Thread entry', async ({ page }) => {
    await page.goto('/#/thread')
    const entry = page.locator('.nav-link:has-text("Threads"), .nav-link:has-text("会话")')
    await expect(entry.first()).toBeVisible({ timeout: 5000 })
  })
})

test.describe('Thread — workbench content', () => {
  test('shows main conversation and push timeline when a thread exists', async ({ page }) => {
    await loginAs(page, USER)
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const hasWorkbench = await page.locator('.thread-header').isVisible().catch(() => false)
    if (!hasWorkbench) {
      test.skip(true, 'no bound thread for this user')
      return
    }

    // Column B (main) is always present; column C (rail) is open by default.
    await expect(page.locator('.thread-main')).toBeVisible()
    await expect(page.locator('.thread-timeline-title')).toBeVisible()
  })

  test('toggling the rail hides and shows the push timeline', async ({ page }) => {
    await loginAs(page, USER)
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const toggle = page.locator('.thread-rail-toggle')
    if (!(await toggle.isVisible().catch(() => false))) {
      test.skip(true, 'no bound thread for this user')
      return
    }

    await expect(page.locator('.thread-rail')).toBeVisible()
    await toggle.click()
    await expect(page.locator('.thread-rail')).toHaveCount(0)
    await toggle.click()
    await expect(page.locator('.thread-rail')).toBeVisible()
  })
})

test.describe('Thread — read-only run modal', () => {
  test('clicking a push card opens the run modal and it can be closed', async ({ page }) => {
    await loginAs(page, USER)
    await page.goto('/#/thread')
    await page.waitForTimeout(3000)

    const cards = page.locator('.thread-card')
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
