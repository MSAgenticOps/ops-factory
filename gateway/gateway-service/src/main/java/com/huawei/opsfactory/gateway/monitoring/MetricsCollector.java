/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.monitoring;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collects metrics from running goosed instances every 30 seconds.
 * Calls GET /sessions/insights on each instance and aggregates with
 * request timing data captured by the SSE relay layer.
 */
@Component
public class MetricsCollector {
    private static final Logger log = LoggerFactory.getLogger(MetricsCollector.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final InstanceManager instanceManager;

    private final GoosedProxy goosedProxy;

    private final MetricsBuffer metricsBuffer;

    private long previousTotalTokens = -1;

    /**
     * Creates the metrics collector instance.
     *
     * @param instanceManager manages goosed process instances
     * @param goosedProxy proxy for fetching insights from goosed instances
     * @param metricsBuffer buffer for recording collected metrics
     */
    public MetricsCollector(InstanceManager instanceManager, GoosedProxy goosedProxy, MetricsBuffer metricsBuffer) {
        this.instanceManager = instanceManager;
        this.goosedProxy = goosedProxy;
        this.metricsBuffer = metricsBuffer;
    }

    /**
     * Collects metrics from all running goosed instances on a 30-second interval.
     */
    @Scheduled(fixedDelay = 30000)
    public void collect() {
        try {
            doCollect();
        } catch (IllegalStateException e) {
            log.warn("Metrics collection failed: {}", e.getMessage());
        }
    }

    private void doCollect() {
        List<ManagedInstance> running = loadRunningInstances();
        InsightTotals insightTotals = sumInsights(fetchInsights(running));
        List<RequestTiming> timings = metricsBuffer.drainTimings();
        TimingStats timingStats = computeTimingStats(timings);
        double tokensPerSec = computeTokensPerSecond(insightTotals.totalTokens());
        MetricsSnapshot snapshot = buildSnapshot(running.size(), insightTotals, timingStats, tokensPerSec);

        metricsBuffer.record(snapshot);
        metricsBuffer.persistToDisk();

        log.debug("Metrics collected: instances={} tokens={} sessions={} requests={} avgLatency={}ms", running.size(),
            insightTotals.totalTokens(), insightTotals.totalSessions(), timingStats.requestCount(),
            Math.round(timingStats.avgLatency()));
    }

    private List<ManagedInstance> loadRunningInstances() {
        return instanceManager.getAllInstances()
            .stream()
            .filter(i -> i.getStatus() == ManagedInstance.Status.RUNNING)
            .collect(Collectors.toList());
    }

    private List<Mono<long[]>> fetchInsights(List<ManagedInstance> running) {
        return running.stream().map(this::fetchSingleInsight).collect(Collectors.toList());
    }

    private Mono<long[]> fetchSingleInsight(ManagedInstance instance) {
        return goosedProxy.fetchJson(instance.getPort(), "/sessions/insights", instance.getSecretKey())
            .timeout(Duration.ofSeconds(5))
            .map(this::parseInsight)
            .onErrorReturn(new long[] {0, 0});
    }

    private long[] parseInsight(String json) {
        try {
            JsonNode node = MAPPER.readTree(json);
            return new long[] {node.path("total_tokens").asLong(0), node.path("total_sessions").asLong(0)};
        } catch (JsonProcessingException e) {
            return new long[] {0, 0};
        }
    }

    private InsightTotals sumInsights(List<Mono<long[]>> fetches) {
        List<long[]> results = Flux.merge(fetches).collectList().block(Duration.ofSeconds(10));
        long totalTokens = 0;
        long totalSessions = 0;
        if (results == null) {
            return new InsightTotals(totalTokens, totalSessions);
        }
        for (long[] result : results) {
            totalTokens += result[0];
            totalSessions += result[1];
        }
        return new InsightTotals(totalTokens, totalSessions);
    }

    private TimingStats computeTimingStats(List<RequestTiming> timings) {
        int requestCount = timings.size();
        if (timings.isEmpty()) {
            return new TimingStats(requestCount, 0, 0, 0, 0, 0, 0);
        }

        List<Long> latencies = new ArrayList<>();
        List<Long> ttfts = new ArrayList<>();
        long latencySum = 0;
        long ttftSum = 0;
        long totalBytes = 0;
        int errorCount = 0;

        for (RequestTiming timing : timings) {
            latencies.add(timing.getTotalMs());
            ttfts.add(timing.getTtftMs());
            latencySum += timing.getTotalMs();
            ttftSum += timing.getTtftMs();
            totalBytes += timing.getTotalBytes();
            if (timing.isError()) {
                errorCount++;
            }
        }

        return new TimingStats(
            requestCount,
            errorCount,
            totalBytes,
            (double) latencySum / requestCount,
            (double) ttftSum / requestCount,
            percentile(latencies, requestCount),
            percentile(ttfts, requestCount));
    }

    private double percentile(List<Long> values, int requestCount) {
        Collections.sort(values);
        int p95Index = (int) Math.ceil(requestCount * 0.95) - 1;
        p95Index = Math.max(0, Math.min(p95Index, requestCount - 1));
        return values.get(p95Index);
    }

    private double computeTokensPerSecond(long totalTokens) {
        double tokensPerSec = 0;
        if (previousTotalTokens >= 0 && totalTokens >= previousTotalTokens) {
            tokensPerSec = (totalTokens - previousTotalTokens) / 30.0;
        }
        previousTotalTokens = totalTokens;
        return tokensPerSec;
    }

    private MetricsSnapshot buildSnapshot(int activeInstances, InsightTotals insightTotals, TimingStats timingStats,
        double tokensPerSec) {
        MetricsSnapshot snapshot = new MetricsSnapshot();
        snapshot.setTimestamp(System.currentTimeMillis());
        snapshot.setActiveInstances(activeInstances);
        snapshot.setTotalTokens(insightTotals.totalTokens());
        snapshot.setTotalSessions(insightTotals.totalSessions());
        snapshot.setRequestCount(timingStats.requestCount());
        snapshot.setAvgLatencyMs(timingStats.avgLatency());
        snapshot.setAvgTtftMs(timingStats.avgTtft());
        snapshot.setP95LatencyMs(timingStats.p95Latency());
        snapshot.setP95TtftMs(timingStats.p95Ttft());
        snapshot.setTotalBytes(timingStats.totalBytes());
        snapshot.setErrorCount(timingStats.errorCount());
        snapshot.setTokensPerSec(tokensPerSec);
        return snapshot;
    }

    private record InsightTotals(long totalTokens, long totalSessions) {
    }

    private record TimingStats(int requestCount, int errorCount, long totalBytes, double avgLatency, double avgTtft,
        double p95Latency, double p95Ttft) {
    }
}
