package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.ProductConfigRule;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class ProductConfigRuleStore {

    private final JsonFileStore<ProductConfigRule> store;

    public ProductConfigRuleStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "product_config_rule",
                new TypeReference<List<ProductConfigRule>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<ProductConfigRule> loadAll() { return store.loadAll(); }
    public void replaceAll(List<ProductConfigRule> items) { store.replaceAll(items); }
}
