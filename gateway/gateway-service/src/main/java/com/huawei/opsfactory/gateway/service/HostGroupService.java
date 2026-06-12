/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.common.util.ValidationUtils;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages host group hierarchy and tree construction with cascade delete support.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class HostGroupService extends JsonFileEntityStore {
    private static final String MSG_CODE_REQUIRED = "Environment code is required";

    private static final String MSG_CODE_EXISTS = "Environment code already exists";

    private final GatewayProperties properties;

    /**
     * Creates the host group service instance.
     *
     * @param properties gateway configuration properties
     */
    public HostGroupService(GatewayProperties properties) {
        super("host group");
        this.properties = properties;
    }

    /**
     * Initializes the host groups data directory at startup.
     */
    @PostConstruct
    public void init() {
        initDataDir(properties.getGatewayRootPath().resolve("data"), "host-groups");
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all host groups.
     *
     * @return list of all host group maps
     */
    public List<Map<String, Object>> listGroups() {
        return listEntities();
    }

    /**
     * Gets a host group by its ID.
     *
     * @param id entity identifier
     * @return a host group by its ID
     */
    public Map<String, Object> getGroup(String id) throws NotFoundException {
        Map<String, Object> group = readFile(resolveEntityFile(id));
        if (group == null) {
            throw new NotFoundException("Host group not found");
        }
        return group;
    }

    /**
     * Build tree structure: top-level groups → sub-groups → clusters (leaf nodes).
     * Clusters are attached based on their groupId matching a group's id.
     * Business services are attached to their groupId node.
     *
     * @param groups list of host groups
     * @param clusters list of clusters
     * @return tree structure with groups and clusters
     */
    public Map<String, Object> getTree(List<Map<String, Object>> groups, List<Map<String, Object>> clusters) {
        return getTree(groups, clusters, List.of());
    }

    /**
     * Builds tree structure including top-level groups, sub-groups, clusters, and business services.
     *
     * @param groups list of host groups
     * @param clusters list of clusters
     * @param businessServices list of business services
     * @return tree structure map containing the group hierarchy
     */
    public Map<String, Object> getTree(List<Map<String, Object>> groups, List<Map<String, Object>> clusters,
        List<Map<String, Object>> businessServices) {
        Map<String, Map<String, Object>> groupNodeMap = buildGroupNodeMap(groups);
        attachClusters(groupNodeMap, clusters);
        attachBusinessServices(groupNodeMap, businessServices);
        List<Map<String, Object>> tree = buildGroupHierarchy(groupNodeMap);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tree", tree);
        return result;
    }

    /**
     * Builds a map of group ID to group node with initialized child collections.
     *
     * @param groups list of host groups
     * @return map of group ID to group node
     */
    private Map<String, Map<String, Object>> buildGroupNodeMap(List<Map<String, Object>> groups) {
        Map<String, Map<String, Object>> groupNodeMap = new LinkedHashMap<>();
        for (Map<String, Object> group : groups) {
            Map<String, Object> node = new LinkedHashMap<>(group);
            node.put("children", new ArrayList<Map<String, Object>>());
            node.put("clusters", new ArrayList<Map<String, Object>>());
            node.put("businessServices", new ArrayList<Map<String, Object>>());
            groupNodeMap.put((String) group.get("id"), node);
        }
        return groupNodeMap;
    }

    /**
     * Attaches clusters to their respective group nodes.
     *
     * @param groupNodeMap map of group ID to group node
     * @param clusters list of clusters to attach
     */
    private void attachClusters(Map<String, Map<String, Object>> groupNodeMap, List<Map<String, Object>> clusters) {
        for (Map<String, Object> cluster : clusters) {
            appendGroupedItem(groupNodeMap, (String) cluster.get("groupId"), "clusters", cluster);
        }
    }

    /**
     * Attaches business services to their respective group nodes.
     *
     * @param groupNodeMap map of group ID to group node
     * @param businessServices list of business services to attach
     */
    private void attachBusinessServices(Map<String, Map<String, Object>> groupNodeMap,
        List<Map<String, Object>> businessServices) {
        for (Map<String, Object> bs : businessServices) {
            appendGroupedItem(groupNodeMap, (String) bs.get("groupId"), "businessServices", bs);
        }
    }

    /**
     * Appends an item to the specified collection of a group node.
     *
     * @param groupNodeMap map of group ID to group node
     * @param groupId ID of the target group
     * @param key collection key (e.g., "clusters", "businessServices")
     * @param item item to append
     */
    @SuppressWarnings("unchecked")
    private void appendGroupedItem(Map<String, Map<String, Object>> groupNodeMap, String groupId, String key,
        Map<String, Object> item) {
        if (groupId == null || !groupNodeMap.containsKey(groupId)) {
            return;
        }
        ((List<Map<String, Object>>) groupNodeMap.get(groupId).get(key)).add(item);
    }

    /**
     * Builds the group hierarchy by attaching sub-groups to their parent nodes.
     *
     * @param groupNodeMap map of group ID to group node
     * @return list of top-level group nodes with nested children
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> buildGroupHierarchy(Map<String, Map<String, Object>> groupNodeMap) {
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Map<String, Object> node : groupNodeMap.values()) {
            String parentId = (String) node.get("parentId");
            Map<String, Object> parent = parentId != null ? groupNodeMap.get(parentId) : null;
            if (parent == null) {
                tree.add(node);
            } else {
                ((List<Map<String, Object>>) parent.get("children")).add(node);
            }
        }
        return tree;
    }

    /**
     * Creates a new host group from the provided field map.
     *
     * @param body request body
     * @return the created host group map
     * @throws BadRequestException if required fields are missing or validation fails
     * @throws ConflictException if name or code already exists
     */
    public Map<String, Object> createGroup(Map<String, Object> body) throws BadRequestException, ConflictException {
        String name = ValidationUtils.validateStringField(body, "name", "Group name", 100, true);

        String code = ValidationUtils.validateStringField(body, "code", "Group code", 50, true);

        List<Map<String, Object>> allGroups = listGroups();
        boolean nameDuplicate = allGroups.stream()
            .anyMatch(g -> name.equalsIgnoreCase(String.valueOf(g.get("name"))));
        if (nameDuplicate) {
            throw new ConflictException("Group name already exists");
        }

        boolean codeDuplicate = allGroups.stream()
            .anyMatch(g -> code.equalsIgnoreCase(String.valueOf(g.get("code"))));
        if (codeDuplicate) {
            throw new ConflictException(MSG_CODE_EXISTS);
        }

        ValidationUtils.validateStringField(body, "description", "Group description", 500, false);

        // Validate parentId if provided
        Object parentIdObj = body.get("parentId");
        if (parentIdObj != null && !parentIdObj.toString().isEmpty()) {
            String parentId = parentIdObj.toString();
            boolean parentExists = allGroups.stream()
                .anyMatch(g -> parentId.equals(g.get("id")));
            if (!parentExists) {
                throw new BadRequestException("Parent group not found: " + parentId);
            }
        }

        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> group = new LinkedHashMap<>();
        group.put("id", id);
        group.put("name", name);
        group.put("parentId", body.get("parentId"));
        group.put("description", body.getOrDefault("description", ""));
        group.put("code", code);
        group.put("enabled", body.getOrDefault("enabled", true));
        group.put("createdAt", now);
        group.put("updatedAt", now);

        writeEntityFile(id, group);
        log.info("Created host group: id={}, name={}, code={}", id, group.get("name"), code);
        return group;
    }

    /**
     * Updates an existing host group with the provided field map.
     *
     * @param id host group identifier
     * @param body request body containing updated fields
     * @return the updated host group map
     * @throws NotFoundException if host group not found
     * @throws BadRequestException if validation fails
     * @throws ConflictException if name or code already exists
     */
    public Map<String, Object> updateGroup(String id, Map<String, Object> body)
        throws NotFoundException, BadRequestException, ConflictException {
        Map<String, Object> group = readFile(resolveEntityFile(id));
        if (group == null) {
            throw new NotFoundException("Host group not found");
        }

        if (body.containsKey("name")) {
            String newName = ValidationUtils.validateStringField(body, "name", "Group name", 100, true);

            List<Map<String, Object>> allGroups = listGroups();
            boolean nameDuplicate = allGroups.stream()
                .filter(g -> !id.equals(g.get("id")))
                .anyMatch(g -> newName.equalsIgnoreCase(String.valueOf(g.get("name"))));
            if (nameDuplicate) {
                throw new ConflictException("Group name already exists");
            }
            group.put("name", newName);
        }
        if (body.containsKey("code")) {
            String newCode = ValidationUtils.validateStringField(body, "code", "Group code", 50, true);

            List<Map<String, Object>> allGroups = listGroups();
            boolean codeDuplicate = allGroups.stream()
                .filter(g -> !id.equals(g.get("id")))
                .anyMatch(g -> newCode.equalsIgnoreCase(String.valueOf(g.get("code"))));
            if (codeDuplicate) {
                throw new ConflictException(MSG_CODE_EXISTS);
            }
            group.put("code", newCode);
        }
        if (body.containsKey("parentId")) {
            Object newParentIdObj = body.get("parentId");
            if (newParentIdObj != null && !newParentIdObj.toString().isEmpty()) {
                String newParentId = newParentIdObj.toString();
                List<Map<String, Object>> allGroups = listGroups();
                boolean parentExists = allGroups.stream()
                    .anyMatch(g -> newParentId.equals(g.get("id")));
                if (!parentExists) {
                    throw new BadRequestException("Parent group not found: " + newParentId);
                }
                group.put("parentId", newParentId);
            } else {
                group.put("parentId", null);
            }
        }
        if (body.containsKey("description")) {
            String description = ValidationUtils.validateStringField(body, "description", "Group description", 500, false);
            group.put("description", description);
        }
        if (body.containsKey("enabled")) {
            group.put("enabled", body.get("enabled"));
        }

        group.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, group);
        log.info("Updated host group: id={}", id);
        return group;
    }

    /**
     * Delete a group. Rejects if the group has sub-groups or clusters.
     *
     * @param id entity identifier
     * @param clusterService used to check for clusters in this group
     * @return true if deleted
     */
    public boolean deleteGroup(String id, ClusterService clusterService) throws ConflictException {
        // Check for sub-groups
        List<Map<String, Object>> allGroups = listGroups();
        for (Map<String, Object> g : allGroups) {
            String parentId = (String) g.get("parentId");
            if (id.equals(parentId)) {
                throw new ConflictException(
                    "Cannot delete group with sub-groups. Remove sub-groups first.");
            }
        }

        // Check for clusters
        List<Map<String, Object>> clusters = clusterService.listClusters(id, null);
        if (!clusters.isEmpty()) {
            throw new ConflictException(
                "Cannot delete group with clusters. Remove clusters first.");
        }

        return deleteEntityFile(id);
    }

    /**
     * Force-delete a group with cascade: deletes business services, recursively force-deletes
     * sub-groups, force-deletes clusters (which cascade-delete hosts), then deletes the group.
     *
     * @param id group identifier
     * @param clusterService cluster service for cascade deletion
     * @param hostService host service for cascade deletion
     * @param businessServiceService business service service for cascade deletion
     * @return true if the group was deleted
     */
    public boolean forceDeleteGroup(String id, ClusterService clusterService, HostService hostService,
        BusinessServiceService businessServiceService) {
        // 1. Delete business services under this group
        for (Map<String, Object> bs : businessServiceService.listBusinessServices(id, null)) {
            businessServiceService.deleteBusinessService((String) bs.get("id"));
            log.info("Force-deleted business service {} in group {}", bs.get("id"), id);
        }

        // 2. Recursively force-delete sub-groups
        for (Map<String, Object> g : listGroups()) {
            if (id.equals(g.get("parentId"))) {
                forceDeleteGroup((String) g.get("id"), clusterService, hostService, businessServiceService);
            }
        }

        // 3. Force-delete all clusters in this group
        for (Map<String, Object> c : clusterService.listClusters(id, null)) {
            clusterService.forceDeleteCluster((String) c.get("id"), hostService);
        }

        // 4. Delete the group file itself
        return deleteEntityFile(id);
    }

    /**
     * Compute the set of group IDs that are effectively disabled, either directly
     * or by inheritance from a disabled ancestor. Uses fixed-point iteration to
     * handle arbitrary nesting depth.
     *
     * @param groups list of host groups
     * @return set of group IDs that are effectively disabled
     */
    public Set<String> getDisabledGroupIds(List<Map<String, Object>> groups) {
        Set<String> disabled = new HashSet<>();
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Map<String, Object> g : groups) {
                String id = (String) g.get("id");
                if (disabled.contains(id)) {
                    continue;
                }
                boolean selfOff = Boolean.FALSE.equals(g.get("enabled"));
                String pid = (String) g.get("parentId");
                boolean parentOff = pid != null && disabled.contains(pid);
                if (selfOff || parentOff) {
                    disabled.add(id);
                    changed = true;
                }
            }
        }
        return disabled;
    }

}
