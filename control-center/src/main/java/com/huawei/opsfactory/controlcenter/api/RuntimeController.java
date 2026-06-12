/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.api;

import com.huawei.opsfactory.controlcenter.observe.GatewayRuntimeSourceClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;

import org.apache.servicecomb.provider.rest.common.RestSchema;

/**
 * Runtime Controller.
 *
 * @author x00000000
 * @since 2026-05-27
 */
@RestController
@RestSchema(schemaId = "runtimeController")
@RequestMapping("/api/control-center/runtime")
public class RuntimeController {

    private static final Set<String> INSTANCE_ACTIONS = Set.of("start", "stop", "restart");

    private final GatewayRuntimeSourceClient gatewayRuntimeSourceClient;

    /**
     * Creates the runtime controller instance.
     *
     * @param gatewayRuntimeSourceClient the gateway runtime source client
     */
    public RuntimeController(GatewayRuntimeSourceClient gatewayRuntimeSourceClient) {
        this.gatewayRuntimeSourceClient = gatewayRuntimeSourceClient;
    }

    @GetMapping("/system")
    public Map<String, Object> system() {
        return gatewayRuntimeSourceClient.getSystem();
    }

    @GetMapping("/instances")
    public Map<String, Object> instances() {
        return gatewayRuntimeSourceClient.getInstances();
    }

    @GetMapping("/agents")
    public Map<String, Object> agents() {
        return gatewayRuntimeSourceClient.getAgents();
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        return gatewayRuntimeSourceClient.getMetrics();
    }

    /**
     * Proxies a lifecycle action on a single goosed instance to the gateway.
     *
     * @param agentId agent identifier owning the instance
     * @param userId user identifier owning the instance
     * @param action lifecycle action: start, stop, or restart
     * @return the gateway response body
     */
    @PostMapping("/instances/{agentId}/{userId}/{action}")
    public Map<String, Object> instanceAction(@PathVariable("agentId") String agentId,
            @PathVariable("userId") String userId, @PathVariable("action") String action) {
        if (!INSTANCE_ACTIONS.contains(action)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported instance action: " + action);
        }
        return gatewayRuntimeSourceClient.postInstanceAction(agentId, userId, action);
    }
}
