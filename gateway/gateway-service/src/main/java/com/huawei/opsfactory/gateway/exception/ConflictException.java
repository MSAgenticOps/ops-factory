/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.exception;

/**
 * Thrown when the request conflicts with the current state.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class ConflictException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a conflict exception with the given message.
     *
     * @param message detail message
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Creates a conflict exception with the given message and cause.
     *
     * @param message detail message
     * @param cause   the cause
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
