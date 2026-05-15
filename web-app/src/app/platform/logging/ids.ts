function randomSegment(): string {
    if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
        return crypto.randomUUID()
    }
    return `${Date.now().toString(36)}-${performance.now().toString(36).replace('.', '')}`
}

function createScopedId(scope: string) {
    return `${scope}_${randomSegment()}`
}

export function createPageViewId() {
    return createScopedId('pv')
}

export function createInteractionId() {
    return createScopedId('ix')
}

export function createRequestId() {
    return createScopedId('req')
}

export function createStreamId() {
    return createScopedId('stream')
}
