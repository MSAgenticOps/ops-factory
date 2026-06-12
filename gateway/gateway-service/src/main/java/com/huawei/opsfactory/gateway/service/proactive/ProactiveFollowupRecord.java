/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

/**
 * One proactive follow-up record (PRD §7.1): a single delivery fact, not a state machine. Stored append-only as
 * one JSON object per line in {@code proactive-followups/records.jsonl}. Serves IM delivery audit and IM-reply
 * context injection only.
 *
 * @param time delivery time (ISO-8601 UTC instant)
 * @param scheduleId originating schedule identifier
 * @param sessionId originating goosed session identifier (used for delivery idempotency)
 * @param targetKey IM conversation target key ({@link ChannelTargetKey})
 * @param summary the delivered report text (verbatim)
 * @author x00000000
 * @since 2026-06-07
 */
public record ProactiveFollowupRecord(String time, String scheduleId, String sessionId, String targetKey,
    String summary) {
}
