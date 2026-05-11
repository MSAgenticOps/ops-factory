/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.PerformanceIndicatorScope;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class PerformanceIndicatorScopeStore {

    private final JsonFileStore<PerformanceIndicatorScope> store;

    public PerformanceIndicatorScopeStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "performance_indicator_scope",
            new TypeReference<List<PerformanceIndicatorScope>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<PerformanceIndicatorScope> loadAll() {
        return store.loadAll();
    }

    public void replaceAll(List<PerformanceIndicatorScope> items) {
        store.replaceAll(items);
    }
}
