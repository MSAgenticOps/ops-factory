/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.callchainsubgraph.model;

import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;

/**
 * Generated call chain entity subgraph payload.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class CallChainSubgraphResult extends CallChainSubgraphBase {
    private GraphSnapshot graph;

    public GraphSnapshot getGraph() {
        return graph;
    }

    public void setGraph(GraphSnapshot graph) {
        this.graph = graph;
    }
}
