/** First meaningful line of a delivered summary, markdown-stripped, truncated for a one-line card preview. */
export function previewOf(summary: string): string {
    const firstMeaningful = (summary || '')
        .split('\n')
        .map(line => line.trim())
        .find(line => /[\p{L}\p{N}]/u.test(line)) ?? ''
    const plain = stripMarkdown(firstMeaningful)
    return plain.length > 90 ? `${plain.slice(0, 90)}…` : plain
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
