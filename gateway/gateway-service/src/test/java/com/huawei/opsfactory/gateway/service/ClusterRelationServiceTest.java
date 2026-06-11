/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.exception.BadRequestException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests for ClusterRelationService, focusing on the collectSubGroupClusters BFS logic.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class ClusterRelationServiceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private ClusterRelationService relationService;
    private HostGroupService hostGroupService;
    private ClusterService clusterService;
    private BusinessServiceService businessServiceService;
    private BusinessTypeService businessTypeService;

    private Path groupsDir;
    private Path clustersDir;
    private Path businessServicesDir;

    /**
     * Initializes the test environment with temporary directories and service instances.
     *
     * @throws IOException if the operation fails
     */
    @Before
    public void setUp() throws IOException {
        GatewayProperties properties = new GatewayProperties();
        GatewayProperties.Paths paths = new GatewayProperties.Paths();
        paths.setProjectRoot(tempFolder.getRoot().getAbsolutePath());
        properties.setPaths(paths);

        hostGroupService = new HostGroupService(properties);
        hostGroupService.init();

        clusterService = new ClusterService(properties);
        clusterService.init();

        businessTypeService = new BusinessTypeService(properties);
        businessTypeService.init();

        HostService hostService = new HostService(properties);
        hostService.init();

        businessServiceService = new BusinessServiceService(properties);
        businessServiceService.init();
        businessServiceService.setBusinessTypeService(businessTypeService);
        businessServiceService.setHostService(hostService);

        relationService = new ClusterRelationService(properties);
        relationService.init();
        relationService.setHostGroupService(hostGroupService);
        relationService.setClusterService(clusterService);
        relationService.setBusinessServiceService(businessServiceService);
        businessServiceService.setClusterRelationService(relationService);

        Path gatewayData = Path.of(tempFolder.getRoot().getAbsolutePath())
            .toAbsolutePath().normalize()
            .resolve("gateway").resolve("data");
        groupsDir = gatewayData.resolve("host-groups");
        clustersDir = gatewayData.resolve("clusters");
        businessServicesDir = gatewayData.resolve("business-services");
    }

    // ── collectSubGroupClusters via reflection ─────────────────

    /**
     * Invokes the private collectSubGroupClusters method via reflection.
     *
     * @param groupId the group identifier to collect clusters for
     * @return the list of collected cluster maps
     * @throws Exception if reflection invocation fails
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> invokeCollect(String groupId) throws Exception {
        Method method = ClusterRelationService.class.getDeclaredMethod(
            "collectSubGroupClusters", String.class, List.class);
        method.setAccessible(true);
        List<Map<String, Object>> result = new ArrayList<>();
        method.invoke(relationService, groupId, result);
        return result;
    }

    // ── Tests ────────────────────────────────────────────────────

    /**
     * Tests no sub groups returns empty.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testNoSubGroups_returnsEmpty() throws Exception {
        String gId = createGroupOnDisk("g1", "ROOT", null);

        List<Map<String, Object>> result = invokeCollect(gId);

        assertTrue(result.isEmpty());
    }

    /**
     * Tests single level sub groups clusters collected.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSingleLevelSubGroups_clustersCollected() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        String g1a = createGroupOnDisk("g1a", "CHILD-A", g1);
        createClusterOnDisk("c1", "Cluster-1", g1a);

        List<Map<String, Object>> result = invokeCollect(g1);

        assertEquals(1, result.size());
        assertEquals("c1", result.get(0).get("id"));
    }

    /**
     * Tests multi level BFS collects all descendants.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testMultiLevelBFS_collectsAllDescendants() throws Exception {
        String root = createGroupOnDisk("root", "ROOT", null);
        String a = createGroupOnDisk("a", "A", root);
        String b = createGroupOnDisk("b", "B", root);
        String aa = createGroupOnDisk("aa", "AA", a);
        createClusterOnDisk("c-a", "Cluster-A", a);
        createClusterOnDisk("c-b", "Cluster-B", b);
        createClusterOnDisk("c-aa", "Cluster-AA", aa);

        List<Map<String, Object>> result = invokeCollect(root);

        assertEquals(3, result.size());
        List<String> ids = result.stream().map(m -> (String) m.get("id")).toList();
        assertTrue(ids.contains("c-a"));
        assertTrue(ids.contains("c-b"));
        assertTrue(ids.contains("c-aa"));
    }

    /**
     * Tests does not collect clusters from parent or sibling trees.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testDoesNotCollectFromOtherTrees() throws Exception {
        String root1 = createGroupOnDisk("root1", "R1", null);
        String root2 = createGroupOnDisk("root2", "R2", null);
        String child1 = createGroupOnDisk("child1", "C1", root1);
        createClusterOnDisk("c1", "Cluster-1", child1);
        createClusterOnDisk("c2", "Cluster-2", root2);

        List<Map<String, Object>> result = invokeCollect(root1);

        assertEquals(1, result.size());
        assertEquals("c1", result.get(0).get("id"));
    }

    /**
     * Tests empty group with no clusters.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testEmptyGroupHierarchy_noClusters() throws Exception {
        String root = createGroupOnDisk("root", "ROOT", null);
        String groupA = createGroupOnDisk("a", "A", root);
        createGroupOnDisk("b", "B", groupA);

        List<Map<String, Object>> result = invokeCollect(root);

        assertTrue(result.isEmpty());
    }

    // ── Disk helpers ──────────────────────────────────────────────

    /**
     * Creates a host group JSON file on disk for testing.
     *
     * @param id the group identifier
     * @param name the group name
     * @param parentId the parent group identifier, or null for root groups
     * @return the group identifier
     * @throws IOException if writing to disk fails
     */
    private String createGroupOnDisk(String id, String name, String parentId) throws IOException {
        Map<String, Object> group = new LinkedHashMap<>();
        group.put("id", id);
        group.put("name", name);
        group.put("parentId", parentId);
        group.put("description", "");
        group.put("code", "");
        group.put("enabled", true);
        group.put("createdAt", "2026-01-01T00:00:00Z");
        group.put("updatedAt", "2026-01-01T00:00:00Z");

        String json = new com.fasterxml.jackson.databind.ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(group);
        Files.writeString(groupsDir.resolve(id + ".json"), json, StandardCharsets.UTF_8);
        return id;
    }

    /**
     * Creates a cluster JSON file on disk for testing.
     *
     * @param id the cluster identifier
     * @param name the cluster name
     * @param groupId the parent group identifier
     * @throws IOException if writing to disk fails
     */
    private void createClusterOnDisk(String id, String name, String groupId) throws IOException {
        Map<String, Object> cluster = new LinkedHashMap<>();
        cluster.put("id", id);
        cluster.put("name", name);
        cluster.put("type", "default");
        cluster.put("groupId", groupId);
        cluster.put("purpose", "");
        cluster.put("description", "");
        cluster.put("createdAt", "2026-01-01T00:00:00Z");
        cluster.put("updatedAt", "2026-01-01T00:00:00Z");

        String json = new com.fasterxml.jackson.databind.ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(cluster);
        Files.writeString(clustersDir.resolve(id + ".json"), json, StandardCharsets.UTF_8);
    }

    /**
     * Creates a business service JSON file on disk for testing.
     *
     * @param id the business service identifier
     * @param name the business service name
     * @throws IOException if writing to disk fails
     */
    private void createBusinessServiceOnDisk(String id, String name) throws IOException {
        Map<String, Object> bs = new LinkedHashMap<>();
        bs.put("id", id);
        bs.put("name", name);
        bs.put("typeId", "bt-1");
        bs.put("description", "");
        bs.put("hostIds", List.of());
        bs.put("createdAt", "2026-01-01T00:00:00Z");
        bs.put("updatedAt", "2026-01-01T00:00:00Z");

        String json = new com.fasterxml.jackson.databind.ObjectMapper()
            .writerWithDefaultPrettyPrinter().writeValueAsString(bs);
        Files.writeString(businessServicesDir.resolve(id + ".json"), json, StandardCharsets.UTF_8);
    }

    // ── createRelation validation tests ───────────────────────────

    /**
     * Tests that an invalid sourceType throws BadRequestException.
     *
     * @throws IOException if disk operation fails
     */
    @Test(expected = BadRequestException.class)
    public void testCreateRelation_invalidSourceType_throwsBadRequest() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceType", "invalid-type");
        body.put("sourceId", "c1");
        body.put("targetId", "c1");

        relationService.createRelation(body);
    }

    /**
     * Tests that a description exceeding 500 characters throws IllegalArgumentException.
     *
     * @throws Exception if the test fails
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateRelation_descriptionTooLong_throwsException() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceId", "c1");
        body.put("targetId", "c1");
        body.put("description", "a".repeat(501));

        relationService.createRelation(body);
    }

    /**
     * Tests that a description at exactly 500 characters is accepted.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateRelation_descriptionAtMaxLength_success() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceId", "c1");
        body.put("targetId", "c1");
        body.put("description", "a".repeat(500));

        Map<String, Object> result = relationService.createRelation(body);
        assertNotNull(result.get("id"));
        assertEquals("a".repeat(500), result.get("description"));
    }

    /**
     * Tests creating a relation with business-service source type succeeds.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testCreateRelation_businessServiceSource_success() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);
        createBusinessServiceOnDisk("bs1", "Business-Service-1");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceType", "business-service");
        body.put("sourceId", "bs1");
        body.put("targetId", "c1");
        body.put("description", "test relation");

        Map<String, Object> result = relationService.createRelation(body);
        assertNotNull(result.get("id"));
        assertEquals("business-service", result.get("sourceType"));
        assertEquals("bs1", result.get("sourceId"));
        assertEquals("c1", result.get("targetId"));
    }

    // ── updateRelation validation tests ───────────────────────────

    /**
     * Tests that updating a relation with a too-long description throws IllegalArgumentException.
     *
     * @throws Exception if the test fails
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateRelation_descriptionTooLong_throwsException() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceId", "c1");
        body.put("targetId", "c1");
        Map<String, Object> created = relationService.createRelation(body);
        String id = (String) created.get("id");

        Map<String, Object> updateBody = new LinkedHashMap<>();
        updateBody.put("description", "a".repeat(501));
        relationService.updateRelation(id, updateBody);
    }

    /**
     * Tests that updating a relation with a null description succeeds and clears the description.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUpdateRelation_descriptionNull_success() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceId", "c1");
        body.put("targetId", "c1");
        body.put("description", "initial desc");
        Map<String, Object> created = relationService.createRelation(body);
        String id = (String) created.get("id");

        Map<String, Object> updateBody = new LinkedHashMap<>();
        updateBody.put("description", null);
        Map<String, Object> updated = relationService.updateRelation(id, updateBody);
        assertNull(updated.get("description"));
    }

    /**
     * Tests that updating a relation with a valid description succeeds.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUpdateRelation_descriptionValid_success() throws Exception {
        String g1 = createGroupOnDisk("g1", "ROOT", null);
        createClusterOnDisk("c1", "Cluster-1", g1);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sourceId", "c1");
        body.put("targetId", "c1");
        Map<String, Object> created = relationService.createRelation(body);
        String id = (String) created.get("id");

        Map<String, Object> updateBody = new LinkedHashMap<>();
        updateBody.put("description", "updated description");
        Map<String, Object> updated = relationService.updateRelation(id, updateBody);
        assertEquals("updated description", updated.get("description"));
    }
}
