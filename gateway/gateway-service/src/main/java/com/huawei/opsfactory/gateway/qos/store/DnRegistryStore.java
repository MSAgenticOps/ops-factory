package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.DnRegistry;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class DnRegistryStore {
    private final JsonFileStore<DnRegistry> store;

    public DnRegistryStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "dn_registry",
                new TypeReference<List<DnRegistry>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<DnRegistry> loadAll() { return store.loadAll(); }
    public void replaceAll(List<DnRegistry> items) { store.replaceAll(items); }
}
