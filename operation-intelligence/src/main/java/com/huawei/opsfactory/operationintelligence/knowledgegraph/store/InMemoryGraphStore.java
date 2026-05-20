/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.store;

import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphEntity;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphObservation;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphRelation;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * In-memory graph store.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@Component
public class InMemoryGraphStore {
    private final Map<String, EnvGraph> graphs = new ConcurrentHashMap<>();

    /**
     * Loads a complete environment snapshot.
     *
     * @param snapshot the snapshot
     */
    public void loadSnapshot(GraphSnapshot snapshot) {
        EnvGraph envGraph = EnvGraph.fromSnapshot(snapshot);
        graphs.put(snapshot.getEnvCode(), envGraph);
    }

    /**
     * Upserts import data into one environment.
     *
     * @param incoming the incoming snapshot
     * @return merged snapshot
     */
    public GraphSnapshot upsert(GraphSnapshot incoming) {
        EnvGraph envGraph = graphs.computeIfAbsent(incoming.getEnvCode(), envCode -> new EnvGraph(envCode));
        envGraph.lock.writeLock().lock();
        try {
            envGraph.upsert(incoming);
            return envGraph.toSnapshot(incoming);
        } finally {
            envGraph.lock.writeLock().unlock();
        }
    }

    /**
     * Gets a snapshot for the environment.
     *
     * @param envCode the envCode
     * @return the result
     */
    public Optional<GraphSnapshot> getSnapshot(String envCode) {
        EnvGraph envGraph = graphs.get(envCode);
        if (envGraph == null) {
            return Optional.empty();
        }
        envGraph.lock.readLock().lock();
        try {
            return Optional.of(envGraph.toSnapshot(null));
        } finally {
            envGraph.lock.readLock().unlock();
        }
    }

    /**
     * Gets an entity.
     *
     * @param envCode the envCode
     * @param entityId the entityId
     * @return the result
     */
    public Optional<GraphEntity> getEntity(String envCode, String entityId) {
        EnvGraph envGraph = graphs.get(envCode);
        if (envGraph == null) {
            return Optional.empty();
        }
        envGraph.lock.readLock().lock();
        try {
            return Optional.ofNullable(envGraph.entities.get(entityId));
        } finally {
            envGraph.lock.readLock().unlock();
        }
    }

    /**
     * Queries a subgraph.
     *
     * @param envCode the envCode
     * @param entityId the entityId
     * @param maxHops the maxHops
     * @return the result
     */
    public Optional<GraphSnapshot> querySubgraph(String envCode, String entityId, int maxHops) {
        EnvGraph envGraph = graphs.get(envCode);
        if (envGraph == null) {
            return Optional.empty();
        }
        envGraph.lock.readLock().lock();
        try {
            if (!envGraph.entities.containsKey(entityId)) {
                return Optional.empty();
            }
            Set<String> selectedEntities = collectEntityIds(envGraph, entityId, maxHops);
            Set<String> selectedRelations = new LinkedHashSet<>();
            for (GraphRelation relation : envGraph.relations.values()) {
                if (selectedEntities.contains(relation.getFrom()) && selectedEntities.contains(relation.getTo())) {
                    selectedRelations.add(relation.getId());
                }
            }
            GraphSnapshot result = new GraphSnapshot();
            result.setEnvCode(envCode);
            result.setSnapshotId(envGraph.snapshotId);
            result.setSchemaVersion(envGraph.schemaVersion);
            selectedEntities.forEach(id -> result.getEntities().add(envGraph.entities.get(id)));
            selectedRelations.forEach(id -> result.getRelations().add(envGraph.relations.get(id)));
            for (GraphObservation observation : envGraph.observations.values()) {
                if (selectedEntities.contains(observation.getEntityId())) {
                    result.getObservations().add(observation);
                }
            }
            return Optional.of(result);
        } finally {
            envGraph.lock.readLock().unlock();
        }
    }

