import { useEffect, useMemo, useState, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { X, Clock, Zap, AlertTriangle, BarChart2 } from 'lucide-react'
import {
    fetchFinOpsAgentSessions,
    fetchFinOpsUserSessions,
    type SessionUsage,
    type AgentUsage,
    type UserUsage,
    type SessionMessagesResponse,
    type SessionMessageDetail,
    type PageResponse,
} from '../../../../services/finopsAPI'
import {
    formatNumber,
    formatPercent,
    formatDate,
    formatMessageContent,
    roleLabel,
    DataTable,
    PaginationControls,
    MiniMetric,
    InsightLine,
    SplitBar,
} from './SharedComponents'

type MessageRoleFilter = 'all' | 'user' | 'assistant' | 'tool'

interface SessionMessagesDrawerProps {
    session: SessionUsage
    data: SessionMessagesResponse | null
    loading: boolean
    error: string | null
    locale: string
    onClose: () => void
}

export function SessionMessagesDrawer({
    session,
    data,
    loading,
    error,
    locale,
    onClose,
}: SessionMessagesDrawerProps) {
    const { t } = useTranslation()
    const [roleFilter, setRoleFilter] = useState<MessageRoleFilter>('all')
    const [expandedRows, setExpandedRows] = useState<Set<string>>(() => new Set())
    const bodyRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const el = bodyRef.current
        if (!el) return

        let timeoutId: number | undefined

        const handleScroll = () => {
            el.classList.add('is-scrolling')
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
            timeoutId = window.setTimeout(() => {
                el.classList.remove('is-scrolling')
            }, 800)
        }

        el.addEventListener('scroll', handleScroll, { passive: true })
        return () => {
            el.removeEventListener('scroll', handleScroll)
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
        }
    }, [])

    const messagesWithLatency = useMemo(() => {
        const rawMessages = data?.messages ?? []
        return rawMessages.map((msg, index) => {
            let durationSeconds: number | null = null
            if (index > 0) {
                const currentMs = new Date(msg.createdAt).getTime()
                const prevMs = new Date(rawMessages[index - 1].createdAt).getTime()
                const diff = (currentMs - prevMs) / 1000
                if (diff >= 0 && diff < 3600) {
                    durationSeconds = diff
                }
            }
            return {
                ...msg,
                durationSeconds,
            }
        })
    }, [data])

    const messages = data?.messages ?? []
    const roleCounts: Record<MessageRoleFilter, number> = {
        all: messages.length,
        user: messages.filter(message => !message.toolRequest && !message.toolResponse && message.role === 'user').length,
        assistant: messages.filter(message => !message.toolRequest && !message.toolResponse && message.role === 'assistant').length,
        tool: messages.filter(message => message.toolRequest || message.toolResponse).length,
    }
    const filteredMessages = messagesWithLatency.filter(message => {
        if (roleFilter === 'all') return true
        if (roleFilter === 'tool') return message.toolRequest || message.toolResponse
        return !message.toolRequest && !message.toolResponse && message.role === roleFilter
    })
    const largestLabel = data?.stats.largestContentPreview || '-'
    const inputOutputTotal = session.inputTokens + session.outputTokens
    const inputShare = inputOutputTotal > 0 ? session.inputTokens / inputOutputTotal : 0

    function toggleMessage(rowKey: string) {
        setExpandedRows(prev => {
            const next = new Set(prev)
            if (next.has(rowKey)) {
                next.delete(rowKey)
            } else {
                next.add(rowKey)
            }
            return next
        })
    }

    return (
        <div className="finops-drawer-backdrop" onClick={onClose} style={{ zIndex: 75 }}>
            <aside className="finops-session-drawer" role="dialog" aria-modal="true" aria-label={t('finops.drawer.title')} onClick={event => event.stopPropagation()}>
                <header className="finops-drawer-header">
                    <div>
                        <h2 title={session.label || session.id}>{session.label || session.id}</h2>
                        <span>{t('finops.drawer.eyebrow')}</span>
                    </div>
                    <button type="button" className="finops-drawer-close" onClick={onClose} aria-label={t('common.close')}>
                        <X size={18} />
                    </button>
                </header>

                <div ref={bodyRef} className="finops-drawer-body">
                    <section className="finops-drawer-meta" aria-label={t('finops.drawer.sessionMeta')}>
                        <span><b>{t('finops.columns.user')}</b>{session.userId}</span>
                        <span><b>{t('finops.columns.agent')}</b>{session.agentId}</span>
                        <span><b>{t('finops.columns.model')}</b>{session.modelName || '-'}</span>
                        <span><b>{t('finops.columns.updated')}</b>{formatDate(session.updatedAt, locale)}</span>
                    </section>

                    <section className="finops-drawer-metrics">
                        <MiniMetric label={t('finops.metrics.totalTokens')} value={formatNumber(session.totalTokens)} />
                        <MiniMetric label={t('finops.metrics.inputTokens')} value={formatNumber(session.inputTokens)} />
                        <MiniMetric label={t('finops.metrics.outputTokens')} value={formatNumber(session.outputTokens)} />
                        <MiniMetric label={t('finops.columns.messages')} value={formatNumber(data?.stats.messageCount ?? session.messageCount)} />
                    </section>

                    {error ? <div className="conn-banner conn-banner-error">{t('finops.loadFailed', { error })}</div> : null}
                    {loading ? <div className="finops-table-loading">{t('finops.drawer.loadingMessages')}</div> : null}

                    {data ? (
                        <>
                            <section className="finops-drawer-insights" aria-label={t('finops.drawer.explainability')}>
                                <InsightLine
                                    label={t('finops.drawer.inputOutput')}
                                    value={`${formatNumber(session.inputTokens)} / ${formatNumber(session.outputTokens)}`}
                                    detail={t('finops.drawer.inputShare', { percent: formatPercent(inputShare) })}
                                />
                                <InsightLine
                                    label={t('finops.drawer.messageMix')}
                                    value={t('finops.drawer.messageMixValue', {
                                        users: roleCounts.user,
                                        assistants: roleCounts.assistant,
                                        tools: roleCounts.tool,
                                    })}
                                    detail={t('finops.drawer.messageMixDetail', { count: data.stats.messageCount })}
                                />
                                <InsightLine
                                    label={t('finops.drawer.largestContent')}
                                    value={t('finops.drawer.largestContentValue', {
                                        length: formatNumber(data.stats.largestContentLength),
                                        role: roleLabel(data.stats.largestContentRole, t),
                                    })}
                                    detail={largestLabel}
                                />
                            </section>

                            <section className="finops-role-filter" role="tablist" aria-label={t('finops.drawer.roleFilter')}>
                                {(['all', 'user', 'assistant', 'tool'] as const).map(role => (
                                    <button
                                        key={role}
                                        type="button"
                                        className={`finops-role-filter-${role} ${roleFilter === role ? 'active' : ''}`}
                                        disabled={role !== 'all' && roleCounts[role] === 0}
                                        onClick={() => setRoleFilter(role)}
                                    >
                                        <span>{t(`finops.drawer.roles.${role}`)}</span>
                                        <b>{roleCounts[role]}</b>
                                    </button>
                                ))}
                            </section>

                            <section className="finops-message-timeline">
                                {filteredMessages.length === 0 ? (
                                    <div className="empty-state">
                                        <div className="empty-state-title">{t('common.noResults')}</div>
                                    </div>
                                ) : null}
                                {filteredMessages.map(message => {
                                    const rowKey = `${message.rowId}:${message.messageId || ''}`
                                    const expanded = expandedRows.has(rowKey)
                                    return (
                                        <MessageTimelineItem
                                            key={rowKey}
                                            message={message}
                                            durationSeconds={message.durationSeconds}
                                            expanded={expanded}
                                            locale={locale}
                                            onToggle={() => toggleMessage(rowKey)}
                                        />
                                    )
                                })}
                            </section>
                        </>
                    ) : null}
                </div>
            </aside>
        </div>
    )
}

