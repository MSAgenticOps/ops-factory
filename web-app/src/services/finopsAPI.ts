import { runtime } from '../config/runtime'

export interface SnapshotStatus {
    status: string
    lastRefreshedAt: string | null
    sourceDbCount: number
    skippedDbCount: number
    sessionCount: number
    lastRefreshError: string | null
}

export interface UsageSummary {
    sessionCount: number
    totalTokens: number
    inputTokens: number
    outputTokens: number
    activeUsers: number
    activeAgents: number
    activeModels: number
    scheduledSessionCount: number
    manualSessionCount: number
    avgTokensPerSession: number
}

export interface ComparisonSummary {
    current: UsageSummary
    previous: UsageSummary
    tokenDelta: number | null
    tokenGrowthRate: number | null
    sessionDelta: number | null
    sessionGrowthRate: number | null
}

export interface TrendPoint {
    bucket: string
    sessionCount: number
    totalTokens: number
    inputTokens: number
    outputTokens: number
}

export interface AgentUsage {
    agentId: string
    activeUsers: number
    sessionCount: number
    totalTokens: number
    inputTokens: number
    outputTokens: number
    avgTokensPerSession: number
    scheduledSessionCount: number
    highTokenSessionCount: number
}

export interface UserUsage {
    userId: string
    activeAgents: number
    sessionCount: number
    totalTokens: number
    inputTokens: number
    outputTokens: number
    avgTokensPerSession: number
    lastActiveAt: string | null
    topAgent: string
}

export interface SessionUsage {
    id: string
    userId: string
    agentId: string
    name: string | null
    sessionType: string
    providerName: string
    modelName: string
    createdAt: string
    updatedAt: string
    totalTokens: number
    inputTokens: number
    outputTokens: number
    scheduleId: string | null
    messageCount: number
    userMessageCount: number
    assistantMessageCount: number
    toolResponseCount: number
    label: string
}

export interface ModelUsage {
    providerName: string
    modelName: string
    sessionCount: number
    activeUsers: number
    activeAgents: number
    totalTokens: number
    inputTokens: number
    outputTokens: number
    avgTokensPerSession: number
}

export interface DistributionItem {
    id: string
    label: string
    sessionCount: number
    totalTokens: number
    percentage: number
}

export interface TaskExecutionLoad {
    avgTokensPerTask: number
    avgMessagesPerTask: number
    avgToolResponsesPerTask: number
}

export interface OverviewResponse {
    snapshotStatus: SnapshotStatus
    summary: ComparisonSummary
    tokenTrend: TrendPoint[]
    topAgents: AgentUsage[]
    topUsers: UserUsage[]
    topSessions: SessionUsage[]
    models: ModelUsage[]
    taskExecutionLoad: TaskExecutionLoad
    sessionTypeDistribution: DistributionItem[]
    providerDistribution: DistributionItem[]
}

export interface PageResponse<T> {
    snapshotStatus: SnapshotStatus
    items: T[]
    page: number
    size: number
    totalItems: number
    totalPages: number
}

export interface SessionMessageDetail {
    messageId: string | null
    rowId: number
    role: string
    createdAt: string
    insertedAt: string
    tokens: number | null
    contentLength: number
    contentPreview: string
    contentText: string
    contentTruncated: boolean
    toolRequest: boolean
    toolResponse: boolean
    toolName: string | null
    error: boolean
    userVisible: boolean
    agentVisible: boolean
}

export interface SessionMessageStats {
    messageCount: number
    userMessageCount: number
    assistantMessageCount: number
    toolRequestCount: number
    toolResponseCount: number
    messagesWithTokenCount: number
    largestContentLength: number
    largestContentRole: string | null
    largestContentPreview: string | null
}

export interface SessionMessageCapabilities {
    messageTokenAvailable: boolean
    contentPreviewAvailable: boolean
    toolSignalAvailable: boolean
}

export interface SessionMessagesResponse {
    snapshotStatus: SnapshotStatus
    session: SessionUsage
    stats: SessionMessageStats
    capabilities: SessionMessageCapabilities
    messages: SessionMessageDetail[]
}

function finopsHeaders(userId?: string | null): Record<string, string> {
    const h: Record<string, string> = {
        'Content-Type': 'application/json',
        'x-secret-key': runtime.FINOPS_SECRET_KEY,
    }
    if (userId) h['x-user-id'] = userId
    return h
}

async function request<T>(path: string, init?: RequestInit, userId?: string | null): Promise<T> {
    const response = await fetch(`${runtime.FINOPS_URL}${path}`, {
        ...init,
        headers: {
            ...finopsHeaders(userId),
            ...(init?.headers || {}),
        },
    })
    if (!response.ok) {
        const text = await response.text()
        throw new Error(text || `HTTP ${response.status}`)
    }
    return response.json() as Promise<T>
}

export async function fetchFinOpsOverview(userId?: string | null): Promise<OverviewResponse> {
    return request<OverviewResponse>('/overview?compare=true', undefined, userId)
}

function pageQuery(page: number, size: number): string {
    return `page=${encodeURIComponent(page)}&size=${encodeURIComponent(size)}`
}

export async function fetchFinOpsAgents(page = 1, size = 25, userId?: string | null): Promise<PageResponse<AgentUsage>> {
    return request<PageResponse<AgentUsage>>(`/agents?${pageQuery(page, size)}`, undefined, userId)
}

export async function fetchFinOpsUsers(page = 1, size = 25, userId?: string | null): Promise<PageResponse<UserUsage>> {
    return request<PageResponse<UserUsage>>(`/users?${pageQuery(page, size)}`, undefined, userId)
}

export async function fetchFinOpsSessions(page = 1, size = 25, userId?: string | null): Promise<PageResponse<SessionUsage>> {
    return request<PageResponse<SessionUsage>>(`/sessions?${pageQuery(page, size)}`, undefined, userId)
}

export async function fetchFinOpsSessionMessages(session: SessionUsage, userId?: string | null): Promise<SessionMessagesResponse> {
    const query = new URLSearchParams({
        userId: session.userId,
        agentId: session.agentId,
    })
    return request<SessionMessagesResponse>(`/sessions/${encodeURIComponent(session.id)}/messages?${query.toString()}`, undefined, userId)
}

export async function fetchFinOpsModels(page = 1, size = 25, userId?: string | null): Promise<PageResponse<ModelUsage>> {
    return request<PageResponse<ModelUsage>>(`/models?${pageQuery(page, size)}`, undefined, userId)
}

export async function fetchFinOpsAgentSessions(agentId: string, page = 1, size = 25, userId?: string | null): Promise<PageResponse<SessionUsage>> {
    return request<PageResponse<SessionUsage>>(`/agents/${encodeURIComponent(agentId)}?${pageQuery(page, size)}`, undefined, userId)
}

export async function fetchFinOpsUserSessions(targetUserId: string, page = 1, size = 25, loggedInUserId?: string | null): Promise<PageResponse<SessionUsage>> {
    return request<PageResponse<SessionUsage>>(`/users/${encodeURIComponent(targetUserId)}?${pageQuery(page, size)}`, undefined, loggedInUserId)
}

export async function refreshFinOpsSnapshot(userId?: string | null): Promise<SnapshotStatus> {
    return request<SnapshotStatus>('/refresh', { method: 'POST' }, userId)
}
