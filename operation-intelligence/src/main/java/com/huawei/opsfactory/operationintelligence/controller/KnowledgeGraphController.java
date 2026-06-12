/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.controller;

import com.huawei.opsfactory.operationintelligence.controller.base.BaseKnowledgeGraphController;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.service.KnowledgeGraphService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Knowledge graph controller.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@RestController
@RestSchema(schemaId = "knowledgeGraphController")
@RequestMapping("/api/operation-intelligence/graph")
public class KnowledgeGraphController extends BaseKnowledgeGraphController {

    /**
     * Constructs a KnowledgeGraphController.
     *
     * @param knowledgeGraphService the knowledge graph service
     */
    public KnowledgeGraphController(KnowledgeGraphService knowledgeGraphService) {
        super(knowledgeGraphService);
    }
}
