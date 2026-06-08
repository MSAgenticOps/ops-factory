import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const ROOT = resolve(process.cwd())

function read(path: string): string {
    return readFileSync(resolve(ROOT, path), 'utf-8')
}

function nestedKeys(value: unknown, prefix = ''): string[] {
    if (!value || typeof value !== 'object' || Array.isArray(value)) {
        return [prefix]
    }
    return Object.entries(value as Record<string, unknown>).flatMap(([key, child]) => {
        const next = prefix ? `${prefix}.${key}` : key
        return nestedKeys(child, next)
    })
}

describe('thread frontend structure', () => {
    it('registers an authenticated route + sidebar item with the unread badge', () => {
        const moduleSource = read('src/app/modules/thread/module.ts')
        expect(moduleSource).toContain("id: 'thread'")
        expect(moduleSource).toContain("path: '/thread'")
        expect(moduleSource).toContain("titleKey: 'sidebar.thread'")
        expect(moduleSource).toContain("icon: 'thread'")
        expect(moduleSource).toContain("badge: 'threadUnread'")
        expect(moduleSource).toContain("access: 'authenticated'")
    })

    it('wires the badge source, icon key, and sidebar mapping in the platform', () => {
        const types = read('src/app/platform/module-types.ts')
        expect(types).toContain("| 'thread'")
        expect(types).toContain("'threadUnread'")

        const icons = read('src/app/platform/icons.tsx')
        expect(icons).toContain('thread: ThreadIcon')

        const sidebar = read('src/app/platform/navigation/Sidebar.tsx')
        expect(sidebar).toContain('useThreadUnread')
        expect(sidebar).toContain("badge === 'threadUnread'")

        const app = read('src/App.tsx')
        expect(app).toContain('ThreadUnreadProvider')
    })

    it('keeps the run modal read-only (no composer) and column B with a composer', () => {
        const modal = read('src/app/modules/thread/components/ProactiveRunModal.tsx')
        expect(modal).toContain('DetailDialog')
        expect(modal).not.toContain('ChatInput')

        const main = read('src/app/modules/thread/components/ThreadMainConversation.tsx')
        expect(main).toContain('ChatInput')
        expect(main).toContain('resumeSession')
    })

    it('reuses platform chat primitives and never imports another module', () => {
        const files = [
            'src/app/modules/thread/components/ThreadMainConversation.tsx',
            'src/app/modules/thread/components/ProactiveRunModal.tsx',
            'src/app/modules/thread/components/ProactivePushTimeline.tsx',
            'src/app/modules/thread/pages/ThreadPage.tsx',
        ]
        for (const file of files) {
            const source = read(file)
            expect(source).not.toMatch(/from '\.\.\/\.\.\/(?!\.\.\/platform)[a-z-]+\//)
            expect(source).not.toContain("modules/chat")
            expect(source).not.toContain("modules/history")
        }
    })

    it('keeps visible thread copy aligned in both locales', () => {
        const en = JSON.parse(read('src/i18n/en.json'))
        const zh = JSON.parse(read('src/i18n/zh.json'))
        expect(en.sidebar.thread).toBe('Assistant')
        expect(zh.sidebar.thread).toBe('助理')
        expect(nestedKeys(en.thread).sort()).toEqual(nestedKeys(zh.thread).sort())
    })
})
