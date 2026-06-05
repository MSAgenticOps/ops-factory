/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.common.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Manual validation utilities for Map-based request bodies.
 * Used by services to enforce the same rules as the frontend form validators.
 *
 * @author x00000000
 * @since 2026-06-04
 */
public final class ValidationUtils {
    private static final Pattern XSS_PATTERN = Pattern.compile("[<>\"'&`/]");
    private static final Pattern ASCII_ONLY_PATTERN = Pattern.compile("^[\\p{ASCII}]*$");

    private ValidationUtils() {
    }

    /**
     * Validates that the given field in the body is non-blank (not null, not empty, not whitespace only).
     * Returns the trimmed string value for reuse.
     *
     * @param body request body map
     * @param field field name to check
     * @param message error message if validation fails
     * @return trimmed string value of the field
     * @throws IllegalArgumentException if the field is missing or blank
     */
    public static String requireNonBlank(Map<String, Object> body, String field, String message) {
        Object value = body.get(field);
        String str = value != null ? value.toString().trim() : "";
        if (str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return str;
    }

    /**
     * Validates that the given string is non-blank.
     *
     * @param value string to check
     * @param fieldName field name for the error message
     * @return trimmed string value
     * @throws IllegalArgumentException if the string is null or blank
     */
    public static String requireNonBlank(String value, String fieldName) {
        String str = value != null ? value.trim() : "";
        if (str.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return str;
    }

    /**
     * Validates that the given string does not exceed the maximum length.
     *
     * @param value string to check
     * @param maxLength maximum allowed length
     * @param fieldName field name for the error message
     * @throws IllegalArgumentException if the string exceeds the limit
     */
    public static void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " exceeds maximum length of " + maxLength);
        }
    }

    /**
     * Validates that the given string does not contain XSS-sensitive characters.
     *
     * @param value string to check
     * @param fieldName field name for the error message
     * @throws IllegalArgumentException if the string contains illegal characters
     */
    public static void requireNoXssChars(String value, String fieldName) {
        if (value != null && !value.isEmpty() && XSS_PATTERN.matcher(value).find()) {
            throw new IllegalArgumentException(
                fieldName + " contains invalid characters (< > \" ' & ` /)");
        }
    }

    /**
     * Validates that the given string contains only ASCII characters.
     *
     * @param value string to check
     * @param fieldName field name for the error message
     * @throws IllegalArgumentException if the string contains non-ASCII characters
     */
    public static void requireAsciiOnly(String value, String fieldName) {
        if (value != null && !value.isEmpty() && !ASCII_ONLY_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " must contain only ASCII characters");
        }
    }

    /**
     * Validates a string field from a request body map.
     * Performs null-safety, non-blank check (if required), XSS check, and max-length check.
     *
     * @param body the request body map
     * @param field the field name to extract
     * @param displayName display name for error messages
     * @param maxLength maximum allowed length (0 = no limit)
     * @param required whether the field is required
     * @return the validated trimmed string, or empty string if not required and missing/null
     * @throws IllegalArgumentException if validation fails
     */
    public static String validateStringField(Map<String, Object> body, String field, String displayName,
                                             int maxLength, boolean required) {
        Object value = body.get(field);
        if (value == null) {
            if (required) {
                throw new IllegalArgumentException(displayName + " is required");
            }
            return "";
        }
        String str = value.toString().trim();
        if (required && str.isEmpty()) {
            throw new IllegalArgumentException(displayName + " is required");
        }
        if (!str.isEmpty()) {
            requireNoXssChars(str, displayName);
            if (maxLength > 0) {
                requireMaxLength(str, maxLength, displayName);
            }
        }
        return str;
    }

    /**
     * Validates that the list of maps has no duplicate values for the given key field.
     *
     * @param list list of maps to check
     * @param keyField the key whose values must be unique
     * @param message error message if duplicates are found
     * @throws IllegalArgumentException if duplicate keys are found
     */
    public static void requireUniqueKeys(List<Map<String, Object>> list, String keyField, String message) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<String> seen = new HashSet<>();
        for (Map<String, Object> item : list) {
            Object keyObj = item.get(keyField);
            if (keyObj == null) {
                continue;
            }
            String key = keyObj.toString();
            if (!seen.add(key)) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
