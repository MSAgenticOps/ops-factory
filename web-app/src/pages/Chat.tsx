import { useState, useEffect, useCallback } from 'react'
import { useSearchParams, useLocation, useNavigate } from 'react-router-dom'
import { useGoosed } from '../contexts/GoosedContext'
import { useChat, convertBackendMessage } from '../hooks/useChat'
import MessageList from '../components/MessageList'
import ChatInput from '../components/ChatInput'
import { getDefaultAgent, getAgentWorkingDir, getAvailableAgents } from '../components/AgentSelector'
import type { Session } from '@goosed/sdk'

interface LocationState {
    initialMessage?: string
}

interface ModelInfo {
    provider: string
    model: string
}

// Helper to detect agent from working directory
function detectAgentFromWorkingDir(workingDir: string): string {
    const agents = getAvailableAgents()
    for (const agent of agents) {
        if (workingDir.includes(agent.id)) {
            return agent.id
        }
    }
    return getDefaultAgent()
}

export default function Chat() {
    const [searchParams] = useSearchParams()
    const location = useLocation()
    const navigate = useNavigate()
    const { client, isConnected } = useGoosed()

    const sessionId = searchParams.get('sessionId')
    const [session, setSession] = useState<Session | null>(null)
    const [isInitializing, setIsInitializing] = useState(true)
    const [initError, setInitError] = useState<string | null>(null)
    const [selectedAgent, setSelectedAgent] = useState(getDefaultAgent())
    const [isCreatingSession, setIsCreatingSession] = useState(false)
    const [modelInfo, setModelInfo] = useState<ModelInfo | null>(null)

    const { messages, isLoading, error, sendMessage, clearMessages, setInitialMessages } = useChat({
        sessionId
    })

    // Get initial message from navigation state
    const locationState = location.state as LocationState | null
    const initialMessage = locationState?.initialMessage

    // Fetch model info from system_info
    useEffect(() => {
        const fetchModelInfo = async () => {
            if (!isConnected) return
            try {
                const systemInfo = await client.systemInfo()
                if (systemInfo.provider && systemInfo.model) {
                    setModelInfo({
                        provider: systemInfo.provider,
                        model: systemInfo.model
                    })
                }
            } catch (err) {
                console.error('Failed to fetch model info:', err)
            }
        }
        fetchModelInfo()
    }, [client, isConnected])

    // Create session with specified agent's working directory
    const createSessionWithAgent = useCallback(async (agentId: string) => {
        setIsCreatingSession(true)
        try {
            const workingDir = getAgentWorkingDir(agentId)
            const newSession = await client.startSession(workingDir)
            await client.resumeSession(newSession.id)
            setSession(newSession)
            setSelectedAgent(agentId)
            clearMessages()
            navigate(`/chat?sessionId=${newSession.id}`, { replace: true })
            return newSession
        } catch (err) {
            console.error('Failed to create session:', err)
            setInitError(err instanceof Error ? err.message : 'Failed to create session')
            return null
        } finally {
            setIsCreatingSession(false)
        }
    }, [client, clearMessages, navigate])

    // Handle agent change - create new session
    const handleAgentChange = useCallback(async (agentId: string) => {
        if (agentId === selectedAgent) return
        await createSessionWithAgent(agentId)
    }, [selectedAgent, createSessionWithAgent])

    // Initialize session
    useEffect(() => {
        const initSession = async () => {
            if (!isConnected) return

            if (!sessionId) {
                // No session ID, create new session with default working dir
                setIsInitializing(true)
                await createSessionWithAgent(getDefaultAgent())
                setIsInitializing(false)
                return
            }

            setIsInitializing(true)
            setInitError(null)

            try {
                // Get session details
                const sessionDetails = await client.getSession(sessionId)
                setSession(sessionDetails)

                // Detect agent from working directory
                if (sessionDetails.working_dir) {
                    const detectedAgent = detectAgentFromWorkingDir(sessionDetails.working_dir)
                    setSelectedAgent(detectedAgent)
                }

                // Resume session to load model and extensions
                await client.resumeSession(sessionId)

                // Load existing messages from session conversation
                if (sessionDetails.conversation && Array.isArray(sessionDetails.conversation)) {
                    const historyMessages = sessionDetails.conversation.map(msg =>
                        convertBackendMessage(msg as Record<string, unknown>)
                    )
                    setInitialMessages(historyMessages)
                }
            } catch (err) {
                console.error('Failed to initialize session:', err)
                setInitError(err instanceof Error ? err.message : 'Failed to load session')
            } finally {
                setIsInitializing(false)
            }
        }

        initSession()
    }, [client, isConnected, sessionId, setInitialMessages, createSessionWithAgent])

    // Send initial message if provided
    useEffect(() => {
        if (initialMessage && sessionId && !isInitializing && messages.length === 0) {
            sendMessage(initialMessage)
            // Clear the state so it doesn't resend on refresh
            window.history.replaceState({}, document.title)
        }
    }, [initialMessage, sessionId, isInitializing, messages.length, sendMessage])

    const handleSendMessage = useCallback((text: string) => {
        sendMessage(text)
    }, [sendMessage])


    if (isInitializing) {
        return (
            <div className="chat-container">
                <div style={{
                    flex: 1,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}>
                    <div style={{ textAlign: 'center' }}>
                        <div className="loading-spinner" style={{ margin: '0 auto var(--spacing-4)' }} />
                        <p style={{ color: 'var(--color-text-secondary)' }}>Loading session...</p>
                    </div>
                </div>
            </div>
        )
    }

    if (initError) {
        return (
            <div className="chat-container">
                <div style={{
                    flex: 1,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}>
                    <div className="empty-state">
                        <svg
                            className="empty-state-icon"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="1.5"
                        >
                            <circle cx="12" cy="12" r="10" />
                            <line x1="12" y1="8" x2="12" y2="12" />
                            <line x1="12" y1="16" x2="12.01" y2="16" />
                        </svg>
                        <h3 className="empty-state-title">Failed to load session</h3>
                        <p className="empty-state-description">{initError}</p>
                        <button
                            className="btn btn-primary"
                            style={{ marginTop: 'var(--spacing-4)' }}
                            onClick={() => navigate('/')}
                        >
                            Back to Home
                        </button>
                    </div>
                </div>
            </div>
        )
    }

    return (
        <div className="chat-container">
            <header className="chat-header">
                <div>
                    <h1 className="chat-title">{session?.name || 'Chat'}</h1>
                    {session?.working_dir && (
                        <p style={{
                            fontSize: 'var(--font-size-xs)',
                            color: 'var(--color-text-muted)',
                            marginTop: 'var(--spacing-1)'
                        }}>
                            {session.working_dir}
                        </p>
                    )}
                </div>
            </header>

            <div className="chat-messages-wrapper">
                <div className="chat-messages-scroll">
                    <MessageList messages={messages} isLoading={isLoading} />
                </div>

                {error && (
                    <div style={{
                        padding: 'var(--spacing-3) var(--spacing-6)',
                        background: 'rgba(239, 68, 68, 0.1)',
                        borderTop: '1px solid rgba(239, 68, 68, 0.3)',
                        color: 'var(--color-error)',
                        fontSize: 'var(--font-size-sm)'
                    }}>
                        {error}
                    </div>
                )}
            </div>

            <div className="chat-input-area-sticky">
                <ChatInput
                    onSubmit={handleSendMessage}
                    disabled={isLoading || !isConnected || isCreatingSession}
                    placeholder={isCreatingSession ? "Switching agent..." : isLoading ? "Waiting for response..." : "Type a message..."}
                    autoFocus
                    selectedAgent={selectedAgent}
                    onAgentChange={handleAgentChange}
                    showAgentSelector={true}
                    modelInfo={modelInfo}
                />
            </div>
        </div>
    )
}
