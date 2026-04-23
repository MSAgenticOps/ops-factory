export interface SkillMarketEntry {
    id: string
    name: string
    description: string
    path: string
    containsScripts: boolean
    checksum: string
    sizeBytes: number
    fileCount: number
    createdAt: string
    updatedAt: string
}

export interface SkillMarketDetail extends SkillMarketEntry {
    entrypoint: string
    files: string[]
    instructions: string
}

export interface SkillMarketListResponse {
    items: SkillMarketEntry[]
    total: number
}

export interface SkillMarketMutationResponse {
    skill: SkillMarketEntry
    warnings?: Array<{
        code: string
        message: string
    }>
}
