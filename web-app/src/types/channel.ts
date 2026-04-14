export interface WhatsAppChannelConfig {
    loginStatus: string
    sessionLabel: string
    selfPhone: string
    authStateDir: string
    lastConnectedAt: string
    lastDisconnectedAt: string
    lastError: string
}

export interface ChannelBinding {
    channelId: string
    accountId: string
    peerId: string
    conversationId: string
    threadId?: string | null
    conversationType: string
    syntheticUserId: string
    agentId: string
    sessionId: string
    lastInboundAt?: string | null
    lastOutboundAt?: string | null
}

export interface ChannelEvent {
    id: string
    channelId: string
    level: string
    type: string
    summary: string
    createdAt: string
}

export interface ChannelVerificationResult {
    ok: boolean
    issues: string[]
}

export interface ChannelSummary {
    id: string
    name: string
    type: string
    enabled: boolean
    defaultAgentId: string
    ownerUserId: string
    status: string
    lastInboundAt?: string | null
    lastOutboundAt?: string | null
    bindingCount: number
}

export interface ChannelDetail {
    id: string
    name: string
    type: string
    enabled: boolean
    defaultAgentId: string
    ownerUserId: string
    createdAt: string
    updatedAt: string
    webhookPath: string
    config: WhatsAppChannelConfig
    verification: ChannelVerificationResult
    bindings: ChannelBinding[]
    events: ChannelEvent[]
}

export interface ChannelUpsertRequest {
    id?: string
    name: string
    type: string
    enabled: boolean
    defaultAgentId: string
    config: WhatsAppChannelConfig
}

export interface ChannelMutationResponse {
    success: boolean
    error?: string
    channel?: ChannelDetail
}

export interface ChannelLoginState {
    channelId: string
    status: string
    message: string
    authStateDir: string
    sessionLabel: string
    selfPhone: string
    lastConnectedAt: string
    lastDisconnectedAt: string
    lastError: string
    qrCodeDataUrl?: string | null
}
