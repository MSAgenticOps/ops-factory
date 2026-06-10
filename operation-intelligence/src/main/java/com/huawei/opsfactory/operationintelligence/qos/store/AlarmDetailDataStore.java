/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.AlarmDetailData;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

/**
 * Alarm Detail Data Store.
 *
 * @author x00000000
 * @since 2026-05-11
 */
@Component
public class AlarmDetailDataStore extends AbstractQosDataStore<AlarmDetailData> {

    /**
     * Alarm Detail Data Store.
     *
     * @param properties the properties
     */
    public AlarmDetailDataStore(OperationIntelligenceProperties properties) {
        super(properties, "raw", "alarm_detail_data", new TypeReference<>() {},
            properties.getQos().getRawDataRetentionDays());
    }
}
