import { useEffect, useMemo, useRef, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useGoosed } from '../../../platform/providers/GoosedContext'
import { useChat, convertBackendMessage } from '../../../platform/chat/useChat'
import MessageList from '../../../platform/chat/MessageList'
import ChatInput from '../../../platform/chat/ChatInput'
import { A2AProgressContext } from '../../../platform/chat/a2aProgress'

interface ThreadMainConversationProps {
    sessionId: string
    agentId: string
}

/**
 * Column B of the Thread entry: the main conversation. Resumes the binding's session (reusing the platform chat
 * primitives) and lets the FO lead reply in place. This is a fixed-session resume + reply — no agent switching,
 * no new-session creation (PRD §13.2).
 */
export default function ThreadMainConversation({ sessionId, agentId }: ThreadMainConversationProps) {
    const { t } = useTranslation()
    const { getClient } = useGoosed()
    const client = useMemo(() => getClient(agentId), [getClient, agentId])
    const {
        messages, chatState, isLoading, outputFilesEvent, a2aProgress, sendMessage, stopMessage, setInitialMessages,
    } = useChat({ sessionId, client })
    const [error, setError] = useState<string | null>(null)
    const scrollRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        let cancelled = false
        setError(null)
        const resume = async () => {
            try {
                const result = await client.resumeSession(sessionId)
                if (cancelled) return
                const conversation = result.session?.conversation
                if (Array.isArray(conversation)) {
                    setInitialMessages(conversation.map(message =>
                        convertBackendMessage(message as Record<string, unknown>)))
                }
            } catch (err) {
                if (!cancelled) setError(err instanceof Error ? err.message : String(err))
            }
        }
        void resume()
        return () => {
            cancelled = true
        }
    }, [sessionId, client, setInitialMessages])

    return (
        <div className="thread-conversation">
            {error && <div className="thread-conversation-error">{t('thread.resumeError', { error })}</div>}
            <div className="thread-messages" ref={scrollRef}>
                <A2AProgressContext.Provider value={a2aProgress}>
                    <MessageList
                        messages={messages}
                        isLoading={isLoading}
                        chatState={chatState}
                        agentId={agentId}
                        sessionId={sessionId}
                        outputFilesEvent={outputFilesEvent}
                        scrollContainerRef={scrollRef}
                    />
                </A2AProgressContext.Provider>
            </div>
            <ChatInput
                onSubmit={sendMessage}
                onStopGeneration={() => { void stopMessage() }}
                isGenerating={isLoading}
                showAgentSelector={false}
                placeholder={t('thread.replyPlaceholder')}
            />
        </div>
    )
}
