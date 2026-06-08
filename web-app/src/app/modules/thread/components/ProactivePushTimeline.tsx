import { useTranslation } from 'react-i18next'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import { formatRunTime, previewOf, scheduleLabel } from '../threadFormat'

interface ProactivePushTimelineProps {
    records: ThreadFollowup[]
    onOpen: (index: number) => void
}

/**
 * Body of the Assistant page's right panel: the proactive-push timeline. One card per delivered run, newest-first.
 * The card leads with the delivered content (markdown-stripped preview); the schedule (friendly name) and time
 * sit below as muted metadata. Clicking opens the brief in the read-only modal.
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
                    <button type="button" className="thread-card" onClick={() => onOpen(index)}>
                        <div className="thread-card-preview">{previewOf(record.summary)}</div>
                        <div className="thread-card-meta">
                            <span className="thread-card-schedule">{scheduleLabel(record.scheduleId, t)}</span>
                            <span className="thread-card-time">{formatRunTime(record.time)}</span>
                        </div>
                    </button>
                </li>
            ))}
        </ul>
    )
}
