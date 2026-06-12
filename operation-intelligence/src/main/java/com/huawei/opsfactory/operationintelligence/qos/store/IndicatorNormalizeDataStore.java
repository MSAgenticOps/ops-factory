/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.IndicatorNormalizeData;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

/**
 * Indicator Normalize Data Store.
 *
 * @author x00000000
 * @since 2026-05-11
 */
@Component
public class IndicatorNormalizeDataStore extends AbstractQosDataStore<IndicatorNormalizeData> {

    /**
     * Indicator Normalize Data Store.
     *
     * @param properties the properties
     */
    public IndicatorNormalizeDataStore(OperationIntelligenceProperties properties) {
        super(properties, "normalize", "indicator_normalize_data", new TypeReference<>() {},
            properties.getQos().getNormalizeDataRetentionDays());
    }
}
