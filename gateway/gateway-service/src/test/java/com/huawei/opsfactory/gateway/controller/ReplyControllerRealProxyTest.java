/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.config.GlobalExceptionHandler;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.hook.HookContext;
import com.huawei.opsfactory.gateway.hook.HookPipeline;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.huawei.opsfactory.gateway.service.FileService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRoutes;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Test coverage for Reply Controller Real Proxy.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class ReplyControllerRealProxyTest {
    private static final String AGENT_ID = "test-agent";

    private static final String USER_ID = "alice";

    private static final String SESSION_ID = "session-123";

    private static final String REQUEST_ID = "00000000-0000-0000-0000-000000000001";


    /**
     * Executes the session reply real goosed400 returns gateway error envelope operation.
     *
     * @throws Exception if the operation fails
     */
    @Test
    public void sessionReply_realGoosed400ReturnsGatewayErrorEnvelope() throws Exception {
        DisposableServer server = startReplyErrorServer();
        try {
            ReplyTestHarness harness = buildReplyHarness(server, true);
            when(harness.fileService().listCapsuleRelevantFiles(any())).thenReturn(Collections.emptyList());

            MvcResult result = performReply(harness.mockMvc(), "hello").andExpect(status().isBadRequest()).andReturn();
            assertBodyContains(result, "goosed_active_request_conflict",
                "Session already has an active request. Cancel it first.", SESSION_ID, AGENT_ID, "wait", "cancel",
                "retry");
        } finally {
            server.disposeNow();
        }
    }

    /**
     * Executes the session events real goosed404 returns gateway error envelope operation.
     */
    @Test
    public void sessionEvents_realGoosed404ReturnsGatewayErrorEnvelope() throws Exception {
        DisposableServer server = startEventsNotFoundServer();

        try {
            ReplyTestHarness harness = buildReplyHarness(server, false);
            MvcResult result = performEvents(harness.mockMvc())
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, org.hamcrest.Matchers.containsString(
                    MediaType.TEXT_EVENT_STREAM_VALUE)))
                .andReturn();
            assertBodyContains(result, "event: error", "\"error\":\"Agent resource not found\"");
        } finally {
            server.disposeNow();
        }
    }

    /**
     * Executes the session events active requests drained emits output files after original event operation.
     *
     * @throws Exception if the operation fails
     */
    @Test
    public void sessionEvents_drainedActiveReqEmitsOutputFilesAfterEvent() throws Exception {
        DisposableServer server = startActiveRequestEventsServer();

        try {
            ReplyTestHarness harness = buildReplyHarness(server, true);
            List<Map<String, Object>> beforeFiles = Collections.emptyList();
            List<Map<String, Object>> afterFiles = outputFilesSnapshot();
            when(harness.fileService().listCapsuleRelevantFiles(any())).thenReturn(beforeFiles).thenReturn(afterFiles);
            when(harness.fileService().diffFiles(anyList(), anyList())).thenReturn(changedFilesSnapshot());

            performReply(harness.mockMvc(), "create a file").andExpect(status().isOk());
            String eventBody = dispatchAsyncEvents(harness.mockMvc());
            assertContainsOutputFilesEvent(eventBody);
        } finally {
            server.disposeNow();
        }
    }

    /**
     * Executes the session reply invalid json body still returns gateway error envelope operation.
     */
    @Test
    public void sessionReply_invalidJsonBodyStillReturnsGatewayErrorEnvelope() throws Exception {
        InstanceManager instanceManager = mock(InstanceManager.class);
        HookPipeline hookPipeline = mock(HookPipeline.class);
        AgentConfigService agentConfigService = mock(AgentConfigService.class);
        FileService fileService = mock(FileService.class);

        when(hookPipeline.executeRequest(any(HookContext.class)))
            .thenAnswer(inv -> Mono.just(((HookContext) inv.getArgument(0)).getBody()));
        when(instanceManager.getOrSpawn("test-agent", "alice"))
            .thenReturn(Mono.error(new IllegalStateException("spawn failed")));

        GatewayProperties properties = new GatewayProperties();
        properties.setGooseTls(false);
        GoosedProxy goosedProxy = new GoosedProxy(properties);
        ReplyController controller =
            new ReplyController(instanceManager, goosedProxy, hookPipeline, agentConfigService, fileService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .defaultRequest(post("/").requestAttr(UserContextFilter.USER_ID_ATTR, "alice"))
            .build();

        mockMvc.perform(post("/api/gateway/agents/test-agent/sessions/session-123/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not-json"))
            .andExpect(status().is5xxServerError())
            .andExpect(content().string(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.containsString("gateway_submit_failed"),
                org.hamcrest.Matchers.containsString("session-123"),
                org.hamcrest.Matchers.containsString("test-agent"))));
    }

    /**
     * Executes the session reply snapshot io failure still proxies request operation.
     *
     * @throws Exception if the operation fails
     */
    @Test
    public void sessionReply_snapshotIoFailureStillProxiesRequest() throws Exception {
        DisposableServer server = HttpServer.create()
            .host("127.0.0.1")
            .port(0)
            .route(routes -> routes
                .post("/agent/resume",
                    (request, response) -> response.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .sendString(Mono.just("{\"session\":{\"id\":\"session-123\"}," + "\"extension_results\":[]}")))
                .post("/sessions/session-123/reply",
                    (request, response) -> response.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .sendString(Mono.just("{\"ok\":true}"))))
            .bindNow();

        try {
            InstanceManager instanceManager = mock(InstanceManager.class);
            HookPipeline hookPipeline = mock(HookPipeline.class);
            AgentConfigService agentConfigService = mock(AgentConfigService.class);
            FileService fileService = mock(FileService.class);
            ManagedInstance instance =
                new ManagedInstance("test-agent", "alice", server.port(), 12345L, null, "test-secret");
            instance.setStatus(ManagedInstance.Status.RUNNING);

            when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(instance));
            when(hookPipeline.executeRequest(any(HookContext.class)))
                .thenAnswer(inv -> Mono.just(((HookContext) inv.getArgument(0)).getBody()));
            when(agentConfigService.getUserAgentDir("alice", "test-agent")).thenReturn(Path.of("."));
            when(fileService.listCapsuleRelevantFiles(any())).thenThrow(new IllegalStateException("disk busy"));

            GatewayProperties properties = new GatewayProperties();
            properties.setGooseTls(false);
            GoosedProxy goosedProxy = new GoosedProxy(properties);
            ReplyController controller =
                new ReplyController(instanceManager, goosedProxy, hookPipeline, agentConfigService, fileService);
            MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    request.setAttribute(UserContextFilter.USER_ID_ATTR, "alice");
                    chain.doFilter(request, response);
                })
                .build();

            mockMvc.perform(post("/api/gateway/agents/test-agent/sessions/session-123/reply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"request_id\":\"00000000-0000-0000-0000-000000000001\",\"user_message\":{"
                        + "\"role\":\"user\",\"created\":1776928807}}"))
                .andExpect(status().isOk());
        } finally {
            server.disposeNow();
        }
    }

    private DisposableServer startReplyErrorServer() {
        return startProxyTestServer(routes -> {
            addResumeRoute(routes);
            routes.post("/sessions/" + SESSION_ID + "/reply",
                (request, response) -> response.status(400)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .sendString(Mono.just("Session already has an active request. Cancel it first.")));
        });
    }

    private DisposableServer startEventsNotFoundServer() {
        return startProxyTestServer(routes -> {
            addResumeRoute(routes);
            routes.get("/sessions/" + SESSION_ID + "/events",
                (request, response) -> response.status(404)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                    .sendString(Mono.just("session not found")));
        });
    }

    private DisposableServer startActiveRequestEventsServer() {
        return startProxyTestServer(routes -> {
            addResumeRoute(routes);
            routes.post("/sessions/" + SESSION_ID + "/reply",
                (request, response) -> response.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .sendString(Mono.just("{\"request_id\":\"" + REQUEST_ID + "\"}")));
            routes.get("/sessions/" + SESSION_ID + "/events",
                (request, response) -> response.header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .sendString(Flux.just("data: {\"type\":\"ActiveRequests\",\"request_ids\":[]}\n\n", "")
                        .delayElements(Duration.ofMillis(100))));
        });
    }

    private DisposableServer startProxyTestServer(Consumer<HttpServerRoutes> routesConfigurer) {
        return HttpServer.create().host("127.0.0.1").port(0).route(routesConfigurer::accept).bindNow();
    }

    private void addResumeRoute(HttpServerRoutes routes) {
        routes.post("/agent/resume",
            (request, response) -> response.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .sendString(Mono.just("{\"session\":{\"id\":\"" + SESSION_ID + "\"},\"extension_results\":[]}")));
    }

    private ReplyTestHarness buildReplyHarness(DisposableServer server, boolean filterUserContext) {
        InstanceManager instanceManager = mock(InstanceManager.class);
        HookPipeline hookPipeline = mock(HookPipeline.class);
        AgentConfigService agentConfigService = mock(AgentConfigService.class);
        FileService fileService = mock(FileService.class);
        stubCommonReplyFlow(instanceManager, hookPipeline, agentConfigService, server);
        ReplyController controller =
            new ReplyController(instanceManager, buildGoosedProxy(), hookPipeline, agentConfigService, fileService);
        return new ReplyTestHarness(instanceManager, hookPipeline, agentConfigService, fileService,
            buildMockMvcWithAlice(controller, filterUserContext));
    }

    private void stubCommonReplyFlow(InstanceManager instanceManager, HookPipeline hookPipeline,
        AgentConfigService agentConfigService, DisposableServer server) {
        when(instanceManager.getOrSpawn(AGENT_ID, USER_ID)).thenReturn(Mono.just(runningInstanceFor(server)));
        when(hookPipeline.executeRequest(any(HookContext.class)))
            .thenAnswer(inv -> Mono.just(((HookContext) inv.getArgument(0)).getBody()));
        when(agentConfigService.getUserAgentDir(USER_ID, AGENT_ID)).thenReturn(Path.of("."));
    }

    private ManagedInstance runningInstanceFor(DisposableServer server) {
        ManagedInstance instance = new ManagedInstance(AGENT_ID, USER_ID, server.port(), 12345L, null, "test-secret");
        instance.setStatus(ManagedInstance.Status.RUNNING);
        return instance;
    }

    private GoosedProxy buildGoosedProxy() {
        GatewayProperties properties = new GatewayProperties();
        properties.setGooseTls(false);
        return new GoosedProxy(properties);
    }

    private MockMvc buildMockMvcWithAlice(ReplyController controller, boolean filterUserContext) {
        var builder = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler());
        if (filterUserContext) {
            builder.addFilter((request, response, chain) -> {
                request.setAttribute(UserContextFilter.USER_ID_ATTR, USER_ID);
                chain.doFilter(request, response);
            });
        } else {
            builder.defaultRequest(get("/").requestAttr(UserContextFilter.USER_ID_ATTR, USER_ID));
        }
        return builder.build();
    }

    private ResultActions performReply(MockMvc mockMvc, String userText) throws Exception {
        return mockMvc.perform(post("/api/gateway/agents/" + AGENT_ID + "/sessions/" + SESSION_ID + "/reply")
            .contentType(MediaType.APPLICATION_JSON)
            .content(buildReplyBody(userText)));
    }

    private ResultActions performEvents(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(
            get("/api/gateway/agents/" + AGENT_ID + "/sessions/" + SESSION_ID + "/events").accept(MediaType.TEXT_EVENT_STREAM));
    }

    private String buildReplyBody(String userText) {
        return "{\"request_id\":\"" + REQUEST_ID + "\",\"user_message\":{\"role\":\"user\",\"created\":1776928807,"
            + "\"content\":[{\"type\":\"text\",\"text\":\"" + userText
            + "\"}],\"metadata\":{\"userVisible\":true,\"agentVisible\":true}}}";
    }

    private String dispatchAsyncEvents(MockMvc mockMvc) throws Exception {
        MvcResult initialResult = performEvents(mockMvc).andExpect(status().isOk()).andExpect(request().asyncStarted())
            .andReturn();
        return mockMvc.perform(asyncDispatch(initialResult)).andExpect(status().isOk()).andReturn().getResponse()
            .getContentAsString();
    }

    private void assertContainsOutputFilesEvent(String eventBody) {
        assertBodyContains(eventBody, "\"type\":\"ActiveRequests\"", "\"type\":\"OutputFiles\"",
            "\"request_id\":\"" + REQUEST_ID + "\"");
    }

    private void assertBodyContains(MvcResult result, String... fragments) throws Exception {
        assertBodyContains(result.getResponse().getContentAsString(), fragments);
    }

    private void assertBodyContains(String body, String... fragments) {
        for (String fragment : fragments) {
            org.junit.Assert.assertTrue("Body should contain: " + fragment, body.contains(fragment));
        }
    }

    private List<Map<String, Object>> outputFilesSnapshot() {
        return List.of(Map.of("path", "goose-intro.md", "name", "goose-intro.md", "type", "md", "rootId",
            "workingDir", "displayPath", "goose-intro.md", "size", 16, "modifiedAt", "2026-04-25T00:00:00Z"));
    }

    private List<Map<String, String>> changedFilesSnapshot() {
        return List.of(Map.of("path", "goose-intro.md", "name", "goose-intro.md", "ext", "md", "rootId",
            "workingDir", "displayPath", "goose-intro.md"));
    }

    private record ReplyTestHarness(InstanceManager instanceManager, HookPipeline hookPipeline,
                                    AgentConfigService agentConfigService, FileService fileService, MockMvc mockMvc) {
    }
}
