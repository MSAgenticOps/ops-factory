import { useEffect, useMemo, useRef, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import type { ImageData } from '@goosed/sdk'
import { useGoosed } from '../../../platform/providers/GoosedContext'
import { useChat, convertBackendMessage } from '../../../platform/chat/useChat'
import { useConversationScroll } from '../../../platform/chat/useConversationScroll'
import ConversationShell from '../../../platform/chat/ConversationShell'
import MessageList from '../../../platform/chat/MessageList'
import ChatInput from '../../../platform/chat/ChatInput'
import { A2AProgressContext } from '../../../platform/chat/a2aProgress'
import type { AttachedFile, SelectedSkill } from '../../../../types/message'

// Product rule (mirrors the chat module): only the orchestrator ("digital human") delegates via @mention;
// tool-agents are call targets only. Duplicated here as a literal because modules must not import across boundaries.
const A2A_INITIATOR_AGENT_ID = 'fo-copilot'

interface ThreadMainConversationProps {
    sessionId: string
    agentId: string
    /** 48px header content (the assistant breadcrumb + switcher), rendered by the shared conversation shell. */
    header: ReactNode
}

interface ModelInfo {
    provider: string
    model: string
}

/**
 * Column B of the Assistant page: the main conversation. A faithful replica of the chat resume page — the shared
 * {@link ConversationShell} (48px header, centered messages, full-width composer), the full composer toolbar
 * (read-only model badge, token usage, file upload, skill picker, quick-continue), @mention delegation when the
 * bound agent is the orchestrator, and the shared scroll affordances (jump-to-bottom + send-to-top anchoring via
 * {@link useConversationScroll}). It resumes the binding's session so the FO lead can reply in place. The one
 * deliberate difference from the chat page is that the agent picker is suppressed (`showAgentSelector={false}`):
 * the assistant identity is owned by the header switcher, and B is a fixed-session view (no agent switching, no
 * new-session creation).
 */
export default function ThreadMainConversation({ sessionId, agentId, header }: ThreadMainConversationProps) {
    const { t } = useTranslation()
    const navigate = useNavigate()
    const { getClient, agents, isConnected } = useGoosed()
    const client = useMemo(() => getClient(agentId), [getClient, agentId])
    const {
        messages, chatState, isLoading, tokenState, outputFilesEvent, a2aProgress,
        sendMessage, stopMessage, setInitialMessages,
    } = useChat({ sessionId: sessionId || null, client })
    const [error, setError] = useState<string | null>(null)
    const [modelInfo, setModelInfo] = useState<ModelInfo | null>(null)
    const scrollRef = useRef<HTMLDivElement>(null)

    const { showScrollToBottom, handleJumpToBottom, anchorSentMessage, pendingAnchorId } = useConversationScroll({
        scrollContainerRef: scrollRef,
        messages,
        isLoading,
        sessionId: sessionId || null,
    })

    const agentSkills = useMemo(
        () => agents.find(agent => agent.id === agentId)?.skills || [],
        [agents, agentId],
    )

    // Agents this conversation may delegate to via @mention — only when the bound agent is the orchestrator.
    const delegationAgents = useMemo(
        () => (agentId === A2A_INITIATOR_AGENT_ID ? agents.filter(agent => agent.id !== agentId) : []),
        [agentId, agents],
    )

    // Resume the bound session and load its history into the message list.
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

    // Read-only provider/model badge for the composer toolbar (same source as the chat page).
    useEffect(() => {
        if (!isConnected) return undefined
        let cancelled = false
        const load = async () => {
            try {
                const info = await client.systemInfo()
                if (!cancelled && info.provider && info.model) {
                    setModelInfo({ provider: info.provider, model: info.model })
                }
            } catch {
                // best-effort: the model badge is informational, never block the conversation on it
            }
        }
        void load()
        return () => {
            cancelled = true
        }
        // client is memoized per agentId, so it already changes with the agent — agentId in deps would be redundant.
    }, [client, isConnected])

    const handleSend = (
        text: string,
        images?: ImageData[],
        attachedFiles?: AttachedFile[],
        selectedSkill?: SelectedSkill,
    ) => {
        const messageId = sendMessage(text, images, attachedFiles, selectedSkill)
        if (messageId) anchorSentMessage(messageId)
    }

    const handleUploadFile = async (file: File): Promise<{ path: string }> => {
        const result = await client.uploadFile(file, sessionId)
        return { path: result.path }
    }

    const handleRetry = () => {
        for (let i = messages.length - 1; i >= 0; i--) {
            const message = messages[i]
            if (message.role !== 'user') continue
            const retryPayload = message.metadata?.retryPayload
            if (retryPayload) {
                const messageId = sendMessage(
                    retryPayload.text,
                    retryPayload.images,
                    retryPayload.attachedFiles,
                    retryPayload.selectedSkill,
                )
                if (messageId) anchorSentMessage(messageId)
                return
            }
            const textContent = message.content.find(part => part.type === 'text')
            const text = textContent && 'text' in textContent ? textContent.text : undefined
            if (text) {
                const messageId = sendMessage(text, undefined, undefined, message.metadata?.selectedSkill)
                if (messageId) anchorSentMessage(messageId)
                return
            }
        }
    }

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
                <>
                    {showScrollToBottom && (
                        <div className="chat-scroll-bottom-action">
                            <button
                                type="button"
                                className="chat-scroll-bottom-button"
                                onClick={handleJumpToBottom}
                                aria-label={t('chat.jumpToBottom')}
                                title={t('chat.jumpToBottom')}
                            >
                                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="18" height="18" aria-hidden="true">
                                    <path d="M12 5v12" />
                                    <path d="m6 13 6 6 6-6" />
                                </svg>
                            </button>
                        </div>
                    )}
                    <ChatInput
                        onSubmit={handleSend}
                        onUploadFile={handleUploadFile}
                        onStopGeneration={() => { void stopMessage() }}
                        disabled={!isConnected || isLoading}
                        isGenerating={isLoading}
                        canQuickContinue={messages.length > 0}
                        placeholder={t('thread.replyPlaceholder')}
                        showAgentSelector={false}
                        modelInfo={modelInfo}
                        tokenState={tokenState}
                        skills={agentSkills}
                        agents={delegationAgents}
                        onBrowseSkillMarket={() => navigate('/skill-market')}
                        autoFocus
                    />
                </>
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
                    onRetry={handleRetry}
                    onCancelRequest={() => { void stopMessage() }}
                    showAnchorSpacer={!!pendingAnchorId}
                    scrollContainerRef={scrollRef}
                />
            </A2AProgressContext.Provider>
        </ConversationShell>
    )
}
