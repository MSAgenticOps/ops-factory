/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

import java.util.List;

/**
 * Call Flow.
 * Represents a single call flow with its nodes and statistics.
 *
 * @author call-chain
 * @since 2026-05-14
 */
public class CallFlow extends CostStatistics {
    private String flowId;

    private Double callRatio;

    private List<FlowNode> nodes;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public Double getCallRatio() {
        return callRatio;
    }

    public void setCallRatio(Double callRatio) {
        this.callRatio = callRatio;
    }

    public List<FlowNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<FlowNode> nodes) {
        this.nodes = nodes;
    }
}
