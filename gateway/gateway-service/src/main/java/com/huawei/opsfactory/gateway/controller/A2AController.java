/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.common.constants.GatewayConstants;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.service.a2a.A2AOrchestrationService;
import com.huawei.opsfactory.gateway.service.a2a.A2ASessionStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * A2A (agent-to-agent) delegation endpoint. The {@code delegation} MCP extension running inside an initiating agent's
 * goosed calls {@code POST /api/gateway/agents/{targetAgentId}/a2a} to delegate a task; the gateway runs the target as
 * a one-off sub-session and streams condensed progress + a terminal result back over SSE.
 *
 * <p>Identity headers ({@code x-user-id}, {@code x-a2a-origin}, {@code x-a2a-origin-session}) are self-reported by the
 * extension (user/agent from its CWD, session id from goose's {@code agent-session-id} {@code _meta}). The nesting
 * guard is derived here from the side-record: a caller whose own session is itself a recorded sub-run is rejected with
 * 409 (depth is never trusted from the request).
 *
 * @author x00000000
 * @since 2026-06-05
 */
@RestController
@RestSchema(schemaId = "a2aController")
@RequestMapping("/api/gateway")
public class A2AController {
    private static final Logger log = LoggerFactory.getLogger(A2AController.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final A2AOrchestrationService orchestrationService;

    private final A2ASessionStore a2aSessionStore;

    /**
     * Creates the A2A controller.
     *
     * @param orchestrationService runs the delegated sub-session and streams frames
     * @param a2aSessionStore side-record store (backs the nesting guard)
     */
    public A2AController(A2AOrchestrationService orchestrationService, A2ASessionStore a2aSessionStore) {
        this.orchestrationService = orchestrationService;
        this.a2aSessionStore = a2aSessionStore;
    }

    /**
     * Delegates a task to the target agent and streams {@code a2a_progress} + terminal {@code a2a_result} frames.
     *
     * @param targetAgentId the delegated-to agent id (path)
     * @param body JSON body carrying {@code message} (the verbatim task)
     * @param request the HTTP request (user id attribute + A2A identity headers)
     * @return an SSE emitter for the run
     */
    @PostMapping(value = "/agents/{targetAgentId}/a2a", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter delegate(@PathVariable("targetAgentId") String targetAgentId,
        @RequestBody(required = false) String body, HttpServletRequest request) {
        String userId = (String) request.getAttribute(UserContextFilter.USER_ID_ATTR);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user id is required");
        }
        String originAgentId = trimToNull(request.getHeader(GatewayConstants.HEADER_A2A_ORIGIN));
        String originSessionId = trimToNull(request.getHeader(GatewayConstants.HEADER_A2A_ORIGIN_SESSION));
        String message = extractMessage(body);
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message is required");
        }

        // Nesting guard: a caller whose own session is already a recorded A2A sub-run (on its own agent) must not
        // delegate again. Qualified by agent because per-instance session ids are not globally unique.
        if (originSessionId != null && a2aSessionStore.isAgentCallSession(userId, originAgentId, originSessionId)) {
            log.warn("[A2A] nested delegation rejected userId={} originSession={} target={}", userId, originSessionId,
                targetAgentId);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "nested agent-to-agent delegation is not allowed");
        }

        log.info("[A2A] delegate target={} userId={} origin={} originSession={} msgLen={}", targetAgentId, userId,
            originAgentId, originSessionId, message.length());
        return orchestrationService.delegate(targetAgentId, userId, message, originAgentId, originSessionId);
    }

    private String extractMessage(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            Map<String, Object> map = MAPPER.readValue(body, new TypeReference<Map<String, Object>>() {});
            Object value = map.get("message");
            return value == null ? null : String.valueOf(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private static String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
