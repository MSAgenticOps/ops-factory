import { useTranslation } from 'react-i18next'
import ListCard from '../../../platform/ui/list/ListCard'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import { formatRunTime, previewOf, scheduleLabel } from '../threadFormat'

interface ProactivePushTimelineProps {
    records: ThreadFollowup[]
    onOpen: (index: number) => void
}

/**
 * Body of the Assistant page's right panel: the proactive-push timeline. One {@link ListCard} per delivered run
 * (newest-first), so it matches the History session-list visual. The card leads with the delivered content
 * (markdown-stripped preview); the schedule (friendly name) and time sit below as muted metadata. The whole
 * card is a real `<button>` (keyboard-accessible) that opens the brief in the read-only modal.
 */
export default function ProactivePushTimeline({ records, onOpen }: ProactivePushTimelineProps) {
    const { t } = useTranslation()
    if (records.length === 0) {
        return <div className="thread-timeline-empty">{t('thread.noPushes')}</div>
    }
    return (
        <ul className="thread-timeline-list">
            {records.map((record, index) => (
                <li key={`${record.sessionId}-${record.time}`}>
                    <ListCard className="thread-push-card">
                        <button type="button" className="thread-push-button" onClick={() => onOpen(index)}>
                            <span className="thread-push-preview">{previewOf(record.summary)}</span>
                            <span className="thread-push-meta">
                                <span className="thread-push-schedule">{scheduleLabel(record.scheduleId, t)}</span>
                                <span className="thread-push-time">{formatRunTime(record.time)}</span>
                            </span>
                        </button>
                    </ListCard>
                </li>
            ))}
        </ul>
    )
}
