/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.service.channel.ChannelConfigService;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.proactive.ChannelTargetKey;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveFollowupRecord;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveFollowupService;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller backing the Thread entry (PRD §13): the proactive-push timeline (column C) for one IM
 * conversation. A "thread" is identified by its {@link ChannelTargetKey} (per the conversation's binding
 * coordinates); this controller returns the proactive follow-up records delivered to that target so the UI can
 * render them as a time-ordered push timeline.
 *
 * <p>The target key is built server-side from the channel type + binding coordinates (mirroring the delivery
 * write path) so an inbound conversation always matches the records written at delivery time — the formula is
 * never exposed to the frontend (see {@link ChannelTargetKey}).
 *
 * @author x00000000
 * @since 2026-06-08
 */
@RestController
@RestSchema(schemaId = "threadController")
@RequestMapping("/api/gateway/agents/{agentId}/threads")
public class ThreadController {
    private static final Logger log = LoggerFactory.getLogger(ThreadController.class);

    private static final int DEFAULT_LIMIT = 50;

    private static final int MAX_LIMIT = 200;

    private static final String DEFAULT_ACCOUNT_ID = "default";

    private final ProactiveFollowupService followupService;

    private final ChannelConfigService channelConfigService;

    /**
     * Creates the thread controller instance.
     *
     * @param followupService per-user follow-up record reader
     * @param channelConfigService channel registry (resolves a channel's type by id)
     */
    public ThreadController(ProactiveFollowupService followupService, ChannelConfigService channelConfigService) {
        this.followupService = followupService;
        this.channelConfigService = channelConfigService;
    }

    /**
     * Lists the proactive follow-up records delivered to one thread (column C of the Thread entry). The thread is
     * located by its conversation's binding coordinates; the target key is built server-side from the channel
     * type so it matches the records written at delivery time.
     *
     * @param agentId agent identifier
     * @param query thread/conversation query parameters (channel id + binding coordinates + limit)
     * @param request current HTTP request
     * @return {@code {"followups": [...]}} oldest-first (the UI renders time-descending), or 400 on a bad/missing
     *     channel
     */
    @GetMapping(value = "/followups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> listFollowups(@PathVariable("agentId") String agentId,
        FollowupQuery query, HttpServletRequest request) {
        String userId = currentUserId(request);
        if (query.getChannelId() == null || query.getChannelId().isBlank()
            || query.getConversationId() == null || query.getConversationId().isBlank()) {
            // channelId + conversationId are required to locate a thread (previously enforced by @RequestParam).
            return ResponseEntity.badRequest().body(Map.of("error", "channelId and conversationId are required"));
        }
        String channelId = query.getChannelId();
        String effectiveAccount = query.getAccountId() == null || query.getAccountId().isBlank()
            ? DEFAULT_ACCOUNT_ID : query.getAccountId();
        int effectiveLimit = clampLimit(query.getLimit());
        ChannelDetail channel;
        try {
            channel = channelConfigService.getChannel(channelId, userId);
        } catch (IllegalArgumentException e) {
            // Malformed channel id (path validation). Keep the conversation id out of the log (it is a contact
            // identifier); report only the channel and reason.
            log.warn("Thread followups: cannot resolve channel {} for {}:{}: {}", channelId, agentId, userId,
                e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "unknown or invalid channel"));
        }
        if (channel == null) {
            // getChannel returns null (it does not throw) for an unknown channel; guard so a stale/unknown
            // channelId yields a clean 400 instead of an NPE on channel.type().
            log.warn("Thread followups: unknown channel {} for {}:{}", channelId, agentId, userId);
            return ResponseEntity.badRequest().body(Map.of("error", "unknown or invalid channel"));
        }
        String targetKey = ChannelTargetKey.of(channel.type(), channelId, effectiveAccount, query.getConversationId(),
            query.getThreadId());
        List<ProactiveFollowupRecord> followups =
            followupService.recentByTargetKey(userId, agentId, targetKey, effectiveLimit);
        log.info("Thread followups: {} record(s) on channel {} for {}:{}", followups.size(), channelId, agentId,
            userId);
        return ResponseEntity.ok(Map.of("followups", followups));
    }

    private int clampLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String currentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute(UserContextFilter.USER_ID_ATTR);
        return userId == null || userId.isBlank() ? "admin" : userId;
    }

    /**
     * Query parameters for {@link #listFollowups}, bound from the request query string. Bundled into one object to
     * keep the handler within the method-parameter budget.
     *
     * @param channelId channel identifier the conversation belongs to (required)
     * @param conversationId conversation identifier — binding coordinate (required)
     * @param accountId account identifier (binding coordinate); defaults to {@code default} when blank
     * @param threadId thread identifier (binding coordinate), or empty for a direct conversation
     * @param limit maximum records to return (defaults to 50, capped at 200)
     */
    public static class FollowupQuery {
        private String channelId;
        private String conversationId;
        private String accountId;
        private String threadId;
        private Integer limit;

        public FollowupQuery() {
        }

        public FollowupQuery(String channelId, String conversationId, String accountId, String threadId,
            Integer limit) {
            this.channelId = channelId;
            this.conversationId = conversationId;
            this.accountId = accountId;
            this.threadId = threadId;
            this.limit = limit;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getThreadId() {
            return threadId;
        }

        public void setThreadId(String threadId) {
            this.threadId = threadId;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }
    }
}
