/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.controller.base.BaseClusterTypeController;
import com.huawei.opsfactory.gateway.service.ClusterTypeService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for CRUD operations on cluster type definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "clusterTypeController")
@RequestMapping("/api/gateway/cluster-types")
public class ClusterTypeController extends BaseClusterTypeController {

    /**
     * Creates the cluster type controller instance.
     *
     * @param clusterTypeService service handling cluster type CRUD operations
     */
    public ClusterTypeController(ClusterTypeService clusterTypeService) {
        super(clusterTypeService);
    }
}
