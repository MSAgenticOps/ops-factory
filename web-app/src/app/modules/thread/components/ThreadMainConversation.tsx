import { useEffect, useMemo, useRef, useState, type ReactNode } from 'react'
import { useTranslation } from 'react-i18next'
import { useGoosed } from '../../../platform/providers/GoosedContext'
import { useChat, convertBackendMessage } from '../../../platform/chat/useChat'
import ConversationShell from '../../../platform/chat/ConversationShell'
import MessageList from '../../../platform/chat/MessageList'
import ChatInput from '../../../platform/chat/ChatInput'
import { A2AProgressContext } from '../../../platform/chat/a2aProgress'

interface ThreadMainConversationProps {
    sessionId: string
    agentId: string
    /** 48px header content (the assistant breadcrumb + switcher), rendered by the shared conversation shell. */
    header: ReactNode
}

/**
 * Column B of the Assistant page: the main conversation. Reuses the shared {@link ConversationShell} (same layout
 * as the chat page — 48px header, centered messages, fixed full-width composer) and resumes the binding's session
 * so the FO lead can reply in place. Fixed-session resume + reply; no agent switching, no new-session creation.
 */
export default function ThreadMainConversation({ sessionId, agentId, header }: ThreadMainConversationProps) {
    const { t } = useTranslation()
    const { getClient } = useGoosed()
    const client = useMemo(() => getClient(agentId), [getClient, agentId])
    const {
        messages, chatState, isLoading, outputFilesEvent, a2aProgress, sendMessage, stopMessage, setInitialMessages,
    } = useChat({ sessionId: sessionId || null, client })
    const [error, setError] = useState<string | null>(null)
    const scrollRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        if (!sessionId) return undefined
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

    if (!sessionId) {
        return (
            <ConversationShell header={header}>
                <div className="thread-main-empty">{t('thread.noConversation')}</div>
            </ConversationShell>
        )
    }

    return (
        <ConversationShell
            header={header}
            scrollRef={scrollRef}
            composer={(
                <ChatInput
                    onSubmit={sendMessage}
                    onStopGeneration={() => { void stopMessage() }}
                    isGenerating={isLoading}
                    showAgentSelector={false}
                    placeholder={t('thread.replyPlaceholder')}
                />
            )}
        >
            {error && <div className="thread-conversation-error">{t('thread.resumeError', { error })}</div>}
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
        </ConversationShell>
    )
}
