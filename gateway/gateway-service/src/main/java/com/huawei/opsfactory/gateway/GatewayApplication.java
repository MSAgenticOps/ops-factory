/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Entry point for the OpsFactory Gateway Spring Boot application.
 *
 * @author x00000000
 * @since 2025-05-09
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.huawei.opsfactory.gateway",
    "com.huawei.opsfactory.common"
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class GatewayApplication {

    /**
     * Starts the OpsFactory Gateway application.
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
