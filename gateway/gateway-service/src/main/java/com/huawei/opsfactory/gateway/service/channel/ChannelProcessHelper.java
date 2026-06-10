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

    static ObjectMapper mapper() {
        return MAPPER;
    }

    static String asString(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    static String normalizeStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return "disconnected";
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }

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
                // ignore
            }
        }
    }

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

    static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // best-effort cleanup
        }
    }

    static void moveToProcessed(Path processedDir, Path file, String suffix) {
        try {
            Files.createDirectories(processedDir);
            Files.move(file,
                processedDir.resolve(file.getFileName().toString().replace(".json", "-" + suffix + ".json")));
        } catch (IOException e) {
            deleteQuietly(file);
        }
    }
}
