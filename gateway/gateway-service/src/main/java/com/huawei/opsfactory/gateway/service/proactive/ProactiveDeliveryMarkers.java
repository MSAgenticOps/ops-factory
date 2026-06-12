/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stateless read/modify/write helpers for the per-user proactive delivery-marker file
 * ({@code proactive-followups/delivery.json}): a map of {@code scheduleId -> {deliver, updatedAt}}.
 *
 * <p>The marker records which scheduled tasks should have their report delivered to IM. It lives on the
 * Gateway side only (never sent to goosed) and is keyed by schedule id. Both the schedule-create endpoint
 * (user toggle) and schedule seeding (built-in tasks) write through here, so the file format has a single
 * owner. Operating on an explicit {@link Path} keeps it reusable from {@code AgentConfigService} seeding
 * without a Spring dependency, and trivially testable against a temp directory.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public final class ProactiveDeliveryMarkers {
    /** Per-user proactive subdirectory name (shared with follow-up records). */
    public static final String DIR = "proactive-followups";

    /** Delivery-marker file name within {@link #DIR}. */
    public static final String DELIVERY_FILE = "delivery.json";

    /** Delivery channel value meaning "push the report to the user's bound IM channels". */
    public static final String DELIVER_IM = "im";

    private static final String FIELD_DELIVER = "deliver";

    private static final String FIELD_UPDATED_AT = "updatedAt";

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private ProactiveDeliveryMarkers() {
    }

    /**
     * Returns the delivery channel configured for a schedule, or {@code null} when no marker is set.
     *
     * @param deliveryFile path to the per-user {@code delivery.json}
     * @param scheduleId schedule identifier
     * @return the configured delivery channel (e.g. {@link #DELIVER_IM}), or {@code null}
     * @throws IOException if the file exists but cannot be read or parsed
     */
    public static String getDeliver(Path deliveryFile, String scheduleId) throws IOException {
        Map<String, Object> entry = read(deliveryFile).get(scheduleId);
        return entry == null ? null : asString(entry.get(FIELD_DELIVER));
    }

    /**
     * Sets (or overwrites) the delivery channel for a schedule and persists the file.
     *
     * @param deliveryFile path to the per-user {@code delivery.json}
     * @param scheduleId schedule identifier
     * @param deliver delivery channel value (e.g. {@link #DELIVER_IM})
     * @throws IOException if the file cannot be written
     */
    public static void setDeliver(Path deliveryFile, String scheduleId, String deliver) throws IOException {
        synchronized (ProactiveFiles.lockFor(deliveryFile)) {
            Map<String, Map<String, Object>> markers = read(deliveryFile);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put(FIELD_DELIVER, deliver);
            entry.put(FIELD_UPDATED_AT, Instant.now().toString());
            markers.put(scheduleId, entry);
            write(deliveryFile, markers);
        }
    }

    /**
     * Removes any delivery marker for a schedule. No-op when none exists.
     *
     * @param deliveryFile path to the per-user {@code delivery.json}
     * @param scheduleId schedule identifier
     * @throws IOException if the file cannot be rewritten
     */
    public static void remove(Path deliveryFile, String scheduleId) throws IOException {
        synchronized (ProactiveFiles.lockFor(deliveryFile)) {
            Map<String, Map<String, Object>> markers = read(deliveryFile);
            if (markers.remove(scheduleId) != null) {
                write(deliveryFile, markers);
            }
        }
    }

    private static Map<String, Map<String, Object>> read(Path deliveryFile) throws IOException {
        if (deliveryFile == null || Files.notExists(deliveryFile)) {
            return new LinkedHashMap<>();
        }
        String content = Files.readString(deliveryFile, StandardCharsets.UTF_8);
        if (content.isBlank()) {
            return new LinkedHashMap<>();
        }
        return MAPPER.readValue(content, new TypeReference<LinkedHashMap<String, Map<String, Object>>>() { });
    }

    private static void write(Path deliveryFile, Map<String, Map<String, Object>> markers) throws IOException {
        ProactiveFiles.atomicWrite(deliveryFile, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(markers));
    }

    private static String asString(Object value) {
        return value instanceof String s ? s : null;
    }
}
