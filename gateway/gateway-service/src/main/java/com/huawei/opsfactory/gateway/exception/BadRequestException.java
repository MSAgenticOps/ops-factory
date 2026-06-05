/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.exception;

/**
 * Thrown when the request is invalid or malformed.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class BadRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a bad-request exception with the given message.
     *
     * @param message detail message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Creates a bad-request exception with message and cause.
     *
     * @param message detail message
     * @param cause original cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
