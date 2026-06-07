import { describe, expect, it } from 'vitest'
import { formatRunTime, previewOf } from '../app/modules/thread/threadFormat'
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
