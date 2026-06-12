/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.controller.base;

import com.huawei.opsfactory.knowledge.service.KnowledgeServiceFacade;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Base retrieval controller containing shared logic.
 *
 * @author x00000000
 * @since 2026-06-06
 */
public abstract class BaseRetrievalController {

    protected final KnowledgeServiceFacade facade;

    /**
     * Constructs a BaseRetrievalController.
     *
     * @param facade the knowledge service facade
     */
    public BaseRetrievalController(KnowledgeServiceFacade facade) {
        this.facade = facade;
    }

    /**
     * Searches for matching chunks.
     *
     * @param request the search request
     * @return the search response
     */
    @PostMapping("/search")
    public abstract SearchResponse search(@RequestBody SearchRequest request);

    /**
     * Compares search results across different modes.
     *
     * @param request the compare search request
     * @return the compare search response
     */
    @PostMapping("/search/compare")
    public abstract CompareSearchResponse compare(@RequestBody CompareSearchRequest request);

    /**
     * Fetches a chunk by ID.
     *
     * @param chunkId the chunk ID
     * @param includeNeighbors whether to include neighbors
     * @param neighborWindow the neighbor window size
     * @param includeMarkdown whether to include markdown
     * @param includeRawText whether to include raw text
     * @return the fetch response
     */
    @GetMapping("/fetch/{chunkId}")
    public abstract FetchResponse fetch(@PathVariable("chunkId") String chunkId,
        @RequestParam(defaultValue = "false") boolean includeNeighbors,
        @RequestParam(defaultValue = "1") int neighborWindow,
        @RequestParam(defaultValue = "true") boolean includeMarkdown,
        @RequestParam(defaultValue = "true") boolean includeRawText);

    /**
     * Retrieves evidence for a query.
     *
     * @param request the retrieve request
     * @return the retrieve response
     */
    @PostMapping("/retrieve")
    public abstract RetrieveResponse retrieve(@RequestBody RetrieveRequest request);

    /**
     * Explains a chunk's relevance to a query.
     *
     * @param request the explain request
     * @return the explain response
     */
    @PostMapping("/explain")
    public abstract ExplainResponse explain(@RequestBody ExplainRequest request);

    // Record type definitions - these must be defined here for the abstract method signatures
    // Subclasses should provide compatible implementations

    public record SearchRequest(String query, List<String> sourceIds, List<String> documentIds,
        String retrievalProfileId, Integer topK, SearchFilters filters, SearchOverride override) {
    }

    public record SearchFilters(List<String> contentTypes) {
    }

    public record SearchOverride(String mode, Integer lexicalTopK, Integer semanticTopK, Integer rrfK,
        Double scoreThreshold, Boolean includeScores, Boolean includeExplain, Integer snippetLength) {
    }

    public record SearchResponse(String query, List<SearchHit> hits, int total) {
    }

    public record CompareSearchRequest(String query, List<String> sourceIds, List<String> documentIds,
        String retrievalProfileId, SearchFilters filters, List<String> modes) {
    }

    public record CompareSearchResponse(String query, int fetchedTopK, CompareModeResponse hybrid,
        CompareModeResponse semantic, CompareModeResponse lexical) {
    }

    public record CompareModeResponse(List<SearchHit> hits, int total) {
    }

    public record SearchHit(String chunkId, String documentId, String sourceId, String title, List<String> titlePath,
        String snippet, double score, double lexicalScore, double semanticScore, double fusionScore, Integer pageFrom,
        Integer pageTo) {
    }

    public record FetchResponse(String chunkId, String documentId, String sourceId, String title,
        List<String> titlePath, String text, String markdown, List<String> keywords, Integer pageFrom, Integer pageTo,
        String previousChunkId, String nextChunkId, List<NeighborChunk> neighbors) {
    }

    public record NeighborChunk(String position, String chunkId, String text) {
    }

    public record RetrieveRequest(String query, List<String> sourceIds, String retrievalProfileId, Integer topK,
        RetrieveOverride override) {
    }

    public record RetrieveOverride(Boolean expandContext, String expandMode, Integer neighborWindow,
        Integer maxEvidenceCount, Integer maxEvidenceTokens, Boolean includeMetadata, Boolean includeReferences,
        Boolean includeExplain) {
    }

    public record RetrieveResponse(String query, List<Evidence> evidences) {
    }

    public record Evidence(String chunkId, String documentId, String sourceId, String title, String content,
        String markdown, double score, List<String> keywords, List<Reference> references) {
    }

    public record Reference(String type, Integer pageFrom, Integer pageTo) {
    }

    public record ExplainRequest(String query, String chunkId, List<String> sourceIds, String retrievalProfileId) {
    }

    public record ExplainResponse(String query, String chunkId, LexicalExplain lexical, SemanticExplain semantic,
        FusionExplain fusion) {
    }

    public record LexicalExplain(List<String> matchedFields, double score, int rank) {
    }

    public record SemanticExplain(double score, int rank) {
    }

    public record FusionExplain(String mode, double score) {
    }
}
