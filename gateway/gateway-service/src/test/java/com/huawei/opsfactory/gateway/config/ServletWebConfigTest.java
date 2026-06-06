/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import jakarta.servlet.Filter;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Focused tests for ServletWebConfig CORS behavior.
 */
public class ServletWebConfigTest {

    /**
     * Verifies allowed origins are echoed and the request continues through the chain.
     *
     * @throws Exception if filtering fails
     */
    @Test
    public void corsFilter_allowsConfiguredOriginForGetRequest() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.setCorsOrigin("https://allowed.example");
        Filter filter = new ServletWebConfig(properties).corsFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gateway/test");
        request.addHeader(HttpHeaders.ORIGIN, "https://allowed.example");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals("https://allowed.example", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals(HttpHeaders.ORIGIN, response.getHeader(HttpHeaders.VARY));
        assertNotNull(chain.getRequest());
    }

    /**
     * Verifies disallowed preflight requests are rejected before reaching the chain.
     *
     * @throws Exception if filtering fails
     */
    @Test
    public void corsFilter_rejectsDisallowedPreflightRequest() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.setCorsOrigin("https://allowed.example");
        Filter filter = new ServletWebConfig(properties).corsFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/gateway/test");
        request.addHeader(HttpHeaders.ORIGIN, "https://denied.example");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(403, response.getStatus());
        assertNull(chain.getRequest());
    }

    /**
     * Verifies wildcard CORS configuration echoes the request origin.
     *
     * @throws Exception if filtering fails
     */
    @Test
    public void corsFilter_allowsAnyOriginWhenWildcardConfigured() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.setCorsOrigin("*");
        Filter filter = new ServletWebConfig(properties).corsFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gateway/test");
        request.addHeader(HttpHeaders.ORIGIN, "https://any.example");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals("https://any.example", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNotNull(chain.getRequest());
    }

    /**
     * Verifies blank CORS configuration falls back to wildcard behavior.
     *
     * @throws Exception if filtering fails
     */
    @Test
    public void corsFilter_allowsAnyOriginWhenConfiguredOriginIsBlank() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.setCorsOrigin(" ");
        Filter filter = new ServletWebConfig(properties).corsFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gateway/test");
        request.addHeader(HttpHeaders.ORIGIN, "https://blank-fallback.example");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals("https://blank-fallback.example", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNotNull(chain.getRequest());
    }

    /**
     * Verifies comma-separated configured origins are matched exactly after trimming.
     *
     * @throws Exception if filtering fails
     */
    @Test
    public void corsFilter_allowsOneOfMultipleConfiguredOrigins() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.setCorsOrigin("https://one.example, https://two.example");
        Filter filter = new ServletWebConfig(properties).corsFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gateway/test");
        request.addHeader(HttpHeaders.ORIGIN, "https://two.example");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals("https://two.example", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNotNull(chain.getRequest());
    }

    /**
     * Verifies requests without Origin do not emit an allow-origin header.
     *
     * @throws Exception if filtering fails
     */
    @Test
    public void corsFilter_noOriginRequestDoesNotSetAllowOrigin() throws Exception {
        GatewayProperties properties = new GatewayProperties();
        properties.setCorsOrigin("*");
        Filter filter = new ServletWebConfig(properties).corsFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/gateway/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertNotNull(chain.getRequest());
    }
}
