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
 * REST controller for executing and risk-checking remote commands on managed hosts.
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

    @Override
    @PostMapping("/execute")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> execute(@RequestBody Map<String, Object> request) {
        return super.execute(request);
    }

    @Override
    @PostMapping("/check-risk")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> checkRisk(@RequestBody Map<String, Object> request) {
        return super.checkRisk(request);
    }
}
