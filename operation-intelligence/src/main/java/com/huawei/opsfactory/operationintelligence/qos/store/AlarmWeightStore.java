/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.AlarmWeight;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class AlarmWeightStore {

    private final JsonFileStore<AlarmWeight> store;

    public AlarmWeightStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "alarm_weight", new TypeReference<List<AlarmWeight>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<AlarmWeight> loadAll() {
        return store.loadAll();
    }

    public void replaceAll(List<AlarmWeight> items) {
        store.replaceAll(items);
    }
}
