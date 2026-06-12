/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.e2e;

import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.common.model.AgentRegistryEntry;
import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.monitoring.MetricsSnapshot;

import org.junit.Test;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import java.util.Collections;
import java.util.List;

/**
 * E2E tests for InternalRuntimeSourceController endpoints:
 * GET /runtime-source/system
 * GET /runtime-source/instances
 * GET /runtime-source/metrics
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class RuntimeSourceEndpointE2ETest extends BaseE2ETest {

    /**
     * Executes the system admin returns system info operation.
     */
    @Test
    public void system_admin_returnsSystemInfo() {
        when(agentConfigService.getRegistry()).thenReturn(List.of(new AgentRegistryEntry("agent-a", "Agent A")));
        when(instanceManager.getAllInstances()).thenReturn(Collections.emptyList());
        when(langfuseService.isConfigured()).thenReturn(false);

        webClient.get()
            .uri("/api/gateway/runtime-source/system")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "admin")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.gateway.uptimeMs")
            .isNumber()
            .jsonPath("$.gateway.host")
            .isNotEmpty()
            .jsonPath("$.gateway.port")
            .isNumber()
            .jsonPath("$.agents.configured")
            .isEqualTo(1)
            .jsonPath("$.idle.timeoutMs")
            .isNumber()
            .jsonPath("$.langfuse.configured")
            .isEqualTo(false);
    }

    /**
     * Executes the system non admin succeeds operation.
     */
    @Test
    public void system_nonAdmin_succeeds() {
        when(agentConfigService.getRegistry()).thenReturn(List.of(new AgentRegistryEntry("agent-a", "Agent A")));
        when(instanceManager.getAllInstances()).thenReturn(Collections.emptyList());
        when(langfuseService.isConfigured()).thenReturn(false);

        webClient.get()
            .uri("/api/gateway/runtime-source/system")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk();
    }

    /**
     * Executes the system unauthenticated returns401 operation.
     */
    @Test
    public void system_unauthenticated_returns401() {
        webClient.get().uri("/api/gateway/runtime-source/system").exchange().expectStatus().isUnauthorized();
    }

    /**
     * Executes the instances admin returns instance list operation.
     */
    @Test
    public void instances_admin_returnsInstanceList() {
        ManagedInstance inst = new ManagedInstance("agent-a", "alice", 9001, 54321L, null, "test-secret");
        inst.setStatus(ManagedInstance.Status.RUNNING);
        when(instanceManager.getAllInstances()).thenReturn(List.of(inst));
        when(agentConfigService.findAgent("agent-a")).thenReturn(new AgentRegistryEntry("agent-a", "Agent A"));

        webClient.get()
            .uri("/api/gateway/runtime-source/instances")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "admin")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.totalInstances")
            .isEqualTo(1)
            .jsonPath("$.runningInstances")
            .isEqualTo(1)
            .jsonPath("$.byAgent.length()")
            .isEqualTo(1)
            .jsonPath("$.byAgent[0].agentId")
            .isEqualTo("agent-a")
            .jsonPath("$.byAgent[0].agentName")
            .isEqualTo("Agent A")
            .jsonPath("$.byAgent[0].instances[0].userId")
            .isEqualTo("alice")
            .jsonPath("$.byAgent[0].instances[0].port")
            .isEqualTo(9001)
            .jsonPath("$.byAgent[0].instances[0].pid")
            .isEqualTo(54321)
            .jsonPath("$.byAgent[0].instances[0].status")
            .isEqualTo("running")
            .jsonPath("$.byAgent[0].instances[0].lastActivity")
            .isNumber()
            .jsonPath("$.byAgent[0].instances[0].idleSinceMs")
            .isNumber();
    }

    /**
     * Executes the instances non admin succeeds operation.
     */
    @Test
    public void instances_nonAdmin_succeeds() {
        when(instanceManager.getAllInstances()).thenReturn(Collections.emptyList());

        webClient.get()
            .uri("/api/gateway/runtime-source/instances")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "bob")
            .exchange()
            .expectStatus()
            .isOk();
    }

    /**
     * Executes the metrics admin returns metrics data operation.
     */
    @Test
    public void metrics_admin_returnsMetricsData() {
        MetricsSnapshot snapshot = sampleMetricsSnapshot();
        stubMetricsSnapshot(snapshot);

        BodyContentSpec body = performMetricsRequest("admin");
        assertMetricsOverview(body);
        assertMetricsSeriesRow(body, snapshot.getTimestamp(), snapshot.getActiveInstances(), snapshot.getRequestCount(),
            snapshot.getErrorCount());
    }

    /**
     * Executes the metrics non admin succeeds operation.
     */
    @Test
    public void metrics_nonAdmin_succeeds() {
        when(metricsBuffer.getSnapshots(120)).thenReturn(Collections.emptyList());
        when(metricsBuffer.getAgentStats()).thenReturn(Collections.emptyMap());

        webClient.get()
            .uri("/api/gateway/runtime-source/metrics")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, "alice")
            .exchange()
            .expectStatus()
            .isOk();
    }

    /**
     * Executes the metrics unauthenticated returns401 operation.
     */
    @Test
    public void metrics_unauthenticated_returns401() {
        webClient.get().uri("/api/gateway/runtime-source/metrics").exchange().expectStatus().isUnauthorized();
    }

    private MetricsSnapshot sampleMetricsSnapshot() {
        MetricsSnapshot snapshot = new MetricsSnapshot();
        snapshot.setTimestamp(1000L);
        snapshot.setActiveInstances(2);
        snapshot.setTotalTokens(5000);
        snapshot.setTotalSessions(3);
        snapshot.setRequestCount(4);
        snapshot.setAvgLatencyMs(2500.0);
        snapshot.setAvgTtftMs(800.0);
        snapshot.setP95LatencyMs(4000.0);
        snapshot.setP95TtftMs(1500.0);
        snapshot.setTotalBytes(15000);
        snapshot.setErrorCount(1);
        return snapshot;
    }

    private void stubMetricsSnapshot(MetricsSnapshot snapshot) {
        when(metricsBuffer.getSnapshots(120)).thenReturn(List.of(snapshot));
        when(metricsBuffer.getAgentStats()).thenReturn(Collections.emptyMap());
    }

    private BodyContentSpec performMetricsRequest(String userId) {
        return webClient.get()
            .uri("/api/gateway/runtime-source/metrics")
            .header(HEADER_SECRET_KEY, SECRET_KEY)
            .header(HEADER_USER_ID, userId)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody();
    }

    private void assertMetricsOverview(BodyContentSpec body) {
        body.jsonPath("$.collectionIntervalSec")
            .isEqualTo(30)
            .jsonPath("$.maxSlots")
            .isEqualTo(120)
            .jsonPath("$.returnedSlots")
            .isEqualTo(1)
            .jsonPath("$.current.activeInstances")
            .isEqualTo(2)
            .jsonPath("$.current.totalTokens")
            .isEqualTo(5000)
            .jsonPath("$.current.totalSessions")
            .isEqualTo(3)
            .jsonPath("$.aggregate.totalRequests")
            .isEqualTo(4)
            .jsonPath("$.aggregate.totalErrors")
            .isEqualTo(1)
            .jsonPath("$.aggregate.avgLatencyMs")
            .isEqualTo(2500.0)
            .jsonPath("$.aggregate.avgTtftMs")
            .isEqualTo(800.0)
            .jsonPath("$.series.length()")
            .isEqualTo(1);
    }

    private void assertMetricsSeriesRow(BodyContentSpec body, long timestamp, int instances, int requests, int errors) {
        body.jsonPath("$.series[0].t")
            .isEqualTo(timestamp)
            .jsonPath("$.series[0].instances")
            .isEqualTo(instances)
            .jsonPath("$.series[0].requests")
            .isEqualTo(requests)
            .jsonPath("$.series[0].errors")
            .isEqualTo(errors);
    }
}
