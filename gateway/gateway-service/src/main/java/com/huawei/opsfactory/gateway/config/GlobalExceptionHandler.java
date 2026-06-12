/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import com.huawei.opsfactory.common.exception.ApiCallException;
import com.huawei.opsfactory.common.exception.AuthException;
import com.huawei.opsfactory.gateway.controller.SessionErrorResponseException;
import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler that catches and normalizes errors from all controllers.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final GatewayProperties properties;

    /**
     * Creates a global exception handler with default gateway properties.
     */
    public GlobalExceptionHandler() {
        this(new GatewayProperties());
    }

    /**
     * Creates a global exception handler with the given gateway properties.
     */
    public GlobalExceptionHandler(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Handles request input errors such as invalid JSON body.
     *
     * @param ex ex
     * @return the handles request input errors such as invalid JSON body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInputException(HttpMessageNotReadableException ex) {
        log.warn("Request input error: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage() != null ? ex.getMessage() : "Invalid request body");
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles session error response exceptions with structured error body.
     *
     * @param ex session error response exception
     * @return response entity with the structured error body
     */
    public ResponseEntity<Map<String, Object>> handleSessionErrorResponse(SessionErrorResponseException ex) {
        return handleSessionErrorResponse(ex, null, null);
    }

    @ExceptionHandler(SessionErrorResponseException.class)
    public ResponseEntity<Map<String, Object>> handleSessionErrorResponse(SessionErrorResponseException ex,
        HttpServletRequest request, HttpServletResponse response) {
        log.warn("Session error: status={} code={}", ex.getStatusCode(), ex.getErrorBody().get("code"));
        if (isSseRequest(request)) {
            writeSseErrorResponse(response, HttpStatus.valueOf(ex.getStatusCode().value()), ex.getErrorBody());
            return null;
        }
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getErrorBody());
    }

    /**
     * Handles response status exceptions and returns a normalized error body.
     *
     * @param ex ex
     * @return the handles response status exceptions and returns a normalized error body
     */
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return handleResponseStatus(ex, null, null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex,
        HttpServletRequest request, HttpServletResponse response) {
        log.warn("Request rejected with status={} reason={}", ex.getStatusCode(), ex.getReason());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getReason() != null ? ex.getReason() : "Request rejected");
        if (isSseRequest(request)) {
            body.put("type", "Error");
            body.put("layer", "gateway");
            body.put("severity", "error");
            writeSseErrorResponse(response, HttpStatus.valueOf(ex.getStatusCode().value()), body);
            return null;
        }
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    /**
     * Handles not-found exceptions and returns 404 with a clear error message.
     *
     * @param ex not found exception
     * @param request HTTP request
     * @param response HTTP response
     * @return 404 response with error details
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex,
        HttpServletRequest request, HttpServletResponse response) {
        log.warn("Not found: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        if (isSseRequest(request)) {
            body.put("type", "Error");
            body.put("layer", "gateway");
            body.put("severity", "error");
            writeSseErrorResponse(response, HttpStatus.NOT_FOUND, body);
            return null;
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles illegal-argument exceptions and returns 400 with a clear error message.
     *
     * @param ex illegal argument exception
     * @param request HTTP request
     * @param response HTTP response
     * @return 400 response with error details
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex,
        HttpServletRequest request, HttpServletResponse response) {
        log.warn("Bad request: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        if (isSseRequest(request)) {
            body.put("type", "Error");
            body.put("layer", "gateway");
            body.put("severity", "error");
            writeSseErrorResponse(response, HttpStatus.BAD_REQUEST, body);
            return null;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles bad-request exceptions and returns 400 with a clear error message.
     *
     * @param ex bad request exception
     * @param request HTTP request
     * @param response HTTP response
     * @return 400 response with error details
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex,
        HttpServletRequest request, HttpServletResponse response) {
        log.warn("Bad request: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        if (isSseRequest(request)) {
            body.put("type", "Error");
            body.put("layer", "gateway");
            body.put("severity", "error");
            writeSseErrorResponse(response, HttpStatus.BAD_REQUEST, body);
            return null;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles conflict exceptions and returns 409 with a clear error message.
     *
     * @param ex conflict exception
     * @param request HTTP request
     * @param response HTTP response
     * @return 409 response with error details
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex,
        HttpServletRequest request, HttpServletResponse response) {
        log.warn("Conflict: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        if (isSseRequest(request)) {
            body.put("type", "Error");
            body.put("layer", "gateway");
            body.put("severity", "error");
            writeSseErrorResponse(response, HttpStatus.CONFLICT, body);
            return null;
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles NoResourceFoundException and returns 404 with a clear error message.
     *
     * @param ex ex
     * @return 404 response with error details
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        String resourceLocation = ex.getResourcePath();
        log.warn("Resource not found: {}", resourceLocation);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", "Resource not found: " + resourceLocation);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles authentication exceptions and returns 401 Unauthorized.
     *
     * @param ex authentication exception
     * @return 401 response with error details
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Handles API call exceptions and returns 400 Bad Request.
     *
     * @param ex API call exception
     * @return 400 response with error details
     */
    @ExceptionHandler(ApiCallException.class)
    public ResponseEntity<Map<String, Object>> handleApiCallException(ApiCallException ex) {
        log.warn("API call error: {}", ex.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Last-resort catch-all for any unhandled exception.
     * Returns 500 with a generic message and never leaks internal details.
     *
     * @param ex last-resort catch-all for any unhandled exception
     * @return the last-resort catch-all for any unhandled exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() > maxLength ? value.substring(0, maxLength) + "..." : value;
    }

    private boolean isSseRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        return accept != null && accept.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
    }

    private void writeSseErrorResponse(HttpServletResponse response, HttpStatus status, Map<String, Object> body) {
        if (response == null) {
            return;
        }
        response.setStatus(status.value());
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write("event: error\ndata: " + toJson(body) + "\n\n");
            response.getWriter().flush();
        } catch (IOException ex) {
            log.warn("Failed to write SSE error response: {}", ex.getMessage());
        }
    }

    private String toJson(Map<String, Object> body) {
        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        } catch (JsonProcessingException ex) {
            log.warn("Failed to serialize SSE error body: {}", ex.getMessage());
            return "{\"type\":\"Error\",\"layer\":\"gateway\",\"severity\":\"error\",\"error\":\"Internal server error\"}";
        }
    }
}
