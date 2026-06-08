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
 * Machine-to-machine REST controller for knowledge retrieval operations.
 * <p>
 * Requires Basic authentication for all endpoints.
 *
 * @author x00000000
 * @since 2026-05-26
 */
@RestController
@RestSchema(schemaId = "retrievalMachineController")
@RequestMapping("/machine/knowledge")
public class RetrievalMachineController extends BaseRetrievalController {

    /**
     * Constructs a RetrievalMachineController.
     *
     * @param facade the knowledge service facade
     */
    public RetrievalMachineController(KnowledgeServiceFacade facade) {
        super(facade);
    }

    /**
     * Searches knowledge documents based on a query.
     *
     * @param request the search request containing query parameters
     * @return search results matching the query
     */
    @Override
    @PostMapping("/search")
    @BasicAuth
    public SearchResponse search(@RequestBody SearchRequest request) {
        return facade.search(request);
    }

    /**
     * Compares two knowledge documents to find their differences.
     *
     * @param request the compare request containing document IDs
     * @return comparison results highlighting differences
     */
    @Override
    @PostMapping("/search/compare")
    @BasicAuth
    public CompareSearchResponse compare(@RequestBody CompareSearchRequest request) {
        return facade.compare(request);
    }

    /**
     * Fetches a knowledge chunk by its identifier.
     *
     * @param chunkId the unique identifier of the chunk to fetch
     * @param includeNeighbors whether to include neighboring chunks in the result
     * @param neighborWindow the number of hops to consider for neighbors (1-20)
     * @param includeMarkdown whether to include markdown formatting
     * @param includeRawText whether to include raw text content
     * @return the fetched chunk data
     */
    @Override
    @GetMapping("/fetch/{chunkId}")
    @BasicAuth
    public FetchResponse fetch(
        @PathVariable("chunkId") String chunkId,
        @RequestParam(defaultValue = "false") boolean includeNeighbors,
        @RequestParam(defaultValue = "1") int neighborWindow,
        @RequestParam(defaultValue = "true") boolean includeMarkdown,
        @RequestParam(defaultValue = "true") boolean includeRawText
    ) {
        return facade.fetch(chunkId, includeNeighbors, neighborWindow);
    }

    /**
     * Retrieves relevant knowledge for a query based on an entity.
     *
     * @param request the retrieve request containing entity information
     * @return retrieved knowledge snippets
     */
    @Override
    @PostMapping("/retrieve")
    @BasicAuth
    public RetrieveResponse retrieve(@RequestBody RetrieveRequest request) {
        return facade.retrieve(request);
    }

    /**
     * Explains the reasoning behind a knowledge retrieval result.
     *
     * @param request the explain request containing result details
     * @return explanation of the reasoning process
     */
    @Override
    @PostMapping("/explain")
    @BasicAuth
    public ExplainResponse explain(@RequestBody ExplainRequest request) {
        return facade.explain(request);
    }
}
