// Skill types for agent workspace skills

export interface SkillEntry {
    id?: string
    name: string
    description: string
    path: string  // relative path in agent workspace
    pinned?: boolean | string
    displayOrder?: number | string
}

export interface SkillsResponse {
    skills: SkillEntry[]
}
