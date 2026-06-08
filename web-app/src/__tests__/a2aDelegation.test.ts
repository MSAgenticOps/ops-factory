import { describe, it, expect } from 'vitest'
import {
    parseDelegationDirectives,
    makeCallAgentToolMessage,
    delegationResultText,
    applyDelegationResult,
    applyDelegationCancelled,
    CALL_AGENT_TOOL_NAME,
    type TranslateFn,
} from '../app/platform/chat/a2aDelegation'
import type { ChatMessage, ToolRequestContent, ToolResponseContent, TextContent } from '../types/message'
import type { A2AResultFrame } from '@goosed/sdk'

const agents = [{ id: 'supervisor-agent' }, { id: 'qa-agent' }, { id: 'qa-cli-agent' }]

/** Mirrors the en.json `chat.a2a.*` wrappers so assertions read naturally; production passes react-i18next's `t`. */
const t: TranslateFn = (key, opts) => {
    switch (key) {
        case 'chat.a2a.resultNoResult': return '[delegation error] no result returned'
        case 'chat.a2a.resultError': return `[delegated agent error] ${opts?.error ?? ''}`
        case 'chat.a2a.resultErrorUnknown': return 'unknown error'
        case 'chat.a2a.resultTimeout': return '[delegated agent timed out]'
        case 'chat.a2a.resultTimeoutPartial': return `[delegated agent timed out] Partial result: ${opts?.result ?? ''}`
        case 'chat.a2a.cancelled': return '[delegation cancelled]'
        default: return key
    }
}

describe('parseDelegationDirectives', () => {
    it('parses a leading single mention and strips the marker from the task', () => {
        expect(parseDelegationDirectives('@supervisor-agent analyze the full health', agents))
            .toEqual([{ agentId: 'supervisor-agent', task: 'analyze the full health' }])
    })
    it('parses a mid-message mention', () => {
        expect(parseDelegationDirectives('please @qa-agent run the smoke tests', agents))
            .toEqual([{ agentId: 'qa-agent', task: 'please run the smoke tests' }])
    })
    it('returns [] for an unknown agent (caller falls back to the normal reply path)', () => {
        expect(parseDelegationDirectives('@not-an-agent do x', agents)).toEqual([])
    })
    it('returns [] when there is no mention', () => {
        expect(parseDelegationDirectives('just a normal message', agents)).toEqual([])
    })
    it('ignores a mid-word @ (e.g. an email address)', () => {
        expect(parseDelegationDirectives('reach me at user@qa-agent.example', agents)).toEqual([])
    })
    it('recognizes a mention followed by trailing punctuation (does not fall back to the model)', () => {
        expect(parseDelegationDirectives('@qa-agent, run the smoke tests', agents))
            .toEqual([{ agentId: 'qa-agent', task: ', run the smoke tests' }])
        expect(parseDelegationDirectives('ask @qa-agent. then stop', agents))
            .toEqual([{ agentId: 'qa-agent', task: 'ask . then stop' }])
    })
    it('does not split a longer unknown token into a known prefix (e.g. @qa-agentx is not @qa-agent)', () => {
        expect(parseDelegationDirectives('@qa-agentx do x', agents)).toEqual([])
    })
    it('returns [] for a bare mention with no remaining task (would 400 at the gateway)', () => {
        expect(parseDelegationDirectives('@qa-agent', agents)).toEqual([])
        expect(parseDelegationDirectives('   @qa-agent   ', agents)).toEqual([])
    })
    it('dedupes repeated mentions of the same agent', () => {
        expect(parseDelegationDirectives('@qa-agent then again @qa-agent go', agents))
            .toEqual([{ agentId: 'qa-agent', task: 'then again go' }])
    })
    it('returns one directive per distinct agent, each with the de-mentioned task', () => {
        expect(parseDelegationDirectives('@qa-agent @qa-cli-agent compare results', agents)).toEqual([
            { agentId: 'qa-agent', task: 'compare results' },
            { agentId: 'qa-cli-agent', task: 'compare results' },
        ])
    })
    it('treats an empty agents list as delegation-disabled', () => {
        expect(parseDelegationDirectives('@qa-agent x', [])).toEqual([])
    })
})

describe('makeCallAgentToolMessage', () => {
    it('builds a pending Call-agent tool message bound to the requestId', () => {
        const message = makeCallAgentToolMessage('rid-1', 'qa-agent', 'do x', 1000)
        expect(message.role).toBe('assistant')
        expect(message.id).toBe('a2a-rid-1')
        expect(message.metadata?.userVisible).toBe(true)
        expect(message.metadata?.agentVisible).toBe(false)
        expect(message.content).toHaveLength(1)
        const content = message.content[0] as ToolRequestContent
        expect(content.type).toBe('toolRequest')
        expect(content.id).toBe('rid-1')
        expect(content.toolCall?.status).toBe('pending')
        expect(content.toolCall?.value?.name).toBe(CALL_AGENT_TOOL_NAME)
        expect(content.toolCall?.value?.arguments).toEqual({ target: 'qa-agent', message: 'do x' })
    })
})

