/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.channel.ChannelConfigService;
import com.huawei.opsfactory.gateway.service.channel.ChannelRuntimeStorageService;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelBinding;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelSummary;

import reactor.core.publisher.Mono;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

/**
 * Test coverage for {@link ProactiveDeliveryService}: completed scheduled runs marked for IM are delivered to the
 * channel outbox with a follow-up record; everything else is skipped.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public class ProactiveDeliveryServiceTest {
    private static final String AGENT = "fo-copilot";

    private static final String USER = "alice";

    private static final int PORT = 9000;

    private static final String SECRET = "secret";

    private static final String SCHEDULE = "ticket-watch-loop";

    private static final String SESSION = "sess-1";

    private InstanceManager instanceManager;

    private GoosedProxy goosedProxy;

    private ProactiveDeliveryMarkerService markerService;

    private ProactiveFollowupService followupService;

    private ChannelConfigService channelConfigService;

    private ChannelRuntimeStorageService runtimeStorageService;

    private Path outboxDir;

    private ProactiveDeliveryService service;

    @Before
    public void setUp() throws Exception {
        instanceManager = mock(InstanceManager.class);
        goosedProxy = mock(GoosedProxy.class);
        markerService = mock(ProactiveDeliveryMarkerService.class);
        followupService = mock(ProactiveFollowupService.class);
        channelConfigService = mock(ChannelConfigService.class);
        runtimeStorageService = mock(ChannelRuntimeStorageService.class);
        outboxDir = Files.createTempDirectory("proactive-outbox");

        service = new ProactiveDeliveryService(instanceManager, goosedProxy, markerService, followupService,
            channelConfigService, runtimeStorageService, true, 1440);

        ManagedInstance instance = mock(ManagedInstance.class);
        when(instance.getStatus()).thenReturn(ManagedInstance.Status.RUNNING);
        when(instance.getAgentId()).thenReturn(AGENT);
        when(instance.getUserId()).thenReturn(USER);
        when(instance.getPort()).thenReturn(PORT);
        when(instance.getSecretKey()).thenReturn(SECRET);
        when(instance.getKey()).thenReturn(AGENT + "::" + USER);
        when(instanceManager.getAllInstances()).thenReturn(List.of(instance));

        when(goosedProxy.fetchJson(eq(PORT), eq("/sessions"), eq(SECRET))).thenReturn(Mono.just(
            "{\"sessions\":[{\"id\":\"" + SESSION + "\",\"schedule_id\":\"" + SCHEDULE + "\",\"created_at\":\""
                + Instant.now() + "\"}]}"));
        when(goosedProxy.fetchJson(eq(PORT), eq("/schedule/list"), eq(SECRET))).thenReturn(
            Mono.just("{\"jobs\":[{\"id\":\"" + SCHEDULE + "\",\"currently_running\":false}]}"));
        when(goosedProxy.fetchJson(eq(PORT), eq("/sessions/" + SESSION), eq(SECRET))).thenReturn(Mono.just(
            "{\"conversation\":[{\"role\":\"assistant\",\"metadata\":{\"userVisible\":true},"
                + "\"content\":[{\"type\":\"text\",\"text\":\"INC-1 建议关单\"}]}]}"));

        when(runtimeStorageService.outboxPendingDirectory(any(ChannelDetail.class))).thenReturn(outboxDir);
    }

    @Test
    public void deliversImMarkedReport_writesOutboxAndRecord() throws Exception {
        markImAndBindOneWhatsApp();

        service.pollAndDeliver();

        assertEquals(1, outboxFileCount());
        verify(followupService).append(eq(USER), eq(AGENT), argThat(r -> SESSION.equals(r.sessionId())
            && "im:whatsapp:wa-1:default:conv-1:".equals(r.targetKey()) && r.summary().contains("INC-1")));
    }

    @Test
    public void deliversWhenConversationIsWrappedObject() throws Exception {
        markImAndBindOneWhatsApp();
        // goosed may wrap the message list as conversation:{messages:[...]} instead of a bare array.
        when(goosedProxy.fetchJson(eq(PORT), eq("/sessions/" + SESSION), eq(SECRET))).thenReturn(Mono.just(
            "{\"conversation\":{\"messages\":[{\"role\":\"assistant\",\"content\":[{\"type\":\"text\","
                + "\"text\":\"wrapped report\"}],\"metadata\":{\"userVisible\":true}}]}}"));

        service.pollAndDeliver();

        assertEquals(1, outboxFileCount());
        verify(followupService).append(eq(USER), eq(AGENT), argThat(r -> r.summary().contains("wrapped report")));
    }

    @Test
    public void notMarkedForIm_skips() throws Exception {
        when(markerService.readDeliver(USER, AGENT, SCHEDULE)).thenReturn(null);

        service.pollAndDeliver();

        assertEquals(0, outboxFileCount());
        verify(followupService, never()).append(anyString(), anyString(), any());
    }

    @Test
    public void noImBinding_logsAndSkipsWithoutRecord() throws Exception {
        when(markerService.readDeliver(USER, AGENT, SCHEDULE)).thenReturn("im");
        when(channelConfigService.listChannels(USER)).thenReturn(List.of());

        service.pollAndDeliver();

        assertEquals(0, outboxFileCount());
        verify(followupService, never()).append(anyString(), anyString(), any());
    }

    @Test
    public void alreadyRecordedSession_skips() throws Exception {
        markImAndBindOneWhatsApp();
        when(followupService.existsForSession(USER, AGENT, SESSION)).thenReturn(true);

        service.pollAndDeliver();

        assertEquals(0, outboxFileCount());
        verify(followupService, never()).append(anyString(), anyString(), any());
    }

    @Test
    public void currentlyRunningSession_skipsUntilComplete() throws Exception {
        markImAndBindOneWhatsApp();
        when(goosedProxy.fetchJson(eq(PORT), eq("/schedule/list"), eq(SECRET))).thenReturn(Mono.just(
            "{\"jobs\":[{\"id\":\"" + SCHEDULE + "\",\"currently_running\":true,\"current_session_id\":\"" + SESSION
                + "\"}]}"));

        service.pollAndDeliver();

        assertEquals(0, outboxFileCount());
        verify(followupService, never()).append(anyString(), anyString(), any());
    }

    @Test
    public void secondPoll_doesNotRedeliver() throws Exception {
        markImAndBindOneWhatsApp();

        service.pollAndDeliver();
        service.pollAndDeliver();

        assertEquals(1, outboxFileCount());
        verify(followupService, times(1)).append(anyString(), anyString(), any());
    }

    @Test
    public void transientDeliverMarkerReadFailure_retriesInsteadOfDropping() throws Exception {
        markImAndBindOneWhatsApp();
        // First read fails transiently; the run must NOT be marked processed, so the next poll retries rather than
        // silently dropping a delivery the user opted into.
        when(markerService.readDeliver(USER, AGENT, SCHEDULE)).thenThrow(new IOException("locked")).thenReturn("im");

        service.pollAndDeliver();
        assertEquals(0, outboxFileCount());
        verify(followupService, never()).append(anyString(), anyString(), any());

        service.pollAndDeliver();
        assertEquals(1, outboxFileCount());
        verify(followupService, times(1)).append(anyString(), anyString(), any());
    }

    @Test
    public void deliversOnePerConfiguredChannel_typeAgnostic() throws Exception {
        // Delivery is decoupled from which channel types exist: one outbox per enabled, bound channel, whatever the
        // type. Two channels of different types (one not even an IM the system special-cases) → two deliveries, each
        // with a targetKey carrying that channel's own type. No channel type is hardcoded in the delivery path.
        when(markerService.readDeliver(USER, AGENT, SCHEDULE)).thenReturn("im");
        ChannelSummary whatsApp = new ChannelSummary("wa-1", "WA", "whatsapp", true, AGENT, USER, "ok", null, null, 1);
        ChannelSummary other = new ChannelSummary("tg-1", "Telegram", "telegram", true, AGENT, USER, "ok", null, null,
            1);
        when(channelConfigService.listChannels(USER)).thenReturn(List.of(whatsApp, other));
        when(channelConfigService.getChannel("wa-1", USER)).thenReturn(mock(ChannelDetail.class));
        when(channelConfigService.getChannel("tg-1", USER)).thenReturn(mock(ChannelDetail.class));
        when(channelConfigService.listBindings("wa-1", USER)).thenReturn(List.of(new ChannelBinding("wa-1", "default",
            "wa-peer", "wa-conv", null, "direct", USER, "s", AGENT, "cs", null, null)));
        when(channelConfigService.listBindings("tg-1", USER)).thenReturn(List.of(new ChannelBinding("tg-1", "default",
            "tg-peer", "tg-conv", null, "direct", USER, "s", AGENT, "cs", null, null)));

        service.pollAndDeliver();

        assertEquals(2, outboxFileCount());
        verify(followupService).append(eq(USER), eq(AGENT),
            argThat(r -> "im:whatsapp:wa-1:default:wa-conv:".equals(r.targetKey())));
        verify(followupService).append(eq(USER), eq(AGENT),
            argThat(r -> "im:telegram:tg-1:default:tg-conv:".equals(r.targetKey())));
    }

    @Test
    public void skipsDisabledOrUnboundChannels() throws Exception {
        // Only enabled channels that actually have a bound conversation receive the report.
        when(markerService.readDeliver(USER, AGENT, SCHEDULE)).thenReturn("im");
        ChannelSummary active = new ChannelSummary("a-1", "Active", "whatsapp", true, AGENT, USER, "ok", null, null, 1);
        ChannelSummary disabled = new ChannelSummary("d-1", "Disabled", "whatsapp", false, AGENT, USER, "off", null,
            null, 1);
        ChannelSummary unbound = new ChannelSummary("u-1", "Unbound", "wechat", true, AGENT, USER, "ok", null, null, 0);
        when(channelConfigService.listChannels(USER)).thenReturn(List.of(active, disabled, unbound));
        when(channelConfigService.getChannel("a-1", USER)).thenReturn(mock(ChannelDetail.class));
        when(channelConfigService.listBindings("a-1", USER)).thenReturn(List.of(new ChannelBinding("a-1", "default",
            "a-peer", "a-conv", null, "direct", USER, "s", AGENT, "cs", null, null)));

        service.pollAndDeliver();

        assertEquals(1, outboxFileCount());
        verify(followupService, times(1)).append(eq(USER), eq(AGENT), any());
    }

    @Test
    public void deliversPerUserInIsolation_evenWithCollidingSessionIds() throws Exception {
        // alice & bob share a goosed session id (per-instance ids can collide); carol differs.
        Path aliceOutbox = Files.createTempDirectory("ob-alice");
        Path bobOutbox = Files.createTempDirectory("ob-bob");
        Path carolOutbox = Files.createTempDirectory("ob-carol");
        ManagedInstance alice = userInstance("alice", 9101);
        ManagedInstance bob = userInstance("bob", 9102);
        ManagedInstance carol = userInstance("carol", 9103);
        when(instanceManager.getAllInstances()).thenReturn(List.of(alice, bob, carol));
        stubUserDelivery("alice", 9101, "s-shared", "report-alice", "wa-alice", aliceOutbox);
        stubUserDelivery("bob", 9102, "s-shared", "report-bob", "wa-bob", bobOutbox);
        stubUserDelivery("carol", 9103, "s-carol", "report-carol", "wa-carol", carolOutbox);

        service.pollAndDeliver();

        assertEquals(1, countJson(aliceOutbox));
        assertEquals(1, countJson(bobOutbox));
        assertEquals(1, countJson(carolOutbox));
        verify(followupService).append(eq("alice"), eq(AGENT),
            argThat(r -> "s-shared".equals(r.sessionId()) && r.summary().contains("report-alice")));
        verify(followupService).append(eq("bob"), eq(AGENT),
            argThat(r -> "s-shared".equals(r.sessionId()) && r.summary().contains("report-bob")));
        verify(followupService).append(eq("carol"), eq(AGENT), argThat(r -> r.summary().contains("report-carol")));
    }

    private void markImAndBindOneWhatsApp() throws IOException {
        when(markerService.readDeliver(USER, AGENT, SCHEDULE)).thenReturn("im");
        ChannelSummary summary = new ChannelSummary("wa-1", "WhatsApp", "whatsapp", true, AGENT, USER, "ok", null,
            null, 1);
        when(channelConfigService.listChannels(USER)).thenReturn(List.of(summary));
        when(channelConfigService.getChannel("wa-1", USER)).thenReturn(mock(ChannelDetail.class));
        ChannelBinding binding = new ChannelBinding("wa-1", "default", "447900", "conv-1", null, "direct", USER,
            "synthetic", AGENT, "chan-sess", null, null);
        when(channelConfigService.listBindings("wa-1", USER)).thenReturn(List.of(binding));
    }

    private long outboxFileCount() throws Exception {
        return countJson(outboxDir);
    }

    private long countJson(Path dir) throws Exception {
        try (Stream<Path> files = Files.list(dir)) {
            return files.filter(p -> p.toString().endsWith(".json")).count();
        }
    }

    private ManagedInstance userInstance(String user, int port) {
        ManagedInstance instance = mock(ManagedInstance.class);
        when(instance.getStatus()).thenReturn(ManagedInstance.Status.RUNNING);
        when(instance.getAgentId()).thenReturn(AGENT);
        when(instance.getUserId()).thenReturn(user);
        when(instance.getPort()).thenReturn(port);
        when(instance.getSecretKey()).thenReturn(SECRET);
        when(instance.getKey()).thenReturn(AGENT + "::" + user);
        return instance;
    }

    private void stubUserDelivery(String user, int port, String sessionId, String report, String channelId,
        Path outbox) throws IOException {
        when(goosedProxy.fetchJson(eq(port), eq("/sessions"), eq(SECRET))).thenReturn(Mono.just(
            "{\"sessions\":[{\"id\":\"" + sessionId + "\",\"schedule_id\":\"" + SCHEDULE + "\",\"created_at\":\""
                + Instant.now() + "\"}]}"));
        when(goosedProxy.fetchJson(eq(port), eq("/schedule/list"), eq(SECRET))).thenReturn(
            Mono.just("{\"jobs\":[{\"id\":\"" + SCHEDULE + "\",\"currently_running\":false}]}"));
        when(goosedProxy.fetchJson(eq(port), eq("/sessions/" + sessionId), eq(SECRET))).thenReturn(Mono.just(
            "{\"conversation\":[{\"role\":\"assistant\",\"metadata\":{\"userVisible\":true},"
                + "\"content\":[{\"type\":\"text\",\"text\":\"" + report + "\"}]}]}"));
        when(markerService.readDeliver(user, AGENT, SCHEDULE)).thenReturn("im");
        ChannelSummary summary = new ChannelSummary(channelId, "WA", "whatsapp", true, AGENT, user, "ok", null, null,
            1);
        when(channelConfigService.listChannels(user)).thenReturn(List.of(summary));
        ChannelDetail detail = mock(ChannelDetail.class);
        when(channelConfigService.getChannel(channelId, user)).thenReturn(detail);
        when(runtimeStorageService.outboxPendingDirectory(detail)).thenReturn(outbox);
        when(channelConfigService.listBindings(channelId, user)).thenReturn(List.of(new ChannelBinding(channelId,
            "default", "peer-" + user, "conv-" + user, null, "direct", user, "syn", AGENT, "cs", null, null)));
    }
}
