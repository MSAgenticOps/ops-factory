package com.huawei.opsfactory.operationintelligence.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.IndicatorDetailData;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class IndicatorDetailDataStore {

    private final JsonFileStore<IndicatorDetailData> store;

    public IndicatorDetailDataStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("detail");
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = properties.getQos().getDetailDataRetentionDays() * 86400_000L;
        this.store = new JsonFileStore<>(dir, "indicator_detail_data",
                new TypeReference<List<IndicatorDetailData>>() {}, true, rotationMs, retentionMs);
        this.store.init();
    }

    public List<IndicatorDetailData> loadRange(long startMs, long endMs) { return store.loadRange(startMs, endMs); }
    public void append(IndicatorDetailData item) { store.append(item); }
    public void appendAll(List<IndicatorDetailData> items) { store.appendAll(items); }
    public void cleanup() { store.cleanup(); }
}
