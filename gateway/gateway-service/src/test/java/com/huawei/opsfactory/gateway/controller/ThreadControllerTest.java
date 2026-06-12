/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.service.channel.ChannelConfigService;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.proactive.ChannelTargetKey;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveFollowupRecord;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveFollowupService;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveStorage;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Verifies the Thread followups endpoint against the REAL follow-up storage stack: the target key is built
 * server-side from the channel type + binding coordinates (so it matches the delivery write path), records are
 * isolated per user, and an unknown channel yields a 400.
 *
 * @author x00000000
 * @since 2026-06-08
 */
public class ThreadControllerTest {
    private static final String AGENT = "fo-copilot";

    private static final String CHANNEL = "wechat-main";

    private static final String TYPE = "wechat";

    private static final String CONVERSATION = "o9cq8014jZAFjA8yzcrtqm3TBFC8@im.wechat";

    private ProactiveFollowupService followupService;

    private ChannelConfigService channelConfigService;

    private ThreadController controller;

    @Before
    public void setUp() throws Exception {
        Path root = Files.createTempDirectory("thread-controller");
        GatewayProperties properties = mock(GatewayProperties.class);
        when(properties.getGatewayRootPath()).thenReturn(root);
        followupService = new ProactiveFollowupService(new ProactiveStorage(properties));

        channelConfigService = mock(ChannelConfigService.class);
        ChannelDetail detail = mock(ChannelDetail.class);
        when(detail.type()).thenReturn(TYPE);
        when(channelConfigService.getChannel(CHANNEL, "alice")).thenReturn(detail);
        when(channelConfigService.getChannel(CHANNEL, "bob")).thenReturn(detail);

        controller = new ThreadController(followupService, channelConfigService);
    }

    @Test
    public void returnsFollowupsForThread_byServerBuiltTargetKey() {
        // Records are written at delivery time keyed by the SAME formula the controller rebuilds from coordinates.
        String targetKey = ChannelTargetKey.of(TYPE, CHANNEL, "default", CONVERSATION, "");
        followupService.append("alice", AGENT, record(targetKey, "ticket-daily-brief", "20260607_9", "brief A"));
        followupService.append("alice", AGENT, record(targetKey, "ticket-watch-loop", "20260607_5", "watch B"));

        ResponseEntity<Map<String, Object>> resp = listFollowups("alice", CONVERSATION, null, null, null);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        List<?> followups = (List<?>) resp.getBody().get("followups");
        assertEquals(2, followups.size());
        ProactiveFollowupRecord first = (ProactiveFollowupRecord) followups.get(0);
        assertEquals("the rebuilt target key must match the delivery-side key", targetKey, first.targetKey());
    }

    @Test
    public void isolatesPerUser() {
        String targetKey = ChannelTargetKey.of(TYPE, CHANNEL, "default", CONVERSATION, "");
        followupService.append("alice", AGENT, record(targetKey, "ticket-daily-brief", "s1", "alice only"));

        ResponseEntity<Map<String, Object>> bob = listFollowups("bob", CONVERSATION, null, null, null);
        assertTrue("bob must not see alice's followups", ((List<?>) bob.getBody().get("followups")).isEmpty());
    }

    @Test
    public void defaultsAccountId_whenOmitted() {
        // A record written with accountId=default must be found when the request omits accountId.
        String targetKey = ChannelTargetKey.of(TYPE, CHANNEL, "default", CONVERSATION, "");
        followupService.append("alice", AGENT, record(targetKey, "ticket-daily-brief", "s1", "x"));

        ResponseEntity<Map<String, Object>> resp = listFollowups("alice", CONVERSATION, null, null, null);
        assertEquals(1, ((List<?>) resp.getBody().get("followups")).size());
    }

    @Test
    public void malformedChannel_returns400() {
        when(channelConfigService.getChannel("nope", "alice"))
            .thenThrow(new IllegalArgumentException("no such channel"));
        ResponseEntity<Map<String, Object>> resp = listFollowups("alice", CONVERSATION, "nope", null, null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    public void unknownChannel_returns400_notNpe() {
        // getChannel returns null (not throws) for an unknown channel — must not NPE on channel.type().
        when(channelConfigService.getChannel("ghost", "alice")).thenReturn(null);
        ResponseEntity<Map<String, Object>> resp = listFollowups("alice", CONVERSATION, "ghost", null, null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    private ResponseEntity<Map<String, Object>> listFollowups(String userId, String conversationId, String channelId,
        String accountId, String threadId) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(UserContextFilter.USER_ID_ATTR)).thenReturn(userId);
        return controller.listFollowups(AGENT,
            new ThreadController.FollowupQuery(channelId == null ? CHANNEL : channelId, conversationId, accountId,
                threadId, null),
            request);
    }

    private ProactiveFollowupRecord record(String targetKey, String scheduleId, String sessionId, String summary) {
        return new ProactiveFollowupRecord(Instant.now().toString(), scheduleId, sessionId, targetKey, summary);
    }
}
