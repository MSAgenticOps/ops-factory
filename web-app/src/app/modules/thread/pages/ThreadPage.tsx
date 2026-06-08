import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useThreadUnread, type ThreadDescriptor } from '../../../platform/providers/ThreadUnreadContext'
import { usePagePanel } from '../../../platform/providers/PagePanelContext'
import Button from '../../../platform/ui/primitives/Button'
import ThreadMainConversation from '../components/ThreadMainConversation'
import ProactivePushTimeline from '../components/ProactivePushTimeline'
import ProactiveRunModal from '../components/ProactiveRunModal'
import ThreadSwitcher from '../components/ThreadSwitcher'
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
        <div className="thread-header-bar">
            <ThreadSwitcher threads={threads} selectedKey={selected?.key} onSelect={setSelectedKey} />
            {!isPanelOpen && (
                <Button
                    variant="ghost"
                    size="sm"
                    iconOnly
                    className="thread-panel-show"
                    onClick={() => setIsPanelOpen(true)}
                    aria-label={t('thread.showPushes')}
                    title={t('thread.showPushes')}
                >
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" width="16" height="16">
                        <rect x="3" y="4" width="18" height="16" rx="2" />
                        <line x1="15" y1="4" x2="15" y2="20" />
                    </svg>
                </Button>
            )}
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
