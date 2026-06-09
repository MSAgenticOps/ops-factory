/**
 * Deterministic `@mention` delegation — pure helpers (no I/O, unit-tested).
 *
 * The initiating agent (fo-copilot) is a weak model that unreliably calls the `delegation__call_agent` tool and can
 * fabricate a target's reply. So an `@<agentId>` is treated as an explicit user instruction: the frontend triggers the
 * gateway A2A call directly and synthesizes the same "Call agent" tool card + result the model path would produce —
 * without letting the model decide whether to delegate. These helpers do the parsing and the message-model synthesis;
 * `useChat` does the I/O (driving the gateway A2A SSE via `client.delegateAgent`).
 */
import type { A2AResultFrame } from '@goosed/sdk'
import type { ChatMessage } from '../../../types/message'

export interface DelegationDirective {
    /** The target agent id (the canonical `<agentId>` after `@`). */
    agentId: string
    /** The task to relay — the user's message with every known `@<agentId>` marker removed. */
    task: string
}

/**
 * Translator seam. The production caller passes react-i18next's `t`; unit tests pass a stub. Keeps these helpers pure
 * while routing the only user-facing literals (error/timeout/cancel wrappers) through i18n.
 */
export type TranslateFn = (key: string, options?: Record<string, unknown>) => string

/** The synthetic tool name; matches the model-initiated tool so `ToolCallDisplay` renders the identical "Call agent" card. */
export const CALL_AGENT_TOOL_NAME = 'delegation__call_agent'

interface FoundMention {
    agentId: string
    start: number
    end: number
}

/** Finds every `@<token>` at a whitespace boundary whose token is a known agent id (rejects mid-word `@`, e.g. emails). */
function findKnownMentions(text: string, knownAgentIds: ReadonlySet<string>): FoundMention[] {
    const mentions: FoundMention[] = []
    // (^|\s)@(slug): the `@` must start at the message start or after whitespace; the slug is an agent-id run
    // ([A-Za-z0-9_-]). Restricting the token to id characters means trailing punctuation (e.g. `@qa-agent,` or
    // `@qa-agent.`) is excluded from the token rather than breaking the match and falling back to the model path.
    const re = /(^|\s)@([A-Za-z0-9_-]+)/g
    let match: RegExpExecArray | null
    while ((match = re.exec(text)) !== null) {
        const agentId = match[2]
        if (!knownAgentIds.has(agentId)) continue
        const at = match.index + match[1].length
        mentions.push({ agentId, start: at, end: at + 1 + agentId.length })
    }
    return mentions
}

/**
 * Parses a sent message into delegation directives. Returns one directive per *distinct* mentioned known agent (in
 * first-appearance order); each carries the same `task` (the message with all known `@<agentId>` markers stripped).
 * Returns `[]` when no known agent is mentioned (the caller then takes the normal reply path). `agents` must be the
 * same list that powers the `@mention` picker so the trigger matches what the user saw.
 */
export function parseDelegationDirectives(
    text: string,
    agents: ReadonlyArray<{ id: string }>,
): DelegationDirective[] {
    if (!text) return []
    const known = new Set(agents.map(a => a.id))
    const mentions = findKnownMentions(text, known)
    if (mentions.length === 0) return []

    // Strip the markers right-to-left so earlier offsets stay valid; collapse the resulting whitespace.
    let task = text
    for (let i = mentions.length - 1; i >= 0; i--) {
        task = task.slice(0, mentions[i].start) + task.slice(mentions[i].end)
    }
    task = task.replace(/\s+/g, ' ').trim()

    // A bare mention with no remaining instruction (e.g. just `@qa-agent`) has nothing to delegate, and the gateway
    // rejects an empty message with 400. Fall back to the normal reply path instead of firing a doomed call.
    if (!task) return []

    const seen = new Set<string>()
    const directives: DelegationDirective[] = []
    for (const m of mentions) {
        if (seen.has(m.agentId)) continue
        seen.add(m.agentId)
        directives.push({ agentId: m.agentId, task })
    }
    return directives
}

/**
 * Builds the pending assistant "Call agent" message for a delegation. The `requestId` is the single id that binds the
 * tool card, its `A2AStatusLine`, and the eventual `toolResponse` — so all three render against the same delegation.
 */
