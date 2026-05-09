package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.AlarmDetailData;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class AlarmDetailDataStore {
    private final JsonFileStore<AlarmDetailData> store;

    public AlarmDetailDataStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("raw");
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = properties.getQos().getRawDataRetentionDays() * 86400_000L;
        this.store = new JsonFileStore<>(dir, "alarm_detail_data",
                new TypeReference<List<AlarmDetailData>>() {}, true, rotationMs, retentionMs);
        this.store.init();
    }

    public List<AlarmDetailData> loadRange(long startMs, long endMs) { return store.loadRange(startMs, endMs); }
    public void append(AlarmDetailData item) { store.append(item); }
    public void appendAll(List<AlarmDetailData> items) { store.appendAll(items); }
    public void cleanup() { store.cleanup(); }
}
