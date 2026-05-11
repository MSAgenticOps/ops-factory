/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.IndicatorRawData;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class IndicatorRawDataStore {

    private final JsonFileStore<IndicatorRawData> store;

    public IndicatorRawDataStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("raw");
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = properties.getQos().getRawDataRetentionDays() * 86400_000L;
        this.store = new JsonFileStore<>(dir, "indicator_raw_data", new TypeReference<List<IndicatorRawData>>() {},
            true, rotationMs, retentionMs);
        this.store.init();
    }

    public List<IndicatorRawData> loadRange(long startMs, long endMs) {
        return store.loadRange(startMs, endMs);
    }

    public void append(IndicatorRawData item) {
        store.append(item);
    }

    public void appendAll(List<IndicatorRawData> items) {
        store.appendAll(items);
    }

    public void cleanup() {
        store.cleanup();
    }
}
