/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.IndicatorNormalizeData;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class IndicatorNormalizeDataStore {

    private final JsonFileStore<IndicatorNormalizeData> store;

    public IndicatorNormalizeDataStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("normalize");
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = properties.getQos().getNormalizeDataRetentionDays() * 86400_000L;
        this.store = new JsonFileStore<>(dir, "indicator_normalize_data",
            new TypeReference<List<IndicatorNormalizeData>>() {}, true, rotationMs, retentionMs);
        this.store.init();
    }

    public List<IndicatorNormalizeData> loadRange(long startMs, long endMs) {
        return store.loadRange(startMs, endMs);
    }

    public void append(IndicatorNormalizeData item) {
        store.append(item);
    }

    public void appendAll(List<IndicatorNormalizeData> items) {
        store.appendAll(items);
    }

    public void cleanup() {
        store.cleanup();
    }
}