describe('delegationResultText', () => {
    it('returns the result verbatim when completed', () => {
        expect(delegationResultText({ type: 'a2a_result', status: 'completed', result: 'PONG' }, t)).toBe('PONG')
    })
    it('annotates errors', () => {
        expect(delegationResultText({ type: 'a2a_result', status: 'error', error: 'boom' }, t))
            .toBe('[delegated agent error] boom')
    })
    it('falls back to a localized "unknown error" when the error detail is missing', () => {
        expect(delegationResultText({ type: 'a2a_result', status: 'error' }, t))
            .toBe('[delegated agent error] unknown error')
    })
    it('annotates timeouts and keeps any partial result', () => {
        expect(delegationResultText({ type: 'a2a_result', status: 'timeout', result: 'half' }, t))
            .toBe('[delegated agent timed out] Partial result: half')
    })
    it('annotates a timeout with no partial result', () => {
        expect(delegationResultText({ type: 'a2a_result', status: 'timeout' }, t))
            .toBe('[delegated agent timed out]')
    })
    it('handles a missing frame', () => {
        expect(delegationResultText(undefined, t)).toContain('[delegation error]')
    })
})

describe('applyDelegationResult', () => {
    const base = (): ChatMessage[] => [
        { id: 'user-1', role: 'user', content: [{ type: 'text', text: '@qa-agent x' }] },
        makeCallAgentToolMessage('rid-1', 'qa-agent', 'x', 1000),
    ]
    const completed: A2AResultFrame = { type: 'a2a_result', status: 'completed', result: 'done!' }

    it('flips the card to done (success toolResponse) and pushes the result text', () => {
        const out = applyDelegationResult(base(), 'rid-1', completed, t, 2000)
        const tool = out.find(m => m.id === 'a2a-rid-1')!
        const response = tool.content.find(c => c.type === 'toolResponse') as ToolResponseContent
        expect(response.id).toBe('rid-1')
        expect(response.toolResult?.status).toBe('success')
        expect(response.toolResult?.isError).toBe(false)
        expect(response.toolResult?.value).toBe('done!')
        const text = out.find(m => m.id === 'a2a-text-rid-1')!
        expect((text.content[0] as TextContent).text).toBe('done!')
        expect(text.metadata?.userVisible).toBe(true)
    })
    it('marks error results with isError but keeps the success unwrap signal so clean text renders (not raw JSON)', () => {
        const out = applyDelegationResult(base(), 'rid-1', { type: 'a2a_result', status: 'error', error: 'nope' }, t, 2000)
        const response = out.find(m => m.id === 'a2a-rid-1')!.content.find(c => c.type === 'toolResponse') as ToolResponseContent
        expect(response.toolResult?.isError).toBe(true)
        // MessageList only unwraps toolResult.value when status==='success'; the error text lives in value, so keep it.
        expect(response.toolResult?.status).toBe('success')
        expect(response.toolResult?.value).toBe('[delegated agent error] nope')
    })
    it('is idempotent on the card (no duplicate toolResponse when applied twice)', () => {
        let out = applyDelegationResult(base(), 'rid-1', completed, t, 2000)
        out = applyDelegationResult(out, 'rid-1', completed, t, 3000)
        const tool = out.find(m => m.id === 'a2a-rid-1')!
        expect(tool.content.filter(c => c.type === 'toolResponse')).toHaveLength(1)
    })
    it('is idempotent on the result text (no duplicate text message when applied twice)', () => {
        let out = applyDelegationResult(base(), 'rid-1', completed, t, 2000)
        out = applyDelegationResult(out, 'rid-1', completed, t, 3000)
        expect(out.filter(m => m.id === 'a2a-text-rid-1')).toHaveLength(1)
    })
    it('is a no-op when the card is absent (e.g. session switched away)', () => {
        const orphan: ChatMessage[] = [{ id: 'user-9', role: 'user', content: [{ type: 'text', text: 'hi' }] }]
        const out = applyDelegationResult(orphan, 'rid-missing', completed, t, 2000)
        expect(out).toBe(orphan)
        expect(out.some(m => m.id === 'a2a-text-rid-missing')).toBe(false)
    })
})

describe('applyDelegationCancelled', () => {
    const base = (): ChatMessage[] => [makeCallAgentToolMessage('rid-1', 'qa-agent', 'x', 1000)]

    it('resolves the pending card so its spinner does not hang, and pushes a cancelled note', () => {
        const out = applyDelegationCancelled(base(), 'rid-1', t, 2000)
        const response = out.find(m => m.id === 'a2a-rid-1')!.content.find(c => c.type === 'toolResponse') as ToolResponseContent
        expect(response.toolResult?.status).toBe('success')
        expect(response.toolResult?.isError).toBe(false)
        const text = out.find(m => m.id === 'a2a-text-rid-1')!
        expect((text.content[0] as TextContent).text).toBe('[delegation cancelled]')
    })
    it('is a no-op when the card is absent', () => {
        const orphan: ChatMessage[] = []
        expect(applyDelegationCancelled(orphan, 'rid-x', t, 2000)).toBe(orphan)
    })
})
