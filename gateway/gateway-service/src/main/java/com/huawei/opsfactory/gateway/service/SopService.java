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
public class SopService {
    private static final Logger log = LoggerFactory.getLogger(SopService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final GatewayProperties properties;

    private final SolutionTypeService solutionTypeService;

    private Path gatewayRoot;

    private Path sopsDir;

    /**
     * Creates the sop service instance.
     */
    public SopService(GatewayProperties properties, SolutionTypeService solutionTypeService) {
        this.properties = properties;
        this.solutionTypeService = solutionTypeService;
    }

    /**
     * Initializes the SOPs data directory at startup.
     */
    @PostConstruct
    public void init() {
        this.gatewayRoot = properties.getGatewayRootPath();
        this.sopsDir = gatewayRoot.resolve("data").resolve("sops");

        try {
            Files.createDirectories(sopsDir);
        } catch (IOException e) {
            log.error("Failed to create SOPs directory: {}", sopsDir, e);
        }

        log.info("SopService initialized, sopsDir={}", sopsDir);
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all SOP documents.
     *
     * @return the result
     */
    public List<Map<String, Object>> listSops() {
        List<Map<String, Object>> sops = new ArrayList<>();
        if (!Files.isDirectory(sopsDir)) {
            return sops;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sopsDir, "*.json")) {
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
            log.error("Failed to list SOPs from {}", sopsDir, e);
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
        String name = body.getOrDefault("name", "") != null ? body.getOrDefault("name", "").toString() : "";
        validateSopNameUnique(name, null);
        String id = UUID.randomUUID().toString();

        Map<String, Object> sop = new LinkedHashMap<>();
        sop.put("id", id);
        sop.put("name", body.getOrDefault("name", ""));
        sop.put("description", body.getOrDefault("description", ""));
        sop.put("version", body.getOrDefault("version", "1.0.0"));
        sop.put("triggerCondition", body.getOrDefault("triggerCondition", ""));
        sop.put("enabled", body.getOrDefault("enabled", true));
        sop.put("stepsDescription", body.getOrDefault("stepsDescription", ""));
        sop.put("targetSolution", solutionTypeService.validateSolutionTypeReference(
            body.getOrDefault("targetSolution", "universal")));
        sop.put("requiredTools", body.getOrDefault("requiredTools", List.of()));

        writeSopFile(id, sop);
        log.info("Created SOP: id={}, name={}", id, sop.get("name"));
        return sop;
    }

    /**
     * Updates an existing SOP document with the provided field map.
     *
     * @param id an existing SOP document with the provided field map
     * @param body an existing SOP document with the provided field map
     * @return the result
     */
    public Map<String, Object> updateSop(String id, Map<String, Object> body) {
        Path file = resolveSopFile(id);
        Map<String, Object> sop = readSopFile(file);
        if (sop == null) {
            throw new IllegalArgumentException("SOP not found: " + id);
        }

        // Update mutable fields
        if (body.containsKey("name")) {
            validateSopNameUnique(body.get("name").toString(), id);
            sop.put("name", body.get("name"));
        }
        if (body.containsKey("description")) {
            sop.put("description", body.get("description"));
        }
        if (body.containsKey("version")) {
            sop.put("version", body.get("version"));
        }
        if (body.containsKey("triggerCondition")) {
            sop.put("triggerCondition", body.get("triggerCondition"));
        }
        if (body.containsKey("enabled")) {
            sop.put("enabled", body.get("enabled"));
        }
        if (body.containsKey("stepsDescription")) {
            sop.put("stepsDescription", body.get("stepsDescription"));
        }
        if (body.containsKey("targetSolution")) {
            sop.put("targetSolution", solutionTypeService.validateSolutionTypeReference(body.get("targetSolution")));
        }
        if (body.containsKey("requiredTools")) {
            sop.put("requiredTools", body.get("requiredTools"));
        }

        writeSopFile(id, sop);
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
            if (Files.exists(file)) {
                Files.delete(file);
                log.info("Deleted SOP: id={}", id);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Failed to delete SOP file: {}", file, e);
            return false;
        }
    }

    // ── Validation ────────────────────────────────────────────────

    // ── Name Uniqueness Validation ────────────────────────────────

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

    // ── File I/O Helpers ─────────────────────────────────────────────

    /**
     * Resolve the JSON file path for a given SOP id.
     * Tries direct filename first ({id}.json), then scans all files
     * to match by the internal "id" field (e.g. sub-nslb-{uuid}.json).
     */
    private Path resolveSopFile(String id) {
        // Fast path: direct filename match
        Path direct = sopsDir.resolve(id + ".json");
        if (Files.exists(direct)) {
            return direct;
        }
        // Fallback: scan directory for a file whose internal "id" field matches
        if (Files.isDirectory(sopsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(sopsDir, "*.json")) {
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

    private Map<String, Object> readSopFile(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            Map<String, Object> sop = MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
            // Ensure backward-compatible defaults for new fields
            sop.putIfAbsent("enabled", true);
            sop.putIfAbsent("stepsDescription", "");
            sop.putIfAbsent("targetSolution", "universal");
            sop.putIfAbsent("requiredTools", List.of());
            return sop;
        } catch (IOException e) {
            log.error("Failed to read SOP file: {}", file, e);
            return null;
        }
    }

    private void writeSopFile(String id, Map<String, Object> sop) {
        try {
            Files.createDirectories(sopsDir);
            Path file = sopsDir.resolve(id + ".json");
            String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(sop);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to write SOP file for id={}", id, e);
            throw new IllegalStateException("Failed to save SOP", e);
        }
    }
}
