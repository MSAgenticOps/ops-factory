/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.model;

/**
 * Knowledge graph relation.
 *
 * @author x00000000
 * @since 2026-05-20
 */
public class GraphRelation extends GraphElement {
    private String type;

    private String from;

    private String to;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "GraphRelation{id='" + getId() + "', type='" + type + "', from='" + from + "', to='" + to + "'}";
    }
}
