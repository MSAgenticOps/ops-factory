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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getLabels() {
        return new ArrayList<>(labels);
    }

    public void setLabels(List<String> labels) {
        this.labels = labels == null ? List.of() : new ArrayList<>(labels);
    }

    @Override
    public String toString() {
        return "GraphEntity{id='" + getId() + "', type='" + type + "', name='" + name + "'}";
    }
}
