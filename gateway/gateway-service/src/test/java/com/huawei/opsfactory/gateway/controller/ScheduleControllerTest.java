/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveDeliveryMarkerService;

import reactor.core.publisher.Mono;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test coverage for Schedule Controller.
 *
 * @author x00000000
 * @since 2026-05-30
 */
public class ScheduleControllerTest {
    private static final String TEST_AGENT_ID = "test-agent";

    private static final String TEST_USER_ID = "alice";

    private static final String SECRET_KEY = "test-secret";

    private static final int INSTANCE_PORT = 9000;

    private InstanceManager instanceManager;

    private GoosedProxy goosedProxy;

    private ProactiveDeliveryMarkerService deliveryMarkerService;

    private ScheduleController controller;

    /**
     * Initializes test fixtures.
     */
    @Before
    public void setUp() {
        instanceManager = mock(InstanceManager.class);
        goosedProxy = mock(GoosedProxy.class);
        deliveryMarkerService = mock(ProactiveDeliveryMarkerService.class);
        controller = new ScheduleController(instanceManager, goosedProxy, deliveryMarkerService);
    }

    /**
     * Tests listing schedules proxies to goosed.
     */
    @Test
    public void listSchedules_proxiesToGoosed() {
        MockHttpServletRequest request = request("GET", "/gateway/agents/test-agent/schedule/list");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.GET), eq("/schedule/list"), eq(null), eq(30),
            eq(SECRET_KEY))).thenReturn(Mono.just("{\"jobs\":[]}"));

        ResponseEntity<String> result = controller.listSchedules(TEST_AGENT_ID, request);

        assertEquals("{\"jobs\":[]}", result.getBody());
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaders().getContentType());
    }

    /**
     * Tests listing schedules annotates each job with its Gateway-side deliver marker (im / null) keyed by job id.
     */
    @Test
    public void listSchedules_annotatesDeliverMarkers() {
        MockHttpServletRequest request = request("GET", "/gateway/agents/test-agent/schedule/list");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.GET), eq("/schedule/list"), eq(null), eq(30),
            eq(SECRET_KEY))).thenReturn(Mono.just("{\"jobs\":[{\"id\":\"job-1\"},{\"id\":\"job-2\"}]}"));
        // job-1 is opted into IM delivery; job-2 has no marker (the mock returns null by default).
        when(deliveryMarkerService.getDeliver(TEST_USER_ID, TEST_AGENT_ID, "job-1")).thenReturn("im");

        ResponseEntity<String> result = controller.listSchedules(TEST_AGENT_ID, request);

        assertEquals("{\"jobs\":[{\"id\":\"job-1\",\"deliver\":\"im\"},{\"id\":\"job-2\",\"deliver\":null}]}",
            result.getBody());
    }

    /**
     * Tests listing schedules passes a non-JSON goosed response through unchanged (no annotation, no failure).
     */
    @Test
    public void listSchedules_passesThroughNonJson() {
        MockHttpServletRequest request = request("GET", "/gateway/agents/test-agent/schedule/list");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.GET), eq("/schedule/list"), eq(null), eq(30),
            eq(SECRET_KEY))).thenReturn(Mono.just("{bad"));

        ResponseEntity<String> result = controller.listSchedules(TEST_AGENT_ID, request);

        assertEquals("{bad", result.getBody());
    }

    /**
     * Tests creating a schedule proxies the request body and, with no deliver flag, clears any delivery marker.
     */
    @Test
    public void createSchedule_proxiesBody() {
        MockHttpServletRequest request = request("POST", "/gateway/agents/test-agent/schedule/create");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.POST), eq("/schedule/create"),
            eq("{\"id\":\"job-1\"}"), eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{\"id\":\"job-1\"}"));

        ResponseEntity<String> result =
            controller.createSchedule(TEST_AGENT_ID, "{\"id\":\"job-1\"}", null, request);

        assertEquals("{\"id\":\"job-1\"}", result.getBody());
        verify(deliveryMarkerService).remove(TEST_USER_ID, TEST_AGENT_ID, "job-1");
    }

    /**
     * Tests creating a schedule with deliver=im persists the IM delivery marker keyed by schedule id.
     */
    @Test
    public void createSchedule_withDeliverIm_marksSchedule() {
        MockHttpServletRequest request = request("POST", "/gateway/agents/test-agent/schedule/create");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.POST), eq("/schedule/create"),
            eq("{\"id\":\"job-1\"}"), eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{\"id\":\"job-1\"}"));

        controller.createSchedule(TEST_AGENT_ID, "{\"id\":\"job-1\"}", "im", request);

        verify(deliveryMarkerService).setDeliver(TEST_USER_ID, TEST_AGENT_ID, "job-1", "im");
    }

    /**
     * Tests schedule list sessions forwards the query string.
     */
    @Test
    public void listScheduleSessions_forwardsQueryString() {
        MockHttpServletRequest request = request("GET", "/gateway/agents/test-agent/schedule/job-1/sessions");
        request.setQueryString("limit=5");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.GET), eq("/schedule/job-1/sessions?limit=5"),
            eq(null), eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("[]"));

        ResponseEntity<String> result = controller.listScheduleSessions(TEST_AGENT_ID, "job-1", request);

        assertEquals("[]", result.getBody());
        verify(goosedProxy).fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.GET), eq("/schedule/job-1/sessions?limit=5"),
            eq(null), eq(30), eq(SECRET_KEY));
    }

    /**
     * Tests delete proxies to the expected goosed path.
     */
    @Test
    public void deleteSchedule_proxiesDelete() {
        MockHttpServletRequest request = request("DELETE", "/gateway/agents/test-agent/schedule/delete/job-1");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.DELETE), eq("/schedule/delete/job-1"), eq(null),
            eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{}"));

        ResponseEntity<String> result = controller.deleteSchedule(TEST_AGENT_ID, "job-1", request);

        assertEquals("{}", result.getBody());
        verify(deliveryMarkerService).remove(TEST_USER_ID, TEST_AGENT_ID, "job-1");
    }

    /**
     * Tests run now, pause, unpause, kill, and inspect endpoints proxy to goosed.
     */
    @Test
    public void scheduleActions_proxyExpectedPaths() {
        MockHttpServletRequest request = request("POST", "/gateway/agents/test-agent/schedule/job-1/run_now");
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.POST), eq("/schedule/job-1/run_now"), eq(null),
            eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{\"session_id\":\"s1\"}"));
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.POST), eq("/schedule/job-1/pause"), eq(null),
            eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{}"));
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.POST), eq("/schedule/job-1/unpause"), eq(null),
            eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{}"));
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.POST), eq("/schedule/job-1/kill"), eq(null),
            eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{}"));
        when(goosedProxy.fetchJson(eq(INSTANCE_PORT), eq(HttpMethod.GET), eq("/schedule/job-1/inspect"), eq(null),
            eq(30), eq(SECRET_KEY))).thenReturn(Mono.just("{}"));

        assertEquals("{\"session_id\":\"s1\"}", controller.runScheduleNow(TEST_AGENT_ID, "job-1", request).getBody());
        assertEquals("{}", controller.pauseSchedule(TEST_AGENT_ID, "job-1", request).getBody());
        assertEquals("{}", controller.unpauseSchedule(TEST_AGENT_ID, "job-1", request).getBody());
        assertEquals("{}", controller.killSchedule(TEST_AGENT_ID, "job-1", request).getBody());
        assertEquals("{}", controller.inspectSchedule(TEST_AGENT_ID, "job-1", request).getBody());
    }

    private MockHttpServletRequest request(String method, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.setAttribute(UserContextFilter.USER_ID_ATTR, TEST_USER_ID);
        ManagedInstance instance =
            new ManagedInstance(TEST_AGENT_ID, TEST_USER_ID, INSTANCE_PORT, 123L, null, SECRET_KEY);
        when(instanceManager.getOrSpawn(TEST_AGENT_ID, TEST_USER_ID)).thenReturn(Mono.just(instance));
        return request;
    }
}
