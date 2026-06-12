/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.base;

import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base controller for CRUD operations on cluster definitions.
 *
 * @author x00000000
 * @since 2026-06-06
 */
public abstract class BaseClusterController {
    protected final ClusterService clusterService;
    protected final HostService hostService;
    protected final HostGroupService hostGroupService;

    /**
     * Creates the base cluster controller instance.
     */
    public BaseClusterController(ClusterService clusterService, HostService hostService,
        HostGroupService hostGroupService) {
        this.clusterService = clusterService;
        this.hostService = hostService;
        this.hostGroupService = hostGroupService;
    }

    /**
     * Lists clusters, optionally filtered by group, type, or enabled status.
     *
     * @param groupId group identifier
     * @param type type filter
     * @param enabledOnly enabled-only filter flag
     * @param exchange server web exchange
     * @return the result
     */
    @GetMapping
    public Map<String, Object> listClusters(@RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "enabledOnly", required = false, defaultValue = "false") boolean enabledOnly,
        HttpServletRequest request) {
        List<Map<String, Object>> clusters = clusterService.listClusters(groupId, type);
        if (enabledOnly) {
            List<Map<String, Object>> allGroups = hostGroupService.listGroups();
            Set<String> disabledGroupIds = hostGroupService.getDisabledGroupIds(allGroups);
            clusters
                .removeIf(c -> Boolean.FALSE.equals(c.get("enabled")) || disabledGroupIds.contains(c.get("groupId")));
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("clusters", clusters);
        return result;
    }

    /**
     * Gets a cluster by ID with its associated hosts.
     *
     * @param id entity identifier
     * @param exchange server web exchange
     * @return a cluster by ID with its associated hosts
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCluster(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        Map<String, Object> cluster = clusterService.getCluster(id);
        // Attach hosts for this cluster
        List<Map<String, Object>> hosts = hostService.listHostsByCluster(id);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("cluster", cluster);
        body.put("hosts", hosts);
        return ResponseEntity.ok(body);
    }

    /**
     * Returns all distinct cluster types.
     *
     * @param exchange returns all distinct cluster types
     * @return all distinct cluster types
     */
    @GetMapping("/types")
    public Map<String, Object> getClusterTypes(HttpServletRequest request) {
        List<String> types = clusterService.getClusterTypes();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("types", types);
        return result;
    }

    /**
     * Lists all hosts belonging to a cluster.
     *
     * @param id entity identifier
     * @param exchange server web exchange
     * @return the result
     */
    @GetMapping("/{id}/hosts")
    public Map<String, Object> getClusterHosts(@PathVariable("id") String id, HttpServletRequest request) {
        List<Map<String, Object>> hosts = hostService.listHostsByCluster(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hosts", hosts);
        return result;
    }

    /**
     * Creates a new cluster.
     *
     * @param request HTTP request body containing cluster fields
     * @param httpRequest current HTTP request
     * @return response entity with created cluster
     * @throws ConflictException if validation fails (e.g., duplicate name)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCluster(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) throws ConflictException {
        Map<String, Object> cluster = clusterService.createCluster(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("cluster", cluster);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Updates a cluster by ID.
     *
     * @param id cluster identifier
     * @param request request body containing updated fields
     * @param httpRequest current HTTP request
     * @return response entity with updated cluster
     * @throws NotFoundException if cluster not found
     * @throws ConflictException if validation fails (e.g., duplicate name)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCluster(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) throws NotFoundException, ConflictException {
        Map<String, Object> cluster = clusterService.updateCluster(id, request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("cluster", cluster);
        return ResponseEntity.ok(body);
    }

    /**
     * Deletes a cluster by ID, optionally forcing deletion of associated hosts.
     *
     * @param id entity identifier
     * @param force whether to force the operation
     * @param exchange server web exchange
     * @return the result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCluster(@PathVariable("id") String id,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force,
        HttpServletRequest request) throws ConflictException {
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
    }
}
