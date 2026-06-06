/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

/**
 * Gateway-side side-record for an A2A (agent-to-agent) sub-session.
 *
 * <p>goose hardcodes the session type to {@code user} when a session is created via its HTTP API, so the "Agent 调用"
 * (agent_call) classification of a delegated sub-run must be derived gateway-side. One record is written per
 * {@code call_agent} invocation; it also backs the nesting/depth guard (a caller whose own session id is present here
 * is itself a sub-run and must not delegate again) and lets the history list show the sub-session even after the target
 * instance has been idle-reaped.
 *
 * @param subSessionId the goosed session id created on the target (B) instance for this sub-run
 * @param parentSessionId the caller's (AgentA) session id that issued the {@code call_agent} call
 * @param originAgentId the agent id that initiated the delegation
 * @param targetAgentId the delegated-to agent id
 * @param callerUserId the original user identity (A2A never crosses users)
 * @param createdAt ISO-8601 creation timestamp
 * @param status lifecycle status: {@code running}, {@code completed}, {@code cancelled}, {@code timeout}, {@code error}
 * @param title short label for the history list, derived from the delegated message
 * @author x00000000
 * @since 2026-06-05
 */
public record A2ASessionRecord(String subSessionId, String parentSessionId, String originAgentId, String targetAgentId,
    String callerUserId, String createdAt, String status, String title) {

    /** Status: sub-run is in progress. */
    public static final String STATUS_RUNNING = "running";

    /** Status: sub-run finished and returned a result. */
    public static final String STATUS_COMPLETED = "completed";

    /** Status: sub-run was cancelled (by the caller or upstream). */
    public static final String STATUS_CANCELLED = "cancelled";

    /** Status: sub-run hit the gateway idle/total timeout. */
    public static final String STATUS_TIMEOUT = "timeout";

    /** Status: sub-run failed with an error. */
    public static final String STATUS_ERROR = "error";

    /**
     * Returns a copy with the given status.
     *
     * @param newStatus the new lifecycle status
     * @return a new record carrying {@code newStatus}
     */
    public A2ASessionRecord withStatus(String newStatus) {
        return new A2ASessionRecord(subSessionId, parentSessionId, originAgentId, targetAgentId, callerUserId, createdAt,
            newStatus, title);
    }
}
