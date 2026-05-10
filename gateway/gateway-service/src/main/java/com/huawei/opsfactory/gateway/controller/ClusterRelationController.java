/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.service.ClusterRelationService;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

/**
 * REST controller for managing cluster-to-cluster relation edges and graph queries.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/gateway/cluster-relations")
public class ClusterRelationController {
    private final ClusterRelationService clusterRelationService;

    /**
     * Creates the cluster relation controller instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public ClusterRelationController(ClusterRelationService clusterRelationService) {
        this.clusterRelationService = clusterRelationService;
    }

    /**
     * Lists cluster relations, optionally filtered by cluster ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping
    public Mono<Map<String, Object>> listRelations(
            @RequestParam(value = "clusterId", required = false) String clusterId,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            List<Map<String, Object>> relations = clusterRelationService.listRelations(clusterId);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("relations", relations);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Returns the cluster relation graph data for visualization.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/graph")
    public Mono<Map<String, Object>> getGraph(
            @RequestParam(value = "groupId", required = false) String groupId,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> clusterRelationService.getGraphData(groupId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Returns the neighbor clusters for a given cluster.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/clusters/{clusterId}/neighbors")
    public Mono<Map<String, Object>> getClusterNeighbors(
            @PathVariable("clusterId") String clusterId,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> clusterRelationService.getClusterNeighbors(clusterId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Returns the neighbor hosts for a given host via cluster relations.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/hosts/{hostId}/neighbors")
    public Mono<Map<String, Object>> getHostNeighbors(
            @PathVariable("hostId") String hostId,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> clusterRelationService.getHostNeighborsByCluster(hostId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Creates a new cluster relation edge.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createRelation(
            @RequestBody Map<String, Object> request,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> relation = clusterRelationService.createRelation(request);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("relation", relation);
                return ResponseEntity.status(HttpStatus.CREATED).body(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Updates a cluster relation by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> updateRelation(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> request,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> relation = clusterRelationService.updateRelation(id, request);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("relation", relation);
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Deletes a cluster relation by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteRelation(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            boolean deleted = clusterRelationService.deleteRelation(id);
            if (!deleted) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "Cluster relation not found: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            return ResponseEntity.ok(body);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
