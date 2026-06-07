/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Per-user facade over {@link ProactiveDeliveryMarkers} for Spring callers (the schedule-create endpoint and the
 * proactive delivery service). Resolves the per-user {@code delivery.json} path under
 * {@code <gatewayRoot>/users/<userId>/agents/<agentId>/proactive-followups/} and delegates the file I/O.
 *
 * <p>Marker persistence is best-effort: a write/read failure is logged and swallowed so it never fails the
 * caller's primary operation (the schedule was already created in goosed; delivery is decoupled, see PRD §5).
 *
 * @author x00000000
 * @since 2026-06-07
 */
@Service
public class ProactiveDeliveryMarkerService {
    private static final Logger log = LoggerFactory.getLogger(ProactiveDeliveryMarkerService.class);

    private static final Pattern SAFE_SEGMENT = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final GatewayProperties properties;

    /**
     * Creates the delivery-marker service.
     *
     * @param properties gateway configuration properties (provides the gateway root path)
     */
    public ProactiveDeliveryMarkerService(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Records that a schedule's report should be delivered over the given channel. Best-effort: logged on failure.
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param scheduleId schedule identifier
     * @param deliver delivery channel value (e.g. {@link ProactiveDeliveryMarkers#DELIVER_IM})
     */
    public void setDeliver(String userId, String agentId, String scheduleId, String deliver) {
        try {
            ProactiveDeliveryMarkers.setDeliver(deliveryFile(userId, agentId), scheduleId, deliver);
            log.info("Marked schedule deliver=im scheduleId={} for {}:{}", scheduleId, agentId, userId);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to persist deliver marker scheduleId={} for {}:{}: {}", scheduleId, agentId, userId,
                e.getMessage());
        }
    }

    /**
     * Returns the delivery channel configured for a schedule, or {@code null} when none/unreadable.
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param scheduleId schedule identifier
     * @return the configured delivery channel, or {@code null}
     */
    public String getDeliver(String userId, String agentId, String scheduleId) {
        try {
            return ProactiveDeliveryMarkers.getDeliver(deliveryFile(userId, agentId), scheduleId);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to read deliver marker scheduleId={} for {}:{}: {}", scheduleId, agentId, userId,
                e.getMessage());
            return null;
        }
    }

    /**
     * Removes any delivery marker for a schedule. Best-effort: logged on failure.
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param scheduleId schedule identifier
     */
    public void remove(String userId, String agentId, String scheduleId) {
        try {
            ProactiveDeliveryMarkers.remove(deliveryFile(userId, agentId), scheduleId);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to remove deliver marker scheduleId={} for {}:{}: {}", scheduleId, agentId, userId,
                e.getMessage());
        }
    }

    private Path deliveryFile(String userId, String agentId) {
        return userAgentDir(userId, agentId).resolve(ProactiveDeliveryMarkers.DIR)
            .resolve(ProactiveDeliveryMarkers.DELIVERY_FILE);
    }

    private Path userAgentDir(String userId, String agentId) {
        Path usersRoot = properties.getGatewayRootPath().resolve("users").normalize();
        Path dir = usersRoot.resolve(requireSafe(userId, "userId")).resolve("agents")
            .resolve(requireSafe(agentId, "agentId")).normalize();
        if (!dir.startsWith(usersRoot)) {
            throw new IllegalArgumentException("resolved user/agent path escapes the users root");
        }
        return dir;
    }

    private String requireSafe(String segment, String name) {
        if (segment == null || !SAFE_SEGMENT.matcher(segment).matches()) {
            throw new IllegalArgumentException(name + " contains unsafe path characters");
        }
        return segment;
    }
}
