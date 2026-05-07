package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.PerformanceIndicatorScope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class PerformanceIndicatorScopeStore {

    private final JsonFileStore<PerformanceIndicatorScope> store;

    public PerformanceIndicatorScopeStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "performance_indicator_scope",
                new TypeReference<List<PerformanceIndicatorScope>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<PerformanceIndicatorScope> loadAll() { return store.loadAll(); }
    public void replaceAll(List<PerformanceIndicatorScope> items) { store.replaceAll(items); }
}
