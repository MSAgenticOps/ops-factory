import { useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { useInbox } from '../contexts/InboxContext'

export default function Inbox() {
    const navigate = useNavigate()
    const { unreadSessions, unreadCount, isLoading, markSessionRead, markAllRead } = useInbox()

    const groupedByAgent = useMemo(() => {
        const map = new Map<string, typeof unreadSessions>()
        for (const session of unreadSessions) {
            const list = map.get(session.agentId) ?? []
            list.push(session)
            map.set(session.agentId, list)
        }
        return Array.from(map.entries())
    }, [unreadSessions])

    const openSession = (agentId: string, sessionId: string) => {
        markSessionRead(agentId, sessionId)
        navigate(`/chat?sessionId=${sessionId}&agent=${agentId}`)
    }

    return (
        <div className="page-container inbox-page">
            <header className="page-header">
                <h1 className="page-title">Inbox</h1>
                <p className="page-subtitle">Unread scheduled runs that need your attention.</p>
            </header>

            <div className="inbox-toolbar">
                <div className="inbox-count">{unreadCount} unread</div>
                <div className="inbox-toolbar-actions">
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={markAllRead}
                        disabled={unreadCount === 0}
                    >
                        Mark all read
                    </button>
                </div>
            </div>

            {isLoading ? (
                <div className="empty-state">
                    <h3 className="empty-state-title">Loading inbox...</h3>
                </div>
            ) : unreadSessions.length === 0 ? (
                <div className="empty-state">
                    <h3 className="empty-state-title">Inbox is clear</h3>
                    <p className="empty-state-description">No unread scheduled sessions.</p>
                </div>
            ) : (
                <div className="inbox-groups">
                    {groupedByAgent.map(([agentId, sessions]) => (
                        <section key={agentId} className="inbox-group">
                            <h3 className="inbox-group-title">{agentId}</h3>
                            <div className="inbox-list">
                                {sessions.map(session => (
                                    <div key={`${session.agentId}:${session.id}`} className="inbox-item">
                                        <div className="inbox-item-main">
                                            <div className="inbox-item-title">{session.name || session.id}</div>
                                            <div className="inbox-item-meta">
                                                <span className="session-type-badge scheduled">UNREAD</span>
                                                {session.schedule_id && <span>Schedule: {session.schedule_id}</span>}
                                                <span>{new Date(session.updated_at || session.created_at).toLocaleString()}</span>
                                                {session.message_count !== undefined && <span>{session.message_count} messages</span>}
                                            </div>
                                        </div>
                                        <div className="inbox-item-actions">
                                            <button
                                                type="button"
                                                className="btn btn-secondary"
                                                onClick={() => markSessionRead(agentId, session.id)}
                                            >
                                                Dismiss
                                            </button>
                                            <button
                                                type="button"
                                                className="btn btn-primary"
                                                onClick={() => openSession(agentId, session.id)}
                                            >
                                                Open
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </section>
                    ))}
                </div>
            )}
        </div>
    )
}
