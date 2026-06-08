/** First meaningful line of a delivered summary, markdown-stripped, truncated for a one-line card preview. */
export function previewOf(summary: string): string {
    const firstMeaningful = (summary || '')
        .split('\n')
        .map(line => line.trim())
        .find(line => /[\p{L}\p{N}]/u.test(line)) ?? ''
    const plain = stripMarkdown(firstMeaningful)
    return plain.length > 90 ? `${plain.slice(0, 90)}…` : plain
}

/** A muted secondary line for the push card: the meaningful lines after the title, stripped + joined + truncated. */
export function snippetOf(summary: string): string {
    const meaningful = (summary || '')
        .split('\n')
        .map(line => line.trim())
        .filter(line => /[\p{L}\p{N}]/u.test(line))
    const rest = meaningful.slice(1, 3).map(stripMarkdown).filter(Boolean).join(' ')
    return rest.length > 120 ? `${rest.slice(0, 120)}…` : rest
}

/** Schedule-type accent (drives the card's leading color dot). Matched by substring so id variants still map. */
export function scheduleAccent(scheduleId: string): 'brief' | 'watch' | 'memory' | 'default' {
    if (scheduleId.includes('daily-brief')) return 'brief'
    if (scheduleId.includes('watch')) return 'watch'
    if (scheduleId.includes('memory')) return 'memory'
    return 'default'
}

/** Strip the common markdown markers so a card preview reads as plain text (not `## ...` / `**...**`). */
function stripMarkdown(line: string): string {
    return line
        .replace(/^#{1,6}\s*/, '')
        .replace(/^[-*+>]\s+/, '')
        .replace(/\*\*(.+?)\*\*/g, '$1')
        .replace(/\*(.+?)\*/g, '$1')
        .replace(/`(.+?)`/g, '$1')
        .trim()
}

/** Locale-formatted run time; empty string for an unparseable timestamp. */
export function formatRunTime(iso: string): string {
    const date = new Date(iso)
    return Number.isNaN(date.getTime()) ? '' : date.toLocaleString()
}

/** Friendly schedule name: built-ins are mapped via i18n (`thread.schedule.<id>`); others fall back to the id. */
export function scheduleLabel(scheduleId: string, t: (key: string, opts?: { defaultValue: string }) => string): string {
    return t(`thread.schedule.${scheduleId}`, { defaultValue: scheduleId })
}
