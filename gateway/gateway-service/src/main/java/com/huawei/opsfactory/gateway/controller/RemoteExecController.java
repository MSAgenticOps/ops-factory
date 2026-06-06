/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.controller.base.BaseRemoteExecController;
import com.huawei.opsfactory.gateway.service.CommandWhitelistService;
import com.huawei.opsfactory.gateway.service.RemoteExecutionService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for executing and risk-checking remote commands on managed hosts.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "remoteExecController")
@RequestMapping("/api/gateway/remote")
public class RemoteExecController extends BaseRemoteExecController {

    /**
     * Creates the remote exec controller instance.
     *
     * @param remoteExecutionService the remote execution service
     * @param commandWhitelistService the command whitelist service
     */
    public RemoteExecController(RemoteExecutionService remoteExecutionService,
        CommandWhitelistService commandWhitelistService) {
        super(remoteExecutionService, commandWhitelistService);
    }
}
