import { test, expect, type Page, type APIRequestContext } from '@playwright/test'
import { existsSync } from 'node:fs'
import { rm } from 'node:fs/promises'
import { join, resolve } from 'node:path'

const ADMIN_USER = 'admin'
const PROJECT_ROOT = resolve(import.meta.dirname, '../..')
const GATEWAY_ROOT = join(PROJECT_ROOT, 'gateway')
const UNIQUE = Date.now()
const CHANNEL_ID = `e2e-channel-${UNIQUE}`
const CHANNEL_NAME = CHANNEL_ID

async function loginAs(page: Page, username: string) {
  await page.goto('/#/')
  await page.evaluate((userId) => {
    localStorage.setItem('opsfactory:userId', userId)
  }, username)
  await page.reload({ waitUntil: 'domcontentloaded' })
  await page.waitForURL(/\/#\/?$/)
  await page.waitForTimeout(500)
}

async function cleanupChannel(request: APIRequestContext) {
  await request.delete(`/gateway/channels/${CHANNEL_ID}`, {
    headers: {
      'x-secret-key': 'test',
      'x-user-id': ADMIN_USER,
    },
  }).catch(() => undefined)
  await rm(join(GATEWAY_ROOT, 'channels', 'whatsapp', CHANNEL_ID), { recursive: true, force: true })
  await rm(join(GATEWAY_ROOT, 'users', ADMIN_USER, 'channels', 'whatsapp', CHANNEL_ID), { recursive: true, force: true })
}

test.describe('Channels UI', () => {
  test.beforeEach(async ({ request }) => {
    await cleanupChannel(request)
  })

  test.afterEach(async ({ request }) => {
    await cleanupChannel(request)
  })

  test('creates and deletes a channel with split config/runtime storage', async ({ page }) => {
    await loginAs(page, ADMIN_USER)
    await page.goto('/#/channels')
    await expect(page).toHaveURL(/\/#\/channels$/)
    await expect(page.getByRole('heading', { name: /Channels|渠道/ })).toBeVisible({ timeout: 10_000 })

    await page.getByRole('button', { name: /New Channel|新建渠道/ }).click()
    await expect(page.locator('.modal')).toBeVisible({ timeout: 5000 })
    await page.locator('.modal .form-input').first().fill(CHANNEL_NAME)
    await page.locator('.modal select.form-input').first().selectOption('whatsapp')
    await page.locator('.modal').getByRole('button', { name: /New Channel|新建渠道/ }).click()

    await expect(page).toHaveURL(new RegExp(`/#/channels/${CHANNEL_ID}/configure$`), { timeout: 15_000 })
    await expect(page.getByRole('heading', { name: CHANNEL_NAME })).toBeVisible()

    const configDir = join(GATEWAY_ROOT, 'channels', 'whatsapp', CHANNEL_ID)
    const runtimeDir = join(GATEWAY_ROOT, 'users', ADMIN_USER, 'channels', 'whatsapp', CHANNEL_ID)
    expect(existsSync(join(configDir, 'config.json'))).toBe(true)
    expect(existsSync(join(configDir, 'bindings.json'))).toBe(false)
    expect(existsSync(join(configDir, 'login-state.json'))).toBe(false)
    expect(existsSync(join(runtimeDir, 'bindings.json'))).toBe(true)
    expect(existsSync(join(runtimeDir, 'events.json'))).toBe(true)
    expect(existsSync(join(runtimeDir, 'inbound-dedup.json'))).toBe(true)

    await page.goto('/#/channels')
    const card = page.locator('.resource-card', { has: page.getByText(CHANNEL_NAME) })
    await expect(card).toBeVisible({ timeout: 10_000 })
    page.once('dialog', dialog => dialog.accept())
    await card.getByRole('button', { name: /Delete|删除/ }).click()
    await expect(card).not.toBeVisible({ timeout: 10_000 })
    expect(existsSync(configDir)).toBe(false)
    expect(existsSync(runtimeDir)).toBe(false)
  })
})
