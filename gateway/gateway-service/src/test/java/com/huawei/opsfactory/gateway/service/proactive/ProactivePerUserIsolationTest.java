/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * Proves two different users using the proactive capability concurrently do not collide, exercising the REAL
 * storage stack (ProactiveStorage path resolution + delivery markers + follow-up records) against real files —
 * even when both users share the same agent id, schedule id, session id, and conversation targetKey.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public class ProactivePerUserIsolationTest {
    private static final String AGENT = "fo-copilot";

    private static final String SCHEDULE = "ticket-watch-loop";

    private static final String SESSION = "20260607_1200";

    private static final String TARGET_KEY = "im:whatsapp:wa:default:conv:";

    private Path root;

    private ProactiveDeliveryMarkerService markerService;

    private ProactiveFollowupService followupService;

    @Before
    public void setUp() throws Exception {
        root = Files.createTempDirectory("proactive-multiuser");
        GatewayProperties properties = mock(GatewayProperties.class);
        when(properties.getGatewayRootPath()).thenReturn(root);
        ProactiveStorage storage = new ProactiveStorage(properties);
        markerService = new ProactiveDeliveryMarkerService(storage);
        followupService = new ProactiveFollowupService(storage);
    }

    @Test
    public void deliverMarkers_areIsolatedPerUser() {
        markerService.setDeliver("alice", AGENT, SCHEDULE, "im");

        assertEquals("im", markerService.getDeliver("alice", AGENT, SCHEDULE));
        assertNull("bob must not see alice's marker for the same schedule id",
            markerService.getDeliver("bob", AGENT, SCHEDULE));
        assertTrue(Files.exists(deliveryFile("alice")));
        assertFalse("bob's marker file must not be created by alice's write", Files.exists(deliveryFile("bob")));

        markerService.setDeliver("bob", AGENT, SCHEDULE, "im");
        assertEquals("im", markerService.getDeliver("bob", AGENT, SCHEDULE));
        assertEquals("alice's marker is unaffected by bob's write", "im",
            markerService.getDeliver("alice", AGENT, SCHEDULE));
    }

    @Test
    public void followupRecords_areIsolatedPerUser_evenWithIdenticalKeys() {
        followupService.append("alice", AGENT, record("alice report"));
        followupService.append("bob", AGENT, record("bob report"));

        // Same session id for both users (goosed ids are per-instance and can collide) — each user's own file only.
        assertTrue(followupService.existsForSession("alice", AGENT, SESSION));
        assertTrue(followupService.existsForSession("bob", AGENT, SESSION));

        List<ProactiveFollowupRecord> aliceRecent = followupService.recentByTargetKey("alice", AGENT, TARGET_KEY, 10);
        List<ProactiveFollowupRecord> bobRecent = followupService.recentByTargetKey("bob", AGENT, TARGET_KEY, 10);
        assertEquals(1, aliceRecent.size());
        assertEquals(1, bobRecent.size());
        assertEquals("alice report", aliceRecent.get(0).summary());
        assertEquals("bob report", bobRecent.get(0).summary());
    }

    private Path deliveryFile(String userId) {
        return root.resolve("users").resolve(userId).resolve("agents").resolve(AGENT)
            .resolve(ProactiveDeliveryMarkers.DIR).resolve(ProactiveDeliveryMarkers.DELIVERY_FILE);
    }

    private ProactiveFollowupRecord record(String summary) {
        return new ProactiveFollowupRecord(Instant.now().toString(), SCHEDULE, SESSION, TARGET_KEY, summary);
    }
}
