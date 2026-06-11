export const KNOWLEDGE_SOURCE_NAME_MAX_LENGTH = 64
export const KNOWLEDGE_SOURCE_DESCRIPTION_MAX_LENGTH = 256

const KNOWLEDGE_SOURCE_NAME_PATTERN = /^[\p{L}\p{N}_\s-]*$/u

export function hasInvalidKnowledgeSourceNameChars(name: string): boolean {
    return !!name && !KNOWLEDGE_SOURCE_NAME_PATTERN.test(name)
}

export function isDuplicateKnowledgeSourceName(
    name: string,
    existingNames: Set<string>,
    currentName?: string
): boolean {
    const normalizedName = name.trim()
    if (!normalizedName) return false
    if (currentName && normalizedName === currentName.trim()) return false
    return existingNames.has(normalizedName)
}
