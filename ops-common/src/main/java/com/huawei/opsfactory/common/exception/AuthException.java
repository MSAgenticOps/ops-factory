/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Exception thrown when authentication fails.
 * <p>
 * This exception is thrown by the authentication aspect when Basic authentication
 * credentials are invalid or missing.
 *
 * @since 2026-06-06
 */
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int status;

    /**
     * Constructs a new AuthException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param e the cause
     */
    public AuthException(String message, Exception e) {
        super(message, e);
    }

    /**
     * Constructs a new AuthException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthException(String message) {
        super(message);
    }
}
