/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Snapshot file store for knowledge graph.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@Component
public class GraphSnapshotStore {
    private static final Logger log = LoggerFactory.getLogger(GraphSnapshotStore.class);

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final DateTimeFormatter FILE_TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OperationIntelligenceProperties properties;

    public GraphSnapshotStore(OperationIntelligenceProperties properties) {
        this.properties = properties;
    }

    /**
     * Resolves snapshot root directory.
     *
     * @return the result
     */
    public Path resolveRoot() {
        String dataDir = properties.getKnowledgeGraph().getDataDir();
        return properties.resolveDataRoot().resolve(dataDir).normalize();
    }

    /**
     * Saves one environment snapshot atomically.
     *
     * @param snapshot the snapshot
     */
    public void save(GraphSnapshot snapshot) {
        try {
            Path envDir = envDir(snapshot.getEnvCode());
            Files.createDirectories(envDir);
            String fileName = "snapshot_" + OffsetDateTime.now().format(FILE_TS_FORMAT) + ".json";
            Path tmp = envDir.resolve(fileName + ".tmp");
            Path target = envDir.resolve(fileName);
            MAPPER.writeValue(tmp.toFile(), snapshot);
            Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            cleanup(envDir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save graph snapshot", e);
        }
    }

    /**
     * Loads latest snapshot for one environment.
     *
     * @param envCode the envCode
     * @return the result
     */
    public Optional<GraphSnapshot> loadLatest(String envCode) {
        List<Path> files = listSnapshots(envCode);
        for (int index = files.size() - 1; index >= 0; index--) {
            Path file = files.get(index);
            try {
                return Optional.of(MAPPER.readValue(file.toFile(), GraphSnapshot.class));
            } catch (IOException e) {
                log.warn("Failed to load graph snapshot {}: {}", file, e.getMessage());
            }
        }
        return Optional.empty();
    }

    /**
     * Loads all latest snapshots.
     *
     * @return the result
     */
    public List<GraphSnapshot> loadLatestAll() {
        List<GraphSnapshot> snapshots = new ArrayList<>();
        Path root = resolveRoot();
        if (!Files.isDirectory(root)) {
            return snapshots;
        }
        try (Stream<Path> dirs = Files.list(root)) {
            dirs.filter(Files::isDirectory)
                .forEach(dir -> loadLatest(dir.getFileName().toString()).ifPresent(snapshots::add));
        } catch (IOException e) {
            log.warn("Failed to list graph snapshots: {}", e.getMessage());
        }
        return snapshots;
    }

    private void cleanup(Path envDir) throws IOException {
        int retention = Math.max(properties.getKnowledgeGraph().getSnapshotRetention(), 1);
        List<Path> files = listSnapshots(envDir);
        int removable = files.size() - retention;
        for (int index = 0; index < removable; index++) {
            Files.deleteIfExists(files.get(index));
        }
    }

    private List<Path> listSnapshots(String envCode) {
        return listSnapshots(envDir(envCode));
    }

    private List<Path> listSnapshots(Path envDir) {
        if (!Files.isDirectory(envDir)) {
            return List.of();
        }
        try (Stream<Path> paths = Files.list(envDir)) {
            return paths.filter(path -> path.getFileName().toString().startsWith("snapshot_"))
                .filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                .toList();
        } catch (IOException e) {
            log.warn("Failed to list snapshot files under {}: {}", envDir, e.getMessage());
            return List.of();
        }
    }

    private Path envDir(String envCode) {
        return resolveRoot().resolve(envCode).normalize();
    }
}
