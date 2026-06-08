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
 * markdown-formatted) — the content the FO lead actually wants to read. The full run transcript (recipe /
 * thinking / tool calls) is the audit path behind "view run". ←/→ step the timeline without closing.
 */
export default function ProactiveRunModal({ records, index, agentId, onIndexChange, onClose }: ProactiveRunModalProps) {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const record = records[index]
    // The records array can shrink under an open modal (a background poll); guard the index.
    if (!record) return null

    const footer = (
        <div className="thread-modal-footer">
            <Button variant="ghost" size="sm" disabled={index <= 0} onClick={() => onIndexChange(index - 1)}>
                {t('thread.prev')}
            </Button>
            <Button variant="ghost" size="sm" disabled={index >= records.length - 1}
                onClick={() => onIndexChange(index + 1)}>
                {t('thread.next')}
            </Button>
            <span className="thread-modal-pos">{index + 1} / {records.length}</span>
            <span className="thread-modal-spacer" />
            <Button
                variant="secondary"
                size="sm"
                onClick={() => navigate('/chat', { state: buildChatSessionState(record.sessionId, agentId) })}
            >
                {t('thread.viewRun')}
            </Button>
            <Button variant="ghost" size="sm" onClick={onClose}>{t('common.close')}</Button>
        </div>
    )

    return (
        <DetailDialog
            title={`${scheduleLabel(record.scheduleId, t)} · ${formatRunTime(record.time)}`}
            onClose={onClose}
            variant="wide"
            footer={footer}
        >
            <div className="thread-brief">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{record.summary}</ReactMarkdown>
            </div>
        </DetailDialog>
    )
}
