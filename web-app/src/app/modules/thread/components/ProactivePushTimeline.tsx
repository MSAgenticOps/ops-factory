import { useTranslation } from 'react-i18next'
import ListCard from '../../../platform/ui/list/ListCard'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import { formatRunTime, previewOf, scheduleAccent, scheduleLabel, snippetOf } from '../threadFormat'

interface ProactivePushTimelineProps {
    records: ThreadFollowup[]
    onOpen: (index: number) => void
}

/**
 * Body of the Assistant page's right panel: the proactive-push timeline. One {@link ListCard} per delivered run
 * (newest-first). Each card reads like an inbox item — a header row (schedule type dot + name, run time), the
 * brief's lead line as the title, and a muted snippet — so it sits in the same visual family as Inbox/History.
 * The whole card is a real `<button>` (keyboard-accessible) that opens the brief in the read-only modal.
 */
export default function ProactivePushTimeline({ records, onOpen }: ProactivePushTimelineProps) {
    const { t } = useTranslation()
    if (records.length === 0) {
        return <div className="thread-timeline-empty">{t('thread.noPushes')}</div>
    }
    return (
        <ul className="thread-timeline-list">
            {records.map((record, index) => {
                const snippet = snippetOf(record.summary)
                return (
                    <li key={`${record.sessionId}-${record.time}`}>
                        <ListCard className="thread-push-card">
                            <button type="button" className="thread-push-button" onClick={() => onOpen(index)}>
                                <span className="thread-push-head">
                                    <span className="thread-push-source">
                                        <span className={`thread-push-dot thread-push-dot-${scheduleAccent(record.scheduleId)}`} aria-hidden="true" />
                                        <span className="thread-push-schedule">{scheduleLabel(record.scheduleId, t)}</span>
                                    </span>
                                    <span className="thread-push-time">{formatRunTime(record.time)}</span>
                                </span>
                                <span className="thread-push-title">{previewOf(record.summary)}</span>
                                {snippet && <span className="thread-push-snippet">{snippet}</span>}
                            </button>
                        </ListCard>
                    </li>
                )
            })}
        </ul>
    )
}
