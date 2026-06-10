/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared utility methods for REST controllers.
 *
 * @author x00000000
 * @since 2026-06-08
 */
public final class ControllerHelper {

    private ControllerHelper() {
    }

    /**
     * Builds a standard success response map.
     *
     * @param key the result key
     * @param value the result value
     * @return the response map
     */
    public static Map<String, Object> ok(String key, Object value) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put(key, value);
        response.put("error", null);
        return response;
    }

    /**
     * Converts an object to an int value, throwing BAD_REQUEST on failure.
     *
     * @param value the value to convert
     * @return the int value
     */
    public static int intValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid integer value: " + value);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid integer value: " + value);
    }

    /**
     * Converts an object to a string, returning null for null values.
     *
     * @param value the value to convert
     * @return the string value, or null
     */
    public static String stringValue(Object value) {
        return value == null ? null : value.toString();
    }
}
