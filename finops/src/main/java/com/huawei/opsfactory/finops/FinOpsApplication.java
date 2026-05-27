package com.huawei.opsfactory.finops;

import com.huawei.opsfactory.finops.config.FinOpsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(FinOpsProperties.class)
public class FinOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinOpsApplication.class, args);
    }
}
