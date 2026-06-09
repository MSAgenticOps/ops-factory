/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.channel.ChannelConfigService;
import com.huawei.opsfactory.gateway.service.channel.ChannelRuntimeStorageService;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelBinding;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelSummary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.scheduler.Schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Polls completed scheduled goosed runs and, for schedules opted into IM delivery, pushes the run's final report
 * to the user's bound IM channels and appends a {@link ProactiveFollowupRecord} (PRD §5.2/§7).
 *
 * <p>The IM provider boundary is the file-based channel outbox: this service only writes an outbox command
 * ({@code {id, to, text, createdAt}}) to each channel's {@code outbox/pending/}; the existing channel pumps deliver
 * it externally. Delivery is best-effort and decoupled — no binding / a failure only logs (WARN); the run always
 * stays in the WebApp Inbox regardless.
 *
 * <p>Idempotency: a session already recorded in {@code records.jsonl} is never re-delivered (durable across
 * restarts); an in-process set additionally suppresses re-evaluation. Startup mass-delivery of historical reports
 * is avoided by ignoring sessions older than {@code max-age-minutes}. A still-running session (the schedule's
 * current run) is skipped until it completes.
 *
 * @author x00000000
 * @since 2026-06-07
 */
@Service
public class ProactiveDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(ProactiveDeliveryService.class);

    private static final int MAX_PROCESSED_SESSIONS = 10_000;

    private final InstanceManager instanceManager;

    private final GoosedProxy goosedProxy;

    private final ProactiveDeliveryMarkerService markerService;

    private final ProactiveFollowupService followupService;

    private final ChannelConfigService channelConfigService;

    private final ChannelRuntimeStorageService runtimeStorageService;

    private final ObjectMapper mapper = new ObjectMapper();

    /** Guards against overlapping scans when a poll outlives the fixed delay. */
    private final AtomicBoolean scanning = new AtomicBoolean(false);

    /**
     * Session ids handled this process lifetime (delivered or permanently skipped), to avoid re-evaluation. Bounded
     * and FIFO-evicted so it cannot grow without limit; durable idempotency for delivered runs is records.jsonl.
     */
    private final Set<String> processedSessions = Collections.synchronizedSet(Collections.newSetFromMap(
        new LinkedHashMap<>(256, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
                return size() > MAX_PROCESSED_SESSIONS;
            }
        }));

    private final boolean enabled;

    private final long maxAgeMinutes;

    /**
     * Creates the proactive delivery service.
     *
     * @param instanceManager running goosed instance registry
     * @param goosedProxy HTTP proxy to goosed
     * @param markerService delivery-marker reader (which schedules push to IM)
     * @param followupService follow-up record store (idempotency + audit)
     * @param channelConfigService channel + binding lookup
     * @param runtimeStorageService channel runtime path resolver (outbox)
     * @param enabled whether proactive IM delivery is active
     * @param maxAgeMinutes ignore scheduled runs older than this (avoids startup backfill of historical reports)
     */
    public ProactiveDeliveryService(InstanceManager instanceManager, GoosedProxy goosedProxy,
        ProactiveDeliveryMarkerService markerService, ProactiveFollowupService followupService,
        ChannelConfigService channelConfigService, ChannelRuntimeStorageService runtimeStorageService,
        @Value("${gateway.proactive-delivery.enabled:true}") boolean enabled,
        @Value("${gateway.proactive-delivery.max-age-minutes:60}") long maxAgeMinutes) {
        this.instanceManager = instanceManager;
        this.goosedProxy = goosedProxy;
        this.markerService = markerService;
        this.followupService = followupService;
        this.channelConfigService = channelConfigService;
        this.runtimeStorageService = runtimeStorageService;
        this.enabled = enabled;
        this.maxAgeMinutes = maxAgeMinutes;
    }

    /**
     * Periodically scans running instances for newly completed scheduled runs and delivers eligible ones.
     */
    @Scheduled(initialDelayString = "${gateway.proactive-delivery.poll-interval-ms:30000}",
        fixedDelayString = "${gateway.proactive-delivery.poll-interval-ms:30000}")
    public void scheduledPoll() {
        if (!enabled || !scanning.compareAndSet(false, true)) {
            // Disabled, or a previous scan is still running — skip this tick to avoid overlap.
            return;
        }
        // The scan makes blocking goosed calls; run it off the shared scheduler thread so it never starves the other
        // @Scheduled tasks (channel pumps, idle watchdog).
        Schedulers.boundedElastic().schedule(() -> {
            try {
                pollAndDeliver();
            } finally {
                scanning.set(false);
            }
        });
    }

    /**
     * Scans all running instances for completed scheduled runs and delivers eligible ones. Package-private and
     * synchronous so it is directly unit-testable; production invocation is via {@link #scheduledPoll()}.
     */
    void pollAndDeliver() {
        for (ManagedInstance instance : instanceManager.getAllInstances()) {
            if (instance.getStatus() != ManagedInstance.Status.RUNNING) {
                continue;
            }
            try {
                processInstance(instance);
            } catch (RuntimeException e) {
                // Resilience boundary: one instance's arbitrary unchecked failure must not abort the whole scan.
                log.warn("Proactive delivery scan failed for {}:{}: {}", instance.getAgentId(), instance.getUserId(),
                    e.getMessage());
            }
        }
    }

    private void processInstance(ManagedInstance instance) {
        List<Map<String, Object>> sessions = fetchScheduledSessions(instance);
        if (sessions.isEmpty()) {
            return;
        }
        Set<String> runningSessionIds = fetchRunningSessionIds(instance);
        for (Map<String, Object> session : sessions) {
            processSession(instance, session, runningSessionIds);
        }
    }

    private void processSession(ManagedInstance instance, Map<String, Object> session, Set<String> runningSessionIds) {
        String sessionId = asString(session.get("id"));
        String scheduleId = asString(session.get("schedule_id"));
        if (sessionId == null || scheduleId == null) {
            return;
        }
        // goosed session ids are per-instance and can collide across users, so dedupe by (agent,user) + session id —
        // never by session id alone, or one user's run would wrongly suppress another user's same-id run.
        String dedupeKey = instance.getKey() + "::" + sessionId;
        if (processedSessions.contains(dedupeKey)) {
            return;
        }
        if (runningSessionIds.contains(sessionId)) {
            // Still the schedule's active run — wait for it to finish before delivering.
            return;
        }
        if (isOlderThanMaxAge(asString(session.get("created_at")))) {
            // Historical run (e.g. on gateway restart): skip permanently so we never mass-deliver stale reports.
            processedSessions.add(dedupeKey);
            return;
        }
        String userId = instance.getUserId();
        String agentId = instance.getAgentId();
        if (followupService.existsForSession(userId, agentId, sessionId)) {
            processedSessions.add(dedupeKey);
            return;
        }
        String deliver;
        try {
            deliver = markerService.readDeliver(userId, agentId, scheduleId);
        } catch (IOException e) {
            // Transient read failure: do NOT mark processed, so the next poll retries instead of silently dropping
            // a delivery the user opted into.
            log.warn("Deferring proactive delivery decision (deliver marker unreadable) for {}:{} scheduleId={}: {}",
                agentId, userId, oneLine(scheduleId), e.getMessage());
            return;
        }
        if (!ProactiveDeliveryMarkers.DELIVER_IM.equalsIgnoreCase(deliver)) {
            // Not opted into IM delivery — Inbox留底 only.
            processedSessions.add(dedupeKey);
            return;
        }
        String report = extractFinalReport(instance, sessionId);
        if (report == null || report.isBlank()) {
            // No final assistant text yet (run may still be wrapping up); retry on the next poll.
            return;
        }
        deliver(instance, scheduleId, sessionId, report);
        processedSessions.add(dedupeKey);
    }

    private void deliver(ManagedInstance instance, String scheduleId, String sessionId, String report) {
        String userId = instance.getUserId();
        String agentId = instance.getAgentId();
        // scheduleId/sessionId originate from goosed session data; strip CR/LF before logging or putting them in the
        // MDC so they cannot forge log records (CWE-117).
        String safeScheduleId = oneLine(scheduleId);
        MDC.put("scheduleId", safeScheduleId);
        MDC.put("sessionId", oneLine(sessionId));
        try {
            int delivered = 0;
            for (ChannelSummary channel : channelConfigService.listChannels(userId)) {
                if (!channel.enabled() || channel.bindingCount() <= 0) {
                    continue;
                }
                delivered += deliverToChannel(userId, agentId, channel, scheduleId, sessionId, report);
            }
            if (delivered == 0) {
                log.warn("Proactive report not delivered: no IM binding for {}:{} scheduleId={}", agentId, userId,
                    safeScheduleId);
            } else {
                log.info("Delivered proactive report to {} IM conversation(s) for {}:{} scheduleId={} reportLen={}",
                    delivered, agentId, userId, safeScheduleId, report.length());
            }
        } finally {
            MDC.remove("scheduleId");
            MDC.remove("sessionId");
        }
    }

    /** Strips CR/LF so externally-sourced ids cannot forge log records (CWE-117). */
    private static String oneLine(String value) {
        return value == null ? null : value.replace('\r', '_').replace('\n', '_');
    }

    private int deliverToChannel(String userId, String agentId, ChannelSummary channel, String scheduleId,
        String sessionId, String report) {
        ChannelDetail detail = channelConfigService.getChannel(channel.id(), userId);
        if (detail == null) {
            return 0;
        }
        int delivered = 0;
        for (ChannelBinding binding : channelConfigService.listBindings(channel.id(), userId)) {
            if (binding.peerId() == null || binding.peerId().isBlank()) {
                continue;
            }
            if (!writeOutboxCommand(detail, binding.peerId(), report)) {
                continue;
            }
            String targetKey = ChannelTargetKey.of(channel.type(), channel.id(), binding.accountId(),
                binding.conversationId(), binding.threadId());
            followupService.append(userId, agentId, new ProactiveFollowupRecord(Instant.now().toString(), scheduleId,
                sessionId, targetKey, report));
            delivered++;
        }
        return delivered;
    }

    private boolean writeOutboxCommand(ChannelDetail channel, String peerId, String text) {
        Path pendingDir = runtimeStorageService.outboxPendingDirectory(channel);
        Map<String, Object> payload = new LinkedHashMap<>();
        String id = UUID.randomUUID().toString();
        payload.put("id", id);
        payload.put("to", peerId);
        payload.put("text", text);
        payload.put("createdAt", Instant.now().toString());
        try {
            Files.createDirectories(pendingDir);
            Files.writeString(pendingDir.resolve(id + ".json"),
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            log.warn("Failed to enqueue proactive outbox command for channel {}: {}", channel.id(), e.getMessage());
            return false;
        }
    }

    private List<Map<String, Object>> fetchScheduledSessions(ManagedInstance instance) {
        Map<String, Object> parsed = fetchJsonObject(instance, "/sessions");
        List<Map<String, Object>> sessions = asObjectList(parsed == null ? null : parsed.get("sessions"));
        List<Map<String, Object>> scheduled = new ArrayList<>();
        for (Map<String, Object> session : sessions) {
            if (asString(session.get("schedule_id")) != null) {
                scheduled.add(session);
            }
        }
        return scheduled;
    }

    private Set<String> fetchRunningSessionIds(ManagedInstance instance) {
        Map<String, Object> parsed = fetchJsonObject(instance, "/schedule/list");
        Set<String> running = new HashSet<>();
        for (Map<String, Object> job : asObjectList(parsed == null ? null : parsed.get("jobs"))) {
            if (Boolean.TRUE.equals(job.get("currently_running"))) {
                String current = asString(job.get("current_session_id"));
                if (current != null) {
                    running.add(current);
                }
            }
        }
        return running;
    }

    private String extractFinalReport(ManagedInstance instance, String sessionId) {
        Map<String, Object> session = fetchJsonObject(instance, "/sessions/" + sessionId);
        if (session == null) {
            return null;
        }
        String lastVisibleText = null;
        for (Map<String, Object> message : extractMessages(session)) {
            if (!"assistant".equals(message.get("role")) || isHidden(message.get("metadata"))) {
                continue;
            }
            String text = textContent(message.get("content"));
            if (!text.isBlank()) {
                lastVisibleText = text;
            }
        }
        return lastVisibleText == null ? null : lastVisibleText.trim();
    }

    private List<Map<String, Object>> extractMessages(Map<String, Object> session) {
        // goosed's Session.conversation may serialize either as a bare message array or as an object wrapping a
        // "messages" array; also tolerate a top-level "messages" field. Handle all three shapes defensively.
        Object conversation = session.get("conversation");
        if (conversation instanceof Map<?, ?> wrapped) {
            return asObjectList(wrapped.get("messages"));
        }
        if (conversation instanceof List<?>) {
            return asObjectList(conversation);
        }
        return asObjectList(session.get("messages"));
    }

    private boolean isHidden(Object metadata) {
        return metadata instanceof Map<?, ?> map && Boolean.FALSE.equals(map.get("userVisible"));
    }

    private String textContent(Object rawContent) {
        StringBuilder out = new StringBuilder();
        if (rawContent instanceof List<?> items) {
            for (Object item : items) {
                if (item instanceof Map<?, ?> content && "text".equals(content.get("type"))
                    && content.get("text") != null) {
                    out.append(content.get("text"));
                }
            }
        }
        return out.toString();
    }

    private boolean isOlderThanMaxAge(String createdAt) {
        if (createdAt == null) {
            return false;
        }
        try {
            return Instant.parse(createdAt).isBefore(Instant.now().minus(Duration.ofMinutes(maxAgeMinutes)));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private Map<String, Object> fetchJsonObject(ManagedInstance instance, String path) {
        try {
            String json = goosedProxy.fetchJson(instance.getPort(), path, instance.getSecretKey()).block();
            if (json == null || json.isBlank()) {
                return null;
            }
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() { });
        } catch (RuntimeException | IOException e) {
            // Best-effort fetch: a reactive .block() error (wrapped as RuntimeException) or a JSON/IO failure must
            // degrade gracefully (caller treats null as "skip"), not abort the delivery cycle.
            log.warn("Proactive delivery failed to fetch {} from {}:{}: {}", path, instance.getAgentId(),
                instance.getUserId(), e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asObjectList(Object value) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    out.add((Map<String, Object>) map);
                }
            }
        }
        return out;
    }

    private String asString(Object value) {
        return value instanceof String s && !s.isBlank() ? s : null;
    }
}
