/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Resolves the per-user proactive-feature directory and its files under
 * {@code <gatewayRoot>/users/<userId>/agents/<agentId>/proactive-followups/}.
 *
 * <p>Single path authority for the proactive feature (mirrors {@code ChannelRuntimeStorageService} for channels),
 * shared by the delivery-marker and follow-up-record services so the layout lives in one place. User/agent
 * segments are validated to stay within the users root.
 *
 * @author x00000000
 * @since 2026-06-07
 */
@Component
public class ProactiveStorage {
    /** Append-only follow-up record file name within {@link ProactiveDeliveryMarkers#DIR}. */
    public static final String RECORDS_FILE = "records.jsonl";

    private static final Pattern SAFE_SEGMENT = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final GatewayProperties properties;

    /**
     * Creates the proactive storage path resolver.
     *
     * @param properties gateway configuration properties (provides the gateway root path)
     */
    public ProactiveStorage(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Resolves the per-user proactive directory, validating that it stays within the users root.
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @return the proactive-followups directory path
     */
    public Path proactiveDir(String userId, String agentId) {
        Path usersRoot = properties.getGatewayRootPath().resolve("users").normalize();
        Path dir = usersRoot.resolve(requireSafe(userId, "userId")).resolve("agents")
            .resolve(requireSafe(agentId, "agentId")).resolve(ProactiveDeliveryMarkers.DIR).normalize();
        if (!dir.startsWith(usersRoot)) {
            throw new IllegalArgumentException("resolved proactive path escapes the users root");
        }
        return dir;
    }

    /**
     * @param userId user identifier
     * @param agentId agent identifier
     * @return the per-user delivery-marker file path ({@code proactive-followups/delivery.json})
     */
    public Path deliveryMarkersFile(String userId, String agentId) {
        return proactiveDir(userId, agentId).resolve(ProactiveDeliveryMarkers.DELIVERY_FILE);
    }

    /**
     * @param userId user identifier
     * @param agentId agent identifier
     * @return the per-user follow-up record file path ({@code proactive-followups/records.jsonl})
     */
    public Path followupRecordsFile(String userId, String agentId) {
        return proactiveDir(userId, agentId).resolve(RECORDS_FILE);
    }

    private String requireSafe(String segment, String name) {
        if (segment == null || !SAFE_SEGMENT.matcher(segment).matches()) {
            throw new IllegalArgumentException(name + " contains unsafe path characters");
        }
        return segment;
    }
}
