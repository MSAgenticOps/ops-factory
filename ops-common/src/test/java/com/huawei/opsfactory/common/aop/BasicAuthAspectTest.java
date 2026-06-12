/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.aop;

import com.huawei.opsfactory.common.config.CommonProperties;
import com.huawei.opsfactory.common.exception.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.servicecomb.swagger.invocation.context.InvocationContext;
import org.apache.servicecomb.swagger.invocation.context.ContextUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link BasicAuthAspect}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BasicAuthAspectTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private CommonProperties commonProperties;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    private MockedStatic<ContextUtils> contextUtilsMock;

    private BasicAuthAspect aspect;

    /**
     * Sets up test fixtures before each test.
     *
     * @throws Throwable if an error occurs during test execution
     */
    @BeforeEach
    void setUp() throws Throwable {
        when(commonProperties.getUserName()).thenReturn("testUser");
        when(commonProperties.getPassword()).thenReturn("testPass");
        aspect = new BasicAuthAspect(request, commonProperties);
        contextUtilsMock = mockStatic(ContextUtils.class);

        // Configure proceedingJoinPoint mock
        Signature signature = mock(Signature.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
    }

    /**
     * Tears down test fixtures after each test.
     */
    @AfterEach
    void tearDown() {
        contextUtilsMock.close();
    }

    /**
     * Test successful authentication via request header.
     *
     * @throws Throwable if an error occurs during test execution
     */
    @Test
    void testBasicAuthSuccessViaHeader() throws Throwable {
        String credentials = "testUser:testPass";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + base64Credentials);
        when(proceedingJoinPoint.proceed()).thenReturn("Success");

        Object result = aspect.basicAuth(proceedingJoinPoint);

        assertEquals("Success", result);
    }

    /**
     * Test successful authentication via invocation context.
     *
     * @throws Throwable if an error occurs during test execution
     */
    @Test
    void testBasicAuthSuccessViaContext() throws Throwable {
        InvocationContext invocationContext = mock(InvocationContext.class);
        String credentials = "testUser:testPass";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));

        when(request.getHeader("Authorization")).thenReturn(null);
        contextUtilsMock.when(ContextUtils::getInvocationContext).thenReturn(invocationContext);
        when(invocationContext.getContext("Authorization")).thenReturn("Basic " + base64Credentials);
        when(proceedingJoinPoint.proceed()).thenReturn("Success");

        Object result = aspect.basicAuth(proceedingJoinPoint);

        assertEquals("Success", result);
    }

    /**
     * Test authentication failure with invalid credentials.
     * AuthException is thrown when credentials are invalid.
     */
    @Test
    void testBasicAuthFailureInvalidCredentials() {
        String credentials = "wrongUser:wrongPass";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + base64Credentials);

        AuthException exception = assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));

        assertTrue(exception.getMessage().contains("Authentication failed"));
    }

    /**
     * Test authentication failure with empty configuration.
     * AuthException is thrown when configuration is missing.
     */
    @Test
    void testBasicAuthFailureEmptyConfiguration() {
        when(commonProperties.getUserName()).thenReturn("");
        when(commonProperties.getPassword()).thenReturn("testPass");

        String credentials = "testUser:testPass";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + base64Credentials);

        AuthException exception = assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));

        assertTrue(exception.getMessage().contains("Machine authentication not configured"));
    }

    /**
     * Test authentication failure with malformed Base64.
     * AuthException is thrown when Base64 decoding fails.
     */
    @Test
    void testBasicAuthFailureMalformedBase64() {
        when(request.getHeader("Authorization")).thenReturn("Basic InvalidBase64!@#$");

        assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));
    }

    /**
     * Test authentication failure with malformed credentials format.
     * AuthException is thrown when credentials format is invalid.
     */
    @Test
    void testBasicAuthFailureMalformedCredentials() {
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            "NoColonHere".getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + base64Credentials);

        assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));
    }

    /**
     * Test authentication failure with missing Authorization header.
     * AuthException is thrown when Authorization header is missing.
     */
    @Test
    void testBasicAuthFailureMissingAuthorization() {
        when(request.getHeader("Authorization")).thenReturn(null);
        contextUtilsMock.when(ContextUtils::getInvocationContext).thenReturn(null);

        assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));
    }

    /**
     * Test authentication failure with empty Authorization header.
     * AuthException is thrown when Authorization header is empty.
     */
    @Test
    void testBasicAuthFailureEmptyAuthorization() {
        when(request.getHeader("Authorization")).thenReturn("");
        contextUtilsMock.when(ContextUtils::getInvocationContext).thenReturn(null);

        assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));
    }

    /**
     * Test authentication with wrong username.
     * AuthException is thrown when username is incorrect.
     */
    @Test
    void testBasicAuthFailureWrongUsername() {
        String credentials = "wrongUser:testPass";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + base64Credentials);

        assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));
    }

    /**
     * Test authentication with wrong password.
     * AuthException is thrown when password is incorrect.
     */
    @Test
    void testBasicAuthFailureWrongPassword() {
        String credentials = "testUser:wrongPass";
        String base64Credentials = java.util.Base64.getEncoder().encodeToString(
            credentials.getBytes(StandardCharsets.UTF_8));
        when(request.getHeader("Authorization")).thenReturn("Basic " + base64Credentials);

        assertThrows(AuthException.class, () -> aspect.basicAuth(proceedingJoinPoint));
    }
}
