import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import DetailDialog from '../../../platform/ui/primitives/DetailDialog'
import Button from '../../../platform/ui/primitives/Button'
import { buildChatSessionState } from '../../../platform/chat/chatRouteState'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import { formatRunTime, scheduleLabel } from '../threadFormat'

interface ProactiveRunModalProps {
    records: ThreadFollowup[]
    index: number
    agentId: string
    onIndexChange: (index: number) => void
    onClose: () => void
}

/**
 * Read-only viewer for one proactive push (PRD §13.2): renders the **delivered brief** (the follow-up's summary,
 * markdown-formatted) in the platform's large (`wide`) modal at a **stable height** (only the body scrolls, so
 * paging short↔long briefs doesn't resize the frame). Header = schedule name + muted run-time subtitle. Footer =
 * the ←/→ pager on the left, neutral equal-weight actions on the right ("open session" navigates to that run's
 * chat session; "close"). Neither action is primary — closing is at least as likely.
 */
export default function ProactiveRunModal({ records, index, agentId, onIndexChange, onClose }: ProactiveRunModalProps) {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const record = records[index]
    // The records array can shrink under an open modal (a background poll); guard the index.
    if (!record) return null

    const title = (
        <span className="thread-run-title">
            <span className="thread-run-title-main">{scheduleLabel(record.scheduleId, t)}</span>
            <span className="thread-run-title-sub">{formatRunTime(record.time)}</span>
        </span>
    )

    const footer = (
        <div className="thread-run-footer">
            <div className="thread-run-pager">
                {records.length > 1 && (
                    <>
                        <Button
                            variant="ghost"
                            size="sm"
                            iconOnly
                            disabled={index <= 0}
                            onClick={() => onIndexChange(index - 1)}
                            aria-label={t('thread.prev')}
                            title={t('thread.prev')}
                        >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
                                <polyline points="15 18 9 12 15 6" />
                            </svg>
                        </Button>
                        <span className="thread-run-pager-pos">{index + 1} / {records.length}</span>
                        <Button
                            variant="ghost"
                            size="sm"
                            iconOnly
                            disabled={index >= records.length - 1}
                            onClick={() => onIndexChange(index + 1)}
                            aria-label={t('thread.next')}
                            title={t('thread.next')}
                        >
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16" aria-hidden="true">
                                <polyline points="9 18 15 12 9 6" />
                            </svg>
                        </Button>
                    </>
                )}
            </div>
            <div className="thread-run-actions">
                <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => navigate('/chat', { state: buildChatSessionState(record.sessionId, agentId) })}
                >
                    {t('thread.openSession')}
                </Button>
                <Button variant="secondary" size="sm" onClick={onClose}>{t('common.close')}</Button>
            </div>
        </div>
    )

    return (
        <DetailDialog title={title} onClose={onClose} variant="wide" className="thread-run-modal" footer={footer}>
            <div className="thread-brief">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{record.summary}</ReactMarkdown>
            </div>
        </DetailDialog>
    )
}
