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
 * Manages business type definitions persisted as JSON files under the gateway data directory.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class BusinessTypeService {
    private static final Logger log = LoggerFactory.getLogger(BusinessTypeService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final GatewayProperties properties;

    private Path businessTypesDir;

    /**
     * Creates the business type service instance.
     *
     * @param properties gateway configuration properties
     */
    public BusinessTypeService(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Initializes the business types data directory at startup.
     */
    @PostConstruct
    public void init() {
        Path gatewayRoot = properties.getGatewayRootPath();
        this.businessTypesDir = gatewayRoot.resolve("data").resolve("business-types");
        try {
            Files.createDirectories(businessTypesDir);
        } catch (IOException e) {
            log.error("Failed to create business-types directory: {}", businessTypesDir, e);
        }
        log.info("BusinessTypeService initialized, businessTypesDir={}", businessTypesDir);
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all business types.
     *
     * @return list of all business type maps
     */
    public List<Map<String, Object>> listBusinessTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        if (!Files.isDirectory(businessTypesDir)) {
            return types;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(businessTypesDir, "*.json")) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                Map<String, Object> bt = readFile(file);
                if (bt != null) {
                    types.add(bt);
                }
            }
        } catch (IOException e) {
            log.error("Failed to list business-types from {}", businessTypesDir, e);
        }
        return types;
    }

    /**
     * Gets a business type by its ID.
     *
     * @param id entity identifier
     * @return a business type by its ID
     */
    public Map<String, Object> getBusinessType(String id) throws NotFoundException {
        Path file = businessTypesDir.resolve(id + ".json");
        Map<String, Object> bt = readFile(file);
        if (bt == null) {
            throw new NotFoundException("Business type not found");
        }
        return bt;
    }

    /**
     * Creates a new business type from the provided field map.
     * Validates all fields (name, code, description, knowledge, color) before persistence.
     *
     * @param body request body containing business type fields
     * @return the created business type map including generated id and timestamps
     * @throws IllegalArgumentException if validation fails or the code already exists
     */
    public Map<String, Object> createBusinessType(Map<String, Object> body) {
        String name = ValidationUtils.validateStringField(body, "name", "Business type name", 100, true);
        String code = ValidationUtils.validateStringField(body, "code", "Business type code", 50, true);
        validateNameAndCodeUnique(name, code, null);

        // description and knowledge: length validation only, no XSS check
        String description = validateLengthOnly(body, "description", "Description", 500);
        String knowledge = validateLengthOnly(body, "knowledge", "Knowledge", 2000);

        Object colorObj = body.get("color");
        String color = (colorObj != null && !colorObj.toString().isBlank()) ? colorObj.toString() : "#6366f1";

        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> bt = new LinkedHashMap<>();
        bt.put("id", id);
        bt.put("name", name);
        bt.put("code", code);
        bt.put("description", description);
        bt.put("color", color);
        bt.put("knowledge", knowledge);
        bt.put("createdAt", now);
        bt.put("updatedAt", now);

        writeEntityFile(id, bt);
        log.info("Created business type: id={}, name={}, code={}", id, bt.get("name"), bt.get("code"));
        return bt;
    }

    /**
     * Updates an existing business type with the provided field map.
     * Only fields present in the body are updated; each field is validated before being applied.
     * Code field cannot be modified once created.
     *
     * @param id entity identifier
     * @param body updated fields
     * @return the updated business type map
     * @throws NotFoundException if the business type is not found
     * @throws IllegalArgumentException if field validation fails
     */
    public Map<String, Object> updateBusinessType(String id, Map<String, Object> body) throws NotFoundException {
        Path file = businessTypesDir.resolve(id + ".json");
        Map<String, Object> bt = readFile(file);
        if (bt == null) {
            throw new NotFoundException("Business type not found");
        }

        // Code field cannot be modified after creation - silently ignore if present
        body.remove("code");

        if (body.containsKey("name")) {
            String newName = ValidationUtils.validateStringField(body, "name", "Business type name", 100, true);
            validateNameAndCodeUnique(newName, null, id);
            bt.put("name", newName);
        }
        if (body.containsKey("description")) {
            String newDescription = validateLengthOnly(body, "description", "Description", 500);
            bt.put("description", newDescription);
        }
        if (body.containsKey("color")) {
            Object colorObj = body.get("color");
            if (colorObj != null) {
                bt.put("color", colorObj.toString());
            }
        }
        if (body.containsKey("knowledge")) {
            String newKnowledge = validateLengthOnly(body, "knowledge", "Knowledge", 2000);
            bt.put("knowledge", newKnowledge);
        }

        bt.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, bt);
        log.info("Updated business type: id={}", id);
        return bt;
    }

    /**
     * Deletes a business type by its ID.
     *
     * @param id entity identifier
     * @return true if deleted, false if not found
     */
    public boolean deleteBusinessType(String id) {
        Path file = businessTypesDir.resolve(id + ".json");
        try {
            if (Files.exists(file)) {
                Files.delete(file);
                log.info("Deleted business type: id={}", id);
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("Failed to delete business-type file: {}", file, e);
            return false;
        }
    }

    // ── Validation ────────────────────────────────────────────────────

    /**
     * Validates that the business type name and code are unique.
     *
     * @param name the name to validate (may be null to only validate code)
     * @param code the code to validate (may be null to only validate name)
     * @param excludeId the ID of the current business type to exclude from validation (for updates)
     * @throws IllegalArgumentException if name or code already exists
     */
    private void validateNameAndCodeUnique(String name, String code, String excludeId) {
        List<Map<String, Object>> existing = listBusinessTypes();
        for (Map<String, Object> bt : existing) {
            String existingId = bt.get("id") != null ? bt.get("id").toString() : "";
            if (excludeId != null && existingId.equals(excludeId)) {
                continue;
            }
            if (name != null && !name.isBlank()) {
                String existingName = bt.get("name") != null ? bt.get("name").toString() : "";
                if (name.equalsIgnoreCase(existingName)) {
                    throw new IllegalArgumentException("Business type name already exists: " + name);
                }
            }
            if (code != null && !code.isBlank()) {
                String existingCode = bt.get("code") != null ? bt.get("code").toString() : "";
                if (code.equalsIgnoreCase(existingCode)) {
                    throw new IllegalArgumentException("Business type code already exists: " + code);
                }
            }
        }
    }

    /**
     * Validates a field for length only, without XSS character check.
     * Used for description and knowledge fields where XSS characters are allowed.
     *
     * @param body request body map
     * @param field field name to extract
     * @param displayName display name for error messages
     * @param maxLength maximum allowed length (0 = no limit)
     * @return the validated trimmed string, or empty string if missing/null
     * @throws IllegalArgumentException if validation fails
     */
    private String validateLengthOnly(Map<String, Object> body, String field, String displayName, int maxLength) {
        Object value = body.get(field);
        if (value == null) {
            return "";
        }
        String str = value.toString().trim();
        if (maxLength > 0 && str.length() > maxLength) {
            throw new IllegalArgumentException(displayName + " exceeds maximum length of " + maxLength);
        }
        return str;
    }

    // ── File I/O Helpers ─────────────────────────────────────────────

    /**
     * Reads a business type JSON file from disk.
     *
     * @param file path to the JSON file
     * @return the parsed map, or null if the file does not exist or cannot be read
     */
    private Map<String, Object> readFile(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            return MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to read business-type file: {}", file, e);
            return null;
        }
    }

    /**
     * Writes a business type entity to a JSON file on disk.
     *
     * @param id entity identifier used as the filename
     * @param entity business type map to persist
     * @throws IllegalStateException if the file cannot be written
     */
    private void writeEntityFile(String id, Map<String, Object> entity) {
        try {
            Files.createDirectories(businessTypesDir);
            Path file = businessTypesDir.resolve(id + ".json");
            String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to write business-type file for id={}", id, e);
            throw new IllegalStateException("Failed to save business type", e);
        }
    }
}
