/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.monitoring.MetricsBuffer;
import com.huawei.opsfactory.gateway.monitoring.MetricsSnapshot;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.huawei.opsfactory.gateway.service.LangfuseService;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin-only controller exposing system info, instance status, and aggregated metrics.
 *
 * @author x00000000
 * @since 2026-05-09
 */

@RestController
@RestSchema(schemaId = "internalRuntimeSourceController")
@RequestMapping("/api/gateway/runtime-source")
public class InternalRuntimeSourceController {
    private static final Logger log = LoggerFactory.getLogger(InternalRuntimeSourceController.class);

    private final InstanceManager instanceManager;

    private final AgentConfigService agentConfigService;

    private final LangfuseService langfuseService;

    private final GatewayProperties gatewayProperties;

    private final MetricsBuffer metricsBuffer;

    @Value("${server.port:3000}")
    private int serverPort;

    @Value("${server.address:0.0.0.0}")
    private String serverHost;

    /**
     * Creates the internal runtime source controller.
     *
     * @param instanceManager the instance manager
     * @param agentConfigService the agent config service
     * @param langfuseService the langfuse service
     * @param gatewayProperties the gateway properties
     * @param metricsBuffer the metrics buffer
     */
    public InternalRuntimeSourceController(InstanceManager instanceManager, AgentConfigService agentConfigService,
        LangfuseService langfuseService, GatewayProperties gatewayProperties, MetricsBuffer metricsBuffer) {
        this.instanceManager = instanceManager;
        this.agentConfigService = agentConfigService;
        this.langfuseService = langfuseService;
        this.gatewayProperties = gatewayProperties;
        this.metricsBuffer = metricsBuffer;
    }

