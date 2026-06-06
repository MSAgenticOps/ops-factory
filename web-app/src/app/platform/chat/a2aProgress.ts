import { createContext, useContext } from 'react'
import type { A2AProgressFrame } from '@goosed/sdk'

/**
 * Live progress state for one in-flight `call_agent` (A2A) tool call, keyed by the tool-call request id.
 *
 * The binding rides on standard MCP logging notifications: the gateway/extension emit `notifications/message` frames
 * whose `data` carries an `a2a_progress` payload; goose tags each with the `call_agent` tool-call id as the event
 * `request_id` (which equals the rendered tool card's id). This module is the pure reducer/selector for that state —
 * it performs no I/O and is unit-tested in isolation.
 */
export interface A2AProgress {
    targetAgent?: string
    subSessionId?: string
    kind?: string
    label?: string
    /** Raw fragment counter from the backend (text deltas + tool calls); kept for debugging, not displayed. */
    step?: number
    /** Number of tool calls the delegated agent has made — the meaningful, user-facing "work done" count. */
    toolCalls?: number
    /** Local clock base for the elapsed timer (set on first progress for this request). */
    startedAt: number
    updatedAt: number
}

export type A2AProgressMap = Record<string, A2AProgress>

/** Minimal shape of the SSE event this module reads (a subset of the SDK `SSEEvent`). */
export interface A2AProgressEventLike {
    type?: string
    request_id?: string
    message?: unknown
}

const PROGRESS_TYPE = 'a2a_progress'

function asRecord(value: unknown): Record<string, unknown> | null {
    return value && typeof value === 'object' ? (value as Record<string, unknown>) : null
}

/**
 * Defensively locates the `a2a_progress` payload inside a Notification event's `message` (the rmcp ServerNotification).
 * Tries `message.params.data`, `message.data`, then `message` itself.
 */
function findProgressData(message: unknown): Record<string, unknown> | null {
    const root = asRecord(message)
    if (!root) return null
    const candidates: unknown[] = [asRecord(root.params)?.data, root.data, root]
    for (const candidate of candidates) {
        const record = asRecord(candidate)
        if (record && record.type === PROGRESS_TYPE) {
            return record
        }
    }
    return null
}

/**
 * Extracts the `a2a_progress` payload and its binding request id from a Notification event, or null if the event is
 * not an A2A progress notification.
 */
export function extractA2AProgress(
    event: A2AProgressEventLike,
): { requestId: string; data: Record<string, unknown> } | null {
    if (event.type !== 'Notification') return null
    const requestId = event.request_id
    if (!requestId) return null
    const data = findProgressData(event.message)
    if (!data) return null
    return { requestId, data }
}

/**
 * Folds an `a2a_progress` payload (last-wins) into a single progress entry. Shared by the Notification-event path
 * (model-initiated delegation) and the direct-frame path (deterministic `@mention` delegation) so both stay identical.
 */
function foldProgress(prev: A2AProgress | undefined, data: Record<string, unknown>, now: number): A2AProgress {
    return {
        targetAgent: typeof data.target_agent === 'string' ? data.target_agent : prev?.targetAgent,
        subSessionId: typeof data.sub_session_id === 'string' ? data.sub_session_id : prev?.subSessionId,
        kind: typeof data.kind === 'string' ? data.kind : prev?.kind,
        label: typeof data.label === 'string' ? data.label : prev?.label,
        step: typeof data.step === 'number' ? data.step : prev?.step,
        toolCalls: typeof data.tool_calls === 'number' ? data.tool_calls : prev?.toolCalls,
        startedAt: prev?.startedAt ?? now,
        updatedAt: now,
    }
}

/** Folds a Notification event into the progress map (last-wins per request id). Returns the same map if not an A2A event. */
export function reduceA2AProgress(map: A2AProgressMap, event: A2AProgressEventLike, now: number): A2AProgressMap {
    const extracted = extractA2AProgress(event)
    if (!extracted) return map
    const { requestId, data } = extracted
    return { ...map, [requestId]: foldProgress(map[requestId], data, now) }
}

/**
 * Folds an `a2a_progress` frame directly into the map under a given tool-call request id. Used by the deterministic
 * `@mention` path, which drives the gateway A2A SSE itself (no goosed Notification event to extract from).
 */
export function reduceA2AProgressFromFrame(
    map: A2AProgressMap,
    requestId: string,
    frame: A2AProgressFrame,
    now: number,
): A2AProgressMap {
    // Spread to a plain record so the shared `foldProgress` (which reads untyped Notification payloads) accepts it.
    const data: Record<string, unknown> = { ...frame }
    return { ...map, [requestId]: foldProgress(map[requestId], data, now) }
}

/** Context carrying the per-request progress map so deeply-nested tool cards can bind by their id without prop threading. */
export const A2AProgressContext = createContext<A2AProgressMap>({})

/** Selects the progress entry for a given tool-call request id (undefined if none). */
export function useA2AProgress(requestId?: string): A2AProgress | undefined {
    const map = useContext(A2AProgressContext)
    return requestId ? map[requestId] : undefined
}
