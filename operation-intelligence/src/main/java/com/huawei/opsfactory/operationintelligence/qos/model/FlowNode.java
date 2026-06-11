/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

import java.util.List;

/**
 * Flow Node.
 * Represents a single node in a call flow with its statistics.
 *
 * @author call-chain
 * @since 2026-05-14
 */
public class FlowNode extends FlowNodeBase {
    private List<IpStat> ipList;

    private String clusterTypeId;

    private Long avgCost;

    private Long minCost;

    private Long maxCost;

    private Long successCount;

    private Long callCount;

    private Double successPercent;

    public List<IpStat> getIpList() {
        return ipList;
    }

    public void setIpList(List<IpStat> ipList) {
        this.ipList = ipList;
    }

    public String getClusterTypeId() {
        return clusterTypeId;
    }

    public void setClusterTypeId(String clusterTypeId) {
        this.clusterTypeId = clusterTypeId;
    }

    public Long getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(Long avgCost) {
        this.avgCost = avgCost;
    }

    public Long getMinCost() {
        return minCost;
    }

    public void setMinCost(Long minCost) {
        this.minCost = minCost;
    }

    public Long getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Long maxCost) {
        this.maxCost = maxCost;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getCallCount() {
        return callCount;
    }

    public void setCallCount(Long callCount) {
        this.callCount = callCount;
    }

    public Double getSuccessPercent() {
        return successPercent;
    }

    public void setSuccessPercent(Double successPercent) {
        this.successPercent = successPercent;
    }
}
