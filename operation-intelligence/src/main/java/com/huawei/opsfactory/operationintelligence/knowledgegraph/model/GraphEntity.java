/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Knowledge graph entity.
 *
 * @author x00000000
 * @since 2026-05-20
 */
public class GraphEntity extends GraphElement {
    private String type;

    private String name;

    private String displayName;

    private String status = "Unknown";

    private List<String> labels = List.of();

    /**
     * Gets the entity type.
     *
     * @return the entity type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the entity type.
     *
     * @param type the entity type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the entity name.
     *
     * @return the entity name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the entity name.
     *
     * @param name the entity name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the entity status.
     *
     * @return the entity status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the entity status.
     *
     * @param status the entity status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets a defensive copy of entity labels.
     *
     * @return the entity labels
     */
    public List<String> getLabels() {
        return new ArrayList<>(labels);
    }

    /**
     * Sets entity labels.
     *
     * @param labels the entity labels
     */
    public void setLabels(List<String> labels) {
        this.labels = labels == null ? List.of() : new ArrayList<>(labels);
    }

    @Override
    public String toString() {
        return "GraphEntity{id='" + getId() + "', type='" + type + "', name='" + name + "'}";
    }
}
