/**
 * Input validation utilities to prevent XSS injection.
 */

const XSS_CHARS = /[<>"'&]/

export function hasXssChars(input: string): boolean {
    return XSS_CHARS.test(input)
}

export function sanitizeInput(input: string): string {
    return input.replace(/[<>"'&]/g, (char) => {
        switch (char) {
            case '<': return '&lt;'
            case '>': return '&gt;'
            case '"': return '&quot;'
            case "'": return '&#x27;'
            case '&': return '&amp;'
            default: return char
        }
    })
}

export function validateAndSanitize(input: string, fieldName: string): { valid: boolean; sanitized: string; error?: string } {
    const trimmed = input.trim()
    if (hasXssChars(trimmed)) {
        return {
            valid: false,
            sanitized: sanitizeInput(trimmed),
            error: `${fieldName} contains invalid characters (< > " ' &). These characters are not allowed for security reasons.`
        }
    }
    return {
        valid: true,
        sanitized: trimmed
    }
}
