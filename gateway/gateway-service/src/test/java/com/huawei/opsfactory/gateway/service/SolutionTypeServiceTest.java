/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Test coverage for SolutionType Service.
 *
 * @author x00000000
 * @since 2026-05-30
 */
public class SolutionTypeServiceTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private SolutionTypeService solutionTypeService;

    private Path solutionTypesDir;

    /**
     * Sets the up.
     *
     * @throws IOException if the operation fails
     */
    @Before
    public void setUp() throws IOException {
        GatewayProperties properties = new GatewayProperties();
        GatewayProperties.Paths paths = new GatewayProperties.Paths();
        paths.setProjectRoot(tempFolder.getRoot().getAbsolutePath());
        properties.setPaths(paths);

        solutionTypeService = new SolutionTypeService(properties);
        solutionTypeService.init();

        solutionTypesDir = Path.of(tempFolder.getRoot().getAbsolutePath())
            .toAbsolutePath()
            .normalize()
            .resolve("gateway")
            .resolve("data")
            .resolve("solution-types");
    }

    // ── listSolutionTypes ──────────────────────────────────────────

    /**
     * Tests list solution types empty.
     */
    @Test
    public void testListSolutionTypes_empty() {
        List<Map<String, Object>> types = solutionTypeService.listSolutionTypes();
        assertTrue(types.isEmpty());
    }

    /**
     * Tests list solution types returns all.
     */
    @Test
    public void testListSolutionTypes_returnsAll() {
        createSolutionType("st-1", "CRM Commerce", "CRM solution");
        createSolutionType("st-2", "CBS Billing", "CBS solution");

        List<Map<String, Object>> types = solutionTypeService.listSolutionTypes();
        assertEquals(2, types.size());
    }

    /**
     * Tests list solution types skips corrupt file.
     *
     * @throws IOException if the operation fails
     */
    @Test
    public void testListSolutionTypes_skipsCorruptFile() throws IOException {
        createSolutionType("st-1", "CRM Commerce", "CRM solution");
        Files.writeString(solutionTypesDir.resolve("bad.json"), "not valid json {}", StandardCharsets.UTF_8);

        List<Map<String, Object>> types = solutionTypeService.listSolutionTypes();
        assertEquals(1, types.size());
    }

    // ── getSolutionType ────────────────────────────────────────────

    /**
     * Tests get solution type existing.
     */
    @Test
    public void testGetSolutionType_existing() {
        createSolutionType("st-1", "CRM Commerce", "CRM billing solution");

        Map<String, Object> st = solutionTypeService.getSolutionType("st-1");
        assertNotNull(st);
        assertEquals("CRM Commerce", st.get("name"));
        assertEquals("CRM billing solution", st.get("description"));
    }

    /**
     * Tests get solution type not found.
     */
    @Test(expected = ResponseStatusException.class)
    public void testGetSolutionType_notFound() {
        solutionTypeService.getSolutionType("nonexistent");
    }

    // ── createSolutionType ─────────────────────────────────────────

    /**
     * Tests create solution type success.
     */
    @Test
    public void testCreateSolutionType_success() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "彩铃播控");
        body.put("code", "CRBT");
        body.put("description", "彩铃播控解决方案");
        body.put("color", "#e11d48");
        body.put("knowledge", "彩铃系统架构及常见故障知识");

        Map<String, Object> result = solutionTypeService.createSolutionType(body);

        assertNotNull(result.get("id"));
        assertEquals("彩铃播控", result.get("name"));
        assertEquals("CRBT", result.get("code"));
        assertEquals("彩铃播控解决方案", result.get("description"));
        assertEquals("#e11d48", result.get("color"));
        assertEquals("彩铃系统架构及常见故障知识", result.get("knowledge"));
        assertNotNull(result.get("createdAt"));
        assertNotNull(result.get("updatedAt"));
    }

    /**
     * Tests create solution type default values.
     */
    @Test
    public void testCreateSolutionType_defaultValues() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "MinimalSolution");

        Map<String, Object> result = solutionTypeService.createSolutionType(body);

        assertNotNull(result.get("id"));
        assertEquals("MinimalSolution", result.get("name"));
        assertEquals("", result.get("code"));
        assertEquals("", result.get("description"));
        assertEquals("#8b5cf6", result.get("color"));
        assertEquals("", result.get("knowledge"));
    }

    // ── updateSolutionType ─────────────────────────────────────────

    /**
     * Tests update solution type success.
     */
    @Test
    public void testUpdateSolutionType_success() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "Original");
        body.put("description", "orig desc");
        Map<String, Object> created = solutionTypeService.createSolutionType(body);
        String id = (String) created.get("id");

        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("name", "Updated");
        updates.put("description", "new desc");

        Map<String, Object> result = solutionTypeService.updateSolutionType(id, updates);
        assertEquals("Updated", result.get("name"));
        assertEquals("new desc", result.get("description"));
    }

    /**
     * Tests update solution type partial update.
     */
    @Test
    public void testUpdateSolutionType_partialUpdate() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "Original");
        body.put("code", "ORIG");
        body.put("description", "orig desc");
        Map<String, Object> created = solutionTypeService.createSolutionType(body);
        String id = (String) created.get("id");

        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("description", "new desc only");

        Map<String, Object> result = solutionTypeService.updateSolutionType(id, updates);
        assertEquals("Original", result.get("name"));
        assertEquals("ORIG", result.get("code"));
        assertEquals("new desc only", result.get("description"));
    }

    /**
     * Tests update solution type color.
     */
    @Test
    public void testUpdateSolutionType_color() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "ST");
        body.put("color", "#ff0000");
        Map<String, Object> created = solutionTypeService.createSolutionType(body);
        String id = (String) created.get("id");

        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("color", "#00ff00");

        Map<String, Object> result = solutionTypeService.updateSolutionType(id, updates);
        assertEquals("#00ff00", result.get("color"));
    }

    /**
     * Tests update solution type not found.
     */
    @Test(expected = ResponseStatusException.class)
    public void testUpdateSolutionType_notFound() {
        Map<String, Object> updates = new LinkedHashMap<>();
        updates.put("name", "NewName");
        solutionTypeService.updateSolutionType("nonexistent", updates);
    }

    // ── deleteSolutionType ─────────────────────────────────────────

    /**
     * Tests delete solution type success.
     */
    @Test
    public void testDeleteSolutionType_success() {
        createSolutionType("st-del", "ToDelete", "desc");

        boolean deleted = solutionTypeService.deleteSolutionType("st-del");
        assertTrue(deleted);
        assertTrue(solutionTypeService.listSolutionTypes().isEmpty());
    }

    /**
     * Tests delete solution type not found.
     */
    @Test
    public void testDeleteSolutionType_notFound() {
        boolean deleted = solutionTypeService.deleteSolutionType("nonexistent");
        assertFalse(deleted);
    }

    /**
     * Tests delete solution type file removed.
     *
     * @throws IOException if the operation fails
     */
    @Test
    public void testDeleteSolutionType_fileRemoved() throws IOException {
        createSolutionType("st-del", "ToDelete", "desc");
        assertTrue(Files.exists(solutionTypesDir.resolve("st-del.json")));

        solutionTypeService.deleteSolutionType("st-del");
        assertFalse(Files.exists(solutionTypesDir.resolve("st-del.json")));
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void createSolutionType(String id, String name, String description) {
        Map<String, Object> st = new LinkedHashMap<>();
        st.put("id", id);
        st.put("name", name);
        st.put("code", "");
        st.put("description", description);
        st.put("color", "#8b5cf6");
        st.put("knowledge", "");
        st.put("createdAt", "2026-05-30T00:00:00Z");
        st.put("updatedAt", "2026-05-30T00:00:00Z");

        try {
            Path file = solutionTypesDir.resolve(id + ".json");
            String json = new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(st);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
