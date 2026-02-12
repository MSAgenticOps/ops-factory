import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import ToolCallDisplay from './ToolCallDisplay'
import { usePreview } from '../contexts/PreviewContext'

export interface MessageContent {
    type: string
    text?: string
    id?: string
    name?: string
    input?: Record<string, unknown>
    // For toolRequest - contains the tool call details
    toolCall?: {
        status?: string
        value?: {
            name?: string
            arguments?: Record<string, unknown>
        }
    }
    // For toolResponse - contains the tool result
    toolResult?: {
        status?: string
        value?: unknown
    }
}

export interface MessageMetadata {
    userVisible?: boolean
    agentVisible?: boolean
}

export interface ChatMessage {
    id?: string
    role: 'user' | 'assistant'
    content: MessageContent[]
    created?: number
    metadata?: MessageMetadata
}

interface MessageProps {
    message: ChatMessage
    toolResponses?: ToolResponseMap
    agentId?: string
    isStreaming?: boolean
}

export type ToolResponseMap = Map<string, { result?: unknown; isError: boolean }>

// Represents a paired tool call with its request and response
interface ToolCallPair {
    id: string
    name: string
    args?: Record<string, unknown>
    result?: unknown
    isPending: boolean
    isError: boolean
}

const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://127.0.0.1:3000'
const GATEWAY_SECRET_KEY = import.meta.env.VITE_GATEWAY_SECRET_KEY || 'test'

