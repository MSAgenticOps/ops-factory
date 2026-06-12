/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.controller.base.BaseClusterController;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for CRUD operations on cluster definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "clusterController")
@RequestMapping("/api/gateway/clusters")
public class ClusterController extends BaseClusterController {

    /**
     * Creates the cluster controller instance.
     *
     * @param clusterService the cluster service
     * @param hostService the host service
     * @param hostGroupService the host group service
     */
    public ClusterController(ClusterService clusterService, HostService hostService,
        HostGroupService hostGroupService) {
        super(clusterService, hostService, hostGroupService);
    }
}
