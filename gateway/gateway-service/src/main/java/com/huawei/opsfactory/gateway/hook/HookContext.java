/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.hook;

import java.util.HashMap;
import java.util.Map;

/**
 * Carries the request body and contextual state through the request hook pipeline.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class HookContext {
    private String body;
    private final String agentId;
    private final String userId;
    private final Map<String, Object> state = new HashMap<>();

    public HookContext(String body, String agentId, String userId) {
        this.body = body;
        this.agentId = agentId;
        this.userId = userId;
    }

    /**
     * Gets the request body.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the request body.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the agent identifier.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * Gets the user identifier.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the mutable state map shared across hooks.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Map<String, Object> getState() {
        return state;
    }
}
