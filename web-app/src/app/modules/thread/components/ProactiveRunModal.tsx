import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import DetailDialog from '../../../platform/ui/primitives/DetailDialog'
import MessageList from '../../../platform/chat/MessageList'
import { useGoosed } from '../../../platform/providers/GoosedContext'
import { convertBackendMessage } from '../../../platform/chat/useChat'
import { buildChatSessionState } from '../../../platform/chat/chatRouteState'
import type { ThreadFollowup } from '../../../platform/providers/ThreadUnreadContext'
import type { ChatMessage } from '../../../../types/message'
import { formatRunTime } from '../threadFormat'

interface ProactiveRunModalProps {
    records: ThreadFollowup[]
    index: number
    agentId: string
    onIndexChange: (index: number) => void
    onClose: () => void
}

/**
 * The read-only viewer for one proactive run (PRD §13.2): resumes the run's session and renders its transcript
 * with no composer. ←/→ step through the timeline without closing; "goto session" opens the full chat view; the
 * dialog is easy to dismiss back to the timeline.
 */
export default function ProactiveRunModal({ records, index, agentId, onIndexChange, onClose }: ProactiveRunModalProps) {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const { getClient } = useGoosed()
    const [messages, setMessages] = useState<ChatMessage[]>([])
    const [isLoading, setIsLoading] = useState(false)
    const record = records[index]
    const recordSessionId = record?.sessionId

    useEffect(() => {
        if (!recordSessionId) return undefined
        let cancelled = false
        setIsLoading(true)
        setMessages([])
        const load = async () => {
            try {
                const result = await getClient(agentId).resumeSession(recordSessionId)
                if (cancelled) return
                const conversation = result.session?.conversation
                setMessages(Array.isArray(conversation)
                    ? conversation.map(message => convertBackendMessage(message as Record<string, unknown>))
                    : [])
            } catch {
                if (!cancelled) setMessages([])
            } finally {
                if (!cancelled) setIsLoading(false)
            }
        }
        void load()
        return () => {
            cancelled = true
        }
    }, [getClient, agentId, recordSessionId])

    // The records array can shrink under an open modal (a background poll); guard the index.
    if (!record) return null

    const footer = (
        <div className="thread-modal-footer">
            <button type="button" disabled={index <= 0} onClick={() => onIndexChange(index - 1)}>
                {t('thread.prev')}
            </button>
            <button type="button" disabled={index >= records.length - 1} onClick={() => onIndexChange(index + 1)}>
                {t('thread.next')}
            </button>
            <span className="thread-modal-pos">{index + 1} / {records.length}</span>
            <span className="thread-modal-spacer" />
            <button
                type="button"
                onClick={() => navigate('/chat', { state: buildChatSessionState(record.sessionId, agentId) })}
            >
                {t('thread.gotoSession')}
            </button>
            <button type="button" onClick={onClose}>{t('common.close')}</button>
        </div>
    )

    return (
        <DetailDialog
            title={`${record.scheduleId} · ${formatRunTime(record.time)}`}
            onClose={onClose}
            variant="wide"
            footer={footer}
        >
            <MessageList messages={messages} isLoading={isLoading} agentId={agentId} sessionId={record.sessionId} />
        </DetailDialog>
    )
}
