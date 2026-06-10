/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Common cost/performance statistics fields.
 *
 * @author x00000000
 * @since 2026-06-08
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostStatistics {
    private Long avgCost;

    private Long minCost;

    private Long maxCost;

    private Long successCount;

    private Long callCount;

    private Double successPercent;

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
