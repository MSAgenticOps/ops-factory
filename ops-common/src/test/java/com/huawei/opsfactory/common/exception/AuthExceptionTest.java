/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AuthException}.
 *
 * @since 2026-06-06
 */
class AuthExceptionTest {

    /**
     * Test constructor with message only.
     */
    @Test
    void testConstructorWithMessage() {
        String message = "Authentication failed";
        AuthException exception = new AuthException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with message and cause.
     */
    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Authentication failed";
        Exception cause = new RuntimeException("Invalid credentials");
        AuthException exception = new AuthException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /**
     * Test status getter and setter.
     */
    @Test
    void testStatusGetterSetter() {
        AuthException exception = new AuthException("Test message");
        int expectedStatus = 401;

        exception.setStatus(expectedStatus);
        assertEquals(expectedStatus, exception.getStatus());
    }

    /**
     * Test exception is runtime exception.
     */
    @Test
    void testIsRuntimeException() {
        AuthException exception = new AuthException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