export function makeCallAgentToolMessage(
    requestId: string,
    targetAgent: string,
    task: string,
    nowMs: number = Date.now(),
): ChatMessage {
    return {
        id: `a2a-${requestId}`,
        role: 'assistant',
        content: [{
            type: 'toolRequest',
            id: requestId,
            toolCall: {
                status: 'pending',
                value: { name: CALL_AGENT_TOOL_NAME, arguments: { target: targetAgent, message: task } },
            },
        }],
        created: Math.floor(nowMs / 1000),
        // Shown to the user; not part of the goosed conversation (the deterministic path bypasses the model).
        metadata: { userVisible: true, agentVisible: false, requestId },
    }
}

/**
 * Maps a terminal `a2a_result` frame to the user-facing text (mirrors the delegation MCP's `result_text`): errors and
 * timeouts are annotated (via i18n) so the user can react; otherwise the agent's result is shown verbatim.
 */
export function delegationResultText(frame: A2AResultFrame | undefined, t: TranslateFn): string {
    if (!frame) return t('chat.a2a.resultNoResult')
    const result = frame.result ?? ''
    if (frame.status === 'error') {
        return t('chat.a2a.resultError', { error: frame.error ?? t('chat.a2a.resultErrorUnknown') })
    }
    if (frame.status === 'timeout') {
        return result ? t('chat.a2a.resultTimeoutPartial', { result }) : t('chat.a2a.resultTimeout')
    }
    return result
}

/**
 * Appends a terminal `toolResponse` to the pending "Call agent" card (so its spinner resolves) and pushes the reply
 * `text` as a separate assistant message. Both steps are idempotent: re-applying for the same `requestId` adds neither
 * a duplicate `toolResponse` nor a duplicate text message. `isError` flips the card to the error treatment.
 *
 * <p>`toolResult.status` is `MessageList`'s unwrap signal — only `success` surfaces `toolResult.value` as the card
 * output (anything else renders the raw object). So it is always `success` here to show the clean reply/error text;
 * the independent `isError` flag (which `MessageList` and `ToolCallDisplay` read separately) drives the error styling.
 */
function finalizeCard(
    messages: ChatMessage[],
    requestId: string,
    text: string,
    isError: boolean,
    nowMs: number,
): ChatMessage[] {
    const toolMessageId = `a2a-${requestId}`
    // If the card is gone (e.g. the session was switched mid-delegation), there is nothing to finalize — returning the
    // list untouched prevents leaking an orphan result/cancel message into an unrelated session.
    if (!messages.some(m => m.id === toolMessageId)) return messages
    const next = messages.map(message => {
        if (message.id !== toolMessageId) return message
        const alreadyResponded = message.content.some(c => c.type === 'toolResponse' && c.id === requestId)
        if (alreadyResponded) return message
        return {
            ...message,
            content: [...message.content, {
                type: 'toolResponse' as const,
                id: requestId,
                toolResult: { status: 'success', isError, value: text },
            }],
        }
    })

    const textMessageId = `a2a-text-${requestId}`
    if (!next.some(m => m.id === textMessageId)) {
        next.push({
            id: textMessageId,
            role: 'assistant',
            content: [{ type: 'text', text }],
            created: Math.floor(nowMs / 1000),
            metadata: { userVisible: true, agentVisible: false, requestId },
        })
    }
    return next
}

/**
 * Applies the terminal result to the message list: flips the pending "Call agent" card to done and pushes the agent's
 * reply as a separate assistant text message. Idempotent (see {@link finalizeCard}).
 */
export function applyDelegationResult(
    messages: ChatMessage[],
    requestId: string,
    frame: A2AResultFrame | undefined,
    t: TranslateFn,
    nowMs: number = Date.now(),
): ChatMessage[] {
    return finalizeCard(messages, requestId, delegationResultText(frame, t), frame?.status === 'error', nowMs)
}

/**
 * Finalizes a pending card after the user cancels mid-delegation, so the "Call agent" spinner does not hang forever.
 * Idempotent (see {@link finalizeCard}).
 */
export function applyDelegationCancelled(
    messages: ChatMessage[],
    requestId: string,
    t: TranslateFn,
    nowMs: number = Date.now(),
): ChatMessage[] {
    return finalizeCard(messages, requestId, t('chat.a2a.cancelled'), false, nowMs)
}
