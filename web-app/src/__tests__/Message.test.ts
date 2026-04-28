import { describe, expect, it } from 'vitest'
import { getReasoningContent, getThinkingContent } from '../utils/messageContent'
import type { ChatMessage } from '../types/message'

describe('Message thinking content helpers', () => {
    it('extracts structured reasoning content for the thinking panel', () => {
        const message: ChatMessage = {
            id: 'assistant-1',
            role: 'assistant',
            content: [
                { type: 'reasoning', text: 'first chunk' },
                { type: 'reasoning', text: '\nsecond chunk' },
            ],
        }

        expect(getReasoningContent(message)).toBe('first chunk\nsecond chunk')
    })

    it('prefers full accumulated reasoning over earlier deltas (prevents chunk+full duplication)', () => {
        const message: ChatMessage = {
            id: 'assistant-chunk-full',
            role: 'assistant',
            content: [
                { type: 'reasoning', text: '用户询问' },
                { type: 'reasoning', text: '华中测试' },
                { type: 'reasoning', text: '环境。' },
                { type: 'toolRequest', id: 'tool-1' },
                { type: 'reasoning', text: '用户询问华中测试环境。' },
            ],
        }

        expect(getReasoningContent(message)).toBe('用户询问华中测试环境。')
    })

    it('dedupes repeated reasoning segments even when separated by tool content', () => {
        const message: ChatMessage = {
            id: 'assistant-dup',
            role: 'assistant',
            content: [
                { type: 'reasoning', text: 'same' },
                { type: 'toolRequest', id: 'tool-1' },
                { type: 'reasoning', text: 'same' },
            ],
        }

        expect(getReasoningContent(message)).toBe('same')
    })

    it('extracts structured thinking content before falling back to think tags', () => {
        const structuredMessage: ChatMessage = {
            id: 'assistant-2',
            role: 'assistant',
            content: [
                { type: 'thinking', thinking: 'structured thought' },
                { type: 'text', text: '<think>fallback thought</think>' },
            ],
        }

        const taggedMessage: ChatMessage = {
            id: 'assistant-3',
            role: 'assistant',
            content: [
                { type: 'text', text: 'Answer\n<think>fallback thought</think>' },
            ],
        }

        expect(getThinkingContent(structuredMessage)).toBe('structured thought')
        expect(getThinkingContent(taggedMessage)).toBe('fallback thought')
    })
})
