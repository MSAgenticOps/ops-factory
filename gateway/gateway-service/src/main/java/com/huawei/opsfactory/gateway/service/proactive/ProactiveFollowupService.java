/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Per-user facade over {@link ProactiveFollowups} for Spring callers (the proactive delivery service writes
 * records; IM-reply context injection reads them). Resolves the per-user {@code records.jsonl} path via
 * {@link ProactiveStorage} and delegates the file I/O.
 *
 * <p>Record I/O is best-effort: failures are logged and swallowed (delivery is decoupled, see PRD §5/§7).
 *
 * @author x00000000
 * @since 2026-06-07
 */
@Service
public class ProactiveFollowupService {
    private static final Logger log = LoggerFactory.getLogger(ProactiveFollowupService.class);

    private final ProactiveStorage storage;

    /**
     * Creates the follow-up record service.
     *
     * @param storage per-user proactive path resolver
     */
    public ProactiveFollowupService(ProactiveStorage storage) {
        this.storage = storage;
    }

    /**
     * Appends a follow-up record (best-effort; logged on failure).
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param record record to append
     */
    public void append(String userId, String agentId, ProactiveFollowupRecord record) {
        try {
            ProactiveFollowups.append(storage.followupRecordsFile(userId, agentId), record);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to append follow-up record for {}:{}: {}", agentId, userId, e.getMessage());
        }
    }

    /**
     * Returns whether a record already exists for the session (delivery idempotency).
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param sessionId goosed session id
     * @return {@code true} if a record exists; {@code false} on absence or read failure
     */
    public boolean existsForSession(String userId, String agentId, String sessionId) {
        try {
            return ProactiveFollowups.existsForSession(storage.followupRecordsFile(userId, agentId), sessionId);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to read follow-up records for {}:{}: {}", agentId, userId, e.getMessage());
            return false;
        }
    }

    /**
     * Returns up to {@code limit} most recent records for a target key (for IM-reply context injection).
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param targetKey conversation target key
     * @param limit maximum records to return
     * @return matching records, oldest-first; empty on read failure
     */
    public List<ProactiveFollowupRecord> recentByTargetKey(String userId, String agentId, String targetKey,
        int limit) {
        try {
            return ProactiveFollowups.recentByTargetKey(storage.followupRecordsFile(userId, agentId), targetKey,
                limit);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to read follow-up records for {}:{}: {}", agentId, userId, e.getMessage());
            return List.of();
        }
    }
}
