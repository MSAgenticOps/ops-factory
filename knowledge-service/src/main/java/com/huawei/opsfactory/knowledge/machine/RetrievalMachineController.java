/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.knowledge.controller.base.BaseRetrievalController;
import com.huawei.opsfactory.knowledge.service.KnowledgeServiceFacade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.servicecomb.provider.rest.common.RestSchema;

/**
 * The RetrievalMachineController for machine endpoints.
 * @author x00000000
 * @since 2026-05-26
 */
@RestController
@RestSchema(schemaId = "retrievalMachineController")
@RequestMapping("/machine/knowledge")
@BasicAuth
public class RetrievalMachineController extends BaseRetrievalController {

    /**
     * Constructs a RetrievalMachineController.
     *
     * @param facade the knowledge service facade
     */
    public RetrievalMachineController(KnowledgeServiceFacade facade) {
        super(facade);
    }

    @Override
    @PostMapping("/search")
    public SearchResponse search(@RequestBody SearchRequest request) {
        return facade.search(request);
    }

    @Override
    @PostMapping("/search/compare")
    public CompareSearchResponse compare(@RequestBody CompareSearchRequest request) {
        return facade.compare(request);
    }

    @Override
    @GetMapping("/fetch/{chunkId}")
    public FetchResponse fetch(
        @PathVariable("chunkId") String chunkId,
        @RequestParam(defaultValue = "false") boolean includeNeighbors,
        @RequestParam(defaultValue = "1") int neighborWindow,
        @RequestParam(defaultValue = "true") boolean includeMarkdown,
        @RequestParam(defaultValue = "true") boolean includeRawText
    ) {
        return facade.fetch(chunkId, includeNeighbors, neighborWindow);
    }

    @Override
    @PostMapping("/retrieve")
    public RetrieveResponse retrieve(@RequestBody RetrieveRequest request) {
        return facade.retrieve(request);
    }

    @Override
    @PostMapping("/explain")
    public ExplainResponse explain(@RequestBody ExplainRequest request) {
        return facade.explain(request);
    }
}
