/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base class for knowledge graph elements (entities, relations, observations).
 *
 * @author x00000000
 * @since 2026-06-08
 */
public class GraphElement {
    private String id;

    private Map<String, Object> properties = new LinkedHashMap<>();

    private GraphSource source = new GraphSource();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return new LinkedHashMap<>(properties);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties == null ? new LinkedHashMap<>() : new LinkedHashMap<>(properties);
    }

    public GraphSource getSource() {
        return source;
    }

    public void setSource(GraphSource source) {
        this.source = source == null ? new GraphSource() : source;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GraphElement that = (GraphElement) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
