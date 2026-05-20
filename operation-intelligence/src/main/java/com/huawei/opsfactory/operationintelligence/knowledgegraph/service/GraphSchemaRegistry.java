/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.service;

import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphEntity;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphObservation;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphRelation;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

/**
 * Fixed MVP schema registry.
 *
 * @author x00000000
 * @since 2026-05-20
 */
@Component
public class GraphSchemaRegistry {
    private static final Set<String> ENTITY_TYPES =
        Set.of("BusinessCapability", "Service", "Cluster");

    private static final Map<String, RelationType> RELATION_TYPES = Map.of(
        "deployed_in", new RelationType("Service", "Cluster"));

    /**
     * Validates a graph snapshot.
     *
     * @param snapshot the snapshot
     */
    public void validate(GraphSnapshot snapshot) {
        requireText(snapshot.getEnvCode(), "envCode");
        requireText(snapshot.getSourceSystem(), "sourceSystem");
        if (!"UPSERT".equals(snapshot.getImportMode())) {
            throw badRequest("Only UPSERT importMode is supported in MVP");
        }
        for (GraphEntity entity : snapshot.getEntities()) {
            validateEntity(entity);
        }
        for (GraphRelation relation : snapshot.getRelations()) {
            validateRelation(snapshot, relation);
        }
        for (GraphObservation observation : snapshot.getObservations()) {
            validateObservation(snapshot, observation);
        }
    }

    private void validateEntity(GraphEntity entity) {
        requireText(entity.getId(), "entity.id");
        requireText(entity.getType(), "entity.type");
        if (!ENTITY_TYPES.contains(entity.getType())) {
            throw badRequest("Unsupported entity type: " + entity.getType());
        }
        if ("BusinessCapability".equals(entity.getType())) {
            requireProperty(entity, "menuId");
            requireProperty(entity, "menuName");
        } else if ("Service".equals(entity.getType())) {
            requireProperty(entity, "serviceName");
        } else if ("Cluster".equals(entity.getType())) {
            requireProperty(entity, "clusterName");
        }
    }

    private void validateRelation(GraphSnapshot snapshot, GraphRelation relation) {
        requireText(relation.getId(), "relation.id");
        requireText(relation.getType(), "relation.type");
        requireText(relation.getFrom(), "relation.from");
        requireText(relation.getTo(), "relation.to");
        RelationType relationType = RELATION_TYPES.get(relation.getType());
        if (relationType == null) {
            throw badRequest("Unsupported relation type: " + relation.getType());
        }
        GraphEntity from = findEntity(snapshot, relation.getFrom());
        GraphEntity to = findEntity(snapshot, relation.getTo());
        if (from == null || to == null) {
            throw badRequest("Relation endpoint does not exist: " + relation.getId());
        }
        if (!relationType.fromType().equals(from.getType()) || !relationType.toType().equals(to.getType())) {
            throw badRequest("Relation endpoint type mismatch: " + relation.getId());
        }
    }

    private void validateObservation(GraphSnapshot snapshot, GraphObservation observation) {
        requireText(observation.getId(), "observation.id");
        requireText(observation.getEntityId(), "observation.entityId");
        requireText(observation.getObservedAt(), "observation.observedAt");
        if (findEntity(snapshot, observation.getEntityId()) == null) {
            throw badRequest("Observation entity does not exist: " + observation.getId());
        }
    }

    private GraphEntity findEntity(GraphSnapshot snapshot, String entityId) {
        return snapshot.getEntities()
            .stream()
            .filter(entity -> entityId.equals(entity.getId()))
            .findFirst()
            .orElse(null);
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw badRequest(fieldName + " is required");
        }
    }

    private void requireProperty(GraphEntity entity, String propertyName) {
        Object value = entity.getProperties().get(propertyName);
        if (value == null || value.toString().isBlank()) {
            throw badRequest(entity.getType() + "." + propertyName + " is required");
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private record RelationType(String fromType, String toType) {
    }
}
