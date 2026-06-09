import { describe, it, expect } from 'vitest'
import { findAtMentionToken, buildMentionReplacement, filterAgents } from '../app/platform/chat/mention'

describe('findAtMentionToken', () => {
    it('detects a leading @token', () => {
        expect(findAtMentionToken('@qa', 3)).toEqual({ start: 0, end: 3, query: 'qa' })
    })
    it('detects @token after whitespace', () => {
        expect(findAtMentionToken('hi @qa', 6)).toEqual({ start: 3, end: 6, query: 'qa' })
    })
    it('detects empty query right after @', () => {
        expect(findAtMentionToken('hi @', 4)).toEqual({ start: 3, end: 4, query: '' })
    })
    it('rejects mid-word @ (email)', () => {
        expect(findAtMentionToken('mail@host', 9)).toBeNull()
    })
    it('rejects when whitespace already follows the @id', () => {
        expect(findAtMentionToken('@qa agent', 9)).toBeNull()
    })
    it('returns null without @', () => {
        expect(findAtMentionToken('hello', 5)).toBeNull()
    })
})

describe('buildMentionReplacement', () => {
    it('replaces the partial token with canonical @agentId + space', () => {
        const token = findAtMentionToken('run @qa', 7)!
        const { value, caret } = buildMentionReplacement('run @qa', token, 'qa-agent')
        expect(value).toBe('run @qa-agent ')
        expect(caret).toBe('run @qa-agent '.length)
    })
    it('preserves trailing text after the token', () => {
        const token = findAtMentionToken('@q the rest', 2)!
        const { value } = buildMentionReplacement('@q the rest', token, 'qa-agent')
        expect(value).toBe('@qa-agent  the rest')
    })
})

describe('filterAgents', () => {
    const agents = [
        { id: 'qa-agent', name: 'QA Agent' },
        { id: 'qos-agent', name: 'QoS Agent' },
        { id: 'report-agent', name: 'Report' },
    ]
    it('returns all for empty query', () => {
        expect(filterAgents(agents, '')).toHaveLength(3)
    })
    it('matches by id', () => {
        expect(filterAgents(agents, 'qos').map(a => a.id)).toEqual(['qos-agent'])
    })
    it('matches by name', () => {
        expect(filterAgents(agents, 'report').map(a => a.id)).toEqual(['report-agent'])
    })
})