export default function Message({ message, toolResponses = new Map(), agentId, isStreaming = false }: MessageProps) {
    const isUser = message.role === 'user'
    const { openPreview, isPreviewable } = usePreview()

    // Extract text content and tool calls
    const textContent: string[] = []
    const toolRequests: Map<string, { name: string; args?: Record<string, unknown>; status?: string }> = new Map()

    // Collect content from current message
    for (const content of message.content) {
        if (content.type === 'text' && content.text) {
            textContent.push(content.text)
        } else if (content.type === 'toolRequest' && content.id) {
            // toolRequest contains toolCall.value.name and toolCall.value.arguments
            const toolCall = content.toolCall
            toolRequests.set(content.id, {
                name: toolCall?.value?.name || 'unknown',
                args: toolCall?.value?.arguments,
                status: toolCall?.status
            })
        } else if (content.type === 'toolResponse' && content.id) {
            // Also collect from current message
            const toolResult = content.toolResult
            toolResponses.set(content.id, {
                result: toolResult?.status === 'success' ? toolResult.value : toolResult,
                isError: toolResult?.status === 'error'
            })
        }
    }

    // Pair tool requests with their responses
    const toolCalls: ToolCallPair[] = []
    for (const [id, request] of toolRequests) {
        const response = toolResponses.get(id)
        toolCalls.push({
            id,
            name: request.name,
            args: request.args,
            result: response?.result,
            isPending: !response && request.status === 'pending',
            isError: response?.isError || request.status === 'error'
        })
    }

    const fullText = textContent.join('\n')

    // Split thinking blocks from visible text
    const thinkRegex = /<think>([\s\S]*?)<\/think>/gi
    const thinkingParts: string[] = []
    const visibleText = fullText.replace(thinkRegex, (_match, content) => {
        thinkingParts.push(content.trim())
        return ''
    }).trim()
    const thinkingText = thinkingParts.join('\n\n')

    // Check for unclosed thinking block (still thinking)
    const unclosedThinkMatch = fullText.match(/<think>([\s\S]*)$/i)
    const isThinking = !!unclosedThinkMatch
    const unclosedThinkingText = unclosedThinkMatch ? unclosedThinkMatch[1].trim() : ''

    // Detect file paths in text - match absolute paths containing /artifacts/
    // More flexible regex that handles paths in various contexts
    const filePathRegex = /(\/[^\s\n]+\/artifacts\/[^\s\n,，。]+\.[a-zA-Z0-9]+)/g
    const detectedFiles: { path: string; name: string; ext: string }[] = []
    const seenPaths = new Set<string>()
    let match
    while ((match = filePathRegex.exec(visibleText || fullText)) !== null) {
        const filePath = match[1]
        if (seenPaths.has(filePath)) continue  // Deduplicate
        seenPaths.add(filePath)
        const fileName = filePath.split('/').pop() || filePath
        const fileExt = fileName.includes('.') ? fileName.split('.').pop()?.toLowerCase() || '' : ''
        detectedFiles.push({ path: filePath, name: fileName, ext: fileExt })
    }

    // Don't render empty messages (no text and no tool calls)
    if (!fullText && toolCalls.length === 0) {
        return null
    }

    // Determine which text to display for assistant messages
    const displayText = !isUser ? (visibleText || fullText) : fullText
    const hasThinking = !isUser && (thinkingText || isThinking)

    // File capsule component
    const FileCapsule = ({ filePath, fileName, fileExt }: { filePath: string; fileName: string; fileExt: string }) => {
        const downloadUrl = `${GATEWAY_URL}/agents/${agentId}/files/${encodeURIComponent(fileName)}?key=${GATEWAY_SECRET_KEY}`
        const canPreview = isPreviewable(fileExt, fileName, filePath)

        const handlePreview = (e: React.MouseEvent) => {
            e.preventDefault()
            openPreview({
                name: fileName,
                path: filePath,
                type: fileExt,
                agentId: agentId || '',
            })
        }

        return (
            <div className="file-capsule">
                <span className="file-capsule-icon">📄</span>
                <span className="file-capsule-name">{fileName}</span>
                <div className="file-capsule-actions">
                    {canPreview && (
                        <button className="file-capsule-btn" onClick={handlePreview} title="Preview">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                                <circle cx="12" cy="12" r="3" />
                            </svg>
                        </button>
                    )}
                    <a href={downloadUrl} target="_blank" rel="noopener noreferrer" className="file-capsule-btn" title="Download">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                            <polyline points="7 10 12 15 17 10" />
                            <line x1="12" y1="15" x2="12" y2="3" />
                        </svg>
                    </a>
                </div>
            </div>
        )
    }

    return (
        <div className={`message ${isUser ? 'user' : 'assistant'} animate-slide-in`}>
            <div className="message-avatar">
                {isUser ? 'U' : 'G'}
            </div>
            <div className="message-content">
                {/* Thinking block (collapsible) */}
                {hasThinking && (
                    <details className="thinking-block">
                        <summary className="thinking-block-summary">
                            {isThinking ? 'Thinking...' : 'Show thinking'}
                        </summary>
                        <div className="thinking-block-content">
                            <ReactMarkdown remarkPlugins={[remarkGfm]}>
                                {thinkingText || unclosedThinkingText}
                            </ReactMarkdown>
                        </div>
                    </details>
                )}

                {/* Main text content (with thinking stripped) */}
                {displayText && (
                    <div className="message-text">
                        <ReactMarkdown
                            remarkPlugins={[remarkGfm]}
                            components={{
                                a: ({ href, children, ...props }) => {
                                    if (href && !href.startsWith('http://') && !href.startsWith('https://') && !href.startsWith('mailto:') && agentId) {
                                        const downloadUrl = `${GATEWAY_URL}/agents/${agentId}/files/${encodeURIComponent(href)}?key=${GATEWAY_SECRET_KEY}`
                                        const fileName = href.split('/').pop() || href
                                        const fileExt = fileName.includes('.') ? fileName.split('.').pop()?.toLowerCase() || '' : ''
                                        const canPreview = isPreviewable(fileExt, fileName, href)

                                        const handlePreview = (e: React.MouseEvent) => {
                                            e.preventDefault()
                                            openPreview({
                                                name: fileName,
                                                path: href,
                                                type: fileExt,
                                                agentId: agentId,
                                            })
                                        }

                                        return (
                                            <span className="file-link-group" {...props}>
                                                <span className="file-link-name">{children}</span>
                                                {canPreview && (
                                                    <button
                                                        className="file-link-btn file-preview-trigger"
                                                        onClick={handlePreview}
                                                        title="Preview"
                                                    >
                                                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
                                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                                                            <circle cx="12" cy="12" r="3" />
                                                        </svg>
                                                    </button>
                                                )}
                                                <a
                                                    href={downloadUrl}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="file-link-btn"
                                                    title="Download"
                                                >
                                                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="14" height="14">
                                                        <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                                                        <polyline points="7 10 12 15 17 10" />
                                                        <line x1="12" y1="15" x2="12" y2="3" />
                                                    </svg>
                                                </a>
                                            </span>
                                        )
                                    }
                                    return <a href={href} target="_blank" rel="noopener noreferrer" {...props}>{children}</a>
                                }
                            }}
                        >
                            {displayText}
                        </ReactMarkdown>
                    </div>
                )}

                {/* Tool calls */}
                {toolCalls.map(toolCall => (
                    <ToolCallDisplay
                        key={toolCall.id}
                        name={toolCall.name}
                        args={toolCall.args}
                        result={toolCall.result}
                        isPending={toolCall.isPending}
                        isError={toolCall.isError}
                    />
                ))}

                {/* Streaming indicator on last assistant message */}
                {isStreaming && (
                    <div className="streaming-indicator">
                        <div className="loading-dots">
                            <span></span>
                            <span></span>
                            <span></span>
                        </div>
                    </div>
                )}

                {/* Render detected file capsules at the end */}
                {!isUser && detectedFiles.length > 0 && (
                    <div className="file-capsules-container">
                        {detectedFiles.map((file, idx) => (
                            <FileCapsule
                                key={`${file.path}-${idx}`}
                                filePath={file.path}
                                fileName={file.name}
                                fileExt={file.ext}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    )
}
