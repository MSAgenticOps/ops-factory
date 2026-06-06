/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.common.constants.GatewayConstants;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.service.a2a.A2AOrchestrationService;
import com.huawei.opsfactory.gateway.service.a2a.A2ASessionStore;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Test coverage for the A2A controller nesting/validation guard.
 *
 * @author x00000000
 * @since 2026-06-05
 */
public class A2AControllerDepthGuardTest {
    private A2AOrchestrationService orchestration;

    private A2ASessionStore store;

    private A2AController controller;

    /**
     * Builds the controller with mocked collaborators.
     */
    @Before
    public void setUp() {
        orchestration = mock(A2AOrchestrationService.class);
        store = mock(A2ASessionStore.class);
        controller = new A2AController(orchestration, store);
    }

    private MockHttpServletRequest request(String userId, String originSession) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (userId != null) {
            request.setAttribute(UserContextFilter.USER_ID_ATTR, userId);
        }
        request.addHeader(GatewayConstants.HEADER_A2A_ORIGIN, "agentA");
        if (originSession != null) {
            request.addHeader(GatewayConstants.HEADER_A2A_ORIGIN_SESSION, originSession);
        }
        return request;
    }

    /**
     * A top-level delegation (caller session is not a sub-run) proceeds to orchestration.
     */
    @Test
    public void topLevelDelegationProceeds() {
        when(store.isAgentCallSession("alice", "agentA", "A1")).thenReturn(false);
        SseEmitter emitter = new SseEmitter();
        when(orchestration.delegate(eq("agentB"), eq("alice"), eq("do X"), eq("agentA"), eq("A1")))
            .thenReturn(emitter);
        SseEmitter result = controller.delegate("agentB", "{\"message\":\"do X\"}", request("alice", "A1"));
        assertSame(emitter, result);
        verify(orchestration).delegate("agentB", "alice", "do X", "agentA", "A1");
    }

    /**
     * A nested delegation (caller session is itself a recorded sub-run) is rejected with 409.
     */
    @Test
    public void nestedDelegationRejectedWith409() {
        when(store.isAgentCallSession("alice", "agentA", "B1")).thenReturn(true);
        try {
            controller.delegate("agentC", "{\"message\":\"do Y\"}", request("alice", "B1"));
            fail("expected 409");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }
        verify(orchestration, never()).delegate(any(), any(), any(), any(), any());
    }

    /**
     * A missing message is a 400.
     */
    @Test
    public void missingMessageRejectedWith400() {
        try {
            controller.delegate("agentB", "{}", request("alice", "A1"));
            fail("expected 400");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
        verify(orchestration, never()).delegate(any(), any(), any(), any(), any());
    }

    /**
     * A missing user id is a 400.
     */
    @Test
    public void missingUserRejectedWith400() {
        try {
            controller.delegate("agentB", "{\"message\":\"hi\"}", request(null, "A1"));
            fail("expected 400");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    /**
     * An agent delegating to itself (target == origin) is rejected with 409 before any orchestration.
     */
    @Test
    public void selfDelegationRejectedWith409() {
        try {
            controller.delegate("agentA", "{\"message\":\"do X\"}", request("alice", "A1"));
            fail("expected 409");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }
        verify(orchestration, never()).delegate(any(), any(), any(), any(), any());
    }
}
