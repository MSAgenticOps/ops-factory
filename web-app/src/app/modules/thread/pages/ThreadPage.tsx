import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useThreadUnread, type ThreadDescriptor } from '../../../platform/providers/ThreadUnreadContext'
import { usePagePanel } from '../../../platform/providers/PagePanelContext'
import ThreadMainConversation from '../components/ThreadMainConversation'
import ProactivePushTimeline from '../components/ProactivePushTimeline'
import ProactiveRunModal from '../components/ProactiveRunModal'
import '../styles/thread.css'

/**
 * The Assistant entry (PRD §13): an IM conversation viewed per-counterpart, reusing the existing framework — the
 * main conversation fills the main column via the shared {@link ThreadMainConversation}, and the proactive-push
 * timeline mounts into the shared right panel (`RightPanelHost`) as a narrow `thread` mode. A header breadcrumb
 * dropdown switches assistant/copilot; clicking a push card opens a read-only run modal.
 */
export default function ThreadPage() {
    const { t } = useTranslation()
    const { threads, followupsByKey, markThreadRead, isLoading } = useThreadUnread()
    const { setPagePanel } = usePagePanel()
    const [selectedKey, setSelectedKey] = useState<string | null>(null)
    const [isPanelOpen, setIsPanelOpen] = useState(true)
    const [openIndex, setOpenIndex] = useState<number | null>(null)

    const selected: ThreadDescriptor | undefined = useMemo(() => {
        if (threads.length === 0) return undefined
        return threads.find(thread => thread.key === selectedKey) ?? threads[0]
    }, [threads, selectedKey])

    const selectedKeyForEffect = selected?.key
    // Viewing an assistant marks its proactive pushes read; re-fire only on a real selection change.
    useEffect(() => {
        if (selectedKeyForEffect) markThreadRead(selectedKeyForEffect)
    }, [selectedKeyForEffect, markThreadRead])

    // Memoized so an empty thread yields a stable [] (a fresh [] each render would re-fire the panel effect).
    const records = useMemo(
        () => (selected ? (followupsByKey[selected.key] ?? []) : []),
        [selected, followupsByKey],
    )

    // Mount the push timeline into the shared right panel (narrow `thread` mode); clear it on unmount / collapse.
    useEffect(() => {
        if (!selected || !isPanelOpen) {
            setPagePanel(null)
            return undefined
        }
        setPagePanel({
            mode: 'thread',
            title: t('thread.pushesTitle'),
            content: <ProactivePushTimeline records={records} onOpen={setOpenIndex} />,
            onClose: () => setIsPanelOpen(false),
        })
        return () => setPagePanel(null)
    }, [selected, records, isPanelOpen, t, setPagePanel])

    if (threads.length === 0) {
        return (
            <div className="thread-empty">
                <div className="thread-empty-card">
                    <h2>{t('thread.title')}</h2>
                    <p>{isLoading ? t('thread.loading') : t('thread.noThreads')}</p>
                </div>
            </div>
        )
    }

    const header = (
        <div className="thread-breadcrumb">
            <span className="thread-breadcrumb-label">{t('thread.title')}</span>
            <span className="thread-breadcrumb-sep">›</span>
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
            <button
                type="button"
                className="thread-rail-toggle"
                onClick={() => setIsPanelOpen(open => !open)}
            >
                {isPanelOpen ? t('thread.hidePushes') : t('thread.showPushes')}
            </button>
        </div>
    )

    return (
        <>
            {selected && (
                <ThreadMainConversation header={header} sessionId={selected.sessionId} agentId={selected.agentId} />
            )}
            {openIndex !== null && selected && records[openIndex] && (
                <ProactiveRunModal
                    records={records}
                    index={openIndex}
                    agentId={selected.agentId}
                    onIndexChange={setOpenIndex}
                    onClose={() => setOpenIndex(null)}
                />
            )}
        </>
    )
}

function channelLabel(type: string, t: (key: string) => string): string {
    if (type === 'wechat') return t('channels.type_wechat')
    if (type === 'whatsapp') return t('channels.type_whatsapp')
    return type
}
