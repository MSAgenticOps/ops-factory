/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.common.util;

import java.util.regex.Pattern;

/**
 * Validates path segments to prevent path traversal and unsafe characters.
 *
 * @author x00000000
 * @since 2026-05-20
 */
public final class PathValidator {
    /**
     * Pattern matching a single safe path segment: letters, digits, underscores, dots, hyphens.
     */
    public static final Pattern SAFE_SEGMENT = Pattern.compile("[A-Za-z0-9_.-]+");

    private PathValidator() {
    }

    /**
     * Validates that a value is a safe path segment, throwing if it is not.
     *
     * @param value the value to validate
     * @param fieldName the field name used in the error message
     * @return the validated value
     * @throws IllegalArgumentException if the value is null or contains unsupported characters
     */
    public static String requireSafeSegment(String value, String fieldName) {
        if (value == null || !SAFE_SEGMENT.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " contains unsupported path characters");
        }
        return value;
    }
}
