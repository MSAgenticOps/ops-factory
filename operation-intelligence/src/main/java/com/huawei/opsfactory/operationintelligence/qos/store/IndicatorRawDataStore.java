/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.IndicatorRawData;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

/**
 * Indicator Raw Data Store.
 *
 * @author x00000000
 * @since 2026-05-11
 */
@Component
public class IndicatorRawDataStore extends AbstractQosDataStore<IndicatorRawData> {

    /**
     * Indicator Raw Data Store.
     *
     * @param properties the properties
     */
    public IndicatorRawDataStore(OperationIntelligenceProperties properties) {
        super(properties, "raw", "indicator_raw_data", new TypeReference<>() {},
            properties.getQos().getRawDataRetentionDays());
    }
}
