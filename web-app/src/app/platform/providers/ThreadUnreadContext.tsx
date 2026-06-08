import { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState, type ReactNode } from 'react'
import { useGoosed } from './GoosedContext'
import { useUser } from './UserContext'
import { runtime, gatewayHeaders } from '../../../config/runtime'
import type { ChannelBinding, ChannelDetail, ChannelSummary } from '../../../types/channel'

/**
 * One proactive follow-up record as the Thread entry consumes it (mirror of the gateway
 * {@code ProactiveFollowupRecord}). It is the delivered push, not a state machine.
 */
export interface ThreadFollowup {
    time: string
    scheduleId: string
    sessionId: string
    targetKey: string
    summary: string
}

/**
 * One thread = one IM conversation (a channel binding), labelled by its agent (deployment convention, PRD §13.1).
 * `key` is a stable client-side identifier for read-state / cache maps; the server builds the real target key.
 */
export interface ThreadDescriptor {
    key: string
    channelId: string
    channelType: string
    channelName: string
    accountId: string
    conversationId: string
    threadId: string
    peerId: string
    agentId: string
    agentName: string
    sessionId: string
    lastInboundAt?: string | null
    lastOutboundAt?: string | null
}

interface ThreadUnreadContextValue {
    threads: ThreadDescriptor[]
    followupsByKey: Record<string, ThreadFollowup[]>
    unreadByKey: Record<string, number>
    unreadCount: number
    isLoading: boolean
    refresh: () => Promise<void>
    markThreadRead: (key: string) => void
}

const FOLLOWUP_LIMIT = 50

const POLL_INTERVAL_MS = 30000

/** Count follow-ups newer than the thread's last-read water level (absent water level → all unread). */
export function countUnreadFollowups(records: ThreadFollowup[], lastReadIso?: string): number {
    const lastReadAt = lastReadIso ? new Date(lastReadIso).getTime() : 0
    return records.filter(record => new Date(record.time).getTime() > lastReadAt).length
}

const ThreadUnreadContext = createContext<ThreadUnreadContextValue | null>(null)

function readStorageKey(userId: string): string {
    return `opsfactory:${userId}:thread:read:v1`
}

/** Stable client-side key for a thread's read-state / cache maps (not the server target key). */
function threadKeyOf(binding: ChannelBinding): string {
    return [binding.channelId, binding.accountId, binding.conversationId, binding.threadId ?? ''].join('::')
}

function loadReadMap(storageKey: string): Record<string, string> {
    if (typeof window === 'undefined') return {}
    try {
        const raw = window.localStorage.getItem(storageKey)
        if (!raw) return {}
        const parsed = JSON.parse(raw) as unknown
        return parsed && typeof parsed === 'object' ? (parsed as Record<string, string>) : {}
    } catch {
        return {}
    }
}

function saveReadMap(storageKey: string, map: Record<string, string>): void {
    if (typeof window === 'undefined') return
    try {
        window.localStorage.setItem(storageKey, JSON.stringify(map))
    } catch {
        // Ignore write failures (private mode / quota)
    }
}

/**
 * Aggregates the user's IM threads (channel bindings) and the proactive follow-ups delivered to each, so the
 * sidebar can show an unread badge and the Thread page can render column C without re-fetching. Read-state is a
 * per-user localStorage water level (mirrors {@code InboxContext}); polls every 30s like the inbox.
 *
 * <p>v1 scope (PRD §13.5): one channel per user, no cross-channel contact merge.
 */
