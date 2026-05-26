/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Relation type definition in a graph ontology.
 *
 * @author x00000000
 * @since 2026-05-22
 */
public class RelationTypeDefinition {
    private String type;

    private List<String> from = new ArrayList<>();

    private List<String> to = new ArrayList<>();

    private String layer;

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the from.
     *
     * @return the from
     */
    public List<String> getFrom() {
        return from;
    }

    /**
     * Sets the from.
     *
     * @param from the from
     */
    public void setFrom(List<String> from) {
        this.from = from == null ? new ArrayList<>() : new ArrayList<>(from);
    }

    /**
     * Gets the to.
     *
     * @return the to
     */
    public List<String> getTo() {
        return to;
    }

    /**
     * Sets the to.
     *
     * @param to the to
     */
    public void setTo(List<String> to) {
        this.to = to == null ? new ArrayList<>() : new ArrayList<>(to);
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public String getLayer() {
        return layer;
    }

    /**
     * Sets the layer.
     *
     * @param layer the layer
     */
    public void setLayer(String layer) {
        this.layer = layer;
    }
}
