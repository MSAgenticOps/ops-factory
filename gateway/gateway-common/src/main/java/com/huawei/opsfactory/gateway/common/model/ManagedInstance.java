/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.common.model;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Managed instance model.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class ManagedInstance {

    /**
     * Runtime status of a managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public enum Status {
        STARTING, RUNNING, STOPPED, ERROR
    }

    private final String agentId;
    private final String userId;
    private final int port;
    private final long pid;
    private final String secretKey;
    private volatile Status status;
    private volatile long lastActivity;
    private volatile int restartCount = 0;
    private volatile long lastRestartTime = 0;
    private transient Process process;

    /** Sessions that have been resumed (provider+extensions loaded) on this instance. */
    private final Set<String> resumedSessions = ConcurrentHashMap.newKeySet();

    /**
     * Creates a managed instance descriptor.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public ManagedInstance(String agentId, String userId, int port, long pid, Process process, String secretKey) {
        this.agentId = agentId;
        this.userId = userId;
        this.port = port;
        this.pid = pid;
        this.secretKey = secretKey;
        this.process = process;
        this.status = Status.STARTING;
        this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Gets the agent identifier of this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * Gets the user identifier of this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the port number assigned to this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the process identifier (PID) of this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getPid() {
        return pid;
    }

    /**
     * Gets the secret key used for authenticating with this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Gets the current status of this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets the timestamp of the last activity on this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getLastActivity() {
        return lastActivity;
    }

    /**
     * Updates the last activity timestamp to the current time.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void touch() {
        this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Gets the underlying process of this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Marks a session as resumed on this instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void markSessionResumed(String sessionId) {
        resumedSessions.add(sessionId);
    }

    /**
     * Removes the session from the resumed sessions set.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void unmarkSessionResumed(String sessionId) {
        if (sessionId != null) {
            resumedSessions.remove(sessionId);
        }
    }

    /**
     * Checks whether the given session has already been resumed on this instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public boolean isSessionResumed(String sessionId) {
        return sessionId != null && resumedSessions.contains(sessionId);
    }

    /**
     * Gets the number of times this managed instance has been restarted.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public int getRestartCount() {
        return restartCount;
    }

    /**
     * Sets the restart count for this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setRestartCount(int restartCount) {
        this.restartCount = restartCount;
    }

    /**
     * Resets the restart count to zero.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void resetRestartCount() {
        this.restartCount = 0;
    }

    /**
     * Gets the timestamp of the last restart.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public long getLastRestartTime() {
        return lastRestartTime;
    }

    /**
     * Sets the timestamp of the last restart.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setLastRestartTime(long lastRestartTime) {
        this.lastRestartTime = lastRestartTime;
    }

    /**
     * Gets the composite key for this managed instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getKey() {
        return buildKey(agentId, userId);
    }

    /**
     * Builds a composite key from the given agent and user identifiers.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static String buildKey(String agentId, String userId) {
        return agentId + ":" + userId;
    }
}