    private Set<String> collectEntityIds(EnvGraph envGraph, String startId, int maxHops) {
        Set<String> visited = new LinkedHashSet<>();
        Map<String, Integer> distance = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();
        visited.add(startId);
        distance.put(startId, 0);
        queue.add(startId);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDistance = distance.get(current);
            if (currentDistance >= maxHops) {
                continue;
            }
            for (String relationId : envGraph.entityRelations.getOrDefault(current, Set.of())) {
                GraphRelation relation = envGraph.relations.get(relationId);
                if (relation == null) {
                    continue;
                }
                String next = relation.getFrom().equals(current) ? relation.getTo() : relation.getFrom();
                if (visited.add(next)) {
                    distance.put(next, currentDistance + 1);
                    queue.add(next);
                }
            }
        }
        return visited;
    }

    private static class EnvGraph {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        private final String envCode;

        private String schemaVersion = "1.0";

        private String snapshotId;

        private String generatedAt;

        private final Map<String, GraphEntity> entities = new LinkedHashMap<>();

        private final Map<String, GraphRelation> relations = new LinkedHashMap<>();

        private final Map<String, GraphObservation> observations = new LinkedHashMap<>();

        private final Map<String, Set<String>> entityRelations = new LinkedHashMap<>();

        EnvGraph(String envCode) {
            this.envCode = envCode;
        }

        static EnvGraph fromSnapshot(GraphSnapshot snapshot) {
            EnvGraph envGraph = new EnvGraph(snapshot.getEnvCode());
            envGraph.upsert(snapshot);
            return envGraph;
        }

        void upsert(GraphSnapshot snapshot) {
            schemaVersion = firstNonBlank(snapshot.getSchemaVersion(), schemaVersion);
            snapshotId = firstNonBlank(snapshot.getSnapshotId(), snapshotId);
            generatedAt = firstNonBlank(snapshot.getGeneratedAt(), generatedAt);
            for (GraphEntity entity : snapshot.getEntities()) {
                entities.put(entity.getId(), entity);
            }
            for (GraphRelation relation : snapshot.getRelations()) {
                GraphRelation old = relations.put(relation.getId(), relation);
                if (old != null) {
                    removeRelationIndex(old);
                }
                addRelationIndex(relation);
            }
            for (GraphObservation observation : snapshot.getObservations()) {
                observations.put(observation.getId(), observation);
            }
        }

        GraphSnapshot toSnapshot(GraphSnapshot request) {
            GraphSnapshot snapshot = new GraphSnapshot();
            snapshot.setEnvCode(envCode);
            snapshot.setSchemaVersion(request == null
                ? schemaVersion
                : firstNonBlank(request.getSchemaVersion(), schemaVersion));
            snapshot.setSnapshotId(request == null ? snapshotId : firstNonBlank(request.getSnapshotId(), snapshotId));
            snapshot.setGeneratedAt(request == null
                ? generatedAt
                : firstNonBlank(request.getGeneratedAt(), generatedAt));
            snapshot.setSourceSystem(request == null ? null : request.getSourceSystem());
            snapshot.setImportMode("UPSERT");
            if (request != null) {
                snapshot.setMetadata(request.getMetadata());
            }
            snapshot.setEntities(new ArrayList<>(entities.values()));
            snapshot.setRelations(new ArrayList<>(relations.values()));
            snapshot.setObservations(new ArrayList<>(observations.values()));
            snapshot.getRelations().sort(Comparator.comparing(GraphRelation::getId));
            snapshot.getEntities().sort(Comparator.comparing(GraphEntity::getId));
            snapshot.getObservations().sort(Comparator.comparing(GraphObservation::getId));
            return snapshot;
        }

        private void addRelationIndex(GraphRelation relation) {
            entityRelations.computeIfAbsent(relation.getFrom(), key -> new HashSet<>()).add(relation.getId());
            entityRelations.computeIfAbsent(relation.getTo(), key -> new HashSet<>()).add(relation.getId());
        }

        private void removeRelationIndex(GraphRelation relation) {
            removeRelationIndex(relation.getFrom(), relation.getId());
            removeRelationIndex(relation.getTo(), relation.getId());
        }

        private void removeRelationIndex(String entityId, String relationId) {
            Set<String> relationIds = entityRelations.get(entityId);
            if (relationIds == null) {
                return;
            }
            relationIds.remove(relationId);
            if (relationIds.isEmpty()) {
                entityRelations.remove(entityId);
            }
        }

        private static String firstNonBlank(String first, String second) {
            return first == null || first.isBlank() ? second : first;
        }
    }
}
