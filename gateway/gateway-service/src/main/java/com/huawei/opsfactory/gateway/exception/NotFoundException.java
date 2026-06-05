/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.exception;

/**
 * Thrown when a requested entity is not found.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a not-found exception with the given message.
     *
     * @param message detail message
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a not-found exception with message and cause.
     *
     * @param message detail message
     * @param cause original cause
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
