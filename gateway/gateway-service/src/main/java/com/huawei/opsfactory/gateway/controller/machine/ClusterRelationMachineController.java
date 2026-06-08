/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseClusterRelationController;
import com.huawei.opsfactory.gateway.service.ClusterRelationService;
import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Machine-to-machine REST controller for cluster relation CRUD operations and graph queries.
 * <p>
 * Requires Basic authentication for all endpoints.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "clusterRelationMachineController")
@RequestMapping("/machine/gateway/cluster-relations")
public class ClusterRelationMachineController extends BaseClusterRelationController {

    /**
     * Creates the cluster relation machine controller instance.
     *
     * @param clusterRelationService service handling cluster relation CRUD operations
     */
    public ClusterRelationMachineController(ClusterRelationService clusterRelationService) {
        super(clusterRelationService);
    }

    /**
     * Lists cluster relations, optionally filtered by cluster ID.
     *
     * @param clusterId optional cluster identifier to filter relations
     * @param request current HTTP request
     * @return a map with "relations" list
     */
    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listRelations(@RequestParam(value = "clusterId", required = false) String clusterId,
        HttpServletRequest request) {
        return super.listRelations(clusterId, request);
    }

    /**
     * Returns the cluster relation graph data for visualization.
     *
     * @param groupId optional group identifier to filter graph data
     * @param request current HTTP request
     * @return a map with graph nodes and edges
     */
    @Override
    @GetMapping("/graph")
    @BasicAuth
    public Map<String, Object> getGraph(@RequestParam(value = "groupId", required = false) String groupId,
        HttpServletRequest request) {
        return super.getGraph(groupId, request);
    }

    /**
     * Returns the neighbor clusters for a given cluster.
     *
     * @param clusterId cluster identifier to look up neighbors for
     * @param request current HTTP request
     * @return a map with neighbor cluster data
     * @throws NotFoundException if cluster not found
     */
    @Override
    @GetMapping("/clusters/{clusterId}/neighbors")
    @BasicAuth
    public Map<String, Object> getClusterNeighbors(@PathVariable("clusterId") String clusterId,
        HttpServletRequest request) throws NotFoundException {
        return super.getClusterNeighbors(clusterId, request);
    }

    /**
     * Returns the neighbor hosts for a given host via cluster relations.
     *
     * @param hostId host identifier to look up neighbors for
     * @param request current HTTP request
     * @return a map with neighbor host data
     * @throws NotFoundException if host not found
     */
    @Override
    @GetMapping("/hosts/{hostId}/neighbors")
    @BasicAuth
    public Map<String, Object> getHostNeighbors(@PathVariable("hostId") String hostId, HttpServletRequest request)
        throws NotFoundException {
        return super.getHostNeighbors(hostId, request);
    }

    /**
     * Creates a new cluster relation edge.
     *
     * @param requestBody request body containing relation fields
     * @param request current HTTP request
     * @return response entity with created relation or 400 if validation fails
     * @throws BadRequestException if validation fails
     * @throws NotFoundException if referenced entities not found
     */
    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createRelation(@RequestBody Map<String, Object> requestBody,
        HttpServletRequest request) throws BadRequestException, NotFoundException {
        return super.createRelation(requestBody, request);
    }

    /**
     * Updates a cluster relation by ID.
     *
     * @param id relation identifier
     * @param requestBody request body containing updated fields
     * @param request current HTTP request
     * @return response entity with updated relation or 404 if not found
     * @throws BadRequestException if validation fails
     * @throws NotFoundException if relation not found
     */
    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateRelation(@PathVariable("id") String id,
        @RequestBody Map<String, Object> requestBody, HttpServletRequest request)
        throws BadRequestException, NotFoundException {
        return super.updateRelation(id, requestBody, request);
    }

    /**
     * Deletes a cluster relation by ID.
     *
     * @param id relation identifier
     * @param request current HTTP request
     * @return response entity with success status or 404 if not found
     */
    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteRelation(@PathVariable("id") String id,
        HttpServletRequest request) {
        return super.deleteRelation(id, request);
    }
}
