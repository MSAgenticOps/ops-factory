
package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.ProductConfigRule;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class ProductConfigRuleStore {

    private final JsonFileStore<ProductConfigRule> store;

    public ProductConfigRuleStore(OperationIntelligenceProperties properties) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "product_config_rule", new TypeReference<List<ProductConfigRule>>() {},
            false, 0, 0);
        this.store.init();
    }

    public List<ProductConfigRule> loadAll() {
        return store.loadAll();
    }

    public void replaceAll(List<ProductConfigRule> items) {
        store.replaceAll(items);
    }
}
