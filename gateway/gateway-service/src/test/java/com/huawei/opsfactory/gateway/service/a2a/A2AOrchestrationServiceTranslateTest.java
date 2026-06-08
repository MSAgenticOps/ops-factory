/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.huawei.opsfactory.gateway.service.SessionCacheService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Test coverage for the reactive {@link A2AOrchestrationService#translateEvents} seam (progress + terminal framing,
 * idle/total timeout, error handling) without any live goosed.
 *
 * @author x00000000
 * @since 2026-06-05
 */
public class A2AOrchestrationServiceTranslateTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private A2AOrchestrationService service;

    /**
     * Builds the service with mocked collaborators (translateEvents performs no I/O).
     */
    @Before
    public void setUp() {
        service = new A2AOrchestrationService(mock(InstanceManager.class), mock(GoosedProxy.class),
            mock(AgentConfigService.class), mock(A2ASessionStore.class), mock(SessionCacheService.class));
    }

    private Map<String, Object> message(Object... contentItems) {
        return Map.<String, Object>of("type", "Message",
            "message", Map.<String, Object>of("role", "assistant", "content", List.of(contentItems)));
    }

    private Map<String, Object> toolRequest(String name) {
        return Map.<String, Object>of("type", "toolRequest",
            "toolCall", Map.<String, Object>of("value", Map.<String, Object>of("name", name)));
    }

    private Map<String, Object> text(String value) {
        return Map.<String, Object>of("type", "text", "text", value);
    }

    private Map<String, Object> thinking(String value) {
        return Map.<String, Object>of("type", "thinking", "thinking", value);
    }

    private List<Map<String, Object>> run(Flux<Map<String, Object>> events, A2AProgressTranslator tr, Duration idle,
        Duration total) throws Exception {
        List<String> jsons =
            service.translateEvents(events, tr, idle, total).collectList().block(Duration.ofSeconds(10));
        List<Map<String, Object>> out = new ArrayList<>();
        for (String json : jsons) {
            out.add(MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {}));
        }
        return out;
    }

    /**
     * A normal run emits progress frames then a single completed terminal carrying the result.
     */
    @Test
    public void emitsProgressThenTerminalOnFinish() throws Exception {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        Flux<Map<String, Object>> events = Flux.just(
            message(toolRequest("control_center__read_service_logs")),
            message(text("All clear.")),
            message(thinking("internal")),
            Map.<String, Object>of("type", "Finish"));
        List<Map<String, Object>> frames = run(events, tr, Duration.ofSeconds(5), Duration.ofSeconds(30));
        assertEquals(3, frames.size());
        assertEquals("a2a_progress", frames.get(0).get("type"));
        assertEquals("tool_call", frames.get(0).get("kind"));
        assertEquals("a2a_progress", frames.get(1).get("type"));
        assertEquals("text", frames.get(1).get("kind"));
        assertEquals("a2a_result", frames.get(2).get("type"));
        assertEquals("completed", frames.get(2).get("status"));
        assertEquals("All clear.", frames.get(2).get("result"));
    }

    /**
     * An Error event ends the run with an error terminal.
     */
    @Test
    public void emitsErrorTerminalOnErrorEvent() throws Exception {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        Flux<Map<String, Object>> events = Flux.just(message(text("partial")),
            Map.<String, Object>of("type", "Error", "error", "upstream boom"));
        List<Map<String, Object>> frames = run(events, tr, Duration.ofSeconds(5), Duration.ofSeconds(30));
        Map<String, Object> terminal = frames.get(frames.size() - 1);
        assertEquals("a2a_result", terminal.get("type"));
        assertEquals("error", terminal.get("status"));
        assertEquals("upstream boom", terminal.get("error"));
    }

    /**
     * Idle silence past the idle timeout produces a graceful timeout terminal with the partial result.
     */
    @Test
    public void idleTimeoutProducesTimeoutTerminal() throws Exception {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        Flux<Map<String, Object>> events = Flux.concat(Flux.just(message(text("started"))), Flux.never());
        List<Map<String, Object>> frames = run(events, tr, Duration.ofMillis(300), Duration.ofSeconds(30));
        Map<String, Object> terminal = frames.get(frames.size() - 1);
        assertEquals("a2a_result", terminal.get("type"));
        assertEquals("timeout", terminal.get("status"));
        assertEquals("started", terminal.get("result"));
    }

    /**
     * Exceeding the total cap (even with steady progress) produces a timeout terminal.
     */
    @Test
    public void totalCapProducesTimeoutTerminal() throws Exception {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        Flux<Map<String, Object>> events = Flux.interval(Duration.ofMillis(100)).map(i -> message(text("tick")));
        List<Map<String, Object>> frames = run(events, tr, Duration.ofSeconds(5), Duration.ofMillis(400));
        Map<String, Object> terminal = frames.get(frames.size() - 1);
        assertEquals("a2a_result", terminal.get("type"));
        assertEquals("timeout", terminal.get("status"));
    }
}
