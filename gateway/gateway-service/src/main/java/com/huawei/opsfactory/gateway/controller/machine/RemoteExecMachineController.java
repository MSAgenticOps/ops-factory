/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseRemoteExecController;
import com.huawei.opsfactory.gateway.service.CommandWhitelistService;
import com.huawei.opsfactory.gateway.service.RemoteExecutionService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Machine-to-machine REST controller for remote command execution and risk checking.
 * <p>
 * Requires Basic authentication for all endpoints.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "remoteExecMachineController")
@RequestMapping("/machine/gateway/remote")
public class RemoteExecMachineController extends BaseRemoteExecController {

    /**
     * Creates the remote exec controller instance.
     */
    public RemoteExecMachineController(RemoteExecutionService remoteExecutionService,
        CommandWhitelistService commandWhitelistService) {
        super(remoteExecutionService, commandWhitelistService);
    }

    /**
     * Executes a remote command on a managed host after whitelist validation.
     *
     * @param request HTTP request containing hostId, command, and optional timeout
     * @return the execution result
     */
    @Override
    @PostMapping("/execute")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> execute(@RequestBody Map<String, Object> request) {
        return super.execute(request);
    }

    /**
     * Checks the risk level of a command against the whitelist.
     *
     * @param request HTTP request containing the command to check
     * @return the risk level check result
     */
    @Override
    @PostMapping("/check-risk")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> checkRisk(@RequestBody Map<String, Object> request) {
        return super.checkRisk(request);
    }
}
