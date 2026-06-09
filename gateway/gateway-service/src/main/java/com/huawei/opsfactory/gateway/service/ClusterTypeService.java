/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.common.util.ValidationUtils;
import com.huawei.opsfactory.gateway.config.GatewayProperties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

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
import java.util.HashMap;

/**
 * Manages cluster type definitions including mode, command prefix, and environment variables.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class ClusterTypeService {
    private static final Logger log = LoggerFactory.getLogger(ClusterTypeService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final GatewayProperties properties;

    private final SolutionTypeService solutionTypeService;

    private Path clusterTypesDir;

    /**
     * Creates the cluster type service instance.
     */
    public ClusterTypeService(GatewayProperties properties, SolutionTypeService solutionTypeService) {
        this.properties = properties;
        this.solutionTypeService = solutionTypeService;
    }

    /**
     * Initializes the cluster types data directory at startup.
     */
    @PostConstruct
    public void init() {
        Path gatewayRoot = properties.getGatewayRootPath();
        this.clusterTypesDir = gatewayRoot.resolve("data").resolve("cluster-types");
        try {
            Files.createDirectories(clusterTypesDir);
        } catch (IOException e) {
            log.error("Failed to create cluster-types directory: {}", clusterTypesDir, e);
        }
        log.info("ClusterTypeService initialized, clusterTypesDir={}", clusterTypesDir);
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all cluster types.
     *
     * @return the result
     */
    public List<Map<String, Object>> listClusterTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        if (!Files.isDirectory(clusterTypesDir)) {
            return types;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(clusterTypesDir, "*.json")) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                Map<String, Object> ct = readFile(file);
                if (ct != null) {
                    types.add(ct);
                }
            }
        } catch (IOException e) {
            log.error("Failed to list cluster-types from {}", clusterTypesDir, e);
        }
        return types;
    }

    /**
     * Gets a cluster type by its ID.
     *
     * @param id entity identifier
     * @return a cluster type by its ID
     */
    public Map<String, Object> getClusterType(String id) throws NotFoundException {
        Path file = clusterTypesDir.resolve(id + ".json");
        Map<String, Object> ct = readFile(file);
        if (ct == null) {
            throw new NotFoundException("Cluster type not found");
        }
        return ct;
    }

    /**
     * Creates a new cluster type from the provided field map.
     * Validates all fields before persistence.
     *
     * @param body request body
     * @return the result
     */
    public Map<String, Object> createClusterType(Map<String, Object> body) {
        // Validate required fields
        String name = ValidationUtils.validateStringField(body, "name", "Cluster type name", 100, true);
        String code = ValidationUtils.validateStringField(body, "code", "Cluster type code", 50, true);
        validateNameAndCodeUnique(name, code, null);

        // Validate optional fields
        String description = ValidationUtils.validateStringField(body, "description", "Description", 500, false);
        String knowledge = ValidationUtils.validateStringField(body, "knowledge", "Knowledge", 2000, false);
        String commandPrefix = ValidationUtils.validateStringField(body, "commandPrefix", "Command prefix", 0, false);

        // Validate and extract mode
        String mode = body.getOrDefault("mode", "peer").toString();
        if (!"peer".equals(mode) && !"primary-backup".equals(mode)) {
            throw new IllegalArgumentException("Invalid mode. Must be 'peer' or 'primary-backup'");
        }

        // Validate environment variables
        List<Map<String, String>> envVariables = validateEnvVariables(body);

        // Validate solution type reference
        String solutionType = solutionTypeService.validateSolutionTypeReference(
            body.getOrDefault("solutionType", "universal"));

        // Default color
        String color = body.getOrDefault("color", "#10b981").toString();

        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> ct = new LinkedHashMap<>();
        ct.put("id", id);
        ct.put("name", name);
        ct.put("code", code);
        ct.put("description", description);
        ct.put("color", color);
        ct.put("knowledge", knowledge);
        ct.put("commandPrefix", commandPrefix.isEmpty() ? null : commandPrefix);
        ct.put("envVariables", envVariables);
        ct.put("mode", mode);
        ct.put("solutionType", solutionType);
        ct.put("createdAt", now);
        ct.put("updatedAt", now);

        writeEntityFile(id, ct);
        log.info("Created cluster type: id={}, name={}, code={}", id, name, code);
        return ct;
    }

    /**
     * Updates an existing cluster type with the provided field map.
     * Only fields present in the body are updated; each field is validated before being applied.
     *
     * @param id an existing cluster type with the provided field map
     * @param body an existing cluster type with the provided field map
     * @return the result
     */
    public Map<String, Object> updateClusterType(String id, Map<String, Object> body)
            throws NotFoundException, BadRequestException {
        Path file = clusterTypesDir.resolve(id + ".json");
        Map<String, Object> ct = readFile(file);
        if (ct == null) {
            throw new NotFoundException("Cluster type not found");
        }

        if (body.containsKey("name")) {
            String newName = ValidationUtils.validateStringField(body, "name", "Cluster type name", 100, true);
            validateNameAndCodeUnique(newName, null, id);
            ct.put("name", newName);
        }
        if (body.containsKey("code")) {
            String newCode = ValidationUtils.validateStringField(body, "code", "Cluster type code", 50, true);
            validateNameAndCodeUnique(null, newCode, id);
            ct.put("code", newCode);
        }
        if (body.containsKey("description")) {
            String newDescription = ValidationUtils.validateStringField(body, "description", "Description", 500, false);
            ct.put("description", newDescription);
        }
        if (body.containsKey("knowledge")) {
            String newKnowledge = ValidationUtils.validateStringField(body, "knowledge", "Knowledge", 2000, false);
            ct.put("knowledge", newKnowledge);
        }
        if (body.containsKey("commandPrefix")) {
            String newCommandPrefix = ValidationUtils.validateStringField(body, "commandPrefix", "Command prefix", 0, false);
            ct.put("commandPrefix", newCommandPrefix.isEmpty() ? null : newCommandPrefix);
        }
        if (body.containsKey("envVariables")) {
            List<Map<String, String>> validatedEnv = validateEnvVariables(body);
            ct.put("envVariables", validatedEnv);
        }
        if (body.containsKey("mode")) {
            String mode = (String) body.get("mode");
            if (!"peer".equals(mode) && !"primary-backup".equals(mode)) {
                throw new BadRequestException("Invalid mode. Must be 'peer' or 'primary-backup'");
            }
            ct.put("mode", mode);
        }
        if (body.containsKey("solutionType")) {
            ct.put("solutionType", solutionTypeService.validateSolutionTypeReference(body.get("solutionType")));
        }
        if (body.containsKey("color")) {
            ct.put("color", body.get("color"));
        }

        ct.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, ct);
        log.info("Updated cluster type: id={}", id);
        return ct;
    }

    /**
     * Deletes a cluster type by its ID.
     *
     * @param id entity identifier
     * @return the result
     */
    public boolean deleteClusterType(String id) {
        Path file = clusterTypesDir.resolve(id + ".json");
        try {
            if (Files.exists(file)) {
                Files.delete(file);
                log.info("Deleted cluster type: id={}", id);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Failed to delete cluster-type file: {}", file, e);
            return false;
        }
    }

    /**
     * Reads a cluster type from the given JSON file.
     *
     * @param file the JSON file path
     * @return the parsed cluster type, or null if the file does not exist or cannot be read
     */
    private Map<String, Object> readFile(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            Map<String, Object> ct = MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
            // Normalize solutionType from legacy UUID to code
            Object solutionType = ct.get("solutionType");
            if (solutionType != null && !"universal".equals(solutionType)) {
                try {
                    String normalizedCode = solutionTypeService.validateSolutionTypeReference(solutionType);
                    ct.put("solutionType", normalizedCode);
                } catch (IllegalArgumentException e) {
                    // If validation fails, keep original value (may be deleted solution type)
                    log.debug("Failed to normalize solutionType: {}", solutionType);
                }
            }
            return ct;
        } catch (IOException e) {
            log.error("Failed to read cluster-type file: {}", file, e);
            return null;
        }
    }

    /**
     * Writes a cluster type entity to a JSON file.
     *
     * @param id the entity identifier used as the filename
     * @param entity the cluster type data to persist
     * @throws IllegalStateException if the file cannot be written
     */
    private void writeEntityFile(String id, Map<String, Object> entity) {
        try {
            Files.createDirectories(clusterTypesDir);
            Path file = clusterTypesDir.resolve(id + ".json");
            String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to write cluster-type file for id={}", id, e);
            throw new IllegalStateException("Failed to save cluster type", e);
        }
    }

    // ── Validation Helpers ────────────────────────────────────────────────

    /**
     * Validates environment variables array.
     * Checks for XSS characters and duplicate keys (case-insensitive).
     *
     * @param body request body containing envVariables field
     * @return validated and sanitized list of environment variable maps
     * @throws IllegalArgumentException if validation fails
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> validateEnvVariables(Map<String, Object> body) {
        Object envObj = body.get("envVariables");
        if (envObj == null) {
            return new ArrayList<>();
        }

        if (!(envObj instanceof List)) {
            throw new IllegalArgumentException("Environment variables must be an array");
        }

        List<?> envList = (List<?>) envObj;
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> seenKeys = new HashMap<>();

        for (int i = 0; i < envList.size(); i++) {
            Object itemObj = envList.get(i);
            if (!(itemObj instanceof Map)) {
                throw new IllegalArgumentException("Environment variable at index " + i + " must be an object");
            }

            Map<?, ?> item = (Map<?, ?>) itemObj;
            Object keyObj = item.get("key");
            Object valueObj = item.get("value");

            String key = keyObj != null ? keyObj.toString().trim() : "";
            String value = valueObj != null ? valueObj.toString().trim() : "";

            // Skip empty keys
            if (key.isEmpty()) {
                continue;
            }

            // Validate key with XSS check (strict)
            if (ValidationUtils.hasXssChars(key)) {
                throw new IllegalArgumentException(
                    "Environment variable key at index " + i + " contains invalid characters (< > \" ' & ` /)");
            }

            // Validate value with permissive check (allows / for paths)
            if (ValidationUtils.hasDangerousChars(value)) {
                throw new IllegalArgumentException(
                    "Environment variable value at index " + i + " contains invalid characters (< > \" ' & `)");
            }

            // Check for duplicate keys (case-insensitive)
            String lowerKey = key.toLowerCase();
            if (seenKeys.containsKey(lowerKey)) {
                throw new IllegalArgumentException(
                    "Duplicate environment variable key: " + key + " (conflicts with " + seenKeys.get(lowerKey) + ")");
            }
            seenKeys.put(lowerKey, key);

            Map<String, String> envVar = new LinkedHashMap<>();
            envVar.put("key", key);
            envVar.put("value", value);
            result.add(envVar);
        }

        return result;
    }

    /**
     * Validates that the cluster type name and code are unique.
     *
     * @param name the name to validate (may be null)
     * @param code the code to validate (may be null)
     * @param excludeId the id to exclude from uniqueness check (may be null)
     * @throws IllegalArgumentException if name or code already exists
     */
    private void validateNameAndCodeUnique(String name, String code, String excludeId) {
        List<Map<String, Object>> existing = listClusterTypes();
        for (Map<String, Object> ct : existing) {
            String existingId = ct.get("id") != null ? ct.get("id").toString() : "";
            if (excludeId != null && existingId.equals(excludeId)) {
                continue;
            }
            if (name != null && !name.isBlank()) {
                String existingName = ct.get("name") != null ? ct.get("name").toString() : "";
                if (name.equalsIgnoreCase(existingName)) {
                    throw new IllegalArgumentException("Cluster type name already exists: " + name);
                }
            }
            if (code != null && !code.isBlank()) {
                String existingCode = ct.get("code") != null ? ct.get("code").toString() : "";
                if (code.equalsIgnoreCase(existingCode)) {
                    throw new IllegalArgumentException("Cluster type code already exists: " + code);
                }
            }
        }
    }
}
