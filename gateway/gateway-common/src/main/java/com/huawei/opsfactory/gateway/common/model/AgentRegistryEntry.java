/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Agent registry entry model.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgentRegistryEntry(String id, String name) {
}
