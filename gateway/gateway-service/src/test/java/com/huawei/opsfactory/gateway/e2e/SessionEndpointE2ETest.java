/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.e2e;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.service.SessionCacheService;

import reactor.core.publisher.Mono;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * E2E tests for SessionController endpoints:
 * POST /agents/{agentId}/agent/start
 * GET /sessions
 * GET /agents/{agentId}/sessions
 * GET /agents/{agentId}/sessions/{sessionId}
 * DELETE /agents/{agentId}/sessions/{sessionId}
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class SessionEndpointE2ETest extends BaseE2ETest {
    @Autowired
    private SessionCacheService sessionCacheService;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private ManagedInstance runningInstance;

    private Path platformTempDir;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        sessionCacheService.invalidate("alice");
        runningInstance = new ManagedInstance("test-agent", "alice", 9999, 12345L, null, "test-secret");
        runningInstance.setStatus(ManagedInstance.Status.RUNNING);

        platformTempDir = tempFolder.getRoot().toPath().resolve("users");

        // Mock getUserAgentDir for startSession working_dir injection
        when(agentConfigService.getUsersDir()).thenReturn(platformTempDir);
        when(agentConfigService.getUserAgentDir(any(String.class), any(String.class)))
            .thenAnswer(inv -> platformTempDir
                .resolve(inv.getArgument(0, String.class))
                .resolve("agents")
                .resolve(inv.getArgument(1, String.class)));
    }

    /**
     * Executes the start session authenticated calls start then resume operation.
     */
    @Test
    public void startSession_authenticated_callsStartThenResume() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"id\":\"session-123\"}"));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"session\":{\"id\":\"session-123\"},\"extension_results\":[]}"));

        webClient.post()
            .uri("/api/gateway/agents/test-agent/agent/start")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"session_name\":\"test-session\"}")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo("session-123");

        // Verify canonical flow: start → resume(load_model_and_extensions=true)
        verify(goosedProxy).fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"), anyString(), anyInt(),
            anyString());
        verify(goosedProxy).fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"),
            org.mockito.ArgumentMatchers.contains("\"load_model_and_extensions\":true"), anyInt(), anyString());
    }

    /**
     * Executes the start session resume fails propagates error operation.
     */
    @Test
    public void startSession_resumeFails_propagatesError() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"id\":\"session-123\"}"));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.error(new RuntimeException("Extension loading failed")));

        webClient.post()
            .uri("/api/gateway/agents/test-agent/agent/start")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus()
            .is5xxServerError();
    }

    /**
     * Executes the start session resume receives correct session id operation.
     */
    @Test
    public void startSession_resumeReceivesCorrectSessionId() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"id\":\"abc-def-456\",\"name\":\"New Chat\"}"));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"session\":{\"id\":\"abc-def-456\"},\"extension_results\":[]}"));

        webClient.post()
            .uri("/api/gateway/agents/test-agent/agent/start")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus()
            .isOk();

        // Verify resume is called with the correct session ID from start response
        verify(goosedProxy).fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"),
            org.mockito.ArgumentMatchers.contains("\"session_id\":\"abc-def-456\""), anyInt(), anyString());
    }

    /**
     * Executes the start session returns start response not resume response operation.
     */
    @Test
    public void startSession_returnsStartResponse_notResumeResponse() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        String startResponse = "{\"id\":\"session-123\",\"name\":\"New Chat\",\"working_dir\":\"/tmp\"}";
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just(startResponse));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"), anyString(), anyInt(),
            anyString()))
            .thenReturn(Mono.just("{\"session\":{\"id\":\"session-123\"},\"extension_results\":"
                + "[{\"name\":\"developer\",\"success\":true}]}"));

        webClient.post()
            .uri("/api/gateway/agents/test-agent/agent/start")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            // Should return the original start response (Session JSON), not the resume response
            .jsonPath("$.id")
            .isEqualTo("session-123")
            .jsonPath("$.name")
            .isEqualTo("New Chat")
            .jsonPath("$.extension_results")
            .doesNotExist();
    }

    /**
     * Executes the start session invalid json body falls back to working dir payload operation.
     */
    @Test
    public void startSession_invalidJsonBodyFallsBackToWorkingDirPayload() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"id\":\"session-123\"}"));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/resume"), anyString(), anyInt(),
            anyString())).thenReturn(Mono.just("{\"session\":{\"id\":\"session-123\"},\"extension_results\":[]}"));

        webClient.post()
            .uri("/api/gateway/agents/test-agent/agent/start")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("not-json")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo("session-123");

        verify(goosedProxy).fetchJson(eq(9999), eq(HttpMethod.POST), eq("/agent/start"),
            org.mockito.ArgumentMatchers.contains("\"working_dir\":"),
            anyInt(), anyString());
    }

    /**
     * Executes the start session unauthenticated returns401 operation.
     */
    @Test
    public void startSession_unauthenticated_returns401() {
        webClient.post()
            .uri("/api/gateway/agents/test-agent/agent/start")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    /**
     * Executes the list all sessions no instances returns empty array operation.
     */
    @Test
    public void listAllSessions_noInstances_returnsEmptyArray() {
        when(instanceManager.getAllInstances()).thenReturn(Collections.emptyList());

        webClient.get()
            .uri("/api/gateway/sessions")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.sessions.length()")
            .isEqualTo(0)
            .jsonPath("$.total")
            .isEqualTo(0)
            .jsonPath("$.pageIndex")
            .isEqualTo(1)
            .jsonPath("$.pageSize")
            .isEqualTo(20);
    }

    /**
     * Executes the list all sessions persisted sessions included without running instances operation.
     */
    @Test
    public void listAllSessions_noRunningInstances_readsPersistedSessions() throws Exception {
        createPersistedSession("alice", "agent-a", "persisted-1", "Persisted Chat", "user",
            "2026-06-10T10:00:00Z");
        when(instanceManager.getAllInstances()).thenReturn(Collections.emptyList());

        webClient.get()
            .uri("/api/gateway/sessions?type=user")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.sessions.length()")
            .isEqualTo(1)
            .jsonPath("$.sessions[0].id")
            .isEqualTo("persisted-1")
            .jsonPath("$.sessions[0].agentId")
            .isEqualTo("agent-a")
            .jsonPath("$.total")
            .isEqualTo(1);
    }

    /**
     * Executes the list all sessions live data wins when persisted session has same agent and id operation.
     */
    @Test
    public void listAllSessions_deduplicatesPersistedSessionsWithLivePriority() throws Exception {
        createPersistedSession("alice", "agent-a", "session-1", "Persisted Name", "user",
            "2026-06-10T10:00:00Z");
        ManagedInstance instance = new ManagedInstance("agent-a", "alice", 8001, 111L, null, "test-secret");
        instance.setStatus(ManagedInstance.Status.RUNNING);
        when(instanceManager.getAllInstances()).thenReturn(List.of(instance));
        when(sessionService.getSessionsFromInstance(instance)).thenReturn(Mono.just("""
            {"sessions":[{"id":"session-1","name":"Live Name","created_at":"2026-06-10T11:00:00Z",
            "session_type":"user"}]}
            """));

        webClient.get()
            .uri("/api/gateway/sessions?type=user")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.sessions.length()")
            .isEqualTo(1)
            .jsonPath("$.sessions[0].name")
            .isEqualTo("Live Name")
            .jsonPath("$.total")
            .isEqualTo(1);
    }

    /**
     * Executes the list all sessions with running instances aggregates sessions operation.
     */
    @Test
    public void listAllSessions_withRunningInstances_aggregatesSessions() {
        ManagedInstance userInstance = new ManagedInstance("agent-a", "alice", 8001, 111L, null, "test-secret");
        userInstance.setStatus(ManagedInstance.Status.RUNNING);
        ManagedInstance sysInstance = new ManagedInstance("agent-b", "admin", 8002, 222L, null, "test-secret");
        sysInstance.setStatus(ManagedInstance.Status.RUNNING);
        ManagedInstance otherUserInstance = new ManagedInstance("agent-a", "bob", 8003, 333L, null, "test-secret");
        otherUserInstance.setStatus(ManagedInstance.Status.RUNNING);

        when(instanceManager.getAllInstances()).thenReturn(List.of(userInstance, sysInstance, otherUserInstance));

        // Sessions returned for alice's instance and the system instance (not bob's)
        when(sessionService.getSessionsFromInstance(userInstance))
            .thenReturn(Mono.just("{\"sessions\":[{\"id\":\"s1\"}]}"));
        when(sessionService.getSessionsFromInstance(sysInstance))
            .thenReturn(Mono.just("{\"sessions\":[{\"id\":\"s2\"}]}"));

        webClient.get()
            .uri("/api/gateway/sessions")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk();
    }

    private void createPersistedSession(String userId, String agentId, String sessionId, String name, String type,
        String createdAt) throws Exception {
        Path db = platformTempDir.resolve(userId).resolve("agents").resolve(agentId).resolve("data")
            .resolve("sessions").resolve("sessions.db");
        Files.createDirectories(db.getParent());
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + db.toAbsolutePath())) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                    create table sessions (
                        id text primary key,
                        name text not null default '',
                        description text not null default '',
                        user_set_name boolean default false,
                        session_type text not null default 'user',
                        working_dir text not null,
                        created_at text,
                        updated_at text,
                        total_tokens integer,
                        input_tokens integer,
                        output_tokens integer,
                        accumulated_total_tokens integer,
                        accumulated_input_tokens integer,
                        accumulated_output_tokens integer,
                        schedule_id text,
                        recipe_json text,
                        user_recipe_values_json text,
                        provider_name text,
                        model_config_json text,
                        goose_mode text not null default 'auto',
                        thread_id text
                    )
                    """);
            }
            try (PreparedStatement statement = connection.prepareStatement("""
                insert into sessions (
                    id, name, description, user_set_name, session_type, working_dir, created_at, updated_at, goose_mode
                ) values (?, ?, '', false, ?, ?, ?, ?, 'auto')
                """)) {
                statement.setString(1, sessionId);
                statement.setString(2, name);
                statement.setString(3, type);
                statement.setString(4, platformTempDir.resolve(userId).resolve("agents").resolve(agentId).toString());
                statement.setString(5, createdAt);
                statement.setString(6, createdAt);
                statement.executeUpdate();
            }
        }
    }

    /**
     * Executes the list all sessions stopped instances excluded operation.
     */
    @Test
    public void listAllSessions_stoppedInstancesExcluded() {
        ManagedInstance stoppedInstance = new ManagedInstance("agent-a", "alice", 8001, 111L, null, "test-secret");
        stoppedInstance.setStatus(ManagedInstance.Status.STOPPED);

        when(instanceManager.getAllInstances()).thenReturn(List.of(stoppedInstance));

        webClient.get()
            .uri("/api/gateway/sessions")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.sessions.length()")
            .isEqualTo(0)
            .jsonPath("$.total")
            .isEqualTo(0)
            .jsonPath("$.pageIndex")
            .isEqualTo(1)
            .jsonPath("$.pageSize")
            .isEqualTo(20);
    }

    /**
     * Executes the list all sessions invalid payload skipped and invalid dates use string fallback operation.
     */
    @Test
    public void listAllSessions_invalidPayloadSkippedAndBadDatesUseFallback() {
        ManagedInstance invalidPayloadInstance =
            new ManagedInstance("agent-a", "alice", 8001, 111L, null, "test-secret");
        invalidPayloadInstance.setStatus(ManagedInstance.Status.RUNNING);
        ManagedInstance rawArrayInstance = new ManagedInstance("agent-b", "alice", 8002, 222L, null, "test-secret");
        rawArrayInstance.setStatus(ManagedInstance.Status.RUNNING);

        when(instanceManager.getAllInstances()).thenReturn(List.of(invalidPayloadInstance, rawArrayInstance));
        when(sessionService.getSessionsFromInstance(invalidPayloadInstance)).thenReturn(Mono.just("not-json"));
        when(sessionService.getSessionsFromInstance(rawArrayInstance)).thenReturn(Mono.just("""
            [
              {"id":"session-z","created_at":"zzz"},
              {"id":"session-a","created_at":"aaa"}
            ]
            """));

        webClient.get()
            .uri("/api/gateway/sessions")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.total")
            .isEqualTo(2)
            .jsonPath("$.sessions.length()")
            .isEqualTo(2)
            .jsonPath("$.sessions[0].id")
            .isEqualTo("session-z")
            .jsonPath("$.sessions[0].agentId")
            .isEqualTo("agent-b")
            .jsonPath("$.sessions[1].id")
            .isEqualTo("session-a");
    }

    /**
     * Executes the list all sessions unauthenticated returns401 operation.
     */
    @Test
    public void listAllSessions_unauthenticated_returns401() {
        webClient.get().uri("/api/gateway/sessions").exchange().expectStatus().isUnauthorized();
    }

    /**
     * Executes the list agent sessions authenticated proxies to goosed operation.
     */
    @Test
    public void listAgentSessions_authenticated_proxiesToGoosed() {
        when(instanceManager.getInstance("test-agent", "alice")).thenReturn(runningInstance);
        when(goosedProxy.fetchJson(eq(9999), eq("/sessions"), anyString())).thenReturn(Mono.just("[]"));

        webClient.get()
            .uri("/api/gateway/agents/test-agent/sessions")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk();

        verify(goosedProxy).fetchJson(eq(9999), eq("/sessions"), anyString());
    }

    /**
     * Returns the session authenticated proxies to goosed.
     */
    @Test
    public void getSession_authenticated_proxiesToGoosed() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        when(goosedProxy.fetchJson(eq(9999), eq("/sessions/session-123"), anyString()))
            .thenReturn(Mono.just("{\"id\":\"session-123\",\"conversation\":[]}"));

        webClient.get()
            .uri("/api/gateway/agents/test-agent/sessions/session-123")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id")
            .isEqualTo("session-123")
            .jsonPath("$.agentId")
            .isEqualTo("test-agent");
    }

    /**
     * Returns the session unauthenticated returns401.
     */
    @Test
    public void getSession_unauthenticated_returns401() {
        webClient.get()
            .uri("/api/gateway/agents/test-agent/sessions/session-123")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    /**
     * Executes the delete session authenticated removes owner and proxies operation.
     */
    @Test
    public void deleteSession_authenticated_removesOwnerAndProxies() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(runningInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.DELETE), eq("/sessions/session-456"), eq(null), anyInt(),
            anyString())).thenReturn(Mono.just("{}"));

        webClient.delete()
            .uri("/api/gateway/agents/test-agent/sessions/session-456")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk();

        verify(goosedProxy).fetchJson(eq(9999), eq(HttpMethod.DELETE), eq("/sessions/session-456"), eq(null), anyInt(),
            anyString());
    }

    /**
     * Executes the delete session unauthenticated returns401 operation.
     */
    @Test
    public void deleteSession_unauthenticated_returns401() {
        webClient.delete()
            .uri("/api/gateway/agents/test-agent/sessions/session-456")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }
}
