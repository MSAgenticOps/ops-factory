/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.operationintelligence.controller.base.BaseKnowledgeGraphController;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.service.KnowledgeGraphService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.servicecomb.provider.rest.common.RestSchema;

/**
 * Knowledge graph machine controller.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@RestController
@RestSchema(schemaId = "knowledgeGraphMachineController")
@RequestMapping("/machine/operation-intelligence/graph")
@BasicAuth
public class KnowledgeGraphMachineController extends BaseKnowledgeGraphController {

    /**
     * Constructs a KnowledgeGraphMachineController.
     *
     * @param knowledgeGraphService the knowledge graph service
     */
    public KnowledgeGraphMachineController(KnowledgeGraphService knowledgeGraphService) {
        super(knowledgeGraphService);
    }
}