interface MessageTimelineItemProps {
    message: SessionMessageDetail & { durationSeconds?: number | null }
    durationSeconds?: number | null
    expanded: boolean
    locale: string
    onToggle: () => void
}

function MessageTimelineItem({
    message,
    durationSeconds,
    expanded,
    locale,
    onToggle,
}: MessageTimelineItemProps) {
    const { t } = useTranslation()
    const isTool = message.toolRequest || message.toolResponse
    const role = isTool ? 'tool' : message.role
    const preRef = useRef<HTMLPreElement>(null)

    useEffect(() => {
        const el = preRef.current
        if (!el) return

        let timeoutId: number | undefined

        const handleScroll = () => {
            el.classList.add('is-scrolling')
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
            timeoutId = window.setTimeout(() => {
                el.classList.remove('is-scrolling')
            }, 800)
        }

        el.addEventListener('scroll', handleScroll, { passive: true })
        return () => {
            el.removeEventListener('scroll', handleScroll)
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
        }
    }, [expanded])

    return (
        <article className={`finops-message-item finops-message-${role}`}>
            <div className="finops-message-head">
                <div className="finops-message-title">
                    <span className={`finops-message-role finops-role-${role}`}>{roleLabel(role, t)}</span>
                    {message.toolName ? <span className="finops-message-tool-name">{message.toolName}</span> : null}

                    {durationSeconds !== undefined && durationSeconds !== null && durationSeconds > 0 ? (
                        <span className="finops-message-duration">
                            <Clock size={12} />
                            {durationSeconds < 1 ? `${Math.round(durationSeconds * 1000)}ms` : `${durationSeconds.toFixed(1)}s`}
                        </span>
                    ) : null}

                    {message.tokens != null ? (
                        <span className="finops-message-tokens-badge">
                            <Zap size={12} />
                            {t('finops.drawer.messageTokens', { tokens: formatNumber(message.tokens) })}
                        </span>
                    ) : null}

                    {message.error ? (
                        <span className="finops-message-error-badge">
                            <AlertTriangle size={12} />
                            {t('finops.drawer.errorFlag')}
                        </span>
                    ) : null}
                </div>
                <time>{formatDate(message.createdAt, locale)}</time>
            </div>
            <button type="button" className="finops-message-preview" onClick={onToggle}>
                {message.contentPreview || t('finops.drawer.emptyContent')}
            </button>
            <div className="finops-message-meta">
                <span className={`finops-meta-size ${message.contentLength > 5000 ? 'finops-size-large' : ''}`}>
                    <BarChart2 size={12} />
                    {t('finops.drawer.contentLength', { length: formatNumber(message.contentLength) })}
                </span>

                <span className="finops-meta-divider">·</span>

                <span className="finops-meta-visibility">
                    {message.userVisible ? t('finops.drawer.userVisible') : t('finops.drawer.userHidden')}
                </span>

                <span className="finops-meta-divider">·</span>

                <span className="finops-meta-visibility">
                    {message.agentVisible ? t('finops.drawer.agentVisible') : t('finops.drawer.agentHidden')}
                </span>
            </div>
            {expanded ? (
                <pre ref={preRef} className="finops-message-content">
                    <code>
                        {formatMessageContent(message.contentText)
                            .split('\n')
                            .map((line, idx) => (
                                <span key={idx} className="finops-code-line">
                                    <span className="finops-line-number">{idx + 1}</span>
                                    <span className="finops-line-text">{line}</span>
                                </span>
                            ))}
                    </code>
                    {message.contentTruncated ? (
                        <div className="finops-content-truncated-warning">
                            {t('finops.drawer.contentTruncated')}
                        </div>
                    ) : null}
                </pre>
            ) : null}
        </article>
    )
}

