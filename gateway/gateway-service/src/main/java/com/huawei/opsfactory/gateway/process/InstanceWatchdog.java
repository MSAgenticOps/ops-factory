/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.process;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.common.util.ProcessUtil;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.service.AgentConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scheduled watchdog that detects dead agent processes and reaps idle instances.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Component
public class InstanceWatchdog {
    private static final Logger log = LoggerFactory.getLogger(InstanceWatchdog.class);

    private final InstanceManager instanceManager;

    private final GatewayProperties properties;

    private final PrewarmService prewarmService;

    private final AgentConfigService agentConfigService;

    /**
     * Creates the instance watchdog instance.
     */
    public InstanceWatchdog(InstanceManager instanceManager, GatewayProperties properties,
        PrewarmService prewarmService, AgentConfigService agentConfigService) {
        this.instanceManager = instanceManager;
        this.properties = properties;
        this.prewarmService = prewarmService;
        this.agentConfigService = agentConfigService;
    }

    /**
     * Periodically checks instance health and reaps idle instances.
     */
    @Scheduled(fixedDelayString = "${gateway.idle.check-interval-ms:60000}")
    public void watchInstances() {
        checkInstanceHealth();
        reapIdleInstances();
    }

    /**
     * Phase 1: Check all RUNNING instances for process liveness.
     * Dead processes are cleaned up and respawned with backoff.
     */
    private void checkInstanceHealth() {
        int maxRestarts = properties.getIdle().getMaxRestartAttempts();
        long baseDelay = properties.getIdle().getRestartBaseDelayMs();
        long now = System.currentTimeMillis();
        List<ManagedInstance> snapshot = new ArrayList<>(instanceManager.getAllInstances());
        for (ManagedInstance instance : snapshot) {
            if (!shouldCheckInstance(instance)) {
                continue;
            }
            handleDeadInstance(instance, maxRestarts, baseDelay, now);
        }
    }

    private boolean shouldCheckInstance(ManagedInstance instance) {
        if (instance.getStatus() != ManagedInstance.Status.RUNNING) {
            return false;
        }
        return instance.getProcess() == null || !ProcessUtil.isAlive(instance.getProcess());
    }

    private void handleDeadInstance(ManagedInstance instance, int maxRestarts, long baseDelay, long now) {
        int exitCode = resolveExitCode(instance);
        String agentId = instance.getAgentId();
        String userId = instance.getUserId();
        int restartCount = instance.getRestartCount();

        log.warn("Watchdog detected dead instance {}:{} (port={}, pid={}, exit={})", agentId, userId, instance.getPort(),
            instance.getPid(), exitCode);
        instanceManager.stopInstance(instance);
        if (!canRespawn(agentId, userId, restartCount, maxRestarts)) {
            return;
        }
        if (isBackingOff(instance, baseDelay, now, agentId, userId, restartCount)) {
            return;
        }
        log.info("Watchdog respawning instance {}:{} (attempt #{})", agentId, userId, restartCount + 1);
        instanceManager.respawnAsync(agentId, userId, restartCount + 1);
    }

    private int resolveExitCode(ManagedInstance instance) {
        try {
            return instance.getProcess() != null ? instance.getProcess().exitValue() : -1;
        } catch (IllegalThreadStateException e) {
            return -1;
        }
    }

    private boolean canRespawn(String agentId, String userId, int restartCount, int maxRestarts) {
        if (restartCount < maxRestarts) {
            return true;
        }
        log.error("Instance {}:{} exceeded max restart attempts ({}), not respawning. Will retry on next user request.",
            agentId, userId, maxRestarts);
        return false;
    }

    private boolean isBackingOff(ManagedInstance instance, long baseDelay, long now, String agentId, String userId,
        int restartCount) {
        long backoffMs = Math.min(baseDelay * (1L << Math.min(restartCount, 20)), 300_000L);
        long elapsed = now - instance.getLastRestartTime();
        if (instance.getLastRestartTime() <= 0 || elapsed >= backoffMs) {
            return false;
        }
        log.info("Instance {}:{} backing off ({}ms remaining before retry #{})", agentId, userId, backoffMs - elapsed,
            restartCount + 1);
        return true;
    }

    /**
     * Phase 2: Reap idle instances.
     * Resident instances are never reaped for idleness.
     */
    private void reapIdleInstances() {
        long maxIdleMs = properties.getIdle().getTimeoutMinutes() * 60_000L;
        long now = System.currentTimeMillis();
        Set<String> reapedUsers = new HashSet<>();

        List<ManagedInstance> snapshot = new ArrayList<>(instanceManager.getAllInstances());
        for (ManagedInstance instance : snapshot) {
            if (agentConfigService.isResidentInstance(instance.getAgentId(), instance.getUserId())) {
                continue;
            }
            if (instance.getStatus() == ManagedInstance.Status.RUNNING
                && now - instance.getLastActivity() > maxIdleMs) {
                log.info("Reaping idle instance {}:{} (idle {}s)", instance.getAgentId(), instance.getUserId(),
                    (now - instance.getLastActivity()) / 1000);
                instanceManager.stopInstance(instance);
                reapedUsers.add(instance.getUserId());
            }
        }

        // Reset pre-warm state for users who have no remaining instances
        for (String userId : reapedUsers) {
            boolean hasRemaining = instanceManager.getAllInstances()
                .stream()
                .anyMatch(i -> i.getUserId().equals(userId) && i.getStatus() == ManagedInstance.Status.RUNNING);
            if (!hasRemaining) {
                prewarmService.clearUser(userId);
            }
        }
    }
}
