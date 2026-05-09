package com.huawei.opsfactory.operationintelligence;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(OperationIntelligenceProperties.class)
@EnableScheduling
public class OperationIntelligenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationIntelligenceApplication.class, args);
    }
}
