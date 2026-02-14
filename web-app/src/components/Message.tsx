import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import ToolCallDisplay from './ToolCallDisplay'
import CitationMark from './CitationMark'
import ReferenceList from './ReferenceList'
import { usePreview } from '../contexts/PreviewContext'
import { parseCitations, type Citation } from '../utils/citationParser'

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
    onRetry?: () => void
    sourceDocuments?: Citation[]
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

export default function Message({ message, toolResponses = new Map(), agentId, isStreaming = false, onRetry, sourceDocuments }: MessageProps) {
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
    // Skip tool calls that failed before execution (no name, error status) — they are
    // pre-execution failures (MCP connection error, tool not found, etc.) and provide
    // no useful information to the user.
    const toolCalls: ToolCallPair[] = []
    for (const [id, request] of toolRequests) {
        if (request.name === 'unknown' && request.status === 'error') continue
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

    // Detect file paths from multiple sources
    const detectedFiles: { path: string; name: string; ext: string }[] = []
    const seenNames = new Set<string>()

    const addFile = (filePath: string) => {
        const fileName = filePath.split('/').pop() || filePath
        if (seenNames.has(fileName)) return
        seenNames.add(fileName)
        const fileExt = fileName.includes('.') ? fileName.split('.').pop()?.toLowerCase() || '' : ''
        if (!fileExt) return
        detectedFiles.push({ path: fileName, name: fileName, ext: fileExt })
    }

    // 1. Detect absolute paths containing /artifacts/ in message text
    const filePathRegex = /(\/[^\s\n]+\/artifacts\/[^\s\n,，。)）\]】]+\.[a-zA-Z0-9]+)/g
    let match
    const searchText = visibleText || fullText
    while ((match = filePathRegex.exec(searchText)) !== null) {
        addFile(match[1])
    }

    // 2. Detect file links from markdown syntax [text](path.ext) in message text
    const KNOWN_EXTS = 'md|txt|html|htm|pdf|docx|xlsx|pptx|csv|json|yaml|yml|py|js|ts|sh|png|jpg|jpeg|gif|svg|mp3|wav|mp4'
    const mdLinkRegex = new RegExp(`\\[([^\\]]*)\\]\\(([^)]+\\.(?:${KNOWN_EXTS}))\\)`, 'gi')
    while ((match = mdLinkRegex.exec(searchText)) !== null) {
        addFile(match[2])
    }

    // 3. Detect file paths from tool call arguments (e.g., write_file, save_file tools)
    for (const tc of toolCalls) {
        if (!tc.args) continue
        for (const [key, value] of Object.entries(tc.args)) {
            if (typeof value === 'string' && (key === 'path' || key === 'file_path' || key === 'filename' || key === 'file_name')) {
                addFile(value)
            }
        }
    }

    // Detect empty assistant response (model returned nothing)
    const isEmptyAssistantResponse = !isUser && !fullText && toolCalls.length === 0 && !isStreaming

    // Don't render empty user messages
    if (isUser && !fullText) {
        return null
    }

    // Determine which text to display for assistant messages
    const rawDisplayText = !isUser ? (visibleText || fullText) : fullText
    const hasThinking = !isUser && (thinkingText || isThinking)

    // Citation processing — only for assistant text content
    const citations: Citation[] = !isUser && rawDisplayText ? parseCitations(rawDisplayText) : []
    const citationMap = new Map(citations.map(c => [c.index, c]))

    // Replace {{cite:N:TITLE:URL}} markers with Markdown links that the
    // custom `a` component will intercept and render as <CitationMark />.
    // Inline citations are best-effort — they only appear when the LLM
    // follows the citation format instruction.
    const displayText = citations.length > 0
        ? rawDisplayText
            .replace(
                /\{\{cite:(\d+):\s*[^:]*:[^}]*\}\}/g,
                (_, num) => `[CITE_${num}](#cite-${num})`
            )
            .replace(/```[ \t]*\[CITE_/g, '```\n\n[CITE_')
        : rawDisplayText

    // File capsule component
    const FileCapsule = ({ filePath, fileName, fileExt }: { filePath: string; fileName: string; fileExt: string }) => {
        const downloadUrl = `${GATEWAY_URL}/agents/${agentId}/files/${encodeURIComponent(fileName)}?key=${GATEWAY_SECRET_KEY}`
        const canPreview = isPreviewable(fileExt, fileName, filePath)

        const handlePreview = (e: React.MouseEvent) => {
            e.preventDefault()
            openPreview({
                name: fileName,
                path: fileName,
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
                {/* Empty assistant response — model error */}
                {isEmptyAssistantResponse && (
                    <div className="message-error-banner">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" width="16" height="16">
                            <circle cx="12" cy="12" r="10" />
                            <line x1="12" y1="8" x2="12" y2="12" />
                            <line x1="12" y1="16" x2="12.01" y2="16" />
                        </svg>
                        <span>模型未返回有效响应，可能是服务临时异常</span>
                        {onRetry && (
                            <button className="message-error-retry" onClick={onRetry}>
                                重试
                            </button>
                        )}
                    </div>
                )}

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
                                    // Citation markers rendered as #cite-N fragment links
                                    if (href?.startsWith('#cite-')) {
                                        const index = parseInt(href.replace('#cite-', ''), 10)
                                        const citation = citationMap.get(index)
                                        if (citation) return <CitationMark citation={citation} />
                                        return <>{children}</>
                                    }
                                    if (href && !href.startsWith('http://') && !href.startsWith('https://') && !href.startsWith('mailto:') && agentId) {
                                        // Render as a simple styled file name inline — the bottom capsule handles preview/download
                                        return (
                                            <span className="file-link-group">
                                                <span className="file-link-name">{children}</span>
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

                {/* File capsules — right after text content, before tool calls */}
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

                {/* Source references — always shown when available (extracted from tool call results) */}
                {sourceDocuments && sourceDocuments.length > 0 && displayText && (
                    <ReferenceList citations={sourceDocuments} />
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
            </div>
        </div>
    )
}
