package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.IndicatorRawData;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class IndicatorRawDataStore {
    private final JsonFileStore<IndicatorRawData> store;

    public IndicatorRawDataStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("raw");
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = properties.getQos().getRawDataRetentionDays() * 86400_000L;
        this.store = new JsonFileStore<>(dir, "indicator_raw_data",
                new TypeReference<List<IndicatorRawData>>() {}, true, rotationMs, retentionMs);
        this.store.init();
    }

    public List<IndicatorRawData> loadRange(long startMs, long endMs) { return store.loadRange(startMs, endMs); }
    public void append(IndicatorRawData item) { store.append(item); }
    public void appendAll(List<IndicatorRawData> items) { store.appendAll(items); }
    public void cleanup() { store.cleanup(); }
}
