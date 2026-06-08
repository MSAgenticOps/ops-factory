/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for common exceptions.
 * <p>
 * This class provides centralized exception handling across the application,
 * catching specific exceptions and returning appropriate HTTP responses.
 *
 * @since 2026-06-06
 */
@ControllerAdvice
public class CommonGlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommonGlobalExceptionHandler.class);

    /**
     * Handles AuthException and returns UNAUTHORIZED status.
     *
     * @param e the authentication exception
     * @return response entity with error message and UNAUTHORIZED status
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException e) {
        logger.warn("Authentication exception: {}", e.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles ApiCallException and returns BAD_REQUEST status.
     *
     * @param e the API call exception
     * @return response entity with error message and BAD_REQUEST status
     */
    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<Map<String, Object>> handleApiCallException(ApiCallException e) {
        logger.warn("API call exception: {}", e.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
