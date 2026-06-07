import { useTranslation } from 'react-i18next'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import { formatRunTime, previewOf } from '../threadFormat'

interface ProactivePushTimelineProps {
    records: ThreadFollowup[]
    onOpen: (index: number) => void
}

/**
 * Column C of the Thread entry: the proactive-push timeline. One uniform card per delivered run (every schedule
 * looks the same — content is not parsed, PRD §13.2), newest-first. Clicking a card opens it in the read-only
 * modal; the card itself only shows a one-line preview.
 */
export default function ProactivePushTimeline({ records, onOpen }: ProactivePushTimelineProps) {
    const { t } = useTranslation()
    return (
        <div className="thread-timeline">
            <h3 className="thread-timeline-title">{t('thread.pushesTitle')}</h3>
            {records.length === 0 ? (
                <div className="thread-timeline-empty">{t('thread.noPushes')}</div>
            ) : (
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
            )}
        </div>
    )
}

