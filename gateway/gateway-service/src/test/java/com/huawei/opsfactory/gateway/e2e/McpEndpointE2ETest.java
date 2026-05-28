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

import reactor.core.publisher.Mono;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Collections;

/**
 * E2E tests for McpController endpoints:
 * GET /agents/{agentId}/mcp
 * POST /agents/{agentId}/mcp
 * DELETE /agents/{agentId}/mcp/{name}
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class McpEndpointE2ETest extends BaseE2ETest {
    private ManagedInstance sysInstance;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        sysInstance = new ManagedInstance("test-agent", "admin", 9999, 12345L, null, "test-secret");
        sysInstance.setStatus(ManagedInstance.Status.RUNNING);
    }

    /**
     * Returns the mcp extensions admin proxies to sys instance.
     */
    @Test
    public void getMcpExtensions_admin_proxiesToSysInstance() {
        when(instanceManager.getOrSpawn("test-agent", "admin")).thenReturn(Mono.just(sysInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.GET), eq("/config/extensions"),
            eq(null), anyInt(), eq("test-secret"))).thenReturn(Mono.just("[]"));

        webClient.get()
            .uri("/gateway/agents/test-agent/mcp")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "admin")
            .exchange()
            .expectStatus()
            .isOk();

        verify(instanceManager).getOrSpawn("test-agent", "admin");
        verify(goosedProxy).fetchJson(eq(9999), eq(HttpMethod.GET), eq("/config/extensions"),
            eq(null), anyInt(), eq("test-secret"));
    }

    /**
     * Returns the mcp extensions non admin succeeds.
     */
    @Test
    public void getMcpExtensions_nonAdmin_succeeds() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(sysInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.GET), eq("/config/extensions"),
            eq(null), anyInt(), eq("test-secret"))).thenReturn(Mono.just("[]"));

        webClient.get()
            .uri("/gateway/agents/test-agent/mcp")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk();

        verify(instanceManager).getOrSpawn("test-agent", "alice");
    }

    /**
     * Returns the mcp extensions unauthenticated returns401.
     */
    @Test
    public void getMcpExtensions_unauthenticated_returns401() {
        webClient.get().uri("/gateway/agents/test-agent/mcp").exchange().expectStatus().isUnauthorized();
    }

    /**
     * Executes the create mcp extension admin forwards to sys instance operation.
     */
    @Test
    public void createMcpExtension_admin_forwardsToSysInstance() {
        when(instanceManager.getOrSpawn("test-agent", "admin")).thenReturn(Mono.just(sysInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/config/extensions"),
            anyString(), anyInt(), eq("test-secret"))).thenReturn(Mono.just("{\"name\":\"test-mcp\"}"));

        webClient.post()
            .uri("/gateway/agents/test-agent/mcp")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "admin")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"name\":\"test-mcp\",\"type\":\"stdio\"}")
            .exchange()
            .expectStatus()
            .isOk();

        verify(instanceManager).getOrSpawn("test-agent", "admin");
    }

    /**
     * Executes the create mcp extension non admin attempts proxy operation.
     */
    @Test
    public void createMcpExtension_nonAdmin_attemptsProxy() {
        when(instanceManager.getOrSpawn("test-agent", "alice")).thenReturn(Mono.just(sysInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.POST), eq("/config/extensions"),
            anyString(), anyInt(), eq("test-secret"))).thenReturn(Mono.just("{\"name\":\"test-mcp\"}"));

        webClient.post()
            .uri("/gateway/agents/test-agent/mcp")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{\"name\":\"test-mcp\",\"type\":\"stdio\"}")
            .exchange()
            .expectStatus()
            .isOk();

        verify(instanceManager).getOrSpawn("test-agent", "alice");
    }

    /**
     * Executes the delete mcp extension non admin attempts proxy operation.
     */
    @Test
    public void deleteMcpExtension_nonAdmin_attemptsProxy() {
        when(instanceManager.getOrSpawn("test-agent", "bob")).thenReturn(Mono.just(sysInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.DELETE), eq("/config/extensions/my-extension"),
            eq(null), anyInt(), eq("test-secret"))).thenReturn(Mono.just("{}"));

        webClient.delete()
            .uri("/gateway/agents/test-agent/mcp/my-extension")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "bob")
            .exchange()
            .expectStatus()
            .isOk();

        verify(instanceManager).getOrSpawn("test-agent", "bob");
    }

    /**
     * Executes the delete mcp extension unauthenticated returns401 operation.
     */
    @Test
    public void deleteMcpExtension_unauthenticated_returns401() {
        webClient.delete()
            .uri("/gateway/agents/test-agent/mcp/my-extension")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    /**
     * Executes the delete mcp extension admin attempts proxy to sys operation.
     */
    @Test
    public void deleteMcpExtension_admin_attemptsProxyToSys() {
        when(instanceManager.getOrSpawn("test-agent", "admin")).thenReturn(Mono.just(sysInstance));
        when(goosedProxy.fetchJson(eq(9999), eq(HttpMethod.DELETE), eq("/config/extensions/my-extension"),
            eq(null), anyInt(), eq("test-secret"))).thenReturn(Mono.just("{}"));

        webClient.delete()
            .uri("/gateway/agents/test-agent/mcp/my-extension")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "admin")
            .exchange()
            .expectStatus()
            .isOk();

        verify(instanceManager).getOrSpawn("test-agent", "admin");
    }
}
