package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.IndicatorNormalizeData;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class IndicatorNormalizeDataStore {
    private final JsonFileStore<IndicatorNormalizeData> store;

    public IndicatorNormalizeDataStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("normalize");
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = properties.getQos().getNormalizeDataRetentionDays() * 86400_000L;
        this.store = new JsonFileStore<>(dir, "indicator_normalize_data",
                new TypeReference<List<IndicatorNormalizeData>>() {}, true, rotationMs, retentionMs);
        this.store.init();
    }

    public List<IndicatorNormalizeData> loadRange(long startMs, long endMs) { return store.loadRange(startMs, endMs); }
    public void append(IndicatorNormalizeData item) { store.append(item); }
    public void appendAll(List<IndicatorNormalizeData> items) { store.appendAll(items); }
    public void cleanup() { store.cleanup(); }
}
