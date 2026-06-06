/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseClusterRelationController;
import com.huawei.opsfactory.gateway.service.ClusterRelationService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing cluster-to-cluster relation edges and graph queries.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "clusterRelationMachineController")
@RequestMapping("/machine/gateway/cluster-relations")
@BasicAuth
public class ClusterRelationMachineController extends BaseClusterRelationController {

    /**
     * Creates the cluster relation machine controller instance.
     *
     * @param clusterRelationService service handling cluster relation CRUD operations
     */
    public ClusterRelationMachineController(ClusterRelationService clusterRelationService) {
        super(clusterRelationService);
    }
}
