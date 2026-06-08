/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.controller.base.BaseHostGroupController;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for CRUD operations on host group definitions and the group tree.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "hostGroupController")
@RequestMapping("/api/gateway/host-groups")
public class HostGroupController extends BaseHostGroupController {

    /**
     * Creates the host group controller instance.
     *
     * @param hostGroupService the host group service
     * @param clusterService the cluster service
     * @param businessServiceService the business service service
     * @param hostService the host service
     */
    public HostGroupController(HostGroupService hostGroupService, ClusterService clusterService,
        BusinessServiceService businessServiceService, HostService hostService) {
        super(hostGroupService, clusterService, businessServiceService, hostService);
    }
}
