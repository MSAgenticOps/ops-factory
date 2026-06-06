import { describe, it, expect } from 'vitest'
import { extractA2AProgress, reduceA2AProgress, type A2AProgressMap } from '../app/platform/chat/a2aProgress'

const progress = (over: Record<string, unknown> = {}) => ({
    type: 'a2a_progress', target_agent: 'agentB', sub_session_id: 'B1',
    kind: 'tool_call', label: 'read logs', step: 1, ...over,
})

const notif = (requestId: string | undefined, data: unknown, wrap: 'params' | 'data' | 'root' = 'params') => {
    let message: unknown
    if (wrap === 'params') message = { method: 'notifications/message', params: { level: 'info', data } }
    else if (wrap === 'data') message = { data }
    else message = data
    return { type: 'Notification', request_id: requestId, message }
}

describe('extractA2AProgress', () => {
    it('extracts from message.params.data', () => {
        const r = extractA2AProgress(notif('r1', progress()))
        expect(r).not.toBeNull()
        expect(r!.requestId).toBe('r1')
        expect(r!.data.label).toBe('read logs')
    })
    it('extracts from message.data', () => {
        expect(extractA2AProgress(notif('r1', progress(), 'data'))!.requestId).toBe('r1')
    })
    it('extracts from message root', () => {
        expect(extractA2AProgress(notif('r1', progress(), 'root'))!.requestId).toBe('r1')
    })
    it('ignores non-Notification events', () => {
        expect(extractA2AProgress({ type: 'Message', request_id: 'r1', message: progress() })).toBeNull()
    })
    it('ignores Notification without request_id', () => {
        expect(extractA2AProgress(notif(undefined, progress()))).toBeNull()
    })
    it('ignores Notification without a2a_progress payload', () => {
        expect(extractA2AProgress(notif('r1', { type: 'something_else' }))).toBeNull()
    })
})

describe('reduceA2AProgress', () => {
    it('adds a new entry with startedAt', () => {
        const map = reduceA2AProgress({}, notif('r1', progress({ step: 1 })), 1000)
        expect(map.r1.step).toBe(1)
        expect(map.r1.startedAt).toBe(1000)
        expect(map.r1.updatedAt).toBe(1000)
        expect(map.r1.targetAgent).toBe('agentB')
    })
    it('last-wins on subsequent events, preserving startedAt', () => {
        let map: A2AProgressMap = reduceA2AProgress({}, notif('r1', progress({ step: 1, label: 'a' })), 1000)
        map = reduceA2AProgress(map, notif('r1', progress({ step: 3, label: 'b' })), 5000)
        expect(map.r1.step).toBe(3)
        expect(map.r1.label).toBe('b')
        expect(map.r1.startedAt).toBe(1000)
        expect(map.r1.updatedAt).toBe(5000)
    })
    it('maps tool_calls to toolCalls (the displayed count)', () => {
        const map = reduceA2AProgress({}, notif('r1', progress({ tool_calls: 4 })), 1000)
        expect(map.r1.toolCalls).toBe(4)
    })
    it('returns the same map reference for non-a2a events', () => {
        const map: A2AProgressMap = {}
        expect(reduceA2AProgress(map, { type: 'Ping' }, 1)).toBe(map)
    })
    it('keys distinct requests separately', () => {
        let map = reduceA2AProgress({}, notif('r1', progress()), 1)
        map = reduceA2AProgress(map, notif('r2', progress({ target_agent: 'agentC' })), 2)
        expect(Object.keys(map)).toHaveLength(2)
        expect(map.r2.targetAgent).toBe('agentC')
    })
})
