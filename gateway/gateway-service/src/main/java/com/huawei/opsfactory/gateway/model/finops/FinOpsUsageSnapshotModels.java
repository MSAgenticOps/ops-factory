/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.model.finops;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Data contracts exposed by the gateway FinOps usage snapshot endpoint.
 *
 * @since 2026-05-28
 */
public final class FinOpsUsageSnapshotModels {

    private FinOpsUsageSnapshotModels() {
    }

    /**
     * Snapshot payload containing normalized session and message usage facts.
     *
     * @param sessions normalized session usage records
     * @param messages normalized message records
     * @param sourceDbCount number of source session databases discovered
     * @param skippedDbCount number of source session databases skipped during reading
     * @param dataSource source data root used for the scan
     * @param lastError latest source read error, if any
     */
    public record SnapshotPayload(
        List<SessionUsageRecord> sessions,
        List<SessionMessageRecord> messages,
        int sourceDbCount,
        int skippedDbCount,
        String dataSource,
        String lastError
    ) {
    }

    /**
     * Normalized session-level usage record.
     */
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

    /**
     * Normalized message-level record for session detail inspection.
     */
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
}
