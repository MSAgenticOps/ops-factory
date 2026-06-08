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
 * REST controller for managing cluster-to-cluster relation edges and graph queries.
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

    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listRelations(@RequestParam(value = "clusterId", required = false) String clusterId,
        HttpServletRequest request) {
        return super.listRelations(clusterId, request);
    }

    @Override
    @GetMapping("/graph")
    @BasicAuth
    public Map<String, Object> getGraph(@RequestParam(value = "groupId", required = false) String groupId,
        HttpServletRequest request) {
        return super.getGraph(groupId, request);
    }

    @Override
    @GetMapping("/clusters/{clusterId}/neighbors")
    @BasicAuth
    public Map<String, Object> getClusterNeighbors(@PathVariable("clusterId") String clusterId,
        HttpServletRequest request) throws NotFoundException {
        return super.getClusterNeighbors(clusterId, request);
    }

    @Override
    @GetMapping("/hosts/{hostId}/neighbors")
    @BasicAuth
    public Map<String, Object> getHostNeighbors(@PathVariable("hostId") String hostId, HttpServletRequest request)
        throws NotFoundException {
        return super.getHostNeighbors(hostId, request);
    }

    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createRelation(@RequestBody Map<String, Object> requestBody,
        HttpServletRequest request) throws BadRequestException, NotFoundException {
        return super.createRelation(requestBody, request);
    }

    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateRelation(@PathVariable("id") String id,
        @RequestBody Map<String, Object> requestBody, HttpServletRequest request)
        throws BadRequestException, NotFoundException {
        return super.updateRelation(id, requestBody, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteRelation(@PathVariable("id") String id,
        HttpServletRequest request) {
        return super.deleteRelation(id, request);
    }
}
