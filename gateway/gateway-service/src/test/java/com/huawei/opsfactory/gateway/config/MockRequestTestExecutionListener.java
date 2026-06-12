/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import jakarta.servlet.http.HttpServletRequest;

import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Test execution listener that registers mock HttpServletRequest for tests.
 *
 * @since 2026-06-08
 */
public class MockRequestTestExecutionListener implements TestExecutionListener {

    /**
     * Prepares the test instance by registering a mock HttpServletRequest if not already present.
     *
     * @param testContext the test context containing the application context
     * @throws Exception if preparation fails
     */
    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        Object context = testContext.getApplicationContext();
        if (context instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
            ConfigurableListableBeanFactory beanFactory = ctx.getBeanFactory();
            if (!beanFactory.containsBean("testHttpServletRequest")) {
                HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
                beanFactory.registerSingleton("testHttpServletRequest", mockRequest);
            }
        }
    }
}
