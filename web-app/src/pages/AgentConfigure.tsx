import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useAgentConfig } from '../hooks/useAgentConfig'
import { useToast } from '../contexts/ToastContext'
import { McpSection } from '../components/mcp'
import { SkillSection } from '../components/skill'

export default function AgentConfigure() {
    const { agentId } = useParams<{ agentId: string }>()
    const navigate = useNavigate()
    const { config, isLoading, error, fetchConfig, updateConfig, validatePort } = useAgentConfig()
    const { showToast } = useToast()

    // Form state
    const [agentsMd, setAgentsMd] = useState('')
    const [port, setPort] = useState('')
    const [portError, setPortError] = useState<string | null>(null)
    const [isSavingPrompt, setIsSavingPrompt] = useState(false)
    const [isSavingPort, setIsSavingPort] = useState(false)
    const [isValidating, setIsValidating] = useState(false)
    const [validationResult, setValidationResult] = useState<'valid' | 'invalid' | null>(null)

    useEffect(() => {
        if (agentId) {
            fetchConfig(agentId)
        }
    }, [agentId, fetchConfig])

    useEffect(() => {
        if (config) {
            setAgentsMd(config.agentsMd)
            setPort(String(config.port))
        }
    }, [config])

    const handleSavePrompt = async () => {
        if (!agentId) return
        setIsSavingPrompt(true)

        const result = await updateConfig(agentId, { agentsMd })

        if (result.success) {
            showToast('success', 'Prompt saved successfully')
        } else {
            showToast('error', result.error || 'Failed to save prompt')
        }

        setIsSavingPrompt(false)
    }

    const handleValidatePort = async () => {
        if (!agentId) return
        const portNum = parseInt(port, 10)

        if (isNaN(portNum) || portNum < 1024 || portNum > 65535) {
            setPortError('Port must be between 1024 and 65535')
            setValidationResult('invalid')
            return
        }

        setIsValidating(true)
        setValidationResult(null)
        setPortError(null)

        const result = await validatePort(agentId, portNum)

        setIsValidating(false)

        if (result.valid) {
            setValidationResult('valid')
            setPortError(null)
            showToast('success', 'Port is available')
        } else {
            setValidationResult('invalid')
            if (result.conflictWith) {
                setPortError(`Port is already used by ${result.conflictWith}`)
            } else {
                setPortError('Port is not available')
            }
        }
    }

    const handleSavePort = async () => {
        if (!agentId) return
        const portNum = parseInt(port, 10)

        if (isNaN(portNum) || portNum < 1024 || portNum > 65535) {
            setPortError('Port must be between 1024 and 65535')
            return
        }

        // Validate first
        const validation = await validatePort(agentId, portNum)
        if (!validation.valid) {
            if (validation.conflictWith) {
                setPortError(`Port is already used by ${validation.conflictWith}`)
            } else {
                setPortError('Port is not available')
            }
            return
        }

        setIsSavingPort(true)
        setPortError(null)

        const result = await updateConfig(agentId, { port: portNum })

        if (result.success) {
            showToast('success', 'Port saved successfully')
            if (result.requiresRestart) {
                showToast('warning', 'Agent restart required for port change')
            }
        } else {
            showToast('error', result.error || 'Failed to save port')
        }

        setIsSavingPort(false)
    }

    if (isLoading) {
        return (
            <div className="page-container agent-configure-page">
                <div className="agent-configure-loading">Loading agent configuration...</div>
            </div>
        )
    }

    if (error || !config) {
        return (
            <div className="page-container agent-configure-page">
                <div className="agent-configure-error">
                    {error || 'Agent not found'}
                    <button type="button" onClick={() => navigate('/agents')}>
                        Back to Agents
                    </button>
                </div>
            </div>
        )
    }

    return (
        <div className="page-container agent-configure-page">
            <div className="agent-configure-header">
                <button
                    type="button"
                    className="agent-configure-back"
                    onClick={() => navigate('/agents')}
                >
                    ← Back to Agents
                </button>
                <div className="agent-configure-title-section">
                    <h1 className="agent-configure-title">{config.name}</h1>
                    <span className="agent-configure-id">{config.id}</span>
                </div>
            </div>

            <div className="agent-configure-content">
                {/* Agent Prompt Section */}
                <section className="agent-configure-section">
                    <h2 className="agent-configure-section-title">Agent Prompt (AGENTS.md)</h2>
                    <p className="agent-configure-section-desc">
                        Define the agent&apos;s behavior, capabilities, and instructions.
                    </p>
                    <div className="agent-prompt-editor">
                        <textarea
                            value={agentsMd}
                            onChange={(e) => setAgentsMd(e.target.value)}
                            placeholder="# Agent Instructions&#10;&#10;Write your agent prompt here..."
                            rows={15}
                        />
                    </div>
                    <div className="agent-configure-actions">
                        <button
                            type="button"
                            className="btn btn-primary"
                            onClick={handleSavePrompt}
                            disabled={isSavingPrompt}
                        >
                            {isSavingPrompt ? 'Saving...' : 'Save Prompt'}
                        </button>
                    </div>
                </section>

                {/* Port Configuration Section */}
                <section className="agent-configure-section">
                    <h2 className="agent-configure-section-title">Port Configuration</h2>
                    <p className="agent-configure-section-desc">
                        Configure the port for this agent&apos;s goosed instance.
                    </p>
                    <div className="port-config-row">
                        <label className="port-config-label">Port</label>
                        <input
                            type="number"
                            className={`port-config-input ${portError ? 'port-config-input-error' : ''} ${validationResult === 'valid' ? 'port-config-input-valid' : ''}`}
                            value={port}
                            onChange={(e) => {
                                setPort(e.target.value)
                                setPortError(null)
                                setValidationResult(null)
                            }}
                            min={1024}
                            max={65535}
                        />
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={handleValidatePort}
                            disabled={isValidating}
                        >
                            {isValidating ? 'Checking...' : 'Validate'}
                        </button>
                        <button
                            type="button"
                            className="btn btn-primary"
                            onClick={handleSavePort}
                            disabled={isSavingPort}
                        >
                            {isSavingPort ? 'Saving...' : 'Save Port'}
                        </button>
                        {validationResult === 'valid' && !portError && (
                            <span className="port-validation-success">✓ Available</span>
                        )}
                    </div>
                    {portError && (
                        <div className="port-config-error">{portError}</div>
                    )}
                    <p className="port-config-hint">
                        Valid range: 1024 - 65535. Requires agent restart to take effect.
                    </p>
                </section>

                {/* MCP Section */}
                <section className="agent-configure-section">
                    <McpSection agentId={agentId || null} />
                </section>

                {/* Skills Section */}
                <section className="agent-configure-section">
                    <SkillSection agentId={agentId || ''} />
                </section>
            </div>
        </div>
    )
}
