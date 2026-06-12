/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.common.util.ValidationUtils;
import com.huawei.opsfactory.gateway.config.GatewayProperties;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages Standard Operating Procedure documents with command whitelist validation and name uniqueness checks.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class SopService extends JsonFileEntityStore {
    // SOP field length limits
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_VERSION_LENGTH = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_TRIGGER_CONDITION_LENGTH = 500;
    private static final int MAX_STEPS_DESCRIPTION_LENGTH = 1000;

    private final GatewayProperties properties;

    private final SolutionTypeService solutionTypeService;

    /**
     * Creates the sop service instance.
     *
     * @param properties gateway properties
     * @param solutionTypeService the solution type service for validation
     */
    public SopService(GatewayProperties properties, SolutionTypeService solutionTypeService) {
        super("SOP");
        this.properties = properties;
        this.solutionTypeService = solutionTypeService;
    }

    /**
     * Initializes the SOPs data directory at startup.
     */
    @PostConstruct
    public void init() {
        initDataDir(properties.getGatewayRootPath().resolve("data"), "sops");
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all SOP documents.
     *
     * @return the result
     */
    public List<Map<String, Object>> listSops() {
        List<Map<String, Object>> sops = new ArrayList<>();
        if (!Files.isDirectory(getDataDir())) {
            return sops;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(getDataDir(), "*.json")) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                Map<String, Object> sop = readSopFile(file);
                if (sop != null) {
                    sops.add(sop);
                }
            }
        } catch (IOException e) {
            log.error("Failed to list SOPs from {}", getDataDir(), e);
        }
        return sops;
    }

    /**
     * Gets an SOP document by its ID.
     *
     * @param id entity identifier
     * @return an SOP document by its ID
     */
    public Map<String, Object> getSop(String id) {
        Path file = resolveSopFile(id);
        Map<String, Object> sop = readSopFile(file);
        if (sop == null) {
            throw new IllegalArgumentException("SOP not found: " + id);
        }
        return sop;
    }

    /**
     * Creates a new SOP document from the provided field map.
     *
     * @param body request body
     * @return the result
     */
    public Map<String, Object> createSop(Map<String, Object> body) {
        // Validate name (required)
        String name = ValidationUtils.requireNonBlank(body, "name", "SOP name is required");
        ValidationUtils.requireNoXssChars(name, "SOP name");
        ValidationUtils.requireMaxLength(name, MAX_NAME_LENGTH, "SOP name");
        validateSopNameUnique(name, null);
        String id = UUID.randomUUID().toString();

        // Validate optional fields with length limits
        String version = "1.0.0";
        if (body.containsKey("version")) {
            String versionValue = ValidationUtils.validateStringField(body, "version", "Version",
                MAX_VERSION_LENGTH, false);
            if (!versionValue.isEmpty()) {
                version = versionValue;
            }
        }

        String description = "";
        if (body.containsKey("description")) {
            description = ValidationUtils.validateStringField(body, "description", "Description",
                MAX_DESCRIPTION_LENGTH, false);
        }

        String triggerCondition = "";
        if (body.containsKey("triggerCondition")) {
            triggerCondition = ValidationUtils.validateStringField(body, "triggerCondition", "Trigger Condition",
                MAX_TRIGGER_CONDITION_LENGTH, false);
        }

        String stepsDescription = "";
        if (body.containsKey("stepsDescription")) {
            stepsDescription = ValidationUtils.validateStringField(body, "stepsDescription", "Steps Description",
                MAX_STEPS_DESCRIPTION_LENGTH, false);
        }

        // Validate targetSolution
        String targetSolution = solutionTypeService.validateSolutionTypeReference(
            body.getOrDefault("targetSolution", "universal"));

        // Handle enabled
        Object enabledObj = body.get("enabled");
        boolean enabled = (enabledObj instanceof Boolean b) ? b : true;

        Map<String, Object> sop = new LinkedHashMap<>();
        sop.put("id", id);
        sop.put("name", name);
        sop.put("description", description);
        sop.put("version", version);
        sop.put("triggerCondition", triggerCondition);
        sop.put("enabled", enabled);
        sop.put("stepsDescription", stepsDescription);
        sop.put("targetSolution", targetSolution);
        sop.put("requiredTools", body.getOrDefault("requiredTools", List.of()));

        writeEntityFile(id, sop);
        log.info("Created SOP: id={}, name={}", id, sop.get("name"));
        return sop;
    }

    /**
     * Updates an existing SOP document with the provided field map.
     *
     * @param id entity identifier
     * @param body updated fields
     * @return the result
     */
    public Map<String, Object> updateSop(String id, Map<String, Object> body) {
        Path file = resolveSopFile(id);
        Map<String, Object> sop = readSopFile(file);
        if (sop == null) {
            throw new IllegalArgumentException("SOP not found: " + id);
        }

        // Update mutable fields with validation
        if (body.containsKey("name")) {
            String newName = ValidationUtils.validateStringField(body, "name", "SOP name",
                MAX_NAME_LENGTH, true);
            ValidationUtils.requireNoXssChars(newName, "SOP name");
            validateSopNameUnique(newName, id);
            sop.put("name", newName);
        }

        if (body.containsKey("description")) {
            String description = ValidationUtils.validateStringField(body, "description", "Description",
                MAX_DESCRIPTION_LENGTH, false);
            sop.put("description", description);
        }

        if (body.containsKey("version")) {
            String version = ValidationUtils.validateStringField(body, "version", "Version",
                MAX_VERSION_LENGTH, false);
            if (!version.isEmpty()) {
                sop.put("version", version);
            }
        }

        if (body.containsKey("triggerCondition")) {
            String triggerCondition = ValidationUtils.validateStringField(body, "triggerCondition", "Trigger Condition",
                MAX_TRIGGER_CONDITION_LENGTH, false);
            sop.put("triggerCondition", triggerCondition);
        }

        if (body.containsKey("stepsDescription")) {
            String stepsDescription = ValidationUtils.validateStringField(body, "stepsDescription", "Steps Description",
                MAX_STEPS_DESCRIPTION_LENGTH, false);
            sop.put("stepsDescription", stepsDescription);
        }

        if (body.containsKey("targetSolution")) {
            String targetSolution = solutionTypeService.validateSolutionTypeReference(body.get("targetSolution"));
            sop.put("targetSolution", targetSolution);
        }

        if (body.containsKey("requiredTools")) {
            sop.put("requiredTools", body.get("requiredTools"));
        }

        if (body.containsKey("enabled")) {
            Object enabledObj = body.get("enabled");
            boolean enabled = (enabledObj instanceof Boolean b) ? b : true;
            sop.put("enabled", enabled);
        }

        writeEntityFile(id, sop);
        log.info("Updated SOP: id={}", id);
        return sop;
    }

    /**
     * Deletes an SOP document by its ID.
     *
     * @param id entity identifier
     * @return the result
     */
    public boolean deleteSop(String id) {
        Path file = resolveSopFile(id);
        try {
            boolean deleted = Files.deleteIfExists(file);
            if (deleted) {
                log.info("Deleted SOP: id={}", id);
            }
            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete SOP file: {}", file, e);
            return false;
        }
    }

    // ── Validation ────────────────────────────────────────────────

    // ── Name Uniqueness Validation ────────────────────────────────

    /**
     * Validates that the SOP name is unique among existing SOP documents.
     *
     * @param name the SOP name to validate
     * @param excludeId the ID of the SOP to exclude from the check (for updates)
     * @throws IllegalArgumentException if the name already exists
     */
    private void validateSopNameUnique(String name, String excludeId) {
        if (name == null || name.isBlank()) {
            return;
        }
        List<Map<String, Object>> existing = listSops();
        for (Map<String, Object> sop : existing) {
            String existingName = sop.get("name") != null ? sop.get("name").toString() : "";
            String existingId = sop.get("id") != null ? sop.get("id").toString() : "";
            if (name.equalsIgnoreCase(existingName) && !existingId.equals(excludeId)) {
                throw new IllegalArgumentException("SOP name already exists: " + name);
            }
        }
    }

    /**
     * Resolve the JSON file path for a given SOP id.
     * Tries direct filename first ({id}.json), then scans all files
     * to match by the internal "id" field (e.g. sub-nslb-{uuid}.json).
     *
     * @param id entity identifier
     * @return the resolved file path
     */
    private Path resolveSopFile(String id) {
        // Fast path: direct filename match
        Path direct = resolveEntityFile(id);
        if (Files.exists(direct)) {
            return direct;
        }
        // Fallback: scan directory for a file whose internal "id" field matches
        if (Files.isDirectory(getDataDir())) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(getDataDir(), "*.json")) {
                for (Path file : stream) {
                    if (!Files.isRegularFile(file)) {
                        continue;
                    }
                    Map<String, Object> sop = readSopFile(file);
                    if (sop != null && id.equals(sop.get("id"))) {
                        return file;
                    }
                }
            } catch (IOException e) {
                log.error("Failed to scan SOPs directory for id={}", id, e);
            }
        }
        // Return the direct path even if not found (caller will handle null)
        return direct;
    }

    /**
     * Reads an SOP document from the given JSON file.
     *
     * @param file the JSON file path
     * @return the parsed SOP document, or null if the file does not exist or cannot be read
     */
    private Map<String, Object> readSopFile(Path file) {
        Map<String, Object> sop = readFile(file);
        if (sop == null) {
            return null;
        }
        // Ensure backward-compatible defaults for new fields
        sop.putIfAbsent("enabled", true);
        sop.putIfAbsent("stepsDescription", "");
        sop.putIfAbsent("targetSolution", "universal");
        sop.putIfAbsent("requiredTools", List.of());
        // Normalize targetSolution from legacy UUID to code
        Object targetSolution = sop.get("targetSolution");
        if (targetSolution != null && !"universal".equals(targetSolution)) {
            try {
                String normalizedCode = solutionTypeService.validateSolutionTypeReference(targetSolution);
                sop.put("targetSolution", normalizedCode);
            } catch (IllegalArgumentException e) {
                // If validation fails, keep original value (may be deleted solution type)
                log.debug("Failed to normalize targetSolution: {}", targetSolution);
            }
        }
        return sop;
    }
}
