/**
 * Citation parsing utility.
 *
 * Detects {{cite:NUMBER:TITLE:URL}} markers in text and extracts
 * a deduplicated list of citations. The marker format is generic
 * and not tied to any specific data source.
 */

export interface Citation {
    index: number
    title: string
    url: string | null
    snippet?: string
}

const CITE_REGEX = /\{\{cite:(\d+):\s*([^:]*):([^}]*)\}\}/g

/**
 * Extract unique citations from text, ordered by index.
 */
export function parseCitations(text: string): Citation[] {
    const map = new Map<number, Citation>()
    let match: RegExpExecArray | null
    const re = new RegExp(CITE_REGEX.source, CITE_REGEX.flags)

    while ((match = re.exec(text)) !== null) {
        const index = parseInt(match[1], 10)
        if (!map.has(index)) {
            map.set(index, {
                index,
                title: match[2].trim(),
                url: match[3].trim() || null,
            })
        }
    }

    return Array.from(map.values()).sort((a, b) => a.index - b.index)
}

/**
 * Check whether the text contains any citation markers.
 */
export function hasCitations(text: string): boolean {
    return new RegExp(CITE_REGEX.source).test(text)
}

/**
 * Split text into segments: plain text strings interleaved with
 * citation marker objects. This is used by the rendering layer to
 * replace markers with React components while keeping the rest as
 * raw Markdown.
 */
export type TextSegment = { type: 'text'; value: string } | { type: 'cite'; citation: Citation }

export function splitByCitations(text: string): TextSegment[] {
    const segments: TextSegment[] = []
    const re = new RegExp(CITE_REGEX.source, CITE_REGEX.flags)
    let lastIndex = 0
    let match: RegExpExecArray | null

    while ((match = re.exec(text)) !== null) {
        // Push preceding plain text
        if (match.index > lastIndex) {
            segments.push({ type: 'text', value: text.slice(lastIndex, match.index) })
        }

        segments.push({
            type: 'cite',
            citation: {
                index: parseInt(match[1], 10),
                title: match[2].trim(),
                url: match[3].trim() || null,
            },
        })

        lastIndex = re.lastIndex
    }

    // Trailing text
    if (lastIndex < text.length) {
        segments.push({ type: 'text', value: text.slice(lastIndex) })
    }

    return segments
}

/**
 * Strip all citation markers from text, returning clean Markdown.
 */
export function stripCitations(text: string): string {
    return text.replace(new RegExp(CITE_REGEX.source, CITE_REGEX.flags), '')
}

/**
 * Content item shape used by message content arrays.
 */
interface MessageContentItem {
    type: string
    id?: string
    toolCall?: {
        value?: {
            name?: string
            arguments?: Record<string, unknown>
        }
    }
    toolResult?: {
        status?: string
        value?: unknown
    }
}

/**
 * Unwrap a goose tool result value to get the inner data.
 *
 * Goose wraps MCP tool results as: { content: [{ type: "text", text: "..." }], isError: false }
 * This function extracts and parses the JSON from content[0].text.
 * Also handles the case where value is already a plain string or object.
 */
function unwrapToolResult(value: unknown): unknown {
    if (typeof value === 'string') return JSON.parse(value)

    const obj = value as Record<string, unknown>

    // Goose CallToolResult format: { content: [...], isError: ... }
    if (Array.isArray(obj?.content)) {
        for (const item of obj.content) {
            const ci = item as Record<string, unknown>
            if (ci.type === 'text' && typeof ci.text === 'string') {
                try { return JSON.parse(ci.text) } catch { /* not JSON, skip */ }
            }
        }
    }

    // Already a plain object with items/results
    return value
}

/**
 * Extract source documents from tool call results across all messages.
 *
 * Strategy:
 * 1. Collect search results (from tools whose names contain "search") — these have title + url
 * 2. Collect doc tokens that were actually read (from tools whose names contain "get"/"content")
 * 3. Cross-reference: prefer only documents that were both found AND read
 * 4. Fallback: if cross-referencing fails, return all search results
 */
export function extractSourceDocuments(messages: { content: MessageContentItem[] }[]): Citation[] {
    const toolNames = new Map<string, string>()
    const searchItems: { title: string; url: string; tokens: string[] }[] = []
    const readTokens = new Set<string>()

    for (const msg of messages) {
        for (const content of msg.content) {
            if (content.type === 'toolRequest' && content.id) {
                const name = content.toolCall?.value?.name || ''
                toolNames.set(content.id, name)

                // Track doc tokens from read/get operations
                if (/get|read|fetch|content/i.test(name) && !/search/i.test(name)) {
                    const args = content.toolCall?.value?.arguments as Record<string, unknown> | undefined
                    const params = (args?.params || args) as Record<string, unknown> | undefined
                    if (params) {
                        for (const val of Object.values(params)) {
                            if (typeof val === 'string' && val.length > 5) {
                                readTokens.add(val)
                            }
                        }
                    }
                }
            }

            if (content.type === 'toolResponse' && content.id) {
                const name = toolNames.get(content.id) || ''
                if (/search/i.test(name)) {
                    const value = content.toolResult?.status === 'success'
                        ? content.toolResult.value : null
                    if (value) {
                        try {
                            // Unwrap the tool result value. Goose wraps results as:
                            //   { content: [{ type: "text", text: "{...json...}" }], isError: false }
                            // We need to extract the inner JSON from content[0].text.
                            const data = unwrapToolResult(value)
                            const items = (data as Record<string, unknown>)?.items
                                || (data as Record<string, unknown>)?.results
                            if (Array.isArray(items)) {
                                for (const item of items) {
                                    const rec = item as Record<string, unknown>
                                    if (rec.title && rec.url) {
                                        // Collect all token-like fields for cross-referencing
                                        const tokens: string[] = []
                                        for (const [key, val] of Object.entries(rec)) {
                                            if (typeof val === 'string' && /token|id/i.test(key) && val.length > 5) {
                                                tokens.push(val)
                                            }
                                        }
                                        searchItems.push({
                                            title: rec.title as string,
                                            url: rec.url as string,
                                            tokens,
                                        })
                                    }
                                }
                            }
                        } catch { /* ignore parse errors */ }
                    }
                }
            }
        }
    }

    // Cross-reference: only include items that were actually read
    let sources: { title: string; url: string }[]
    if (readTokens.size > 0) {
        const matched = searchItems.filter(item =>
            item.tokens.some(t => readTokens.has(t))
        )
        sources = matched.length > 0 ? matched : searchItems
    } else {
        sources = searchItems
    }

    // Deduplicate by URL
    const seen = new Set<string>()
    const result: Citation[] = []
    let index = 1
    for (const s of sources) {
        if (seen.has(s.url)) continue
        seen.add(s.url)
        result.push({ index: index++, title: s.title, url: s.url })
    }

    return result
}
