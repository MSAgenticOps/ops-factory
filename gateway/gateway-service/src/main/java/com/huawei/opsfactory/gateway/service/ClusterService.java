/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.common.util.ValidationUtils;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides CRUD operations for cluster entities persisted as JSON files, with cascade delete support.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class ClusterService extends JsonFileEntityStore {

    private final GatewayProperties properties;

    private ClusterRelationService clusterRelationService;

    /**
     * Creates the cluster service instance.
     *
     * @param properties gateway configuration properties
     */
    public ClusterService(GatewayProperties properties) {
        super("cluster");
        this.properties = properties;
    }

    /**
     * Sets the cluster relation service via lazy injection.
     *
     * @param clusterRelationService service managing cluster relation edges
     */
    @Lazy
    @Autowired
    public void setClusterRelationService(ClusterRelationService clusterRelationService) {
        this.clusterRelationService = clusterRelationService;
    }

    /**
     * Initializes the clusters data directory at startup.
     */
    @PostConstruct
    public void init() {
        initDataDir(properties.getGatewayRootPath().resolve("data"), "clusters");
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * List clusters with optional filters.
     *
     * @param groupId optional group identifier filter (null for no filter)
     * @param type optional cluster type filter (null for no filter)
     * @return list of cluster maps matching the filters
     */
    public List<Map<String, Object>> listClusters(String groupId, String type) {
        List<Map<String, Object>> allClusters = listEntities();
        List<Map<String, Object>> clusters = new ArrayList<>();
        for (Map<String, Object> cluster : allClusters) {
            // Filter by groupId
            if (groupId != null && !groupId.isEmpty()) {
                Object cg = cluster.get("groupId");
                if (!groupId.equals(cg)) {
                    continue;
                }
            }
            // Filter by type
            if (type != null && !type.isEmpty()) {
                Object ct = cluster.get("type");
                if (!type.equalsIgnoreCase(ct != null ? ct.toString() : "")) {
                    continue;
                }
            }
            clusters.add(cluster);
        }
        return clusters;
    }

    /**
     * Gets a cluster by its ID.
     *
     * @param id cluster identifier
     * @return cluster data map
     */
    public Map<String, Object> getCluster(String id) throws NotFoundException {
        Path file = resolveEntityFile(id);
        Map<String, Object> cluster = readFile(file);
        if (cluster == null) {
            throw new NotFoundException("Cluster not found");
        }
        return cluster;
    }

    /**
     * Returns the distinct cluster types across all clusters.
     *
     * @return distinct cluster type names across all clusters
     */
    public List<String> getClusterTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();
        List<Map<String, Object>> clusters = listClusters(null, null);
        for (Map<String, Object> cluster : clusters) {
            Object type = cluster.get("type");
            if (type != null && !type.toString().isEmpty()) {
                types.add(type.toString());
            }
        }
        return new ArrayList<>(types);
    }

    /**
     * Creates a new cluster from the provided field map.
     *
     * @param body field map for the new cluster
     * @return the created cluster map with generated id and timestamps
     * @throws ConflictException if name already exists in the group
     */
    public Map<String, Object> createCluster(Map<String, Object> body) throws ConflictException {
        String name = ValidationUtils.validateStringField(body, "name", "Cluster name", 100, true);

        String type = ValidationUtils.validateStringField(body, "type", "Cluster type", 100, true);

        String groupId = ValidationUtils.requireNonBlank(body, "groupId", "Group is required");

        List<Map<String, Object>> allClusters = listClusters(null, null);
        boolean nameDuplicate = allClusters.stream()
            .filter(c -> groupId.equals(c.get("groupId")))
            .anyMatch(c -> name.equalsIgnoreCase(String.valueOf(c.get("name"))));
        if (nameDuplicate) {
            throw new ConflictException("Cluster name already exists in this group");
        }

        ValidationUtils.validateStringField(body, "purpose", "Cluster purpose", 200, false);
        ValidationUtils.validateStringField(body, "description", "Cluster description", 500, false);

        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> cluster = new LinkedHashMap<>();
        cluster.put("id", id);
        cluster.put("name", name);
        cluster.put("type", type);
        cluster.put("purpose", body.getOrDefault("purpose", ""));
        cluster.put("groupId", groupId);
        cluster.put("description", body.getOrDefault("description", ""));
        cluster.put("enabled", body.getOrDefault("enabled", true));
        cluster.put("createdAt", now);
        cluster.put("updatedAt", now);

        writeEntityFile(id, cluster);
        log.info("Created cluster: id={}, name={}, type={}", id, cluster.get("name"), cluster.get("type"));
        return cluster;
    }

    /**
     * Updates an existing cluster with the provided field map.
     *
     * @param id cluster identifier
     * @param body field map with updated values
     * @return the updated cluster map
     * @throws NotFoundException if cluster not found
     * @throws ConflictException if name already exists in the group
     */
    public Map<String, Object> updateCluster(String id, Map<String, Object> body) throws NotFoundException, ConflictException {
        Path file = resolveEntityFile(id);
        Map<String, Object> cluster = readFile(file);
        if (cluster == null) {
            throw new NotFoundException("Cluster not found");
        }

        if (body.containsKey("name")) {
            String newName = ValidationUtils.validateStringField(body, "name", "Cluster name", 100, true);

            Object groupIdObj = body.containsKey("groupId") ? body.get("groupId") : cluster.get("groupId");
            String groupId = groupIdObj != null ? groupIdObj.toString() : "";
            List<Map<String, Object>> allClusters = listClusters(null, null);
            boolean nameDuplicate = allClusters.stream()
                .filter(c -> !id.equals(c.get("id")) && groupId.equals(c.get("groupId")))
                .anyMatch(c -> newName.equalsIgnoreCase(String.valueOf(c.get("name"))));
            if (nameDuplicate) {
                throw new ConflictException("Cluster name already exists in this group");
            }
            cluster.put("name", newName);
        }
        if (body.containsKey("type")) {
            String newType = ValidationUtils.validateStringField(body, "type", "Cluster type", 100, true);
            cluster.put("type", newType);
        }
        if (body.containsKey("groupId")) {
            String newGroupId = ValidationUtils.requireNonBlank(body, "groupId", "Group is required");
            cluster.put("groupId", newGroupId);
        }
        if (body.containsKey("purpose")) {
            String purpose = ValidationUtils.validateStringField(body, "purpose", "Cluster purpose", 200, false);
            cluster.put("purpose", purpose);
        }
        if (body.containsKey("description")) {
            String description = ValidationUtils.validateStringField(body, "description", "Cluster description", 500, false);
            cluster.put("description", description);
        }
        if (body.containsKey("enabled")) {
            cluster.put("enabled", body.get("enabled"));
        }

        cluster.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, cluster);
        log.info("Updated cluster: id={}", id);
        return cluster;
    }

    /**
     * Delete a cluster. Rejects if the cluster has hosts.
     *
     * @param id entity identifier
     * @param hostService used to check for hosts in this cluster
     * @return true if deleted
     */
    public boolean deleteCluster(String id, HostService hostService) throws ConflictException {
        // Check for hosts
        List<Map<String, Object>> hosts = hostService.listHostsByCluster(id);
        if (!hosts.isEmpty()) {
            throw new ConflictException("Cannot delete cluster with hosts. Remove hosts first.");
        }

        // Cascade delete cluster relations
        if (clusterRelationService != null) {
            clusterRelationService.deleteRelationsByCluster(id);
        }

        return deleteEntityFile(id);
    }

    /**
     * Force-delete a cluster: deletes all hosts in the cluster first, then the cluster itself.
     * Host deletion cascades to their relations automatically.
     *
     * @param id cluster identifier
     * @param hostService used to delete hosts in this cluster
     * @return true if deleted
     */
    public boolean forceDeleteCluster(String id, HostService hostService) {
        // Delete all hosts (which cascade-deletes their relations)
        List<Map<String, Object>> hosts = hostService.listHostsByCluster(id);
        for (Map<String, Object> host : hosts) {
            hostService.deleteHost((String) host.get("id"));
            log.info("Force-deleted host {} in cluster {}", host.get("id"), id);
        }

        // Cascade delete cluster relations
        if (clusterRelationService != null) {
            clusterRelationService.deleteRelationsByCluster(id);
        }

        // Delete the cluster file itself
        return deleteEntityFile(id);
    }

}
