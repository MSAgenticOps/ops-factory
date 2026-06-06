/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ApiCallException}.
 *
 * @since 2026-06-06
 */
class ApiCallExceptionTest {

    /**
     * Test constructor with message only.
     */
    @Test
    void testConstructorWithMessage() {
        String message = "API call failed";
        ApiCallException exception = new ApiCallException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test constructor with message and cause.
     */
    @Test
    void testConstructorWithMessageAndCause() {
        String message = "API call failed";
        Throwable cause = new RuntimeException("Root cause");
        ApiCallException exception = new ApiCallException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /**
     * Test status getter and setter.
     */
    @Test
    void testStatusGetterSetter() {
        ApiCallException exception = new ApiCallException("Test message");
        int expectedStatus = 400;

        exception.setStatus(expectedStatus);
        assertEquals(expectedStatus, exception.getStatus());
    }

    /**
     * Test exception is runtime exception.
     */
    @Test
    void testIsRuntimeException() {
        ApiCallException exception = new ApiCallException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
