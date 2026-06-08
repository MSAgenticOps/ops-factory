import { useTranslation } from 'react-i18next'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import { formatRunTime, previewOf } from '../threadFormat'

interface ProactivePushTimelineProps {
    records: ThreadFollowup[]
    onOpen: (index: number) => void
}

/**
 * Body of the Assistant page's right panel: the proactive-push timeline. One uniform card per delivered run
 * (every schedule looks the same — content is not parsed, PRD §13.2), newest-first. Clicking a card opens it in
 * the read-only modal. The panel's title lives in the right-panel header, so this renders only the list.
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
                        <div className="thread-card-head">
                            <span className="thread-card-schedule">{record.scheduleId}</span>
                            <span className="thread-card-time">{formatRunTime(record.time)}</span>
                        </div>
                        <div className="thread-card-preview">{previewOf(record.summary)}</div>
                    </button>
                </li>
            ))}
        </ul>
    )
}
