/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * Exception carrying a structured session error body for ReplyController endpoints.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class SessionErrorResponseException extends ResponseStatusException {
    private final Map<String, Object> body;

    /**
     * Creates a session error response exception.
     *
     * @param status HTTP status code
     * @param body structured error body
     * @param cause the underlying cause
     */
    public SessionErrorResponseException(HttpStatus status, Map<String, Object> body, Throwable cause) {
        super(status, (String) body.getOrDefault("code", status.getReasonPhrase()), cause);
        this.body = body;
    }

    /**
     * Returns the structured error body.
     *
     * @return the error body map
     */
    public Map<String, Object> getErrorBody() {
        return body;
    }
}
