/** First meaningful line of a delivered summary, truncated for a one-line timeline-card preview. */
export function previewOf(summary: string): string {
    const firstMeaningful = (summary || '')
        .split('\n')
        .map(line => line.trim())
        .find(line => /[\p{L}\p{N}]/u.test(line)) ?? ''
    return firstMeaningful.length > 90 ? `${firstMeaningful.slice(0, 90)}…` : firstMeaningful
}

/** Locale-formatted run time; empty string for an unparseable timestamp. */
export function formatRunTime(iso: string): string {
    const date = new Date(iso)
    return Number.isNaN(date.getTime()) ? '' : date.toLocaleString()
}
