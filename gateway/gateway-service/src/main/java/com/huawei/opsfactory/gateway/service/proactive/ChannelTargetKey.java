/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

/**
 * Builds the IM conversation target key shared by proactive delivery (write side) and IM-reply context injection
 * (read side): {@code im:<type>:<channelId>:<accountId>:<conversationId>:<threadId>} (PRD §6.2/§7.1).
 *
 * <p>Both sides MUST construct the key through here so an inbound reply matches the follow-up records written at
 * delivery time. {@code null} segments collapse to empty so a missing threadId yields a stable, matchable key.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public final class ChannelTargetKey {
    /** Prefix marking the key as an IM conversation target. */
    public static final String IM_PREFIX = "im";

    private ChannelTargetKey() {
    }

    /**
     * Builds the conversation target key.
     *
     * @param type channel type (e.g. {@code wechat}, {@code whatsapp})
     * @param channelId channel identifier
     * @param accountId account identifier (e.g. {@code default})
     * @param conversationId conversation identifier
     * @param threadId thread identifier, or {@code null}
     * @return the {@code im:...} target key
     */
    public static String of(String type, String channelId, String accountId, String conversationId, String threadId) {
        return String.join(":", IM_PREFIX, seg(type), seg(channelId), seg(accountId), seg(conversationId),
            seg(threadId));
    }

    private static String seg(String value) {
        return value == null ? "" : value;
    }
}
