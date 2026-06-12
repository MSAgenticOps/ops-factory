/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.common.constants;

/**
 * Gateway constant definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public final class GatewayConstants {
    private GatewayConstants() {
    }

    // Headers
    public static final String HEADER_SECRET_KEY = "x-secret-key";

    public static final String HEADER_USER_ID = "x-user-id";

    // A2A (agent-to-agent) request headers — the delegation extension self-reports its caller identity.
    // depth is NOT carried here; it is derived gateway-side from the side-record (see A2ASessionStore).
    public static final String HEADER_A2A_ORIGIN = "x-a2a-origin";

    public static final String HEADER_A2A_ORIGIN_SESSION = "x-a2a-origin-session";

    public static final String QUERY_KEY = "key";

    public static final String QUERY_UID = "uid";

    // A2A classification
    public static final String A2A_ORIGIN = "a2a";

    public static final String SESSION_TYPE_AGENT_CALL = "agent_call";

    public static final String SESSION_FIELD_ORIGIN = "origin";

    // Default users
    public static final String SYSTEM_USER = "admin";

    public static final String DEFAULT_USER = "__default__";

    // Process
    public static final int HEALTH_CHECK_MAX_ATTEMPTS = 30;

    public static final long HEALTH_CHECK_INITIAL_INTERVAL_MS = 100L;

    public static final long HEALTH_CHECK_MAX_INTERVAL_MS = 1000L;

    public static final long STOP_GRACE_PERIOD_MS = 1000L;

    // Idle
    public static final int DEFAULT_IDLE_TIMEOUT_MINUTES = 15;

    public static final long DEFAULT_IDLE_CHECK_INTERVAL_MS = 60000L;

    // Upload
    public static final int DEFAULT_MAX_FILE_SIZE_MB = 50;
}
