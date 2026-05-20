/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.service;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphEntity;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.store.GraphSnapshotStore;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.store.InMemoryGraphStore;

import jakarta.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Knowledge graph service.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@Service
public class KnowledgeGraphService {
    private static final String DEFAULT_SCHEMA_VERSION = "1.0";

    private final OperationIntelligenceProperties properties;

    private final GraphSchemaRegistry schemaRegistry;

    private final InMemoryGraphStore graphStore;

    private final GraphSnapshotStore snapshotStore;

    public KnowledgeGraphService(OperationIntelligenceProperties properties, GraphSchemaRegistry schemaRegistry,
        InMemoryGraphStore graphStore, GraphSnapshotStore snapshotStore) {
        this.properties = properties;
        this.schemaRegistry = schemaRegistry;
        this.graphStore = graphStore;
        this.snapshotStore = snapshotStore;
    }

    /**
     * Loads persisted snapshots on startup.
     */
    @PostConstruct
    public void init() {
        if (!properties.getKnowledgeGraph().isEnabled()) {
            return;
        }
        for (GraphSnapshot snapshot : snapshotStore.loadLatestAll()) {
            graphStore.loadSnapshot(snapshot);
        }
    }

    /**
     * Imports graph data.
     *
     * @param request the request
     * @return the result
     */
    public Map<String, Object> importGraph(GraphSnapshot request) {
        ensureEnabled();
        prepareDefaults(request);
        schemaRegistry.validate(request);
        GraphSnapshot merged = graphStore.upsert(request);
        snapshotStore.save(merged);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("envCode", merged.getEnvCode());
        result.put("snapshotId", merged.getSnapshotId());
        result.put("entityCount", merged.getEntities().size());
        result.put("relationCount", merged.getRelations().size());
        result.put("observationCount", merged.getObservations().size());
        return result;
    }

    /**
     * Gets entity details.
     *
     * @param envCode the envCode
     * @param entityId the entityId
     * @return the result
     */
    public GraphEntity getEntity(String envCode, String entityId) {
        ensureEnabled();
        requireText(envCode, "envCode");
        requireText(entityId, "entityId");
        return graphStore.getEntity(envCode, entityId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
    }

    /**
     * Queries subgraph.
     *
     * @param envCode the envCode
     * @param entityId the entityId
     * @param maxHops the maxHops
     * @return the result
     */
    public GraphSnapshot querySubgraph(String envCode, String entityId, int maxHops) {
        ensureEnabled();
        requireText(envCode, "envCode");
        requireText(entityId, "entityId");
        int configuredMaxHops = Math.max(properties.getKnowledgeGraph().getMaxHops(), 1);
        if (maxHops < 0 || maxHops > configuredMaxHops) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "maxHops must be between 0 and " + configuredMaxHops);
        }
        return graphStore.querySubgraph(envCode, entityId, maxHops)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found"));
    }

    private void prepareDefaults(GraphSnapshot request) {
        if (request.getSchemaVersion() == null || request.getSchemaVersion().isBlank()) {
            request.setSchemaVersion(DEFAULT_SCHEMA_VERSION);
        }
        if (request.getImportMode() == null || request.getImportMode().isBlank()) {
            request.setImportMode("UPSERT");
        }
        if (request.getSnapshotId() == null || request.getSnapshotId().isBlank()) {
            request.setSnapshotId("kg-" + request.getEnvCode() + "-" + System.currentTimeMillis());
        }
        if (request.getGeneratedAt() == null || request.getGeneratedAt().isBlank()) {
            request.setGeneratedAt(OffsetDateTime.now().toString());
        }
    }

    private void ensureEnabled() {
        if (!properties.getKnowledgeGraph().isEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Knowledge graph is disabled");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
    }
}
