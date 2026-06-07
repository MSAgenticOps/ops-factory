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
        when(instanceManager.getAllInstances()).thenReturn(List.of(instance));

        when(goosedProxy.fetchJson(eq(PORT), eq("/sessions"), eq(SECRET))).thenReturn(Mono.just(
            "{\"sessions\":[{\"id\":\"" + SESSION + "\",\"schedule_id\":\"" + SCHEDULE + "\",\"created_at\":\""
                + Instant.now() + "\"}]}"));
        when(goosedProxy.fetchJson(eq(PORT), eq("/schedule/list"), eq(SECRET))).thenReturn(
            Mono.just("{\"jobs\":[{\"id\":\"" + SCHEDULE + "\",\"currently_running\":false}]}"));
        when(goosedProxy.fetchJson(eq(PORT), eq("/sessions/" + SESSION), eq(SECRET))).thenReturn(Mono.just(
            "{\"conversation\":[{\"role\":\"assistant\",\"content\":[{\"type\":\"text\",\"text\":\"INC-1 建议关单\"}],"
                + "\"metadata\":{\"userVisible\":true}}]}"));

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
    public void notMarkedForIm_skips() throws Exception {
        when(markerService.getDeliver(USER, AGENT, SCHEDULE)).thenReturn(null);

        service.pollAndDeliver();

        assertEquals(0, outboxFileCount());
        verify(followupService, never()).append(anyString(), anyString(), any());
    }

    @Test
    public void noImBinding_logsAndSkipsWithoutRecord() throws Exception {
        when(markerService.getDeliver(USER, AGENT, SCHEDULE)).thenReturn("im");
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

    private void markImAndBindOneWhatsApp() {
        when(markerService.getDeliver(USER, AGENT, SCHEDULE)).thenReturn("im");
        ChannelSummary summary = new ChannelSummary("wa-1", "WhatsApp", "whatsapp", true, AGENT, USER, "ok", null,
            null, 1);
        when(channelConfigService.listChannels(USER)).thenReturn(List.of(summary));
        when(channelConfigService.getChannel("wa-1", USER)).thenReturn(mock(ChannelDetail.class));
        ChannelBinding binding = new ChannelBinding("wa-1", "default", "447900", "conv-1", null, "direct", USER,
            "synthetic", AGENT, "chan-sess", null, null);
        when(channelConfigService.listBindings("wa-1", USER)).thenReturn(List.of(binding));
    }

    private long outboxFileCount() throws Exception {
        try (Stream<Path> files = Files.list(outboxDir)) {
            return files.filter(p -> p.toString().endsWith(".json")).count();
        }
    }
}
