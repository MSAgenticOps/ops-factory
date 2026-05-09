/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.monitoring;

/**
 * Immutable timing record captured per proxied agent request.
 * @author x00000000
 * @since 2026-05-09
 */
public class RequestTiming {
    private final long startTime;
    private final long ttftMs;
    private final long totalMs;
    private final long totalBytes;
    private final boolean error;
    private final String agentId;
    private final String userId;
    /**
     * Creates the request timing instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */

    public RequestTiming(long startTime, long ttftMs, long totalMs,
                         long totalBytes, boolean error,
                         String agentId, String userId) {
        this.startTime = startTime;
        this.ttftMs = ttftMs;
        this.totalMs = totalMs;
        this.totalBytes = totalBytes;
        this.error = error;
        this.agentId = agentId;
        this.userId = userId;
    }

    /**
     * Gets the request start time in milliseconds since epoch.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getStartTime() { return startTime; }
    /**
     * Gets the time-to-first-token in milliseconds.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getTtftMs() { return ttftMs; }
    /**
     * Gets the total request duration in milliseconds.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getTotalMs() { return totalMs; }
    /**
     * Gets the total number of bytes transferred.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getTotalBytes() { return totalBytes; }
    /**
     * Checks whether the request resulted in an error.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public boolean isError() { return error; }
    /**
     * Gets the agent identifier associated with this request.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getAgentId() { return agentId; }
    /**
     * Gets the user identifier associated with this request.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getUserId() { return userId; }
}
