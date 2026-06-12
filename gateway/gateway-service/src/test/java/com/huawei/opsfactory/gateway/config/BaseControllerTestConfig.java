/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import jakarta.servlet.http.HttpServletRequest;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Base test configuration for controller tests that provides mock HttpServletRequest.
 *
 * @since 2026-06-08
 */
@TestConfiguration
public class BaseControllerTestConfig {

    /**
     * Creates a mock HttpServletRequest bean for testing.
     *
     * @return a mocked HttpServletRequest instance
     */
    @Bean
    @Primary
    public HttpServletRequest testHttpServletRequest() {
        return Mockito.mock(HttpServletRequest.class);
    }
}
