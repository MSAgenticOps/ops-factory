/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.channel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveContextInjector;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Focused tests for SessionBridgeService reply extraction helpers.
 */
public class SessionBridgeServiceTest {
    private SessionBridgeService service;

    private Method extractFinalAssistantText;

    /**
     * Sets up the service and private method access.
     *
     * @throws Exception if reflection setup fails
     */
    @Before
    public void setUp() throws Exception {
        GoosedProxy goosedProxy = mock(GoosedProxy.class);
        when(goosedProxy.getWebClient()).thenReturn(WebClient.builder().build());
        service = new SessionBridgeService(mock(ChannelConfigService.class), mock(ChannelBindingService.class),
            mock(InstanceManager.class), goosedProxy, mock(AgentConfigService.class),
            mock(ProactiveContextInjector.class));
        extractFinalAssistantText =
            SessionBridgeService.class.getDeclaredMethod("extractFinalAssistantText", List.class, String.class);
        extractFinalAssistantText.setAccessible(true);
    }

    /**
     * Verifies visible assistant text is concatenated while hidden and non-assistant items are ignored.
     *
     * @throws Exception if invocation fails
     */
    @Test
    public void extractFinalAssistantText_collectsVisibleAssistantTextOnly() throws Exception {
        List<Map<String, Object>> events = List.of(
            Map.of("type", "Message", "message",
                Map.of("role", "assistant", "metadata", Map.of("userVisible", false), "content",
                    List.of(Map.of("type", "text", "text", "hidden")))),
            Map.of("type", "Message", "message",
                Map.of("role", "user", "content", List.of(Map.of("type", "text", "text", "ignored")))),
            Map.of("type", "Message", "message",
                Map.of("role", "assistant", "metadata", Map.of("userVisible", true), "content",
                    List.of(Map.of("type", "text", "text", "hello "), Map.of("type", "other", "text", "skip"),
                        Map.of("type", "text", "text", "world")))),
            Map.of("type", "Ping"));

        String reply = (String) extractFinalAssistantText.invoke(service, events, "session-1");

        assertEquals("hello world", reply);
    }

    /**
     * Verifies upstream error events still surface as failures.
     */
    @Test
    public void extractFinalAssistantText_throwsForErrorEvent() {
        List<Map<String, Object>> events = List.of(Map.of("type", "Error", "error", "boom"));

        try {
            extractFinalAssistantText.invoke(service, events, "session-1");
        } catch (InvocationTargetException ex) {
            assertTrue(ex.getCause() instanceof IllegalStateException);
            assertEquals("boom", ex.getCause().getMessage());
            return;
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Unexpected exception type", ex);
        }

        throw new AssertionError("Expected extractFinalAssistantText to throw");
    }

    /**
     * Verifies error events without details use the fallback error message.
     */
    @Test
    public void extractFinalAssistantText_throwsFallbackForErrorWithoutMessage() {
        Map<String, Object> event = new HashMap<>();
        event.put("type", "Error");
        event.put("error", null);

        try {
            extractFinalAssistantText.invoke(service, List.of(event), "session-1");
        } catch (InvocationTargetException ex) {
            assertTrue(ex.getCause() instanceof IllegalStateException);
            assertEquals("Unknown reply error", ex.getCause().getMessage());
            return;
        } catch (IllegalAccessException ex) {
            throw new AssertionError("Unexpected exception type", ex);
        }

        throw new AssertionError("Expected extractFinalAssistantText to throw");
    }

    /**
     * Verifies visible assistant messages with empty content return an empty reply.
     *
     * @throws Exception if invocation fails
     */
    @Test
    public void extractFinalAssistantText_returnsEmptyReplyForEmptyAssistantContent() throws Exception {
        List<Map<String, Object>> events = List.of(Map.of("type", "Message", "message",
            Map.of("role", "assistant", "metadata", Map.of("userVisible", true), "content", List.of())));

        String reply = (String) extractFinalAssistantText.invoke(service, events, "session-1");

        assertEquals("", reply);
    }
}
