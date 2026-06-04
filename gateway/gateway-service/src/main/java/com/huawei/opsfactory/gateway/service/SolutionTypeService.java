/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.common.util.ValidationUtils;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

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
     * Lists all solution types persisted in the data directory.
     *
     * @return a list of all solution type maps; empty list if the directory does not exist or is empty
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
     * @return the solution type map
     * @throws NotFoundException if the solution type is not found
     */
    public Map<String, Object> getSolutionType(String id) throws NotFoundException {
        Path file = solutionTypesDir.resolve(id + ".json");
        Map<String, Object> st = readFile(file);
        if (st == null) {
            throw new NotFoundException("Solution type not found");
        }
        return st;
    }

    /**
     * Creates a new solution type from the provided field map.
     * Validates all fields (name, code, description, color, knowledge) before persistence.
     *
     * @param body request body containing solution type fields
     * @return the created solution type map including generated id and timestamps
     * @throws IllegalArgumentException if validation fails or the code already exists
     */
    public Map<String, Object> createSolutionType(Map<String, Object> body) {
        String name = ValidationUtils.requireNonBlank(body, "name", "Solution type name is required");
        ValidationUtils.requireMaxLength(name, 100, "Solution type name");
        ValidationUtils.requireNoXssChars(name, "Solution type name");

        String code = ValidationUtils.requireNonBlank(body, "code", "Solution type code is required");
        ValidationUtils.requireMaxLength(code, 50, "Solution type code");
        ValidationUtils.requireNoXssChars(code, "Solution type code");
        validateSolutionTypeCodeUnique(code, null);

        String description = body.getOrDefault("description", "").toString();
        ValidationUtils.requireMaxLength(description, 500, "Description");
        ValidationUtils.requireNoXssChars(description, "Description");

        String knowledge = body.getOrDefault("knowledge", "").toString();
        ValidationUtils.requireNoXssChars(knowledge, "Knowledge");

        String color = body.getOrDefault("color", "#8b5cf6").toString();
        if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
            color = "#8b5cf6";
        }

        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> st = new LinkedHashMap<>();
        st.put("id", id);
        st.put("name", name);
        st.put("code", code);
        st.put("description", description);
        st.put("color", color);
        st.put("knowledge", knowledge);
        st.put("createdAt", now);
        st.put("updatedAt", now);

        writeEntityFile(id, st);
        log.info("Created solution type: id={}, name={}, code={}", id, st.get("name"), st.get("code"));
        return st;
    }

    /**
     * Updates an existing solution type with the provided field map.
     * Only fields present in the body are updated; each field is validated before being applied.
     *
     * @param id entity identifier
     * @param body updated fields
     * @return the updated solution type map
     * @throws NotFoundException if the solution type is not found
     * @throws IllegalArgumentException if field validation fails or the new code already exists
     */
    public Map<String, Object> updateSolutionType(String id, Map<String, Object> body) throws NotFoundException {
        Path file = solutionTypesDir.resolve(id + ".json");
        Map<String, Object> st = readFile(file);
        if (st == null) {
            throw new NotFoundException("Solution type not found");
        }

        if (body.containsKey("name")) {
            String newName = body.get("name").toString().trim();
            ValidationUtils.requireNonBlank(Map.of("name", newName), "name", "Solution type name is required");
            ValidationUtils.requireMaxLength(newName, 100, "Solution type name");
            ValidationUtils.requireNoXssChars(newName, "Solution type name");
            st.put("name", newName);
        }
        if (body.containsKey("code")) {
            String newCode = body.get("code").toString().trim();
            ValidationUtils.requireNonBlank(Map.of("code", newCode), "code", "Solution type code is required");
            ValidationUtils.requireMaxLength(newCode, 50, "Solution type code");
            ValidationUtils.requireNoXssChars(newCode, "Solution type code");
            validateSolutionTypeCodeUnique(newCode, id);
            st.put("code", newCode);
        }
        if (body.containsKey("description")) {
            String newDescription = body.get("description").toString();
            ValidationUtils.requireMaxLength(newDescription, 500, "Description");
            ValidationUtils.requireNoXssChars(newDescription, "Description");
            st.put("description", newDescription);
        }
        if (body.containsKey("color")) {
            String newColor = body.get("color").toString();
            if (!newColor.matches("^#[0-9A-Fa-f]{6}$")) {
                newColor = "#8b5cf6";
            }
            st.put("color", newColor);
        }
        if (body.containsKey("knowledge")) {
            String newKnowledge = body.get("knowledge").toString();
            ValidationUtils.requireNoXssChars(newKnowledge, "Knowledge");
            st.put("knowledge", newKnowledge);
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
     * @return true if the file was deleted, false if it did not exist
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

    /**
     * Reads a solution type from the given JSON file.
     *
     * @param file the JSON file path
     * @return the parsed solution type, or null if the file does not exist or cannot be read
     */
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

    // ── Code Uniqueness Validation ──────────────────────────────────

    /**
     * Validates that the solution type code is unique among existing solution types.
     *
     * @param code the solution type code to validate
     * @param excludeId the ID of the solution type to exclude from the check (for updates)
     * @throws IllegalArgumentException if the code already exists
     */
    private void validateSolutionTypeCodeUnique(String code, String excludeId) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Solution type code is required");
        }
        List<Map<String, Object>> existing = listSolutionTypes();
        for (Map<String, Object> st : existing) {
            String existingCode = st.get("code") != null ? st.get("code").toString() : "";
            String existingId = st.get("id") != null ? st.get("id").toString() : "";
            if (code.equalsIgnoreCase(existingCode) && !existingId.equals(excludeId)) {
                throw new IllegalArgumentException("Solution type code already exists: " + code);
            }
        }
    }

    /**
     * Writes a solution type entity to a JSON file.
     *
     * @param id the entity identifier used as the filename
     * @param entity the solution type data to persist
     * @throws IllegalStateException if the file cannot be written
     */
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
