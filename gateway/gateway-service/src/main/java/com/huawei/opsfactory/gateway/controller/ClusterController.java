/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for CRUD operations on cluster definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/gateway/clusters")
public class ClusterController {
    private final ClusterService clusterService;
    private final HostService hostService;
    private final HostGroupService hostGroupService;

    /**
     * Creates the cluster controller instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public ClusterController(ClusterService clusterService, HostService hostService,
                             HostGroupService hostGroupService) {
        this.clusterService = clusterService;
        this.hostService = hostService;
        this.hostGroupService = hostGroupService;
    }

    /**
     * Lists clusters, optionally filtered by group, type, or enabled status.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping
    public Mono<Map<String, Object>> listClusters(
            @RequestParam(value = "groupId", required = false) String groupId,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "enabledOnly", required = false, defaultValue = "false") boolean enabledOnly,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            List<Map<String, Object>> clusters = clusterService.listClusters(groupId, type);
            if (enabledOnly) {
                List<Map<String, Object>> allGroups = hostGroupService.listGroups();
                Set<String> disabledGroupIds = hostGroupService.getDisabledGroupIds(allGroups);
                clusters.removeIf(c -> Boolean.FALSE.equals(c.get("enabled"))
                        || disabledGroupIds.contains(c.get("groupId")));
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("clusters", clusters);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Gets a cluster by ID with its associated hosts.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getCluster(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> cluster = clusterService.getCluster(id);
                // Attach hosts for this cluster
                List<Map<String, Object>> hosts = hostService.listHostsByCluster(id);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("cluster", cluster);
                body.put("hosts", hosts);
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "Cluster not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Returns all distinct cluster types.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/types")
    public Mono<Map<String, Object>> getClusterTypes(ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            List<String> types = clusterService.getClusterTypes();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("types", types);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Lists all hosts belonging to a cluster.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{id}/hosts")
    public Mono<Map<String, Object>> getClusterHosts(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            List<Map<String, Object>> hosts = hostService.listHostsByCluster(id);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("hosts", hosts);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Creates a new cluster.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createCluster(
            @RequestBody Map<String, Object> request,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> cluster = clusterService.createCluster(request);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("cluster", cluster);
                return ResponseEntity.status(HttpStatus.CREATED).body(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "Invalid cluster request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Updates a cluster by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> updateCluster(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> request,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> cluster = clusterService.updateCluster(id, request);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("cluster", cluster);
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "Cluster not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Deletes a cluster by ID, optionally forcing deletion of associated hosts.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteCluster(
            @PathVariable("id") String id,
            @RequestParam(value = "force", required = false, defaultValue = "false") boolean force,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                boolean deleted;
                if (force) {
                    deleted = clusterService.forceDeleteCluster(id, hostService);
                } else {
                    deleted = clusterService.deleteCluster(id, hostService);
                }
                if (!deleted) {
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("success", false);
                    body.put("error", "Cluster not found: " + id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
                }
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                return ResponseEntity.ok(body);
            } catch (IllegalStateException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "Cluster delete conflict");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
