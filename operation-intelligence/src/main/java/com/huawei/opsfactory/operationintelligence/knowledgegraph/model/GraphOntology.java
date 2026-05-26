/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Knowledge graph ontology definition.
 *
 * @author x00000000
 * @since 2026-05-22
 */
public class GraphOntology {
    private String ontologyId;

    private String name;

    private String version = "1.0";

    private String sourceSystem;

    private Map<String, Object> metadata = new LinkedHashMap<>();

    private List<EntityTypeDefinition> entityTypes = new ArrayList<>();

    private List<RelationTypeDefinition> relationTypes = new ArrayList<>();

    /**
     * Gets the ontologyId.
     *
     * @return the ontologyId
     */
    public String getOntologyId() {
        return ontologyId;
    }

    /**
     * Sets the ontologyId.
     *
     * @param ontologyId the ontologyId
     */
    public void setOntologyId(String ontologyId) {
        this.ontologyId = ontologyId;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the sourceSystem.
     *
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * Sets the sourceSystem.
     *
     * @param sourceSystem the sourceSystem
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata == null ? new LinkedHashMap<>() : new LinkedHashMap<>(metadata);
    }

    /**
     * Gets the entityTypes.
     *
     * @return the entityTypes
     */
    public List<EntityTypeDefinition> getEntityTypes() {
        return entityTypes;
    }

    /**
     * Sets the entityTypes.
     *
     * @param entityTypes the entityTypes
     */
    public void setEntityTypes(List<EntityTypeDefinition> entityTypes) {
        this.entityTypes = entityTypes == null ? new ArrayList<>() : new ArrayList<>(entityTypes);
    }

    /**
     * Gets the relationTypes.
     *
     * @return the relationTypes
     */
    public List<RelationTypeDefinition> getRelationTypes() {
        return relationTypes;
    }

    /**
     * Sets the relationTypes.
     *
     * @param relationTypes the relationTypes
     */
    public void setRelationTypes(List<RelationTypeDefinition> relationTypes) {
        this.relationTypes = relationTypes == null ? new ArrayList<>() : new ArrayList<>(relationTypes);
    }
}