interface AgentDetailDrawerProps {
    agent: AgentUsage
    locale: string
    onClose: () => void
    onSessionSelect: (session: SessionUsage) => void
}

export function AgentDetailDrawer({
    agent,
    locale,
    onClose,
    onSessionSelect,
}: AgentDetailDrawerProps) {
    const { t } = useTranslation()
    const [page, setPage] = useState(1)
    const [data, setData] = useState<PageResponse<SessionUsage> | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [sessionFilter, setSessionFilter] = useState<'all' | 'user' | 'scheduled'>('all')

    const bodyRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const el = bodyRef.current
        if (!el) return

        let timeoutId: number | undefined

        const handleScroll = () => {
            el.classList.add('is-scrolling')
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
            timeoutId = window.setTimeout(() => {
                el.classList.remove('is-scrolling')
            }, 800)
        }

        el.addEventListener('scroll', handleScroll, { passive: true })
        return () => {
            el.removeEventListener('scroll', handleScroll)
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
        }
    }, [])

    useEffect(() => {
        let cancelled = false
        async function loadAgentSessions() {
            setLoading(true)
            setError(null)
            try {
                const res = await fetchFinOpsAgentSessions(agent.agentId, page, 25)
                if (!cancelled) {
                    setData(res)
                }
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : String(err))
                }
            } finally {
                if (!cancelled) {
                    setLoading(false)
                }
            }
        }
        void loadAgentSessions()
        return () => {
            cancelled = true
        }
    }, [agent.agentId, page])

    const sessions = data?.items ?? []

    const filteredSessions = useMemo(() => {
        return sessions.filter(session => {
            if (sessionFilter === 'all') return true
            if (sessionFilter === 'scheduled') return session.sessionType === 'scheduled'
            return session.sessionType !== 'scheduled'
        })
    }, [sessions, sessionFilter])

    const manualCount = Math.max(0, agent.sessionCount - agent.scheduledSessionCount)
    const runSegments = [
        { id: 'manual', label: t('finops.sessionTypes.user'), value: manualCount, color: 'var(--chart-1)' },
        { id: 'scheduled', label: t('finops.sessionTypes.scheduled'), value: agent.scheduledSessionCount, color: 'var(--chart-3)' }
    ]

    const tokenSegments = [
        { id: 'input', label: t('finops.metrics.inputTokens'), value: agent.inputTokens, color: 'var(--color-accent)' },
        { id: 'output', label: t('finops.metrics.outputTokens'), value: agent.outputTokens, color: 'var(--chart-2)' }
    ]

    return (
        <div className="finops-drawer-backdrop" onClick={onClose} style={{ zIndex: 70 }}>
            <aside className="finops-session-drawer" role="dialog" aria-modal="true" aria-label={t('finops.drawer.agentTitle')} onClick={event => event.stopPropagation()}>
                <header className="finops-drawer-header">
                    <div>
                        <h2>{agent.agentId}</h2>
                        <span>{t('finops.drawer.agentEyebrow')}</span>
                    </div>
                    <button type="button" className="finops-drawer-close" onClick={onClose} aria-label={t('common.close')}>
                        <X size={18} />
                    </button>
                </header>

                <div ref={bodyRef} className="finops-drawer-body">
                    <section className="finops-drawer-meta" aria-label={t('finops.drawer.agentMeta')}>
                        <span><b>{t('finops.columns.users')}</b>{agent.activeUsers}</span>
                        <span><b>{t('finops.columns.sessions')}</b>{agent.sessionCount}</span>
                        <span><b>{t('finops.columns.scheduled')}</b>{agent.scheduledSessionCount}</span>
                        <span><b>{t('finops.metrics.avgTokens')}</b>{formatNumber(agent.avgTokensPerSession)}</span>
                    </section>

                    <section className="finops-drawer-metrics">
                        <MiniMetric label={t('finops.metrics.totalTokens')} value={formatNumber(agent.totalTokens)} />
                        <MiniMetric label={t('finops.metrics.inputTokens')} value={formatNumber(agent.inputTokens)} />
                        <MiniMetric label={t('finops.metrics.outputTokens')} value={formatNumber(agent.outputTokens)} />
                        <MiniMetric label={t('finops.columns.sessions')} value={String(agent.highTokenSessionCount)} />
                    </section>

                    <section className="finops-drawer-insights" aria-label={t('finops.drawer.agentMeta')}>
                        <div className="finops-insight-card" style={{ gridColumn: 'span 3', display: 'flex', flexDirection: 'column', gap: 'var(--spacing-3)', padding: 'var(--spacing-3)', border: '1px solid var(--color-border)', borderRadius: 'var(--radius-lg)', background: 'var(--color-bg-primary)' }}>
                            <span style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-secondary)', fontWeight: 600 }}>{t('finops.drawer.sessionSplit')}</span>
                            <SplitBar segments={runSegments} />
                        </div>
                        <div className="finops-insight-card" style={{ gridColumn: 'span 3', display: 'flex', flexDirection: 'column', gap: 'var(--spacing-3)', padding: 'var(--spacing-3)', border: '1px solid var(--color-border)', borderRadius: 'var(--radius-lg)', background: 'var(--color-bg-primary)' }}>
                            <span style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-secondary)', fontWeight: 600 }}>{t('finops.sections.tokenComposition')}</span>
                            <SplitBar segments={tokenSegments} />
                        </div>
                    </section>

                    <section className="finops-role-filter" role="tablist" aria-label={t('finops.drawer.associatedSessions')}>
                        <span style={{ marginRight: 'auto', alignSelf: 'center', fontSize: 'var(--font-size-sm)', fontWeight: 600, color: 'var(--color-text-primary)' }}>
                            {t('finops.drawer.associatedSessions')}
                        </span>
                        {(['all', 'user', 'scheduled'] as const).map(filter => (
                            <button
                                key={filter}
                                type="button"
                                className={`finops-role-filter-all ${sessionFilter === filter ? 'active' : ''}`}
                                onClick={() => setSessionFilter(filter)}
                                style={{ padding: '4px 10px', fontSize: 'var(--font-size-xs)' }}
                            >
                                <span>{filter === 'all' ? t('finops.drawer.roles.all') : (filter === 'scheduled' ? t('finops.sessionTypes.scheduled') : t('finops.sessionTypes.user'))}</span>
                            </button>
                        ))}
                    </section>

                    {error ? <div className="conn-banner conn-banner-error">{t('finops.loadFailed', { error })}</div> : null}
                    {loading ? <div className="finops-table-loading">{t('finops.drawer.loadingAgentSessions')}</div> : null}

                    {!loading && data ? (
                        <div style={{ border: '1px solid var(--color-border)', borderRadius: 'var(--radius-lg)', overflow: 'hidden' }}>
                            <DataTable>
                                <thead>
                                    <tr>
                                        <th>{t('finops.columns.session')}</th>
                                        <th>{t('finops.columns.user')}</th>
                                        <th>{t('finops.columns.tokens')}</th>
                                        <th>{t('finops.columns.messages')}</th>
                                        <th>{t('finops.columns.updated')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredSessions.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--spacing-8)', color: 'var(--color-text-muted)' }}>
                                                {t('common.noResults')}
                                            </td>
                                        </tr>
                                    ) : null}
                                    {filteredSessions.map(row => (
                                        <tr
                                            key={row.id}
                                            className="finops-clickable-row"
                                            onClick={() => onSessionSelect(row)}
                                            style={{ cursor: 'pointer' }}
                                        >
                                            <td className="finops-session-cell" style={{ maxWidth: '180px', minWidth: '120px' }} title={row.label || row.id}>
                                                {row.label || row.id}
                                            </td>
                                            <td>{row.userId}</td>
                                            <td style={{ fontWeight: row.totalTokens > 50000 ? 600 : 'normal', color: row.totalTokens > 50000 ? 'var(--color-warning)' : 'inherit' }}>
                                                {formatNumber(row.totalTokens)}
                                                {row.totalTokens > 50000 ? <span style={{ marginLeft: '4px', fontSize: '10px', padding: '1px 4px', background: 'rgba(245, 158, 11, 0.1)', color: 'var(--color-warning)', borderRadius: '2px' }}>⚠️</span> : null}
                                            </td>
                                            <td>{row.messageCount}</td>
                                            <td>{formatDate(row.updatedAt, locale)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </DataTable>
                            <PaginationControls page={data} onPageChange={setPage} />
                        </div>
                    ) : null}
                </div>
            </aside>
        </div>
    )
}

