/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Application context initializer that registers mock HttpServletRequest for tests.
 *
 * To use in a test class:
 * <pre>
 * @ContextConfiguration(initializers = MockRequestInitializer.class)
 * </pre>
 *
 * @since 2026-06-08
 */
public class MockRequestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        applicationContext.getBeanFactory().registerSingleton("testHttpServletRequest", mockRequest);
    }
}
