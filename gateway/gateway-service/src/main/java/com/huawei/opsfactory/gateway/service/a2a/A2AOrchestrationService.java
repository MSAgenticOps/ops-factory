/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

import com.huawei.opsfactory.gateway.common.constants.GatewayConstants;
import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.huawei.opsfactory.gateway.service.SessionCacheService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Orchestrates one A2A delegated run: spawns/locates the target (B) instance, creates a one-off sub-session, drives a
 * reply, consumes B's events, and streams condensed {@code a2a_progress} frames plus a terminal {@code a2a_result}
 * frame back to the caller (the {@code delegation} MCP extension) over SSE.
 *
 * <p>The nesting/depth guard is enforced by the controller before this runs (a caller whose own session is already a
 * recorded sub-run is rejected). All editorial logic — filtering thinking, mapping tool calls to friendly labels,
 * timeouts, terminal framing — lives here so the extension stays a thin relay.
 *
 * @author x00000000
 * @since 2026-06-05
 */
@Service
public class A2AOrchestrationService {
    private static final Logger log = LoggerFactory.getLogger(A2AOrchestrationService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int TITLE_MAX = 60;

    /** Idle timeout: abort B if no progress event arrives within this window. */
    private final Duration idleTimeout = Duration.ofMinutes(3);

    /** Total cap: abort B if the run exceeds this wall-clock budget. */
    private final Duration totalTimeout = Duration.ofMinutes(15);

    private final InstanceManager instanceManager;

    private final GoosedProxy goosedProxy;

    private final AgentConfigService agentConfigService;

    private final A2ASessionStore a2aSessionStore;

    private final SessionCacheService sessionCacheService;

    private final WebClient webClient;

    /**
     * Creates the orchestration service.
     *
     * @param instanceManager goosed instance lifecycle manager
     * @param goosedProxy proxy for goosed HTTP/SSE
     * @param agentConfigService resolves per-user agent working directories
     * @param a2aSessionStore A2A sub-session side-record store
     * @param sessionCacheService session list cache (invalidated when a sub-session is created)
     */
    public A2AOrchestrationService(InstanceManager instanceManager, GoosedProxy goosedProxy,
        AgentConfigService agentConfigService, A2ASessionStore a2aSessionStore,
        SessionCacheService sessionCacheService) {
        this.instanceManager = instanceManager;
        this.goosedProxy = goosedProxy;
        this.agentConfigService = agentConfigService;
        this.a2aSessionStore = a2aSessionStore;
        this.sessionCacheService = sessionCacheService;
        this.webClient = goosedProxy.getWebClient();
    }

    /**
     * Runs a delegated call and returns an SSE emitter that streams {@code a2a_progress} frames followed by one
     * terminal {@code a2a_result} frame. The caller (extension) relays progress as MCP notifications and returns the
     * terminal result as the {@code call_agent} tool result.
     *
     * @param targetAgentId the delegated-to agent id
     * @param userId the original user identity (A2A never crosses users)
     * @param message the verbatim task for the target
     * @param originAgentId the initiating agent id
     * @param originSessionId the initiating (parent) session id
     * @return an SSE emitter for the run
     */
    public SseEmitter delegate(String targetAgentId, String userId, String message, String originAgentId,
        String originSessionId) {
        ManagedInstance instance = instanceManager.getOrSpawn(targetAgentId, userId).block();
        if (instance == null) {
            throw new IllegalStateException("Failed to resolve target agent instance: " + targetAgentId);
        }
        instance.touch();
        instanceManager.touchAllForUser(userId);

        String subSessionId = startSubSession(instance, targetAgentId, userId);
        a2aSessionStore.record(new A2ASessionRecord(subSessionId, originSessionId, originAgentId, targetAgentId, userId,
            Instant.now().toString(), A2ASessionRecord.STATUS_RUNNING, title(message)));
        sessionCacheService.invalidate(userId);

        String requestId = UUID.randomUUID().toString();
        postReply(instance, subSessionId, requestId, message);
        log.info("[A2A] run started target={} userId={} sub={} origin={} originSession={}", targetAgentId, userId,
            subSessionId, originAgentId, originSessionId);

        A2AProgressTranslator translator = new A2AProgressTranslator(targetAgentId, subSessionId);
        Flux<String> frames =
            translateEvents(consumeBEvents(instance, subSessionId, requestId), translator, idleTimeout, totalTimeout);

        SseEmitter emitter = new SseEmitter(totalTimeout.plusMinutes(1).toMillis());
        AtomicBoolean finished = new AtomicBoolean(false);
        Disposable subscription = frames.subscribeOn(Schedulers.boundedElastic())
            .subscribe(json -> {
                try {
                    emitter.send(SseEmitter.event().data(json));
                } catch (IOException | IllegalStateException e) {
                    log.warn("[A2A] send failed sub={} (client gone): {}", subSessionId, e.getMessage());
                }
            }, err -> {
                log.warn("[A2A] stream error sub={}: {}", subSessionId, err.getMessage());
                a2aSessionStore.updateStatus(userId, targetAgentId, subSessionId, A2ASessionRecord.STATUS_ERROR);
                emitter.completeWithError(err);
            }, () -> {
                finished.set(true);
                String status = translator.status();
                a2aSessionStore.updateStatus(userId, targetAgentId, subSessionId, status);
                if (A2ASessionRecord.STATUS_TIMEOUT.equals(status) || A2ASessionRecord.STATUS_ERROR.equals(status)) {
                    // Gateway gave up (idle/total timeout or stream error) before B finished — tell B to stop too,
                    // otherwise the sub-run keeps executing on goosed after we returned a terminal frame.
                    postCancel(instance, subSessionId, requestId);
                }
                log.info("[A2A] run finished target={} sub={} status={}", targetAgentId, subSessionId, status);
                emitter.complete();
            });

        Runnable abort = () -> {
            subscription.dispose();
            if (finished.compareAndSet(false, true)) {
                // Caller disconnected before the terminal frame (goose cancelled the call_agent tool): cancel B.
                log.info("[A2A] caller disconnected, cancelling target sub={}", subSessionId);
                postCancel(instance, subSessionId, requestId);
                a2aSessionStore.updateStatus(userId, targetAgentId, subSessionId, A2ASessionRecord.STATUS_CANCELLED);
            }
        };
        emitter.onTimeout(abort);
        emitter.onError(e -> abort.run());
        emitter.onCompletion(abort);
        return emitter;
    }

    /**
     * Transforms a target session's parsed events into condensed A2A SSE frames (JSON strings): zero or more
     * {@code a2a_progress} frames per event followed by exactly one terminal {@code a2a_result} frame. Thinking is
     * dropped; idle/total timeouts and stream errors are converted into a graceful terminal frame rather than an
     * error. This is the pure, I/O-free seam (unit-tested with StepVerifier).
     *
     * @param events the parsed, request-filtered target session events
     * @param translator the per-run translator (carries step/result/terminal state)
     * @param idle idle timeout (no progress within this window aborts)
     * @param total total wall-clock cap
     * @return a flux of JSON frame strings ending in one terminal frame
     */
    Flux<String> translateEvents(Flux<Map<String, Object>> events, A2AProgressTranslator translator, Duration idle,
        Duration total) {
        return events.timeout(idle)
            .takeUntilOther(Mono.delay(total))
            .takeUntil(translator::observeTerminal)
            .concatMap(event -> Flux.fromIterable(translator.progressFor(event)).map(this::toJson))
            .onErrorResume(err -> {
                // Idle TimeoutException → no terminal observed → status() reports timeout; real errors → mark error.
                if (!(err instanceof TimeoutException)) {
                    translator.markError(err.getMessage());
                }
                return Flux.empty();
            })
            .concatWith(Flux.defer(() -> Flux.just(toJson(translator.resultFrame()))));
    }

    private Flux<Map<String, Object>> consumeBEvents(ManagedInstance instance, String subSessionId, String requestId) {
        return webClient.get()
            .uri(goosedSessionUrl(instance, subSessionId, "events"))
            .header(GatewayConstants.HEADER_SECRET_KEY, instance.getSecretKey())
            .accept(MediaType.TEXT_EVENT_STREAM)
            .retrieve()
            .bodyToFlux(DataBuffer.class)
            .transform(this::decodeSseEvents)
            .map(this::parseEventJson)
            .filter(event -> belongsToRequest(event, requestId));
    }

    private String startSubSession(ManagedInstance instance, String agentId, String userId) {
        Path workingDir = agentConfigService.getUserAgentDir(userId, agentId).toAbsolutePath().normalize();
        String startBody = writeJson(Map.of("working_dir", workingDir.toString()));
        String startResponse =
            goosedProxy.fetchJson(instance.getPort(), HttpMethod.POST, "/agent/start", startBody, 120,
                instance.getSecretKey()).block();
        String subSessionId = extractSessionId(startResponse);
        String resumeBody = writeJson(Map.of("session_id", subSessionId, "load_model_and_extensions", true));
        goosedProxy.fetchJson(instance.getPort(), HttpMethod.POST, "/agent/resume", resumeBody, 120,
            instance.getSecretKey()).block();
        instance.markSessionResumed(subSessionId);
        return subSessionId;
    }

    private void postReply(ManagedInstance instance, String subSessionId, String requestId, String message) {
        Map<String, Object> userMessage = new LinkedHashMap<>();
        userMessage.put("role", "user");
        userMessage.put("created", Math.floorDiv(System.currentTimeMillis(), 1000));
        userMessage.put("content", List.of(Map.of("type", "text", "text", message)));
        userMessage.put("metadata", Map.of("userVisible", true, "agentVisible", true));
        String body = writeJson(Map.of("request_id", requestId, "user_message", userMessage));
        goosedProxy.fetchJson(instance.getPort(), HttpMethod.POST,
            goosedSessionPath(subSessionId, "reply"), body, 120, instance.getSecretKey()).block();
    }

    private void postCancel(ManagedInstance instance, String subSessionId, String requestId) {
        try {
            String body = writeJson(Map.of("request_id", requestId));
            goosedProxy.fetchJson(instance.getPort(), HttpMethod.POST,
                goosedSessionPath(subSessionId, "cancel"), body, 30, instance.getSecretKey()).block();
        } catch (RuntimeException e) {
            log.warn("[A2A] cancel propagation failed sub={}: {}", subSessionId, e.getMessage());
        }
    }

    private String goosedSessionUrl(ManagedInstance instance, String sessionId, String suffix) {
        return goosedProxy.goosedBaseUrl(instance.getPort()) + goosedSessionPath(sessionId, suffix);
    }

    private String goosedSessionPath(String sessionId, String suffix) {
        return "/sessions/" + org.springframework.web.util.UriUtils.encodePathSegment(sessionId, StandardCharsets.UTF_8)
            + "/" + suffix;
    }

    private boolean belongsToRequest(Map<String, Object> event, String requestId) {
        Object type = event.get("type");
        if ("ActiveRequests".equals(type) || "Ping".equals(type)) {
            return false;
        }
        Object chatRequestId = event.get("chat_request_id");
        Object eventRequestId = event.get("request_id");
        if (chatRequestId == null && eventRequestId == null) {
            return true;
        }
        return requestId.equals(String.valueOf(chatRequestId)) || requestId.equals(String.valueOf(eventRequestId));
    }

    private Flux<String> decodeSseEvents(Flux<DataBuffer> buffers) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            Disposable inner = buffers.subscribe(dataBuffer -> {
                try {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // Normalize CRLF so both "\n\n" and "\r\n\r\n" frame separators are handled.
                    buffer.append(new String(bytes, StandardCharsets.UTF_8).replace("\r\n", "\n"));
                } finally {
                    DataBufferUtils.release(dataBuffer);
                }

                int separatorIndex;
                while ((separatorIndex = buffer.indexOf("\n\n")) >= 0) {
                    String eventBlock = buffer.substring(0, separatorIndex);
                    buffer.delete(0, separatorIndex + 2);

                    StringBuilder dataLines = new StringBuilder();
                    for (String line : eventBlock.split("\n")) {
                        if (line.startsWith("data:")) {
                            if (!dataLines.isEmpty()) {
                                dataLines.append('\n');
                            }
                            dataLines.append(line.substring(5).trim());
                        }
                    }
                    if (!dataLines.isEmpty()) {
                        sink.next(dataLines.toString());
                    }
                }
            }, sink::error, sink::complete);
            // Propagate downstream cancellation (terminal / idle / total timeout) to the upstream goosed /events
            // connection, and release buffers — otherwise the SSE connection and pooled buffers leak per run.
            sink.onCancel(inner);
            sink.onDispose(inner);
        });
    }

    private Map<String, Object> parseEventJson(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse A2A SSE event: " + json, e);
        }
    }

    private String extractSessionId(String startResponse) {
        Map<String, Object> map;
        try {
            map = MAPPER.readValue(startResponse, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse sub-session start response", e);
        }
        Object id = map.get("id");
        if (id == null) {
            throw new IllegalStateException("Sub-session id missing from start response");
        }
        return id.toString();
    }

    private String title(String message) {
        if (message == null) {
            return "";
        }
        String trimmed = message.strip();
        return trimmed.length() <= TITLE_MAX ? trimmed : trimmed.substring(0, TITLE_MAX - 1).strip() + "…";
    }

    private String toJson(Map<String, Object> frame) {
        try {
            return MAPPER.writeValueAsString(frame);
        } catch (JsonProcessingException e) {
            return "{\"type\":\"a2a_result\",\"status\":\"error\",\"error\":\"serialize failed\"}";
        }
    }

    private String writeJson(Map<String, Object> payload) {
        try {
            return MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize A2A request payload", e);
        }
    }
}
