/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Translates a target (B) agent's goosed session events into {@code a2a_progress} display frames and accumulates the
 * final assistant text for the {@code a2a_result} terminal frame.
 *
 * <p>This is a stateful per-run helper (step counter + result accumulator + terminal/error flags) with no I/O, so the
 * mapping rules are unit-testable in isolation. Rules:
 * <ul>
 *   <li>{@code thinking} / {@code redactedThinking} content is dropped (never surfaced).</li>
 *   <li>A {@code toolRequest} becomes a {@code tool_call} progress frame whose label is a friendly action name.</li>
 *   <li>User-visible assistant {@code text} becomes a {@code text} progress frame and is accumulated as the result.</li>
 *   <li>{@code Finish} / {@code Error} are terminal; everything else (Ping, ActiveRequests, Notification, ...) is
 *       ignored.</li>
 * </ul>
 *
 * @author x00000000
 * @since 2026-06-05
 */
public class A2AProgressTranslator {
    /** Frame discriminator for an in-progress display update. */
    public static final String TYPE_PROGRESS = "a2a_progress";

    /** Frame discriminator for the terminal result. */
    public static final String TYPE_RESULT = "a2a_result";

    private static final int LABEL_MAX = 80;

    private final String targetAgent;

    private final String subSessionId;

    private final StringBuilder result = new StringBuilder();

    private int step;

    private int toolCalls;

    private boolean sawTerminal;

    private String errorMessage;

    /**
     * Creates a translator for a single delegated run.
     *
     * @param targetAgent the delegated-to agent id (frontend colouring)
     * @param subSessionId the target sub-session id (deep-link)
     */
    public A2AProgressTranslator(String targetAgent, String subSessionId) {
        this.targetAgent = targetAgent;
        this.subSessionId = subSessionId;
    }

    /**
     * Whether the event ends the run. Records terminal state (and any error message) as a side effect.
     *
     * @param event a parsed goosed session event
     * @return {@code true} for {@code Finish} / {@code Error}
     */
    public boolean observeTerminal(Map<String, Object> event) {
        String type = str(event.get("type"));
        if ("Finish".equals(type)) {
            sawTerminal = true;
            return true;
        }
        if ("Error".equals(type)) {
            sawTerminal = true;
            Object error = event.get("error");
            errorMessage = error != null ? String.valueOf(error) : "agent run failed";
            return true;
        }
        return false;
    }

    /**
     * Produces the {@code a2a_progress} frames for one event (empty for ignored or terminal events). Also accumulates
     * user-visible assistant text into the final result.
     *
     * @param event a parsed goosed session event
     * @return zero or more progress payloads
     */
    public List<Map<String, Object>> progressFor(Map<String, Object> event) {
        String type = str(event.get("type"));
        if (!"Message".equals(type)) {
            return List.of();
        }
        if (!(event.get("message") instanceof Map<?, ?> message)) {
            return List.of();
        }
        if (!"assistant".equals(message.get("role"))) {
            return List.of();
        }
        if (message.get("metadata") instanceof Map<?, ?> metadata
            && Boolean.FALSE.equals(metadata.get("userVisible"))) {
            return List.of();
        }
        if (!(message.get("content") instanceof List<?> contentItems)) {
            return List.of();
        }

        List<Map<String, Object>> frames = new ArrayList<>();
        for (Object item : contentItems) {
            if (!(item instanceof Map<?, ?> content)) {
                continue;
            }
            String contentType = str(content.get("type"));
            switch (contentType) {
                case "thinking", "redactedThinking" -> {
                    // dropped: thinking is never surfaced to the caller
                }
                case "toolRequest" -> {
                    toolCalls++;
                    frames.add(progressFrame("tool_call", friendlyToolName(toolName(content))));
                }
                case "text" -> {
                    String text = str(content.get("text"));
                    if (text != null && !text.isBlank()) {
                        result.append(text);
                        frames.add(progressFrame("text", truncate(text.trim())));
                    }
                }
                default -> {
                    // image / toolResponse / other content does not produce a progress line
                }
            }
        }
        return frames;
    }

    /**
     * Records an infrastructure-level failure (e.g. the events stream broke).
     *
     * @param message the failure description
     */
    public void markError(String message) {
        if (this.errorMessage == null) {
            this.errorMessage = message == null || message.isBlank() ? "agent run failed" : message;
        }
    }

    /**
     * Builds the terminal {@code a2a_result} frame from accumulated state.
     *
     * @return the terminal payload
     */
    public Map<String, Object> resultFrame() {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("type", TYPE_RESULT);
        frame.put("target_agent", targetAgent);
        frame.put("sub_session_id", subSessionId);
        frame.put("status", status());
        frame.put("result", result.toString().trim());
        if (errorMessage != null) {
            frame.put("error", errorMessage);
        }
        frame.put("step", step);
        frame.put("tool_calls", toolCalls);
        return frame;
    }

    /**
     * Resolves the lifecycle status for the side-record / terminal frame.
     *
     * @return one of the {@link A2ASessionRecord} status constants
     */
    public String status() {
        if (errorMessage != null) {
            return A2ASessionRecord.STATUS_ERROR;
        }
        // A real terminal wins over a timeout race; otherwise (idle/total cap, or stream ended without Finish) it is
        // a timeout.
        return sawTerminal ? A2ASessionRecord.STATUS_COMPLETED : A2ASessionRecord.STATUS_TIMEOUT;
    }

    private Map<String, Object> progressFrame(String kind, String label) {
        step++;
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("type", TYPE_PROGRESS);
        frame.put("target_agent", targetAgent);
        frame.put("sub_session_id", subSessionId);
        frame.put("kind", kind);
        frame.put("label", label);
        frame.put("step", step);
        frame.put("tool_calls", toolCalls);
        return frame;
    }

    private static String toolName(Map<?, ?> content) {
        if (content.get("toolCall") instanceof Map<?, ?> toolCall
            && toolCall.get("value") instanceof Map<?, ?> value) {
            return str(value.get("name"));
        }
        return null;
    }

    /**
     * Maps a raw tool name to a friendly action label: drops the extension prefix and de-snake-cases it. e.g.
     * {@code control_center__read_service_logs} -> {@code read service logs}.
     *
     * @param rawName the raw {@code extension__tool} name (may be null)
     * @return a human-friendly label
     */
    static String friendlyToolName(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return "working";
        }
        String name = rawName;
        int sep = name.lastIndexOf("__");
        if (sep >= 0 && sep + 2 < name.length()) {
            name = name.substring(sep + 2);
        }
        return truncate(name.replace('_', ' ').trim().toLowerCase(Locale.ROOT));
    }

    private static String truncate(String text) {
        if (text.length() <= LABEL_MAX) {
            return text;
        }
        return text.substring(0, LABEL_MAX - 1).trim() + "…";
    }

    private static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
