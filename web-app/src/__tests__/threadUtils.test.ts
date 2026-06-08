import { describe, expect, it } from 'vitest'
import { formatRunTime, previewOf, scheduleAccent, scheduleLabel, snippetOf } from '../app/modules/thread/threadFormat'
import { countUnreadFollowups, type ThreadFollowup } from '../app/platform/providers/ThreadUnreadContext'

function followup(time: string, summary = 'x'): ThreadFollowup {
    return { time, scheduleId: 'ticket-daily-brief', sessionId: `s-${time}`, targetKey: 'k', summary }
}

describe('thread previewOf', () => {
    it('returns the first meaningful line, skipping markdown rules / blank lines', () => {
        const summary = '---\n\n📋 FO Daily Brief — 2026-06-06\nOpened 6'
        expect(previewOf(summary)).toBe('📋 FO Daily Brief — 2026-06-06')
    })

    it('truncates a long line with an ellipsis', () => {
        const long = 'A'.repeat(200)
        const result = previewOf(long)
        expect(result.endsWith('…')).toBe(true)
        expect(result.length).toBe(91)
    })

    it('handles empty input', () => {
        expect(previewOf('')).toBe('')
    })

    it('strips markdown heading / bold / bullet markers from the preview', () => {
        expect(previewOf('## 第 3 步：核对 INC-1042 现状')).toBe('第 3 步：核对 INC-1042 现状')
        expect(previewOf('# 🎯 工单日报')).toBe('🎯 工单日报')
        expect(previewOf('**FO Daily Brief**')).toBe('FO Daily Brief')
        expect(previewOf('- a list item')).toBe('a list item')
    })
})

describe('thread snippetOf', () => {
    it('returns the meaningful lines after the title, stripped + joined', () => {
        const summary = '## 📋 工单日报\nOpened 6 · Resolved 2\n**Needs your decision (2)**'
        expect(snippetOf(summary)).toBe('Opened 6 · Resolved 2 Needs your decision (2)')
    })

    it('is empty when there is only a title line', () => {
        expect(snippetOf('# 只有标题')).toBe('')
    })

    it('handles empty input', () => {
        expect(snippetOf('')).toBe('')
    })
})

describe('thread scheduleAccent', () => {
    it('maps known schedule ids to a type accent', () => {
        expect(scheduleAccent('ticket-daily-brief')).toBe('brief')
        expect(scheduleAccent('ticket-watch-loop')).toBe('watch')
        expect(scheduleAccent('fo-copilot-memory-maintenance')).toBe('memory')
    })

    it('falls back to default for an unknown schedule', () => {
        expect(scheduleAccent('something-else')).toBe('default')
    })
})

describe('thread scheduleLabel', () => {
    const t = (key: string, opts?: { defaultValue: string }) =>
        (key === 'thread.schedule.ticket-daily-brief' ? '每日简报' : (opts?.defaultValue ?? key))

    it('maps a known schedule id to its friendly name', () => {
        expect(scheduleLabel('ticket-daily-brief', t)).toBe('每日简报')
    })

    it('falls back to the raw id for an unknown schedule', () => {
        expect(scheduleLabel('custom-x', t)).toBe('custom-x')
    })
})

describe('thread formatRunTime', () => {
    it('returns empty string for an unparseable timestamp', () => {
        expect(formatRunTime('not-a-date')).toBe('')
    })

    it('formats a valid ISO timestamp to a non-empty locale string', () => {
        expect(formatRunTime('2026-06-06T09:00:00Z').length).toBeGreaterThan(0)
    })
})

describe('thread countUnreadFollowups', () => {
    const records = [
        followup('2026-06-06T09:00:00Z'),
        followup('2026-06-07T09:00:00Z'),
        followup('2026-06-08T09:00:00Z'),
    ]

    it('counts everything unread when there is no read water level', () => {
        expect(countUnreadFollowups(records, undefined)).toBe(3)
    })

    it('counts only follow-ups newer than the water level', () => {
        expect(countUnreadFollowups(records, '2026-06-07T09:00:00Z')).toBe(1)
    })

    it('counts zero when the water level is at or after the newest', () => {
        expect(countUnreadFollowups(records, '2026-06-08T09:00:00Z')).toBe(0)
    })

    it('handles an empty record set', () => {
        expect(countUnreadFollowups([], undefined)).toBe(0)
    })
})
