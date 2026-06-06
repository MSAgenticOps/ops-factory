/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * Persists per-user A2A sub-session side-records as JSON under {@code gateway/users/{userId}/a2a/sessions.json}.
 *
 * <p>Mirrors the channel binding store pattern (read-modify-write JSON, no DB). Backs three things: the
 * {@code agent_call} history classification, the nesting/depth guard ({@link #isAgentCallSession}), and offline
 * listability of sub-sessions whose target instance has been idle-reaped.
 *
 * <p>The file store is not cross-process concurrency safe (read-modify-write without file locking, same as the
 * existing channel stores); A2A volume is low so this is acceptable. An in-process {@link ReentrantLock} guards
 * same-JVM races.
 *
 * @author x00000000
 * @since 2026-06-05
 */
@Service
public class A2ASessionStore {
    private static final Logger log = LoggerFactory.getLogger(A2ASessionStore.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern SAFE_USER = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final ReentrantLock lock = new ReentrantLock();

    private final GatewayProperties properties;

    /**
     * Creates the A2A session store.
     *
     * @param properties gateway properties (resolves the gateway root path)
     */
    public A2ASessionStore(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Resolves the side-record file path for a user, guarding against path traversal.
     *
     * @param userId the owning user id
     * @return the {@code sessions.json} path
     */
    public Path storeFile(String userId) {
        Path usersRoot = properties.getGatewayRootPath().resolve("users").normalize();
        Path userDir = usersRoot.resolve(requireSafeUser(userId)).resolve("a2a").normalize();
        if (!userDir.startsWith(usersRoot)) {
            throw new IllegalArgumentException("userId must stay within the users directory");
        }
        return userDir.resolve("sessions.json");
    }

    /**
     * Appends (or replaces, keyed by sub-session id) a side-record.
     *
     * @param record the record to persist
     */
    public void record(A2ASessionRecord record) {
        lock.lock();
        try {
            List<A2ASessionRecord> records = new ArrayList<>(readAll(record.callerUserId()));
            records.removeIf(r -> r.subSessionId().equals(record.subSessionId()));
            records.add(record);
            writeAll(record.callerUserId(), records);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Updates the lifecycle status of an existing sub-session record (no-op if not found).
     *
     * @param userId the owning user id
     * @param subSessionId the sub-session id
     * @param status the new status (see {@link A2ASessionRecord} constants)
     */
    public void updateStatus(String userId, String subSessionId, String status) {
        lock.lock();
        try {
            List<A2ASessionRecord> records = new ArrayList<>(readAll(userId));
            boolean changed = false;
            for (int i = 0; i < records.size(); i++) {
                A2ASessionRecord r = records.get(i);
                if (r.subSessionId().equals(subSessionId)) {
                    records.set(i, r.withStatus(status));
                    changed = true;
                    break;
                }
            }
            if (changed) {
                writeAll(userId, records);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Lists all sub-session records for a user (newest entries last, as written).
     *
     * @param userId the owning user id
     * @return the records (empty if none)
     */
    public List<A2ASessionRecord> listForUser(String userId) {
        return readAll(userId);
    }

    /**
     * Finds a sub-session record by its sub-session id.
     *
     * @param userId the owning user id
     * @param subSessionId the sub-session id
     * @return the record if present
     */
    public Optional<A2ASessionRecord> find(String userId, String subSessionId) {
        if (subSessionId == null || subSessionId.isBlank()) {
            return Optional.empty();
        }
        for (A2ASessionRecord r : readAll(userId)) {
            if (r.subSessionId().equals(subSessionId)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    /**
     * Whether the caller's session is itself a recorded A2A sub-run delegated to {@code agentId}. Used as the nesting
     * guard: a caller whose own session is an A2A sub-run must not delegate again (would be {@code depth > 0}).
     *
     * <p>Matched by BOTH session id and agent: goosed session ids are per-instance (not globally unique), so an
     * initiator's session id can coincide with another agent's sub-session id — only the agent-qualified pair
     * identifies a true sub-run.
     *
     * @param userId the owning user id
     * @param agentId the caller's agent id ({@code x-a2a-origin}); the agent its session would have run on
     * @param sessionId the caller's session id
     * @return {@code true} if the session is a recorded sub-run on {@code agentId}
     */
    public boolean isAgentCallSession(String userId, String agentId, String sessionId) {
        if (agentId == null || agentId.isBlank() || sessionId == null || sessionId.isBlank()) {
            return false;
        }
        for (A2ASessionRecord r : readAll(userId)) {
            if (sessionId.equals(r.subSessionId()) && agentId.equals(r.targetAgentId())) {
                return true;
            }
        }
        return false;
    }

    private List<A2ASessionRecord> readAll(String userId) {
        Path file;
        try {
            file = storeFile(userId);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
        try {
            if (!Files.exists(file)) {
                return List.of();
            }
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                return List.of();
            }
            Map<String, Object> wrapper = MAPPER.readValue(content, new TypeReference<Map<String, Object>>() {});
            return MAPPER.convertValue(wrapper.getOrDefault("records", List.of()),
                new TypeReference<List<A2ASessionRecord>>() {});
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to read A2A session store {}: {}", file, e.getMessage());
            return List.of();
        }
    }

    private void writeAll(String userId, List<A2ASessionRecord> records) {
        Path file = storeFile(userId);
        try {
            Files.createDirectories(file.getParent());
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("records", records);
            Files.writeString(file, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper),
                StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write A2A session store: " + file, e);
        }
    }

    private String requireSafeUser(String userId) {
        if (userId == null || userId.isBlank() || !SAFE_USER.matcher(userId).matches() || ".".equals(userId)
            || "..".equals(userId)) {
            throw new IllegalArgumentException("userId contains unsafe path characters");
        }
        return userId;
    }
}
