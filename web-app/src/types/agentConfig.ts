// Agent configuration types

export interface AgentConfig {
    id: string
    name: string
    port: number
    agentsMd: string  // AGENTS.md content
    workingDir: string
    provider?: string
    model?: string
}

export interface UpdateAgentConfigRequest {
    port?: number
    agentsMd?: string
}

export interface UpdateAgentConfigResponse {
    success: boolean
    error?: string
    requiresRestart?: boolean
}

export interface PortValidationResponse {
    valid: boolean
    conflictWith?: string  // agent id if conflict
}