    private static String formatUptime(long ms) {
        long seconds = ms / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        }
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        return minutes + "m " + secs + "s";
    }

    /**
     * Returns system-level information including uptime, agent count, and configuration.
     *
     * @param exchange returns system-level information including uptime, agent count, and configuration
     * @return system-level information including uptime, agent count, and configuration
     */
    @GetMapping("/system")
    public Map<String, Object> system(HttpServletRequest request) {
        long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
        long idleTimeoutMs = gatewayProperties.getIdle().getTimeoutMinutes() * 60_000L;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("gateway", Map.of("uptimeMs", uptimeMs, "uptimeFormatted", formatUptime(uptimeMs), "host",
            serverHost, "port", serverPort));
        result.put("agents", Map.of("configured", agentConfigService.getRegistry().size()));
        result.put("idle",
            Map.of("timeoutMs", idleTimeoutMs, "checkIntervalMs", gatewayProperties.getIdle().getCheckIntervalMs()));
        Map<String, Object> langfuse = new LinkedHashMap<>();
        langfuse.put("configured", langfuseService.isConfigured());
        String langfuseHost = gatewayProperties.getLangfuse().getHost();
        langfuse.put("host", (langfuseHost != null && !langfuseHost.isEmpty()) ? langfuseHost : null);
        result.put("langfuse", langfuse);
        return result;
    }

    /**
     * Returns the current status of all managed goosed instances.
     *
     * @param exchange returns the current status of all managed goosed instances
     * @return the current status of all managed goosed instances
     */
    @GetMapping("/instances")
    public Map<String, Object> instances(HttpServletRequest request) {
        List<ManagedInstance> allInstances = new ArrayList<>(instanceManager.getAllInstances());
        Map<String, List<Map<String, Object>>> grouped = allInstances.stream()
            .collect(
                Collectors.groupingBy(ManagedInstance::getAgentId, LinkedHashMap::new, Collectors.mapping(instance -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("agentId", instance.getAgentId());
                    item.put("userId", instance.getUserId());
                    item.put("port", instance.getPort());
                    item.put("pid", instance.getPid());
                    item.put("status", instance.getStatus().name().toLowerCase(Locale.ROOT));
                    item.put("lastActivity", instance.getLastActivity());
                    item.put("idleSinceMs", System.currentTimeMillis() - instance.getLastActivity());
                    return item;
                }, Collectors.toList())));

        List<Map<String, Object>> byAgent = new ArrayList<>();
        for (var entry : grouped.entrySet()) {
            Map<String, Object> group = new LinkedHashMap<>();
            group.put("agentId", entry.getKey());
            var registryEntry = agentConfigService.findAgent(entry.getKey());
            group.put("agentName", registryEntry != null ? registryEntry.name() : entry.getKey());
            group.put("instances", entry.getValue());
            byAgent.add(group);
        }

        long running =
            allInstances.stream().filter(instance -> instance.getStatus() == ManagedInstance.Status.RUNNING).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalInstances", allInstances.size());
        result.put("runningInstances", (int) running);
        result.put("byAgent", byAgent);
        return result;
    }

    /**
     * Stops a single managed instance identified by agent and user.
     *
     * @param agentId agent identifier from the URL path
     * @param userId user identifier from the URL path
     * @return success with status "stopped", or an error body when no instance is tracked
     */
    @PostMapping("/instances/{agentId}/{userId}/stop")
    public Map<String, Object> stopInstance(@PathVariable("agentId") String agentId,
        @PathVariable("userId") String userId) {
        ManagedInstance instance = instanceManager.getInstance(agentId, userId);
        if (instance == null) {
            return Map.of("success", false, "error", "Instance not found: " + agentId + ":" + userId);
        }
        log.info("[RUNTIME-ACTION] stop requested for {}:{}", agentId, userId);
        instanceManager.stopInstance(instance);
        return Map.of("success", true, "status", "stopped");
    }

    /**
     * Starts an instance for the given agent and user. Spawning happens asynchronously; callers
     * should poll {@code GET /instances} to observe the starting → running transition.
     *
     * @param agentId agent identifier from the URL path
     * @param userId user identifier from the URL path
     * @return success with status "running" when already up or "starting" when a spawn was triggered
     */
    @PostMapping("/instances/{agentId}/{userId}/start")
    public Map<String, Object> startInstance(@PathVariable("agentId") String agentId,
        @PathVariable("userId") String userId) {
        if (agentConfigService.findAgent(agentId) == null) {
            return Map.of("success", false, "error", "Unknown agent: " + agentId);
        }
        ManagedInstance existing = instanceManager.getInstance(agentId, userId);
        if (existing != null && existing.getStatus() == ManagedInstance.Status.RUNNING) {
            return Map.of("success", true, "status", "running");
        }
        log.info("[RUNTIME-ACTION] start requested for {}:{}", agentId, userId);
        spawnDetached(agentId, userId, "start");
        return Map.of("success", true, "status", "starting");
    }

    /**
     * Restarts a single managed instance so spawn-time environment derived from config.yaml and
     * secrets.yaml (provider, model, API keys) is rebuilt. The respawn happens asynchronously.
     *
     * @param agentId agent identifier from the URL path
     * @param userId user identifier from the URL path
     * @return success with status "restarting", or an error body when no instance is tracked
     */
    @PostMapping("/instances/{agentId}/{userId}/restart")
    public Map<String, Object> restartInstance(@PathVariable("agentId") String agentId,
        @PathVariable("userId") String userId) {
        ManagedInstance instance = instanceManager.getInstance(agentId, userId);
        if (instance == null) {
            return Map.of("success", false, "error", "Instance not found: " + agentId + ":" + userId);
        }
        log.info("[RUNTIME-ACTION] restart requested for {}:{}", agentId, userId);
        instanceManager.stopInstance(instance);
        spawnDetached(agentId, userId, "restart");
        return Map.of("success", true, "status", "restarting");
    }

    private void spawnDetached(String agentId, String userId, String action) {
        instanceManager.getOrSpawn(agentId, userId)
            .subscribe(
                inst -> log.info("[RUNTIME-ACTION] {} spawned {}:{} on port {}", action, agentId, userId,
                    inst.getPort()),
                err -> log.error("[RUNTIME-ACTION] {} failed to spawn {}:{}: {}", action, agentId, userId,
                    err.getMessage()));
    }

    /**
     * Returns aggregated metrics including request counts, latency, throughput, and time series data.
     *
     * @param exchange returns aggregated metrics including request counts, latency, throughput, and time series data
     * @return aggregated metrics including request counts, latency, throughput, and time series data
     */
    @GetMapping("/metrics")
    public Map<String, Object> metrics(HttpServletRequest request) {
        List<MetricsSnapshot> snapshots = metricsBuffer.getSnapshots(120);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("collectionIntervalSec", 30);
        result.put("maxSlots", 120);
        result.put("returnedSlots", snapshots.size());
        result.put("current", buildCurrentMetrics(snapshots));
        result.put("aggregate", buildAggregateMetrics(snapshots));
        result.put("series", buildSeries(snapshots));
        result.put("agentMetrics", metricsBuffer.getAgentStats());
        return result;
    }

    private Map<String, Object> buildCurrentMetrics(List<MetricsSnapshot> snapshots) {
        if (snapshots.isEmpty()) {
            return Map.of();
        }
        MetricsSnapshot latest = snapshots.get(snapshots.size() - 1);
        Map<String, Object> current = new LinkedHashMap<>();
        current.put("activeInstances", latest.getActiveInstances());
        current.put("totalTokens", latest.getTotalTokens());
        current.put("totalSessions", latest.getTotalSessions());
        return current;
    }

    private Map<String, Object> buildAggregateMetrics(List<MetricsSnapshot> snapshots) {
        MetricsAggregate aggregate = aggregateSnapshots(snapshots);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalRequests", aggregate.totalRequests());
        result.put("totalErrors", aggregate.totalErrors());
        result.put("avgLatencyMs", roundMetric(aggregate.avgLatency()));
        result.put("avgTtftMs", roundMetric(aggregate.avgTtft()));
        result.put("avgTokensPerSec", roundMetric(aggregate.avgTokensPerSec()));
        result.put("p95LatencyMs", roundMetric(aggregate.maxP95Latency()));
        result.put("p95TtftMs", roundMetric(aggregate.maxP95Ttft()));
        return result;
    }

    private MetricsAggregate aggregateSnapshots(List<MetricsSnapshot> snapshots) {
        int totalRequests = 0;
        int totalErrors = 0;
        double weightedLatencySum = 0;
        double weightedTtftSum = 0;
        double tokensPerSecSum = 0;
        int tokensPerSecCount = 0;
        double maxP95Latency = 0;
        double maxP95Ttft = 0;
        for (MetricsSnapshot snapshot : snapshots) {
            totalRequests += snapshot.getRequestCount();
            totalErrors += snapshot.getErrorCount();
            weightedLatencySum += snapshot.getAvgLatencyMs() * snapshot.getRequestCount();
            weightedTtftSum += snapshot.getAvgTtftMs() * snapshot.getRequestCount();
            if (snapshot.getTokensPerSec() > 0) {
                tokensPerSecSum += snapshot.getTokensPerSec();
                tokensPerSecCount++;
            }
            maxP95Latency = Math.max(maxP95Latency, snapshot.getP95LatencyMs());
            maxP95Ttft = Math.max(maxP95Ttft, snapshot.getP95TtftMs());
        }
        double avgLatency = totalRequests > 0 ? weightedLatencySum / totalRequests : 0;
        double avgTtft = totalRequests > 0 ? weightedTtftSum / totalRequests : 0;
        double avgTokensPerSec = tokensPerSecCount > 0 ? tokensPerSecSum / tokensPerSecCount : 0;
        return new MetricsAggregate(totalRequests, totalErrors, avgLatency, avgTtft, avgTokensPerSec, maxP95Latency,
            maxP95Ttft);
    }

    private List<Map<String, Object>> buildSeries(List<MetricsSnapshot> snapshots) {
        List<Map<String, Object>> series = new ArrayList<>();
        for (MetricsSnapshot snapshot : snapshots) {
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("t", snapshot.getTimestamp());
            point.put("instances", snapshot.getActiveInstances());
            point.put("tokens", snapshot.getTotalTokens());
            point.put("requests", snapshot.getRequestCount());
            point.put("avgLatency", roundMetric(snapshot.getAvgLatencyMs()));
            point.put("avgTtft", roundMetric(snapshot.getAvgTtftMs()));
            point.put("p95Latency", roundMetric(snapshot.getP95LatencyMs()));
            point.put("p95Ttft", roundMetric(snapshot.getP95TtftMs()));
            point.put("bytes", snapshot.getTotalBytes());
            point.put("errors", snapshot.getErrorCount());
            point.put("tokensPerSec", roundMetric(snapshot.getTokensPerSec()));
            series.add(point);
        }
        return series;
    }

    private double roundMetric(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record MetricsAggregate(int totalRequests, int totalErrors, double avgLatency, double avgTtft,
        double avgTokensPerSec, double maxP95Latency, double maxP95Ttft) {
    }
}
