/**
 * Pure helpers for the `@mention` agent picker in the chat input — the deterministic side of A2A triggering.
 *
 * The picker canonicalises the `@<agentId>` token against the real agents list so the target reaching the model is
 * unambiguous (no typos / hallucinated ids); the system-prompt rule is the fallback that actually issues `call_agent`.
 * Mirrors the `/` skill-picker token detection.
 */
export interface AtMentionToken {
    start: number
    end: number
    query: string
}

/**
 * Detects an in-progress `@<query>` token immediately before the cursor (bounded by whitespace), or null.
 * Rejects mid-word `@` (e.g. an email address) since the token must start at a whitespace boundary.
 */
export function findAtMentionToken(text: string, cursor: number): AtMentionToken | null {
    const beforeCursor = text.slice(0, cursor)
    const tokenStart = Math.max(
        beforeCursor.lastIndexOf(' '),
        beforeCursor.lastIndexOf('\n'),
        beforeCursor.lastIndexOf('\t'),
    ) + 1
    const token = beforeCursor.slice(tokenStart)
    if (!token.startsWith('@')) return null
    const rest = token.slice(1)
    if (/\s/.test(rest)) return null
    return { start: tokenStart, end: cursor, query: rest }
}

/**
 * Replaces the detected token with the canonical `@<agentId> ` form and returns the new value + caret position.
 * Keeping the `@<agentId>` marker (rather than stripping it) is what the prompt-fallback rule keys on.
 */
export function buildMentionReplacement(
    text: string,
    token: { start: number; end: number },
    agentId: string,
): { value: string; caret: number } {
    const before = text.slice(0, token.start)
    const after = text.slice(token.end)
    const insert = `@${agentId} `
    return { value: `${before}${insert}${after}`, caret: before.length + insert.length }
}

/** Case-insensitive filter of agents by id/name against the current query. */
export function filterAgents<T extends { id: string; name?: string }>(agents: T[], query: string): T[] {
    const normalized = query.trim().toLowerCase()
    if (!normalized) return agents
    return agents.filter(agent =>
        agent.id.toLowerCase().includes(normalized) || (agent.name ?? '').toLowerCase().includes(normalized))
}
