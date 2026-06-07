/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Per-user facade over {@link ProactiveDeliveryMarkers} for Spring callers (the schedule-create endpoint and the
 * proactive delivery service). Resolves the per-user {@code delivery.json} path via {@link ProactiveStorage} and
 * delegates the file I/O.
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

    private final ProactiveStorage storage;

    /**
     * Creates the delivery-marker service.
     *
     * @param storage per-user proactive path resolver
     */
    public ProactiveDeliveryMarkerService(ProactiveStorage storage) {
        this.storage = storage;
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
            ProactiveDeliveryMarkers.setDeliver(storage.deliveryMarkersFile(userId, agentId), scheduleId, deliver);
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
            return ProactiveDeliveryMarkers.getDeliver(storage.deliveryMarkersFile(userId, agentId), scheduleId);
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
            ProactiveDeliveryMarkers.remove(storage.deliveryMarkersFile(userId, agentId), scheduleId);
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Failed to remove deliver marker scheduleId={} for {}:{}: {}", scheduleId, agentId, userId,
                e.getMessage());
        }
    }
}
