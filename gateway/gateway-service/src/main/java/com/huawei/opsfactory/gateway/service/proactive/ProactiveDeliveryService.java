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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    private static final int GOOSED_TIMEOUT_SEC = 20;

    private final InstanceManager instanceManager;

    private final GoosedProxy goosedProxy;

    private final ProactiveDeliveryMarkerService markerService;

    private final ProactiveFollowupService followupService;

    private final ChannelConfigService channelConfigService;

    private final ChannelRuntimeStorageService runtimeStorageService;

    private final ObjectMapper mapper = new ObjectMapper();

    /** Sessions already handled this process lifetime (delivered or permanently skipped); avoids re-evaluation. */
    private final Set<String> processedSessions = ConcurrentHashMap.newKeySet();

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
    public void pollAndDeliver() {
        if (!enabled) {
            return;
        }
        for (ManagedInstance instance : instanceManager.getAllInstances()) {
            if (instance.getStatus() != ManagedInstance.Status.RUNNING) {
                continue;
            }
            try {
                processInstance(instance);
            } catch (RuntimeException e) {
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
        if (sessionId == null || scheduleId == null || processedSessions.contains(sessionId)) {
            return;
        }
        if (runningSessionIds.contains(sessionId)) {
            // Still the schedule's active run — wait for it to finish before delivering.
            return;
        }
        if (isOlderThanMaxAge(asString(session.get("created_at")))) {
            // Historical run (e.g. on gateway restart): skip permanently so we never mass-deliver stale reports.
            processedSessions.add(sessionId);
            return;
        }
        String userId = instance.getUserId();
        String agentId = instance.getAgentId();
        if (followupService.existsForSession(userId, agentId, sessionId)) {
            processedSessions.add(sessionId);
            return;
        }
        if (!ProactiveDeliveryMarkers.DELIVER_IM.equals(markerService.getDeliver(userId, agentId, scheduleId))) {
            // Not opted into IM delivery — Inbox留底 only.
            processedSessions.add(sessionId);
            return;
        }
        String report = extractFinalReport(instance, sessionId);
        if (report == null || report.isBlank()) {
            // No final assistant text yet (run may still be wrapping up); retry on the next poll.
            return;
        }
        deliver(instance, scheduleId, sessionId, report);
        processedSessions.add(sessionId);
    }

    private void deliver(ManagedInstance instance, String scheduleId, String sessionId, String report) {
        String userId = instance.getUserId();
        String agentId = instance.getAgentId();
        MDC.put("scheduleId", scheduleId);
        MDC.put("sessionId", sessionId);
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
                    scheduleId);
            } else {
                log.info("Delivered proactive report to {} IM conversation(s) for {}:{} scheduleId={} reportLen={}",
                    delivered, agentId, userId, scheduleId, report.length());
            }
        } finally {
            MDC.remove("scheduleId");
            MDC.remove("sessionId");
        }
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
        Set<String> running = ConcurrentHashMap.newKeySet();
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
        Object conversation = session.get("conversation");
        if (conversation == null) {
            conversation = session.get("messages");
        }
        String lastVisibleText = null;
        for (Map<String, Object> message : asObjectList(conversation)) {
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
        } catch (RuntimeException e) {
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
