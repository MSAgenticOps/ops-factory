/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.util;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility helpers for extracting values from Jackson {@link JsonNode} instances.
 *
 * @author x00000000
 * @since 2026-05-14
 */
public final class JsonNodeHelper {

    private static final Logger log = LoggerFactory.getLogger(JsonNodeHelper.class);

    private JsonNodeHelper() {
    }

    /**
     * Extract text value from JSON node by field name.
     *
     * @param node the JSON node
     * @param field the field name
     * @return the text value, or null if not present or null
     */
    public static String textVal(JsonNode node, String field) {
        if (node == null || !node.has(field)) {
            return null;
        }
        JsonNode fieldNode = node.get(field);
        if (fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText();
    }

    /**
     * Parse cost string to Long.
     *
     * @param cost the cost string (e.g., "10ms", "100")
     * @return the parsed cost or null
     */
    public static Long parseCost(String cost) {
        if (cost == null || cost.isEmpty()) {
            return null;
        }
        try {
            String numeric = cost.replaceAll("[^0-9]", "");
            if (numeric.isEmpty()) {
                return null;
            }
            return Long.parseLong(numeric);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse cost: {}", cost);
            return null;
        }
    }

    /**
     * Return null if value is "null" string, otherwise return the value.
     *
     * @param value the value to check
     * @return null if "null", otherwise the value
     */
    public static String safeValue(String value) {
        return (value != null && !value.equals("null")) ? value : null;
    }
}
