package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.AlarmWeight;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class AlarmWeightStore {

    private final JsonFileStore<AlarmWeight> store;

    public AlarmWeightStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "alarm_weight",
                new TypeReference<List<AlarmWeight>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<AlarmWeight> loadAll() { return store.loadAll(); }
    public void replaceAll(List<AlarmWeight> items) { store.replaceAll(items); }
}
