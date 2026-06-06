/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
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
@RestSchema(schemaId = "clusterTypeMachineController")
@RequestMapping("/machine/gateway/cluster-types")
@BasicAuth
public class ClusterTypeMachineController extends BaseClusterTypeController {

    /**
     * Creates the cluster type machine controller instance.
     *
     * @param clusterTypeService service handling cluster type CRUD operations
     */
    public ClusterTypeMachineController(ClusterTypeService clusterTypeService) {
        super(clusterTypeService);
    }
}
