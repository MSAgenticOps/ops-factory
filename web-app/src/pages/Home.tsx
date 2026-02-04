import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGoosed } from '../contexts/GoosedContext'
import ChatInput from '../components/ChatInput'
import SessionList from '../components/SessionList'
import { getDefaultAgent, getAgentWorkingDir } from '../components/AgentSelector'
import type { Session } from '@goosed/sdk'

interface ModelInfo {
    provider: string
    model: string
}

export default function Home() {
    const navigate = useNavigate()
    const { client, isConnected, error: connectionError } = useGoosed()
    const [recentSessions, setRecentSessions] = useState<Session[]>([])
    const [isLoadingSessions, setIsLoadingSessions] = useState(true)
    const [isCreatingSession, setIsCreatingSession] = useState(false)
    const [selectedAgent, setSelectedAgent] = useState(getDefaultAgent())
    const [modelInfo, setModelInfo] = useState<ModelInfo | null>(null)

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

    // Load recent sessions
    useEffect(() => {
        const loadSessions = async () => {
            if (!isConnected) return

            try {
                const sessions = await client.listSessions()
                // Sort by updated_at descending and take first 5
                const sorted = sessions
                    .sort((a, b) => new Date(b.updated_at).getTime() - new Date(a.updated_at).getTime())
                    .slice(0, 5)
                setRecentSessions(sorted)
            } catch (err) {
                console.error('Failed to load sessions:', err)
            } finally {
                setIsLoadingSessions(false)
            }
        }

        loadSessions()
    }, [client, isConnected])

    const handleInputSubmit = async (message: string) => {
        if (isCreatingSession) return

        setIsCreatingSession(true)
        try {
            const workingDir = getAgentWorkingDir(selectedAgent)
            const session = await client.startSession(workingDir)
            await client.resumeSession(session.id)

            navigate(`/chat?sessionId=${session.id}`, {
                state: { initialMessage: message }
            })
        } catch (err) {
            console.error('Failed to create session:', err)
            alert('Failed to create session: ' + (err instanceof Error ? err.message : 'Unknown error'))
        } finally {
            setIsCreatingSession(false)
        }
    }

    const handleResumeSession = (sessionId: string) => {
        navigate(`/chat?sessionId=${sessionId}`)
    }

    const handleDeleteSession = async (sessionId: string) => {
        try {
            await client.deleteSession(sessionId)
            setRecentSessions(prev => prev.filter(s => s.id !== sessionId))
        } catch (err) {
            console.error('Failed to delete session:', err)
        }
    }

    return (
        <div className="home-container">
            <div className="home-hero">
                <h1 className="home-title">Hello, I'm Goose</h1>
                <p className="home-description">
                    Your AI-powered coding assistant. Ask me anything about your codebase,
                    let me help you write, debug, or explain code.
                </p>

                {connectionError && (
                    <div style={{
                        padding: 'var(--spacing-4)',
                        background: 'rgba(239, 68, 68, 0.2)',
                        borderRadius: 'var(--radius-lg)',
                        color: 'var(--color-error)',
                        marginBottom: 'var(--spacing-6)'
                    }}>
                        Connection error: {connectionError}
                    </div>
                )}

                {!isConnected && !connectionError && (
                    <div style={{
                        padding: 'var(--spacing-4)',
                        background: 'rgba(245, 158, 11, 0.2)',
                        borderRadius: 'var(--radius-lg)',
                        color: 'var(--color-warning)',
                        marginBottom: 'var(--spacing-6)'
                    }}>
                        Connecting to goosed server...
                    </div>
                )}
            </div>

            <div className="home-input-container">
                <ChatInput
                    onSubmit={handleInputSubmit}
                    disabled={!isConnected || isCreatingSession}
                    placeholder={isCreatingSession ? "Creating session..." : "Ask me anything..."}
                    autoFocus
                    selectedAgent={selectedAgent}
                    onAgentChange={setSelectedAgent}
                    modelInfo={modelInfo}
                />
            </div>

            {recentSessions.length > 0 && (
                <div style={{
                    width: '100%',
                    maxWidth: '600px',
                    marginTop: 'var(--spacing-10)'
                }}>
                    <h3 style={{
                        fontSize: 'var(--font-size-sm)',
                        fontWeight: 600,
                        color: 'var(--color-text-secondary)',
                        marginBottom: 'var(--spacing-4)',
                        textTransform: 'uppercase',
                        letterSpacing: '0.05em'
                    }}>
                        Recent Chats
                    </h3>
                    <SessionList
                        sessions={recentSessions}
                        isLoading={isLoadingSessions}
                        onResume={handleResumeSession}
                        onDelete={handleDeleteSession}
                    />
                </div>
            )}
        </div>
    )
}
