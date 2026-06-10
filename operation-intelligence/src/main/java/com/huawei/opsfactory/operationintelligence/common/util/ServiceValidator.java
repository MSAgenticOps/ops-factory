/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Common service-layer validation helpers.
 *
 * @author x00000000
 * @since 2026-05-20
 */
public final class ServiceValidator {

    private ServiceValidator() {}

    /**
     * Requires that a text value is non-null and non-blank.
     *
     * @param value the text value
     * @param fieldName the field name for error messaging
     * @throws ResponseStatusException with BAD_REQUEST if the value is null or blank
     */
    public static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is required");
        }
    }
}
