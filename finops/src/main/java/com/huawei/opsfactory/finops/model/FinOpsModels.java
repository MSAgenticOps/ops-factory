package com.huawei.opsfactory.finops.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class FinOpsModels {

    private FinOpsModels() {
    }

    public enum SessionScope {
        CURRENT,
        PREVIOUS
    }

    public record QueryFilter(
        Instant startTime,
        Instant endTime,
        String agentId,
        String userId,
        String sessionType,
        String providerName,
        String modelName,
        boolean compare
    ) {
    }

    public record SessionUsageRecord(
        String id,
        String userId,
        String agentId,
        String name,
        String sessionType,
        String workingDir,
        Instant createdAt,
        Instant updatedAt,
        long totalTokens,
        long inputTokens,
        long outputTokens,
        long accumulatedTotalTokens,
        long accumulatedInputTokens,
        long accumulatedOutputTokens,
        String scheduleId,
        String providerName,
        String modelName,
        String gooseMode,
        String threadId,
        int messageCount,
        int userMessageCount,
        int assistantMessageCount,
        int toolResponseCount,
        String label,
        Map<String, Object> modelConfig,
        Map<String, Object> recipe
    ) {
    }

    public record SessionUsage(
        String id,
        String userId,
        String agentId,
        String name,
        String sessionType,
        Instant createdAt,
        Instant updatedAt,
        long totalTokens,
        long inputTokens,
        long outputTokens,
        String scheduleId,
        String providerName,
        String modelName,
        int messageCount,
        int userMessageCount,
        int assistantMessageCount,
        int toolResponseCount,
        String label
    ) {
    }

    public record SessionMessageRecord(
        String sessionId,
        String userId,
        String agentId,
        String messageId,
        long rowId,
        String role,
        Instant createdAt,
        Instant insertedAt,
        Long tokens,
        int contentLength,
        String contentPreview,
        String contentText,
        boolean contentTruncated,
        boolean toolRequest,
        boolean toolResponse,
        String toolName,
        boolean error,
        boolean userVisible,
        boolean agentVisible
    ) {
    }

    public record SessionMessageDetail(
        String messageId,
        long rowId,
        String role,
        Instant createdAt,
        Instant insertedAt,
        Long tokens,
        int contentLength,
        String contentPreview,
        String contentText,
        boolean contentTruncated,
        boolean toolRequest,
        boolean toolResponse,
        String toolName,
        boolean error,
        boolean userVisible,
        boolean agentVisible
    ) {
    }

    public record SessionMessageStats(
        int messageCount,
        int userMessageCount,
        int assistantMessageCount,
        int toolRequestCount,
        int toolResponseCount,
        int messagesWithTokenCount,
        int largestContentLength,
        String largestContentRole,
        String largestContentPreview
    ) {
    }

    public record SessionMessageCapabilities(
        boolean messageTokenAvailable,
        boolean contentPreviewAvailable,
        boolean toolSignalAvailable
    ) {
    }

    public record SessionMessagesResponse(
        SnapshotStatus snapshotStatus,
        SessionUsage session,
        SessionMessageStats stats,
        SessionMessageCapabilities capabilities,
        List<SessionMessageDetail> messages
    ) {
    }

    public record SnapshotStatus(
        String status,
        Instant lastRefreshedAt,
        int sourceDbCount,
        int skippedDbCount,
        int sessionCount,
        String lastRefreshError
    ) {
    }

    public record UsageSummary(
        long sessionCount,
        long totalTokens,
        long inputTokens,
        long outputTokens,
        long activeUsers,
        long activeAgents,
        long activeModels,
        long scheduledSessionCount,
        long manualSessionCount,
        double avgTokensPerSession
    ) {
    }

    public record ComparisonSummary(
        UsageSummary current,
        UsageSummary previous,
        Long tokenDelta,
        Double tokenGrowthRate,
        Long sessionDelta,
        Double sessionGrowthRate
    ) {
    }

    public record TrendPoint(
        String bucket,
        long sessionCount,
        long totalTokens,
        long inputTokens,
        long outputTokens
    ) {
    }

    public record DistributionItem(
        String id,
        String label,
        long sessionCount,
        long totalTokens,
        double percentage
    ) {
    }

    public record TaskExecutionLoad(
        double avgTokensPerTask,
        double avgMessagesPerTask,
        double avgToolResponsesPerTask
    ) {
    }

    public record AgentUsage(
        String agentId,
        long activeUsers,
        long sessionCount,
        long totalTokens,
        long inputTokens,
        long outputTokens,
        double avgTokensPerSession,
        long scheduledSessionCount,
        long highTokenSessionCount
    ) {
    }

    public record UserUsage(
        String userId,
        long activeAgents,
        long sessionCount,
        long totalTokens,
        long inputTokens,
        long outputTokens,
        double avgTokensPerSession,
        Instant lastActiveAt,
        String topAgent
    ) {
    }

    public record ModelUsage(
        String providerName,
        String modelName,
        long sessionCount,
        long activeUsers,
        long activeAgents,
        long totalTokens,
        long inputTokens,
        long outputTokens,
        double avgTokensPerSession
    ) {
    }

    public record OverviewResponse(
        SnapshotStatus snapshotStatus,
        ComparisonSummary summary,
        List<TrendPoint> tokenTrend,
        List<AgentUsage> topAgents,
        List<UserUsage> topUsers,
        List<SessionUsage> topSessions,
        List<ModelUsage> models,
        TaskExecutionLoad taskExecutionLoad,
        List<DistributionItem> sessionTypeDistribution,
        List<DistributionItem> providerDistribution
    ) {
    }

    public record ListResponse<T>(
        SnapshotStatus snapshotStatus,
        List<T> items
    ) {
    }

    public record PageResponse<T>(
        SnapshotStatus snapshotStatus,
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages
    ) {
    }

}
