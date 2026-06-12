/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

/**
 * IP Statistics.
 * Represents statistics for a single IP address.
 *
 * @author call-chain
 * @since 2026-05-14
 */
public class IpStat extends CostStatistics {
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
