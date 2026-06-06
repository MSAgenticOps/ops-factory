/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.HostRelationService;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @deprecated Use {@link ClusterRelationController} instead. Host-level relations are replaced by cluster-level
 *             relations.
 */
@Deprecated

@RestController
@RestSchema(schemaId = "hostRelationController")
@RequestMapping("/api/gateway/host-relations")
public class HostRelationController {
    private static final Logger log = LoggerFactory.getLogger(HostRelationController.class);

    private final HostRelationService hostRelationService;

    private final BusinessServiceService businessServiceService;

    /**
     * Creates the host relation controller instance.
     */
    public HostRelationController(HostRelationService hostRelationService,
        BusinessServiceService businessServiceService) {
        this.hostRelationService = hostRelationService;
        this.businessServiceService = businessServiceService;
    }

    /**
     * Lists host relations, optionally filtered by host, group, cluster, or source.
     *
     * @param hostId host identifier
     * @param groupId group identifier
     * @param clusterId cluster identifier
     * @param sourceType source type filter
     * @param sourceId source identifier filter
     * @param exchange server web exchange
     * @return the result
     */
    @GetMapping
    public Map<String, Object> listRelations(@RequestParam(value = "hostId", required = false) String hostId,
        @RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "clusterId", required = false) String clusterId,
        @RequestParam(value = "sourceType", required = false) String sourceType,
        @RequestParam(value = "sourceId", required = false) String sourceId, HttpServletRequest request) {
        List<Map<String, Object>> relations =
            hostRelationService.listRelations(hostId, groupId, clusterId, sourceType, sourceId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("relations", relations);
        return result;
    }

    /**
     * Returns the host relation graph data enriched with business services.
     *
     * @param groupId returns the host relation graph data enriched with business services
     * @param clusterId returns the host relation graph data enriched with business services
     * @param exchange returns the host relation graph data enriched with business services
     * @return the host relation graph data enriched with business services
     */
    @GetMapping("/graph")
    public Map<String, Object> getGraph(@RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "clusterId", required = false) String clusterId, HttpServletRequest request) {
        Map<String, Object> graph = hostRelationService.getGraphData(groupId, clusterId);
        enrichWithBusinessServices(graph, groupId, clusterId);
        return graph;
    }

    /**
     * Returns the neighbor hosts for a given host.
     *
     * @param hostId returns the neighbor hosts for a given host
     * @param exchange returns the neighbor hosts for a given host
     * @return the neighbor hosts for a given host
     */
    @GetMapping("/hosts/{hostId}/neighbors")
    public Map<String, Object> getHostNeighbors(@PathVariable("hostId") String hostId, HttpServletRequest request)
        throws NotFoundException {
        return hostRelationService.getNeighbors(hostId);
    }

    /**
     * Creates a new host relation edge.
     *
     * @param request HTTP request
     * @param exchange server web exchange
     * @return the result
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRelation(@RequestBody Map<String, Object> requestBody,
        HttpServletRequest request) throws BadRequestException, NotFoundException {
        Map<String, Object> relation = hostRelationService.createRelation(requestBody);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("relation", relation);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Updates a host relation by ID.
     *
     * @param id a host relation by ID
     * @param request a host relation by ID
     * @param exchange a host relation by ID
     * @return the result
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRelation(@PathVariable("id") String id,
        @RequestBody Map<String, Object> requestBody, HttpServletRequest request)
        throws BadRequestException, NotFoundException {
        Map<String, Object> relation = hostRelationService.updateRelation(id, requestBody);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("relation", relation);
        return ResponseEntity.ok(body);
    }

    /**
     * Deletes a host relation by ID.
     *
     * @param id entity identifier
     * @param exchange server web exchange
     * @return the result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRelation(@PathVariable("id") String id,
        HttpServletRequest request) {
        boolean deleted = hostRelationService.deleteRelation(id);
        if (!deleted) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("error", "Host relation not found: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }

    @SuppressWarnings("unchecked")
    private void enrichWithBusinessServices(Map<String, Object> graph, String groupId, String clusterId) {
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) graph.get("nodes");
        List<Map<String, Object>> edges = (List<Map<String, Object>>) graph.get("edges");
        Set<String> hostNodeIds = collectHostNodeIds(nodes);
        List<Map<String, Object>> bsList = businessServiceService.listBusinessServices(null, null);
        int addedBs = 0;
        for (Map<String, Object> bs : bsList) {
            if (!hasHostOverlap(bs, hostNodeIds)) {
                continue;
            }
            String bsId = (String) bs.get("id");
            nodes.add(buildBusinessServiceNode(bs));
            edges.addAll(buildBusinessServiceEdges(bsId, hostNodeIds));
            addedBs++;
        }
        log.info("enrichWithBusinessServices: added {} BS nodes to graph", addedBs);
    }

    private Set<String> collectHostNodeIds(List<Map<String, Object>> nodes) {
        Set<String> hostNodeIds = new HashSet<>();
        for (Map<String, Object> node : nodes) {
            hostNodeIds.add((String) node.get("id"));
        }
        return hostNodeIds;
    }

    @SuppressWarnings("unchecked")
    private boolean hasHostOverlap(Map<String, Object> businessService, Set<String> hostNodeIds) {
        List<String> bsHostIds = (List<String>) businessService.getOrDefault("hostIds", Collections.emptyList());
        for (String hostId : bsHostIds) {
            if (hostNodeIds.contains(hostId)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> buildBusinessServiceNode(Map<String, Object> bs) {
        Map<String, Object> bsNode = new LinkedHashMap<>();
        bsNode.put("id", bs.get("id"));
        bsNode.put("name", bs.get("name"));
        bsNode.put("ip", null);
        bsNode.put("clusterType", null);
        bsNode.put("clusterName", null);
        bsNode.put("purpose", null);
        bsNode.put("groupId", bs.get("groupId"));
        bsNode.put("nodeType", "business-service");
        return bsNode;
    }

    private List<Map<String, Object>> buildBusinessServiceEdges(String bsId, Set<String> hostNodeIds) {
        List<Map<String, Object>> edges = new ArrayList<>();
        List<Map<String, Object>> relations = hostRelationService.listRelations(null, null, null, "business-service", bsId);
        for (Map<String, Object> rel : relations) {
            String targetId = (String) rel.get("targetHostId");
            if (targetId != null && hostNodeIds.contains(targetId)) {
                Map<String, Object> edge = new LinkedHashMap<>();
                edge.put("source", bsId);
                edge.put("target", targetId);
                edge.put("description", rel.getOrDefault("description", ""));
                edge.put("type", "business-entry");
                edges.add(edge);
            }
        }
        return edges;
    }
}
