/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.huawei.opsfactory.gateway.service.SessionCacheService;
import com.huawei.opsfactory.gateway.service.SessionService;
import com.huawei.opsfactory.gateway.service.a2a.A2ASessionRecord;
import com.huawei.opsfactory.gateway.service.a2a.A2ASessionStore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Test coverage for the agent_call session classification in {@link SessionController#listAllSessions}.
 *
 * @author x00000000
 * @since 2026-06-05
 */
public class SessionControllerA2AClassificationTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String USER = "alice";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private InstanceManager instanceManager;

    private SessionService sessionService;

    private SessionCacheService sessionCacheService;

    private SessionController controller;

    private ManagedInstance instB;

    /**
     * Builds a controller with mocked goosed-backed services and a real on-disk A2A store seeded with one live and
     * one offline (reaped) sub-session.
     */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        instanceManager = mock(InstanceManager.class);
        sessionService = mock(SessionService.class);
        GoosedProxy goosedProxy = mock(GoosedProxy.class);
        AgentConfigService agentConfigService = mock(AgentConfigService.class);
        sessionCacheService = mock(SessionCacheService.class);

        GatewayProperties properties = new GatewayProperties();
        GatewayProperties.Paths paths = new GatewayProperties.Paths();
        paths.setProjectRoot(tempFolder.getRoot().getAbsolutePath());
        properties.setPaths(paths);
        A2ASessionStore store = new A2ASessionStore(properties);

        controller = new SessionController(instanceManager, sessionService, goosedProxy, agentConfigService,
            sessionCacheService, store);

        // Cache always misses and runs the supplier inline.
        when(sessionCacheService.get(USER)).thenReturn(null);
        when(sessionCacheService.getOrFetch(eq(USER), any())).thenAnswer(inv -> {
            Supplier<List<Map<String, Object>>> supplier = inv.getArgument(1);
            return supplier.get();
        });

        // One running instance for agentB exposing a normal user session (U1) and a live a2a sub-run (B1).
        instB = mock(ManagedInstance.class);
        when(instB.getUserId()).thenReturn(USER);
        when(instB.getStatus()).thenReturn(ManagedInstance.Status.RUNNING);
        when(instB.getAgentId()).thenReturn("agentB");
        when(instanceManager.getAllInstances()).thenReturn(List.of(instB));
        String goosedJson = "{\"sessions\":["
            + "{\"id\":\"U1\",\"name\":\"normal chat\",\"created_at\":\"2026-06-05T09:00:00Z\","
            + "\"session_type\":\"user\"},"
            + "{\"id\":\"B1\",\"name\":\"sub run\",\"created_at\":\"2026-06-05T10:00:00Z\","
            + "\"session_type\":\"user\"}]}";
        when(sessionService.getSessionsFromInstance(instB)).thenReturn(Mono.just(goosedJson));

        // B1 is the live sub-run above; B2 is an offline (idle-reaped) sub-run only present in the side-record.
        store.record(new A2ASessionRecord("B1", "A1", "agentA", "agentB", USER, "2026-06-05T10:00:00Z",
            A2ASessionRecord.STATUS_COMPLETED, "investigate logs"));
        store.record(new A2ASessionRecord("B2", "A2", "agentA", "agentB", USER, "2026-06-05T08:00:00Z",
            A2ASessionRecord.STATUS_COMPLETED, "older sub run"));
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(UserContextFilter.USER_ID_ATTR, USER);
        return request;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> list(String type) throws Exception {
        String json = controller.listAllSessions(1, 50, null, null, type, request());
        Map<String, Object> resp = MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        return (List<Map<String, Object>>) resp.get("sessions");
    }

    private static Set<String> ids(List<Map<String, Object>> sessions) {
        Set<String> ids = new HashSet<>();
        for (Map<String, Object> s : sessions) {
            ids.add((String) s.get("id"));
        }
        return ids;
    }

    /**
     * The user tab must not contain A2A sub-runs (neither live nor offline).
     */
    @Test
    public void userTabExcludesA2a() throws Exception {
        Set<String> ids = ids(list("user"));
        assertTrue(ids.contains("U1"));
        assertFalse("live a2a sub-run must not appear in user tab", ids.contains("B1"));
        assertFalse("offline a2a sub-run must not appear in user tab", ids.contains("B2"));
    }

    /**
     * The agent_call tab contains both the live (tagged) and offline (merged) sub-runs, with a2a metadata.
     */
    @Test
    public void agentCallTabIncludesLiveAndOffline() throws Exception {
        List<Map<String, Object>> sessions = list("agent_call");
        Set<String> ids = ids(sessions);
        assertTrue("live a2a sub-run tagged from side-record", ids.contains("B1"));
        assertTrue("offline a2a sub-run merged from side-record", ids.contains("B2"));
        assertFalse(ids.contains("U1"));
        Map<String, Object> b2 =
            sessions.stream().filter(s -> "B2".equals(s.get("id"))).findFirst().orElseThrow();
        assertEquals("a2a", b2.get("origin"));
        assertEquals("agentB", b2.get("agentId"));
        assertEquals("agentA", b2.get("a2a_origin_agent_id"));
    }

    /**
     * The all tab (no type filter) includes user and agent_call sessions alike.
     */
    @Test
    public void allTabIncludesEverything() throws Exception {
        Set<String> ids = ids(list(null));
        assertTrue(ids.contains("U1"));
        assertTrue(ids.contains("B1"));
        assertTrue(ids.contains("B2"));
    }

    /**
     * A normal session on a DIFFERENT agent whose id collides with a recorded sub-run id (goosed session ids are
     * per-instance, not globally unique) must NOT be mis-classified as agent_call: tagging is keyed by
     * (agentId, sub-session id), not by id alone.
     */
    @Test
    public void collidingIdOnDifferentAgentIsNotMistagged() throws Exception {
        // agentC exposes a normal user session whose id "B1" collides with agentB's recorded sub-run B1.
        ManagedInstance instC = mock(ManagedInstance.class);
        when(instC.getUserId()).thenReturn(USER);
        when(instC.getStatus()).thenReturn(ManagedInstance.Status.RUNNING);
        when(instC.getAgentId()).thenReturn("agentC");
        when(sessionService.getSessionsFromInstance(instC)).thenReturn(Mono.just(
            "{\"sessions\":[{\"id\":\"B1\",\"name\":\"agentC normal\",\"created_at\":\"2026-06-05T11:00:00Z\","
                + "\"session_type\":\"user\"}]}"));
        when(instanceManager.getAllInstances()).thenReturn(List.of(instB, instC));

        boolean agentCB1InUser = list("user").stream()
            .anyMatch(s -> "B1".equals(s.get("id")) && "agentC".equals(s.get("agentId")));
        assertTrue("colliding normal session on agentC must remain in the user tab", agentCB1InUser);

        boolean agentCB1InAgentCall = list("agent_call").stream()
            .anyMatch(s -> "B1".equals(s.get("id")) && "agentC".equals(s.get("agentId")));
        assertFalse("agentC's normal session must not be classified agent_call", agentCB1InAgentCall);
    }
}
