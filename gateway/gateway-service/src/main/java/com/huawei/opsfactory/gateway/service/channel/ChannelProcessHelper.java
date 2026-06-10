/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.channel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Shared utility methods for channel login and message pump services.
 *
 * @author x00000000
 * @since 2026-06-08
 */
final class ChannelProcessHelper {

    private static final Logger log = LoggerFactory.getLogger(ChannelProcessHelper.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ChannelProcessHelper() {
    }

    /**
     * Returns the shared object mapper for channel process state files.
     *
     * @return the shared object mapper
     */
    static ObjectMapper mapper() {
        return MAPPER;
    }

    /**
     * Converts a value to trimmed text.
     *
     * @param value the source value
     * @return trimmed text, or null when the value is null or blank
     */
    static String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    /**
     * Normalizes a raw channel connection status.
     *
     * @param raw the raw status
     * @return normalized lowercase status, or disconnected when blank
     */
    static String normalizeStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return "disconnected";
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Reads a channel runtime state file.
     *
     * @param stateFile the runtime state file
     * @return the parsed runtime state, or an empty map when unavailable
     */
    @SuppressWarnings("unchecked")
    static Map<String, Object> readRuntimeState(Path stateFile) {
        try {
            if (!Files.exists(stateFile)) {
                return Map.of();
            }
            String raw = Files.readString(stateFile, StandardCharsets.UTF_8);
            if (raw.isBlank()) {
                return Map.of();
            }
            return MAPPER.readValue(raw, new TypeReference<>() {});
        } catch (IOException e) {
            return Map.of();
        }
    }

    /**
     * Terminates a process referenced by a PID file when it is still running.
     *
     * @param pidFile the PID file
     */
    static void killIfRunning(Path pidFile) {
        try {
            if (!Files.exists(pidFile)) {
                return;
            }
            String raw = Files.readString(pidFile, StandardCharsets.UTF_8).trim();
            if (raw.isBlank()) {
                Files.deleteIfExists(pidFile);
                return;
            }
            Map<String, Object> pidPayload = MAPPER.readValue(raw, new TypeReference<>() {});
            Object pidObj = pidPayload.get("pid");
            if (!(pidObj instanceof Number number)) {
                Files.deleteIfExists(pidFile);
                return;
            }
            long pid = number.longValue();
            ProcessHandle.of(pid).ifPresent(handle -> {
                handle.destroy();
                try {
                    handle.onExit().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    handle.destroyForcibly();
                } catch (ExecutionException e) {
                    handle.destroyForcibly();
                }
            });
            Files.deleteIfExists(pidFile);
        } catch (IOException | NumberFormatException e) {
            try {
                Files.deleteIfExists(pidFile);
            } catch (IOException deleteError) {
                log.debug("Failed to delete PID file {} during cleanup: {}", pidFile, deleteError.getMessage());
            }
        }
    }

    /**
     * Deletes all files under a directory.
     *
     * @param dir the directory to clear
     */
    static void clearDirectory(Path dir) {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (var walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(ChannelProcessHelper::deleteQuietly);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to clear directory " + dir, e);
        }
    }

    /**
     * Deletes a path and suppresses cleanup failures.
     *
     * @param path the path to delete
     */
    static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.debug("Best-effort cleanup failed for {}: {}", path, e.getMessage());
        }
    }

    /**
     * Moves one processed payload file into the processed directory.
     *
     * @param processedDir the processed directory
     * @param file the source file
     * @param suffix the suffix to append before the JSON extension
     */
    static void moveToProcessed(Path processedDir, Path file, String suffix) {
        try {
            Files.createDirectories(processedDir);
            String newName = file.getFileName().toString().replace(".json", "-" + suffix + ".json");
            Files.move(file, processedDir.resolve(newName));
        } catch (IOException e) {
            deleteQuietly(file);
        }
    }
}
