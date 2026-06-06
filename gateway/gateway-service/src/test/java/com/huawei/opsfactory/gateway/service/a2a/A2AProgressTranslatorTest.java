/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Test coverage for the A2A progress translator (event -> a2a_progress / a2a_result mapping).
 *
 * @author x00000000
 * @since 2026-06-05
 */
public class A2AProgressTranslatorTest {
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

    /**
     * Thinking content is never surfaced.
     */
    @Test
    public void dropsThinking() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        assertTrue(tr.progressFor(message(thinking("secret reasoning"))).isEmpty());
        assertEquals("", tr.resultFrame().get("result"));
    }

    /**
     * A tool request becomes a tool_call frame with a friendly label.
     */
    @Test
    public void mapsToolCallWithFriendlyLabel() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        List<Map<String, Object>> frames = tr.progressFor(message(toolRequest("control_center__read_service_logs")));
        assertEquals(1, frames.size());
        Map<String, Object> frame = frames.get(0);
        assertEquals("a2a_progress", frame.get("type"));
        assertEquals("tool_call", frame.get("kind"));
        assertEquals("read service logs", frame.get("label"));
        assertEquals("agentB", frame.get("target_agent"));
        assertEquals("B1", frame.get("sub_session_id"));
        assertEquals(1, frame.get("step"));
        assertEquals(1, frame.get("tool_calls"));
    }

    /**
     * Assistant text becomes a text frame and accumulates into the result.
     */
    @Test
    public void mapsTextAndAccumulatesResult() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        tr.progressFor(message(text("Part one. ")));
        tr.progressFor(message(text("Part two.")));
        assertEquals("Part one. Part two.", tr.resultFrame().get("result"));
    }

    /**
     * The step counter increments across events.
     */
    @Test
    public void stepCountsAcrossEvents() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        tr.progressFor(message(toolRequest("dev__shell")));
        List<Map<String, Object>> second = tr.progressFor(message(toolRequest("dev__editor")));
        assertEquals(2, second.get(0).get("step"));
        assertEquals(2, second.get(0).get("tool_calls"));
    }

    /**
     * Text fragments stream as steps but must not inflate the tool-call count (the displayed metric).
     */
    @Test
    public void textDoesNotCountAsToolCall() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        List<Map<String, Object>> frames = tr.progressFor(message(text("hello")));
        assertEquals(1, frames.size());
        assertEquals("text", frames.get(0).get("kind"));
        assertEquals(0, frames.get(0).get("tool_calls"));
    }

    /**
     * Friendly tool names drop the extension prefix and de-snake-case.
     */
    @Test
    public void friendlyToolNameStripsPrefix() {
        assertEquals("shell", A2AProgressTranslator.friendlyToolName("developer__shell"));
        assertEquals("read service logs",
            A2AProgressTranslator.friendlyToolName("control_center__read_service_logs"));
        assertEquals("working", A2AProgressTranslator.friendlyToolName(null));
        assertEquals("plain", A2AProgressTranslator.friendlyToolName("plain"));
    }

    /**
     * A Finish terminal yields completed status and the accumulated result.
     */
    @Test
    public void terminalFinishGivesCompleted() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        tr.progressFor(message(text("done")));
        assertTrue(tr.observeTerminal(Map.of("type", "Finish")));
        assertEquals(A2ASessionRecord.STATUS_COMPLETED, tr.status());
        Map<String, Object> result = tr.resultFrame();
        assertEquals("a2a_result", result.get("type"));
        assertEquals("completed", result.get("status"));
        assertEquals("done", result.get("result"));
    }

    /**
     * An Error terminal yields error status carrying the error text.
     */
    @Test
    public void terminalErrorGivesError() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        assertTrue(tr.observeTerminal(Map.of("type", "Error", "error", "kaboom")));
        assertEquals(A2ASessionRecord.STATUS_ERROR, tr.status());
        assertEquals("kaboom", tr.resultFrame().get("error"));
    }

    /**
     * A run that produced progress but never saw a terminal event is reported as a timeout.
     */
    @Test
    public void noTerminalGivesTimeoutStatus() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        tr.progressFor(message(text("partial")));
        assertEquals(A2ASessionRecord.STATUS_TIMEOUT, tr.status());
    }

    /**
     * Non-assistant messages are ignored.
     */
    @Test
    public void nonAssistantMessageIgnored() {
        A2AProgressTranslator tr = new A2AProgressTranslator("agentB", "B1");
        Map<String, Object> userMsg = Map.<String, Object>of("type", "Message",
            "message", Map.<String, Object>of("role", "user", "content", List.of(text("hi"))));
        assertTrue(tr.progressFor(userMsg).isEmpty());
    }
}
