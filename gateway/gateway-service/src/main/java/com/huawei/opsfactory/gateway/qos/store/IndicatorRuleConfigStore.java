package com.huawei.opsfactory.gateway.qos.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.qos.model.IndicatorRuleConfig;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class IndicatorRuleConfigStore {

    private final JsonFileStore<IndicatorRuleConfig> store;

    public IndicatorRuleConfigStore(GatewayProperties properties) {
        Path dir = properties.getGatewayRootPath().resolve("data").resolve("qos").resolve("config");
        this.store = new JsonFileStore<>(dir, "indicator_rule_config",
                new TypeReference<List<IndicatorRuleConfig>>() {}, false, 0, 0);
        this.store.init();
    }

    public List<IndicatorRuleConfig> loadAll() { return store.loadAll(); }
    public void replaceAll(List<IndicatorRuleConfig> items) { store.replaceAll(items); }
}