interface UserDetailDrawerProps {
    user: UserUsage
    locale: string
    onClose: () => void
    onSessionSelect: (session: SessionUsage) => void
}

export function UserDetailDrawer({
    user,
    locale,
    onClose,
    onSessionSelect,
}: UserDetailDrawerProps) {
    const { t } = useTranslation()
    const [page, setPage] = useState(1)
    const [data, setData] = useState<PageResponse<SessionUsage> | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [sessionFilter, setSessionFilter] = useState<'all' | 'user' | 'scheduled'>('all')

    const bodyRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const el = bodyRef.current
        if (!el) return

        let timeoutId: number | undefined

        const handleScroll = () => {
            el.classList.add('is-scrolling')
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
            timeoutId = window.setTimeout(() => {
                el.classList.remove('is-scrolling')
            }, 800)
        }

        el.addEventListener('scroll', handleScroll, { passive: true })
        return () => {
            el.removeEventListener('scroll', handleScroll)
            if (timeoutId) {
                window.clearTimeout(timeoutId)
            }
        }
    }, [])

    useEffect(() => {
        let cancelled = false
        async function loadUserSessions() {
            setLoading(true)
            setError(null)
            try {
                const res = await fetchFinOpsUserSessions(user.userId, page, 25)
                if (!cancelled) {
                    setData(res)
                }
            } catch (err) {
                if (!cancelled) {
                    setError(err instanceof Error ? err.message : String(err))
                }
            } finally {
                if (!cancelled) {
                    setLoading(false)
                }
            }
        }
        void loadUserSessions()
        return () => {
            cancelled = true
        }
    }, [user.userId, page])

    const sessions = data?.items ?? []

    const filteredSessions = useMemo(() => {
        return sessions.filter(session => {
            if (sessionFilter === 'all') return true
            if (sessionFilter === 'scheduled') return session.sessionType === 'scheduled'
            return session.sessionType !== 'scheduled'
        })
    }, [sessions, sessionFilter])

    const tokenSegments = [
        { id: 'input', label: t('finops.metrics.inputTokens'), value: user.inputTokens, color: 'var(--color-accent)' },
        { id: 'output', label: t('finops.metrics.outputTokens'), value: user.outputTokens, color: 'var(--chart-2)' }
    ]

    return (
        <div className="finops-drawer-backdrop" onClick={onClose} style={{ zIndex: 70 }}>
            <aside className="finops-session-drawer" role="dialog" aria-modal="true" aria-label={t('finops.drawer.userTitle')} onClick={event => event.stopPropagation()}>
                <header className="finops-drawer-header">
                    <div>
                        <h2>{user.userId}</h2>
                        <span>{t('finops.drawer.userEyebrow')}</span>
                    </div>
                    <button type="button" className="finops-drawer-close" onClick={onClose} aria-label={t('common.close')}>
                        <X size={18} />
                    </button>
                </header>

                <div ref={bodyRef} className="finops-drawer-body">
                    <section className="finops-drawer-meta" aria-label={t('finops.drawer.userMeta')}>
                        <span><b>{t('finops.columns.agents')}</b>{user.activeAgents}</span>
                        <span><b>{t('finops.columns.sessions')}</b>{user.sessionCount}</span>
                        <span><b>{t('finops.columns.topAgent')}</b>{user.topAgent || '-'}</span>
                        <span><b>{t('finops.columns.lastActive')}</b>{formatDate(user.lastActiveAt, locale)}</span>
                    </section>

                    <section className="finops-drawer-metrics">
                        <MiniMetric label={t('finops.metrics.totalTokens')} value={formatNumber(user.totalTokens)} />
                        <MiniMetric label={t('finops.metrics.inputTokens')} value={formatNumber(user.inputTokens)} />
                        <MiniMetric label={t('finops.metrics.outputTokens')} value={formatNumber(user.outputTokens)} />
                        <MiniMetric label={t('finops.metrics.avgTokens')} value={formatNumber(user.avgTokensPerSession)} />
                    </section>

                    <section className="finops-drawer-insights" aria-label={t('finops.drawer.userMeta')}>
                        <div className="finops-insight-card" style={{ gridColumn: 'span 6', display: 'flex', flexDirection: 'column', gap: 'var(--spacing-3)', padding: 'var(--spacing-3)', border: '1px solid var(--color-border)', borderRadius: 'var(--radius-lg)', background: 'var(--color-bg-primary)' }}>
                            <span style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-secondary)', fontWeight: 600 }}>{t('finops.sections.tokenComposition')}</span>
                            <SplitBar segments={tokenSegments} />
                        </div>
                    </section>

                    <section className="finops-role-filter" role="tablist" aria-label={t('finops.drawer.associatedSessions')}>
                        <span style={{ marginRight: 'auto', alignSelf: 'center', fontSize: 'var(--font-size-sm)', fontWeight: 600, color: 'var(--color-text-primary)' }}>
                            {t('finops.drawer.associatedSessions')}
                        </span>
                        {(['all', 'user', 'scheduled'] as const).map(filter => (
                            <button
                                key={filter}
                                type="button"
                                className={`finops-role-filter-all ${sessionFilter === filter ? 'active' : ''}`}
                                onClick={() => setSessionFilter(filter)}
                                style={{ padding: '4px 10px', fontSize: 'var(--font-size-xs)' }}
                            >
                                <span>{filter === 'all' ? t('finops.drawer.roles.all') : (filter === 'scheduled' ? t('finops.sessionTypes.scheduled') : t('finops.sessionTypes.user'))}</span>
                            </button>
                        ))}
                    </section>

                    {error ? <div className="conn-banner conn-banner-error">{t('finops.loadFailed', { error })}</div> : null}
                    {loading ? <div className="finops-table-loading">{t('finops.drawer.loadingUserSessions')}</div> : null}

                    {!loading && data ? (
                        <div style={{ border: '1px solid var(--color-border)', borderRadius: 'var(--radius-lg)', overflow: 'hidden' }}>
                            <DataTable>
                                <thead>
                                    <tr>
                                        <th>{t('finops.columns.session')}</th>
                                        <th>{t('finops.columns.agent')}</th>
                                        <th>{t('finops.columns.tokens')}</th>
                                        <th>{t('finops.columns.messages')}</th>
                                        <th>{t('finops.columns.updated')}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredSessions.length === 0 ? (
                                        <tr>
                                            <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--spacing-8)', color: 'var(--color-text-muted)' }}>
                                                {t('common.noResults')}
                                            </td>
                                        </tr>
                                    ) : null}
                                    {filteredSessions.map(row => (
                                        <tr
                                            key={row.id}
                                            className="finops-clickable-row"
                                            onClick={() => onSessionSelect(row)}
                                            style={{ cursor: 'pointer' }}
                                        >
                                            <td className="finops-session-cell" style={{ maxWidth: '180px', minWidth: '120px' }} title={row.label || row.id}>
                                                {row.label || row.id}
                                            </td>
                                            <td>{row.agentId}</td>
                                            <td style={{ fontWeight: row.totalTokens > 50000 ? 600 : 'normal', color: row.totalTokens > 50000 ? 'var(--color-warning)' : 'inherit' }}>
                                                {formatNumber(row.totalTokens)}
                                                {row.totalTokens > 50000 ? <span style={{ marginLeft: '4px', fontSize: '10px', padding: '1px 4px', background: 'rgba(245, 158, 11, 0.1)', color: 'var(--color-warning)', borderRadius: '2px' }}>⚠️</span> : null}
                                            </td>
                                            <td>{row.messageCount}</td>
                                            <td>{formatDate(row.updatedAt, locale)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </DataTable>
                            <PaginationControls page={data} onPageChange={setPage} />
                        </div>
                    ) : null}
                </div>
            </aside>
        </div>
    )
}