export function ThreadUnreadProvider({ children }: { children: ReactNode }) {
    const { userId } = useUser()
    const { agents, isConnected } = useGoosed()
    const [threads, setThreads] = useState<ThreadDescriptor[]>([])
    const [followupsByKey, setFollowupsByKey] = useState<Record<string, ThreadFollowup[]>>({})
    const [isLoading, setIsLoading] = useState(false)
    const storageKey = readStorageKey(userId || 'anonymous')
    const [readMap, setReadMap] = useState<Record<string, string>>(() => loadReadMap(storageKey))

    useEffect(() => {
        setReadMap(loadReadMap(storageKey))
    }, [storageKey])

    useEffect(() => {
        saveReadMap(storageKey, readMap)
    }, [storageKey, readMap])

    const inFlightRef = useRef<AbortController | null>(null)

    const refresh = useCallback(async () => {
        // Abort any in-flight refresh so an older, slower run can't overwrite newer state out of order.
        inFlightRef.current?.abort()
        if (!isConnected) {
            inFlightRef.current = null
            setThreads([])
            setFollowupsByKey({})
            return
        }
        const controller = new AbortController()
        inFlightRef.current = controller
        const { signal } = controller
        setIsLoading(true)
        try {
            const headers = gatewayHeaders(userId)
            const listRes = await fetch(`${runtime.GATEWAY_URL}/channels`, { headers, signal })
            if (!listRes.ok) {
                setThreads([])
                setFollowupsByKey({})
                return
            }
            const listData = (await listRes.json()) as { channels?: ChannelSummary[] }
            const nameById = new Map(agents.map(agent => [agent.id, agent.name] as [string, string]))
            // Fetch each channel's detail (which carries its bindings) in parallel rather than serially.
            const activeChannels = (listData.channels ?? []).filter(c => c.enabled && c.bindingCount > 0)
            const details = await Promise.all(activeChannels.map(async summary => {
                const detailRes = await fetch(`${runtime.GATEWAY_URL}/channels/${encodeURIComponent(summary.id)}`,
                    { headers, signal })
                return detailRes.ok ? (await detailRes.json()) as ChannelDetail : null
            }))
            const descriptors: ThreadDescriptor[] = []
            for (const detail of details) {
                if (!detail) continue
                for (const binding of detail.bindings ?? []) {
                    descriptors.push({
                        key: threadKeyOf(binding),
                        channelId: binding.channelId,
                        channelType: detail.type,
                        channelName: detail.name,
                        accountId: binding.accountId,
                        conversationId: binding.conversationId,
                        threadId: binding.threadId ?? '',
                        peerId: binding.peerId,
                        agentId: binding.agentId,
                        agentName: nameById.get(binding.agentId) ?? binding.agentId,
                        sessionId: binding.sessionId,
                        lastInboundAt: binding.lastInboundAt,
                        lastOutboundAt: binding.lastOutboundAt,
                    })
                }
            }

            const followups: Record<string, ThreadFollowup[]> = {}
            await Promise.all(descriptors.map(async descriptor => {
                const params = new URLSearchParams({
                    channelId: descriptor.channelId,
                    conversationId: descriptor.conversationId,
                    accountId: descriptor.accountId,
                    threadId: descriptor.threadId,
                    limit: String(FOLLOWUP_LIMIT),
                })
                try {
                    const res = await fetch(
                        `${runtime.GATEWAY_URL}/agents/${encodeURIComponent(descriptor.agentId)}`
                        + `/threads/followups?${params.toString()}`, { headers, signal })
                    if (!res.ok) {
                        followups[descriptor.key] = []
                        return
                    }
                    const data = (await res.json()) as { followups?: ThreadFollowup[] }
                    // The endpoint returns oldest-first; the timeline renders newest-first.
                    followups[descriptor.key] = (data.followups ?? []).slice().reverse()
                } catch {
                    followups[descriptor.key] = []
                }
            }))

            if (inFlightRef.current !== controller) return
            setThreads(descriptors)
            setFollowupsByKey(followups)
        } catch (err) {
            if ((err as { name?: string })?.name === 'AbortError') return
            // Keep the poll resilient: a network/parse failure logs and leaves prior state intact.
            console.warn('Thread unread refresh failed:', err)
        } finally {
            if (inFlightRef.current === controller) {
                inFlightRef.current = null
                setIsLoading(false)
            }
        }
    }, [agents, isConnected, userId])

    useEffect(() => {
        void refresh()
        return () => {
            inFlightRef.current?.abort()
        }
    }, [refresh])

    useEffect(() => {
        const interval = window.setInterval(() => {
            void refresh()
        }, POLL_INTERVAL_MS)
        return () => window.clearInterval(interval)
    }, [refresh])

    const unreadByKey = useMemo(() => {
        const out: Record<string, number> = {}
        for (const descriptor of threads) {
            out[descriptor.key] = countUnreadFollowups(followupsByKey[descriptor.key] ?? [], readMap[descriptor.key])
        }
        return out
    }, [threads, followupsByKey, readMap])

    const unreadCount = useMemo(
        () => Object.values(unreadByKey).reduce((sum, count) => sum + count, 0),
        [unreadByKey],
    )

    const markThreadRead = useCallback((key: string) => {
        setReadMap(prev => ({ ...prev, [key]: new Date().toISOString() }))
    }, [])

    const value = useMemo<ThreadUnreadContextValue>(() => ({
        threads,
        followupsByKey,
        unreadByKey,
        unreadCount,
        isLoading,
        refresh,
        markThreadRead,
    }), [threads, followupsByKey, unreadByKey, unreadCount, isLoading, refresh, markThreadRead])

    return (
        <ThreadUnreadContext.Provider value={value}>
            {children}
        </ThreadUnreadContext.Provider>
    )
}

export function useThreadUnread(): ThreadUnreadContextValue {
    const ctx = useContext(ThreadUnreadContext)
    if (!ctx) {
        throw new Error('useThreadUnread must be used within a ThreadUnreadProvider')
    }
    return ctx
}
