/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CommonGlobalExceptionHandler}.
 *
 * @since 2026-06-06
 */
class CommonGlobalExceptionHandlerTest {

    private CommonGlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new CommonGlobalExceptionHandler();
    }

    /**
     * Test handling AuthException returns UNAUTHORIZED status.
     */
    @Test
    void testHandleAuthException() {
        String errorMessage = "Invalid credentials";
        AuthException exception = new AuthException(errorMessage);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    /**
     * Test handling AuthException with null message.
     */
    @Test
    void testHandleAuthExceptionWithNullMessage() {
        AuthException exception = new AuthException(null);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Test handling ApiCallException returns BAD_REQUEST status.
     */
    @Test
    void testHandleApiCallException() {
        String errorMessage = "Invalid parameter";
        ApiCallException exception = new ApiCallException(errorMessage);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleApiCallException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody().get("error"));
    }

    /**
     * Test handling ApiCallException with null message.
     */
    @Test
    void testHandleApiCallExceptionWithNullMessage() {
        ApiCallException exception = new ApiCallException(null);

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleApiCallException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
