/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Prepends a "recent FO Copilot follow-up" summary to an inbound IM reply so the agent has the context to act on a
 * terse reply like "关吧" (PRD §6.2). The agent sees the augmented text; the IM surface still shows the user's
 * original message (the augmentation is only what is sent to goosed).
 *
 * <p>Stateless: records are only read (never marked "answered"); the most recent {@code ~10} follow-ups for the
 * conversation's {@link ChannelTargetKey} are injected with absolute + relative timestamps, letting the agent judge
 * relevance itself. When there is no matching follow-up, the user's text is returned unchanged.
 *
 * @author x00000000
 * @since 2026-06-07
 */
@Service
public class ProactiveContextInjector {
    private static final DateTimeFormatter ABSOLUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ProactiveFollowupService followupService;

    private final int injectLimit;

    /**
     * Creates the context injector.
     *
     * @param followupService follow-up record reader
     * @param injectLimit maximum number of recent follow-ups to inject
     */
    public ProactiveContextInjector(ProactiveFollowupService followupService,
        @Value("${gateway.proactive-delivery.followup-inject-limit:10}") int injectLimit) {
        this.followupService = followupService;
        this.injectLimit = injectLimit;
    }

    /**
     * Returns the user text with a recent-follow-up context block prepended, or the text unchanged when there is no
     * matching follow-up.
     *
     * @param userId user identifier
     * @param agentId agent identifier
     * @param targetKey conversation target key
     * @param userText the user's original reply
     * @return the augmented text for the agent (or the original text)
     */
    public String augment(String userId, String agentId, String targetKey, String userText) {
        List<ProactiveFollowupRecord> recent = followupService.recentByTargetKey(userId, agentId, targetKey,
            injectLimit);
        if (recent.isEmpty()) {
            return userText;
        }
        StringBuilder sb = new StringBuilder("[最近 FO Copilot 主动跟进]\n");
        for (ProactiveFollowupRecord record : recent) {
            sb.append("- ").append(formatTime(record.time())).append(' ').append(nullToEmpty(record.scheduleId()))
                .append(':').append(oneLine(record.summary())).append('\n');
        }
        sb.append("\n[用户当前回复]\n").append(userText);
        return sb.toString();
    }

    private String formatTime(String iso) {
        Instant instant = parse(iso);
        if (instant == null) {
            return "";
        }
        return ABSOLUTE.format(instant.atZone(ZoneId.systemDefault())) + "(" + relative(instant) + ")";
    }

    private String relative(Instant instant) {
        long days = Duration.between(instant, Instant.now()).toDays();
        if (days <= 0) {
            return "今天";
        }
        if (days == 1) {
            return "昨天";
        }
        return "约 " + days + " 天前";
    }

    private Instant parse(String iso) {
        if (iso == null) {
            return null;
        }
        try {
            return Instant.parse(iso);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private String oneLine(String value) {
        return value == null ? "" : value.replace('\n', ' ').trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
