/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.controller;

import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.service.KnowledgeGraphService;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Knowledge graph controller.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@RestController
@RequestMapping("/operation-intelligence/graph")
public class KnowledgeGraphController {
    private final KnowledgeGraphService knowledgeGraphService;

    public KnowledgeGraphController(KnowledgeGraphService knowledgeGraphService) {
        this.knowledgeGraphService = knowledgeGraphService;
    }

    /**
     * Imports graph data.
     *
     * @param request the request
     * @return the result
     */
    @PostMapping("/admin/import")
    public Mono<Map<String, Object>> importGraph(@RequestBody GraphSnapshot request) {
        return Mono.fromCallable(() -> ok("result", knowledgeGraphService.importGraph(request)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Gets an entity.
     *
     * @param entityId the entityId
     * @param envCode the envCode
     * @return the result
     */
    @GetMapping("/entities/{entityId}")
    public Mono<Map<String, Object>> getEntity(@PathVariable("entityId") String entityId,
        @RequestParam("envCode") String envCode) {
        return Mono.fromCallable(() -> ok("result", knowledgeGraphService.getEntity(envCode, entityId)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Queries a subgraph.
     *
     * @param request the request
     * @return the result
     */
    @PostMapping("/subgraph")
    public Mono<Map<String, Object>> querySubgraph(@RequestBody Map<String, Object> request) {
        return Mono.fromCallable(() -> {
            String envCode = stringValue(request.get("envCode"));
            String entityId = stringValue(request.get("entityId"));
            int maxHops = request.containsKey("maxHops") ? intValue(request.get("maxHops")) : 1;
            return ok("result", knowledgeGraphService.querySubgraph(envCode, entityId, maxHops));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Map<String, Object> ok(String key, Object value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put(key, value);
        response.put("error", null);
        return response;
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    private int intValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid integer value: " + value);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid integer value: " + value);
    }
}
