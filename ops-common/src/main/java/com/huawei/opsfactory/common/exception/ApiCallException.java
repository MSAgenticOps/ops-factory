/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Exception thrown when an API call fails.
 * <p>
 * This exception is used to represent failures in API calls,
 * such as missing configuration or invalid parameters.
 *
 * @since 2026-06-06
 */
public class ApiCallException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int status;

    /**
     * Constructs a new ApiCallException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param throwable the cause
     */
    public ApiCallException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new ApiCallException with the specified detail message.
     *
     * @param message the detail message
     */
    public ApiCallException(String message) {
        super(message);
    }
}
