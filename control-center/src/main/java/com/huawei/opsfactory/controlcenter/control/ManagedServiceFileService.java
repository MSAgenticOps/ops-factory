/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.control;

import com.huawei.opsfactory.controlcenter.config.ControlCenterProperties;
import com.huawei.opsfactory.controlcenter.registry.ManagedServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
/**
 * Managed Service File Service.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class ManagedServiceFileService {

    private final ManagedServiceRegistry registry;
    private final ControlCenterProperties properties;
    private final Path projectRoot;

    /**
     * Creates the managed service file service instance.
     *
     * @param registry the managed service registry
     * @param properties the control center properties
     */
    @Autowired
    public ManagedServiceFileService(ManagedServiceRegistry registry, ControlCenterProperties properties) {
        this.registry = registry;
        this.properties = properties;
        this.projectRoot = resolveProjectRoot();
    }

    /**
     * Resolves the project root directory from configuration.
     *
     * @return the absolute path to the project root directory
     */
    private Path resolveProjectRoot() {
        String configuredRoot = properties.getProjectRoot();
        if (configuredRoot != null && !configuredRoot.isEmpty()) {
            Path configured = Path.of(configuredRoot);
            if (configured.isAbsolute()) {
                return configured.normalize();
            }
            return configured.toAbsolutePath().normalize();
        }
        return Path.of("").toAbsolutePath().normalize();
    }

    /**
     * Reads the configuration file for the specified service.
     *
     * @param serviceId the service identifier
     * @return a map containing serviceId, serviceName, path, and content
     */
    public Map<String, Object> readConfig(String serviceId) {
        ControlCenterProperties.ServiceTarget service = registry.require(serviceId);
        Path configPath = configPathFor(serviceId);
        return Map.of(
                "serviceId", serviceId,
                "serviceName", service.getName(),
                "path", relativePath(configPath),
                "content", readFile(configPath)
        );
    }

    /**
     * Writes content to the configuration file for the specified service.
     *
     * @param serviceId the service identifier
     * @param content the content to write
     * @return a map containing serviceId, serviceName, path, and saved status
     * @throws IllegalStateException if writing the config file fails
     */
    public Map<String, Object> writeConfig(String serviceId, String content) {
        ControlCenterProperties.ServiceTarget service = registry.require(serviceId);
        Path configPath = configPathFor(serviceId);
        try {
            backupExistingFile(serviceId, configPath);
            Files.writeString(configPath, content == null ? "" : content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write config: " + e.getMessage(), e);
        }
        return Map.of(
                "serviceId", serviceId,
                "serviceName", service.getName(),
                "path", relativePath(configPath),
                "saved", true
        );
    }

    /**
     * Reads the specified number of lines from the log file for the specified service.
     *
     * @param serviceId the service identifier
     * @param lines the number of lines to read
     * @return a map containing serviceId, serviceName, path, lines, and content
     */
    public Map<String, Object> readLogs(String serviceId, int lines) {
        ControlCenterProperties.ServiceTarget service = registry.require(serviceId);
        Path logPath = logPathFor(serviceId);
        return Map.of(
                "serviceId", serviceId,
                "serviceName", service.getName(),
                "path", relativePath(logPath),
                "lines", Math.max(1, lines),
                "content", tailFile(logPath, Math.max(1, lines))
        );
    }

    /**
     * Resolves the configuration file path for the specified service.
     *
     * @param serviceId the service identifier
     * @return the absolute path to the configuration file
     */
    private Path configPathFor(String serviceId) {
        ControlCenterProperties.ServiceTarget service = registry.require(serviceId);
        if (service.getConfigPath() != null && !service.getConfigPath().isBlank()) {
            return projectRoot.resolve(service.getConfigPath()).normalize();
        }
        return switch (serviceId) {
            case "gateway" -> projectRoot.resolve("gateway").resolve("config.yaml");
            case "knowledge-service" -> projectRoot.resolve("knowledge-service").resolve("config.yaml");
            case "business-intelligence" -> projectRoot.resolve("business-intelligence").resolve("config.yaml");
            default -> throw new IllegalArgumentException("Unsupported managed service: " + serviceId);
        };
    }

    /**
     * Resolves the log file path for the specified service.
     *
     * @param serviceId the service identifier
     * @return the absolute path to the log file
     */
    private Path logPathFor(String serviceId) {
        ControlCenterProperties.ServiceTarget service = registry.require(serviceId);
        if (service.getLogPath() != null && !service.getLogPath().isBlank()) {
            return projectRoot.resolve(service.getLogPath()).normalize();
        }
        return switch (serviceId) {
            case "gateway" -> projectRoot.resolve("gateway").resolve("logs").resolve("gateway.log");
            case "knowledge-service" -> projectRoot.resolve("knowledge-service").resolve("logs").resolve("knowledge-service.log");
            case "business-intelligence" -> projectRoot.resolve("business-intelligence").resolve("logs").resolve("business-intelligence.log");
            default -> throw new IllegalArgumentException("Unsupported managed service: " + serviceId);
        };
    }

    /**
     * Reads the content of a file.
     *
     * @param path the file path
     * @return the file content, or empty string if file does not exist
     * @throws IllegalStateException if reading the file fails
     */
    private String readFile(Path path) {
        try {
            if (!Files.exists(path)) {
                return "";
            }
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read file: " + e.getMessage(), e);
        }
    }

    /**
     * Reads the specified number of lines from the end of a file.
     *
     * @param path the file path
     * @param lines the number of lines to read
     * @return the last N lines of the file, or empty string if file does not exist
     * @throws IllegalStateException if reading the file fails
     */
    private String tailFile(Path path, int lines) {
        try {
            if (!Files.exists(path)) {
                return "";
            }
            List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int fromIndex = Math.max(0, allLines.size() - lines);
            return String.join("\n", allLines.subList(fromIndex, allLines.size()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read log file: " + e.getMessage(), e);
        }
    }

    /**
     * Converts an absolute path to a path relative to the project root.
     *
     * @param path the absolute path
     * @return the relative path, or the file name if conversion fails
     */
    private String relativePath(Path path) {
        try {
            return projectRoot.relativize(path.toAbsolutePath().normalize()).toString();
        } catch (Exception ignored) {
            return path.getFileName() != null ? path.getFileName().toString() : path.toString();
        }
    }

    /**
     * Creates a backup of an existing file before modification.
     *
     * @param serviceId the service identifier
     * @param path the file path to backup
     * @throws IOException if backup creation fails
     */
    private void backupExistingFile(String serviceId, Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        Path backupDir = projectRoot.resolve("control-center").resolve("data").resolve("config-backups");
        Files.createDirectories(backupDir);
        String fileName = path.getFileName() != null ? path.getFileName().toString() : "config.yaml";
        String backupName = serviceId + "." + fileName + "." + System.currentTimeMillis() + ".bak";
        Files.copy(path, backupDir.resolve(backupName), StandardCopyOption.REPLACE_EXISTING);
        pruneBackups(backupDir, serviceId + "." + fileName + ".", 5);
    }

    /**
     * Prunes old backups, keeping only the most recent ones.
     *
     * @param backupDir the backup directory
     * @param prefix the backup file name prefix
     * @param maxBackups the maximum number of backups to keep
     * @throws IOException if listing or deleting files fails
     */
    private void pruneBackups(Path backupDir, String prefix, int maxBackups) throws IOException {
        try (Stream<Path> stream = Files.list(backupDir)) {
            List<Path> backups = stream
                    .filter(path -> {
                        String name = path.getFileName() != null ? path.getFileName().toString() : "";
                        return name.startsWith(prefix) && name.endsWith(".bak");
                    })
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .toList();
            for (int i = maxBackups; i < backups.size(); i++) {
                Files.deleteIfExists(backups.get(i));
            }
        }
    }
}
