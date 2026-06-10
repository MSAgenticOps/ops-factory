/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Base class for services that persist entities as individual JSON files in a directory.
 * Provides common read/write/list/delete file operations.
 *
 * @author x00000000
 * @since 2026-06-08
 */
public class JsonFileEntityStore {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Pattern SAFE_ENTITY_ID = Pattern.compile("[A-Za-z0-9_.-]+");

    /**
     * Returns the shared ObjectMapper instance for subclasses.
     *
     * @return the object mapper
     */
    protected static ObjectMapper mapper() {
        return MAPPER;
    }

    private final String entityLabel;

    // @PostConstruct happens-before any business method call, so volatile is not required
    private Path dataDir;

    /**
     * Creates a JSON file entity store.
     *
     * @param entityLabel human-readable label used in log messages (e.g. "cluster-type")
     */
    protected JsonFileEntityStore(String entityLabel) {
        this.entityLabel = entityLabel;
    }

    /**
     * Returns the data directory path.
     *
     * @return the data directory
     */
    protected Path getDataDir() {
        return dataDir;
    }

    /**
     * Initializes the data directory. Call from {@code @PostConstruct}.
     *
     * @param parentDir the parent directory (e.g. gatewayRoot/data)
     * @param subDirName the subdirectory name (e.g. "cluster-types")
     */
    protected void initDataDir(Path parentDir, String subDirName) {
        this.dataDir = parentDir.resolve(subDirName);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            log.error("Failed to create {} directory: {}", entityLabel, dataDir, e);
        }
        log.info("{} initialized, dataDir={}", getClass().getSimpleName(), dataDir);
    }

    /**
     * Reads a JSON entity from the given file.
     *
     * @param file the JSON file path
     * @return the parsed entity map, or null if the file does not exist or cannot be read
     */
    protected Map<String, Object> readFile(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            return MAPPER.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (IOException e) {
            log.error("Failed to read {} file: {}", entityLabel, file, e);
            return null;
        }
    }

    /**
     * Writes an entity map to a JSON file.
     *
     * @param id the entity identifier used as the filename
     * @param entity the entity data to persist
     * @throws IllegalStateException if the file cannot be written
     */
    protected void writeEntityFile(String id, Map<String, Object> entity) {
        try {
            Files.createDirectories(dataDir);
            Path file = resolveEntityFile(id);
            String json = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to write {} file for id={}", entityLabel, id, e);
            throw new IllegalStateException("Failed to save " + entityLabel, e);
        }
    }

    /**
     * Lists all entity files in the data directory.
     *
     * @return list of parsed entity maps
     */
    protected List<Map<String, Object>> listEntities() {
        List<Map<String, Object>> entities = new ArrayList<>();
        if (!Files.isDirectory(dataDir)) {
            return entities;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir, "*.json")) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                Map<String, Object> entity = readFile(file);
                if (entity != null) {
                    entities.add(entity);
                }
            }
        } catch (IOException e) {
            log.error("Failed to list {} from {}", entityLabel, dataDir, e);
        }
        return entities;
    }

    /**
     * Deletes an entity file by its ID.
     *
     * @param id the entity identifier
     * @return true if the file was deleted, false if it did not exist
     */
    protected boolean deleteEntityFile(String id) {
        try {
            boolean deleted = Files.deleteIfExists(resolveEntityFile(id));
            if (deleted) {
                log.info("Deleted {}: id={}", entityLabel, id);
            }
            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete {} file for id={}", entityLabel, id, e);
            return false;
        }
    }

    /**
     * Resolves the entity file path after validating the ID with a safe filename whitelist.
     *
     * @param id the entity identifier
     * @return the resolved file path
     * @throws IllegalArgumentException if the ID contains unsafe filename characters
     */
    protected Path resolveEntityFile(String id) {
        if (id == null || !SAFE_ENTITY_ID.matcher(id).matches()) {
            throw new IllegalArgumentException("Invalid entity ID: " + id);
        }
        return dataDir.resolve(id + ".json");
    }
}
