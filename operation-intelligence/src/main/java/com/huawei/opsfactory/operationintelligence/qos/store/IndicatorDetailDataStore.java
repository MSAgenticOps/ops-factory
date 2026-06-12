/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.IndicatorDetailData;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

/**
 * Indicator Detail Data Store.
 *
 * @author x00000000
 * @since 2026-05-11
 */
@Component
public class IndicatorDetailDataStore extends AbstractQosDataStore<IndicatorDetailData> {

    /**
     * Indicator Detail Data Store.
     *
     * @param properties the properties
     */
    public IndicatorDetailDataStore(OperationIntelligenceProperties properties) {
        super(properties, "detail", "indicator_detail_data", new TypeReference<>() {},
            properties.getQos().getDetailDataRetentionDays());
    }
}
