package com.huawei.opsfactory.operationintelligence.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.DnRegistry;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class DnRegistryStore {

    private final JsonFileStore<DnRegistry> store;

    public DnRegistryStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "dn_registry",
                new TypeReference<List<DnRegistry>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<DnRegistry> loadAll() { return store.loadAll(); }
    public void replaceAll(List<DnRegistry> items) { store.replaceAll(items); }
}
