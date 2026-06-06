/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseHostController;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for CRUD operations and connectivity testing on host entries.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "hostMachineController")
@RequestMapping("/machine/gateway/hosts")
@BasicAuth
public class HostMachineController extends BaseHostController {

    /**
     * Creates the host controller instance.
     *
     * @param hostService the host service
     * @param clusterService the cluster service
     * @param businessServiceService the business service service
     * @param hostGroupService the host group service
     */
    public HostMachineController(HostService hostService, ClusterService clusterService,
        BusinessServiceService businessServiceService, HostGroupService hostGroupService) {
        super(hostService, clusterService, businessServiceService, hostGroupService);
    }
}
