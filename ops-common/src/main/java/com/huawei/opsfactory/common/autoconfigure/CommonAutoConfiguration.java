/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Auto-configuration for ops-common module.
 * <p>
 * This configuration automatically registers common components including AOP aspects
 * and exception handlers for all services that depend on opsfactory-common.
 *
 * @since 2026-06-08
 */
@Configuration
@ComponentScan("com.huawei.opsfactory.common")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommonAutoConfiguration {
}
