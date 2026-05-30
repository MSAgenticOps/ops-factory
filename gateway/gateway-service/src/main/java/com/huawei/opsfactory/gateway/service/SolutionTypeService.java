/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages solution type definitions persisted as JSON files under the gateway data directory.
 *
 * @author x00000000
 * @since 2026-05-30
 */
@Service
public class SolutionTypeService {
    private static final Logger log = LoggerFactory.getLogger(SolutionTypeService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final GatewayProperties properties;

    private Path solutionTypesDir;

    /**
     * Creates the solution type service instance.
     *
     * @param properties gateway properties
     */
    public SolutionTypeService(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Initializes the solution types data directory at startup.
     */
    @PostConstruct
    public void init() {
        Path gatewayRoot = properties.getGatewayRootPath();
        this.solutionTypesDir = gatewayRoot.resolve("data").resolve("solution-types");
        try {
            Files.createDirectories(solutionTypesDir);
        } catch (IOException e) {
            log.error("Failed to create solution-types directory: {}", solutionTypesDir, e);
        }
        log.info("SolutionTypeService initialized, solutionTypesDir={}", solutionTypesDir);
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all solution types.
     *
     * @return the result
     */
    public List<Map<String, Object>> listSolutionTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        if (!Files.isDirectory(solutionTypesDir)) {
            return types;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(solutionTypesDir, "*.json")) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                Map<String, Object> st = readFile(file);
                if (st != null) {
                    types.add(st);
                }
            }
        } catch (IOException e) {
            log.error("Failed to list solution-types from {}", solutionTypesDir, e);
        }
        return types;
    }

    /**
     * Gets a solution type by its ID.
     *
     * @param id entity identifier
     * @return a solution type by its ID
     */
    public Map<String, Object> getSolutionType(String id) {
        Path file = solutionTypesDir.resolve(id + ".json");
        Map<String, Object> st = readFile(file);
        if (st == null) {
            throw new IllegalArgumentException("Solution type not found: " + id);
        }
        return st;
    }

    /**
     * Creates a new solution type from the provided field map.
     *
     * @param body request body
     * @return the result
     */
    public Map<String, Object> createSolutionType(Map<String, Object> body) {
        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> st = new LinkedHashMap<>();
        st.put("id", id);
        st.put("name", body.getOrDefault("name", ""));
        st.put("code", body.getOrDefault("code", ""));
        st.put("description", body.getOrDefault("description", ""));
        st.put("color", body.getOrDefault("color", "#8b5cf6"));
        st.put("knowledge", body.getOrDefault("knowledge", ""));
        st.put("createdAt", now);
        st.put("updatedAt", now);

        writeEntityFile(id, st);
        log.info("Created solution type: id={}, name={}, code={}", id, st.get("name"), st.get("code"));
        return st;
    }

    /**
     * Updates an existing solution type with the provided field map.
     *
     * @param id entity identifier
     * @param body updated fields
     * @return the result
     */
    public Map<String, Object> updateSolutionType(String id, Map<String, Object> body) {
        Path file = solutionTypesDir.resolve(id + ".json");
        Map<String, Object> st = readFile(file);
        if (st == null) {
            throw new IllegalArgumentException("Solution type not found: " + id);
        }

        if (body.containsKey("name")) {
            st.put("name", body.get("name"));
        }
        if (body.containsKey("code")) {
            st.put("code", body.get("code"));
        }
        if (body.containsKey("description")) {
            st.put("description", body.get("description"));
        }
        if (body.containsKey("color")) {
            st.put("color", body.get("color"));
        }
        if (body.containsKey("knowledge")) {
            st.put("knowledge", body.get("knowledge"));
        }

        st.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, st);
        log.info("Updated solution type: id={}", id);
        return st;
    }

    /**
     * Deletes a solution type by its ID.
     *
     * @param id entity identifier
     * @return the result
     */
    public boolean deleteSolutionType(String id) {
        Path file = solutionTypesDir.resolve(id + ".json");
        try {
            if (Files.exists(file)) {
                Files.delete(file);
                log.info("Deleted solution type: id={}", id);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Failed to delete solution-type file: {}", file, e);
            return false;
        }
    }

    // ── File I/O Helpers ─────────────────────────────────────────────

    private Map<String, Object> readFile(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            return MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to read solution-type file: {}", file, e);
            return null;
        }
    }

    private void writeEntityFile(String id, Map<String, Object> entity) {
        try {
            Files.createDirectories(solutionTypesDir);
            Path file = solutionTypesDir.resolve(id + ".json");
            String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to write solution-type file for id={}", id, e);
            throw new IllegalStateException("Failed to save solution type", e);
        }
    }
}
