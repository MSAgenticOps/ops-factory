import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useThreadUnread, type ThreadDescriptor } from '../../../platform/providers/ThreadUnreadContext'
import ThreadMainConversation from '../components/ThreadMainConversation'
import ProactivePushTimeline from '../components/ProactivePushTimeline'
import ProactiveRunModal from '../components/ProactiveRunModal'
import '../styles/thread.css'

/**
 * The Thread entry (PRD §13): an IM conversation viewed per-counterpart. A header breadcrumb dropdown switches
 * thread/copilot; column B is the main conversation (resume + reply); column C is the proactive-push timeline;
 * a card opens a read-only run modal. v1: one channel per user, no cross-channel contact merge.
 */
export default function ThreadPage() {
    const { t } = useTranslation()
    const { threads, followupsByKey, markThreadRead, isLoading } = useThreadUnread()
    const [selectedKey, setSelectedKey] = useState<string | null>(null)
    const [isRailOpen, setIsRailOpen] = useState(true)
    const [openIndex, setOpenIndex] = useState<number | null>(null)

    const selected: ThreadDescriptor | undefined = useMemo(() => {
        if (threads.length === 0) return undefined
        return threads.find(thread => thread.key === selectedKey) ?? threads[0]
    }, [threads, selectedKey])

    const selectedKeyForEffect = selected?.key
    // Viewing a thread marks its proactive pushes read (drops the badge); re-fire only on a real selection change.
    useEffect(() => {
        if (selectedKeyForEffect) markThreadRead(selectedKeyForEffect)
    }, [selectedKeyForEffect, markThreadRead])

    const records = selected ? (followupsByKey[selected.key] ?? []) : []

    if (threads.length === 0) {
        return (
            <div className="thread-page thread-page-empty">
                <div className="thread-empty-card">
                    <h2>{t('thread.title')}</h2>
                    <p>{isLoading ? t('thread.loading') : t('thread.noThreads')}</p>
                </div>
            </div>
        )
    }

    return (
        <div className="thread-page">
            <header className="thread-header">
                <span className="thread-breadcrumb-label">{t('thread.title')}</span>
                <span className="thread-breadcrumb-sep">›</span>
                {threads.length > 1 ? (
                    <select
                        className="thread-switcher"
                        value={selected?.key}
                        onChange={event => setSelectedKey(event.target.value)}
                        aria-label={t('thread.switcherLabel')}
                    >
                        {threads.map(thread => (
                            <option key={thread.key} value={thread.key}>
                                {thread.agentName} · {channelLabel(thread.channelType, t)}
                            </option>
                        ))}
                    </select>
                ) : (
                    <span className="thread-title">
                        {selected?.agentName} · {channelLabel(selected?.channelType ?? '', t)}
                    </span>
                )}
                <button type="button" className="thread-rail-toggle" onClick={() => setIsRailOpen(open => !open)}>
                    {isRailOpen ? t('thread.hidePushes') : t('thread.showPushes')}
                </button>
            </header>

            <div className={`thread-body ${isRailOpen ? '' : 'thread-rail-collapsed'}`}>
                <section className="thread-main">
                    {selected && selected.sessionId
                        ? <ThreadMainConversation sessionId={selected.sessionId} agentId={selected.agentId} />
                        : <div className="thread-main-empty">{t('thread.noConversation')}</div>}
                </section>
                {isRailOpen && (
                    <aside className="thread-rail">
                        <ProactivePushTimeline records={records} onOpen={setOpenIndex} />
                    </aside>
                )}
            </div>

            {openIndex !== null && selected && records[openIndex] && (
                <ProactiveRunModal
                    records={records}
                    index={openIndex}
                    agentId={selected.agentId}
                    onIndexChange={setOpenIndex}
                    onClose={() => setOpenIndex(null)}
                />
            )}
        </div>
    )
}

function channelLabel(type: string, t: (key: string) => string): string {
    if (type === 'wechat') return t('channels.type_wechat')
    if (type === 'whatsapp') return t('channels.type_whatsapp')
    return type
}
