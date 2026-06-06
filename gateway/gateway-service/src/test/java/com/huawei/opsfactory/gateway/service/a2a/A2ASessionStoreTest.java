/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.a2a;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test coverage for the A2A side-record store.
 *
 * @author x00000000
 * @since 2026-06-05
 */
public class A2ASessionStoreTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private A2ASessionStore store;

    /**
     * Initializes a store backed by a temporary gateway root.
     */
    @Before
    public void setUp() {
        store = new A2ASessionStore(properties());
    }

    private GatewayProperties properties() {
        GatewayProperties properties = new GatewayProperties();
        GatewayProperties.Paths paths = new GatewayProperties.Paths();
        paths.setProjectRoot(tempFolder.getRoot().getAbsolutePath());
        properties.setPaths(paths);
        return properties;
    }

    private A2ASessionRecord rec(String sub, String parent, String user, String status) {
        return new A2ASessionRecord(sub, parent, "agentA", "agentB", user, "2026-06-05T10:00:00Z", status, "do X");
    }

    /**
     * Records can be persisted and found; only sub-runs (not their parents) are agent_call sessions.
     */
    @Test
    public void recordsAndFinds() {
        store.record(rec("B1", "A1", "alice", A2ASessionRecord.STATUS_RUNNING));
        assertTrue(store.find("alice", "B1").isPresent());
        assertEquals("agentB", store.find("alice", "B1").get().targetAgentId());
        assertTrue(store.isAgentCallSession("alice", "agentB", "B1"));
        assertFalse("parent session is not itself a sub-run", store.isAgentCallSession("alice", "agentA", "A1"));
        assertFalse(store.isAgentCallSession("alice", "agentB", "unknown"));
        // Agent-qualified: the same sub-session id under a DIFFERENT agent is not a match (per-instance ids collide).
        assertFalse("collision guard: B1 belongs to agentB, not agentC",
            store.isAgentCallSession("alice", "agentC", "B1"));
    }

    /**
     * Records are isolated per user.
     */
    @Test
    public void isolatesByUser() {
        store.record(rec("B1", "A1", "alice", A2ASessionRecord.STATUS_RUNNING));
        assertFalse(store.isAgentCallSession("bob", "agentB", "B1"));
        assertTrue(store.listForUser("bob").isEmpty());
    }

    /**
     * Re-recording the same sub-session id replaces (dedups); status updates in place.
     */
    @Test
    public void updatesStatusAndDedups() {
        store.record(rec("B1", "A1", "alice", A2ASessionRecord.STATUS_RUNNING));
        store.record(rec("B1", "A1", "alice", A2ASessionRecord.STATUS_RUNNING));
        assertEquals(1, store.listForUser("alice").size());
        store.updateStatus("alice", "agentB", "B1", A2ASessionRecord.STATUS_COMPLETED);
        assertEquals(A2ASessionRecord.STATUS_COMPLETED, store.find("alice", "B1").get().status());
        assertEquals(1, store.listForUser("alice").size());
    }

    /**
     * Records survive a fresh store instance (persisted to disk).
     */
    @Test
    public void persistsAcrossInstances() {
        store.record(rec("B1", "A1", "alice", A2ASessionRecord.STATUS_COMPLETED));
        A2ASessionStore reopened = new A2ASessionStore(properties());
        assertTrue(reopened.isAgentCallSession("alice", "agentB", "B1"));
    }

    /**
     * The store file lands under {@code gateway/users/{userId}/a2a/sessions.json}.
     */
    @Test
    public void writesUnderUsersA2aPath() {
        store.record(rec("B1", "A1", "alice", A2ASessionRecord.STATUS_RUNNING));
        Path expected = Path.of(tempFolder.getRoot().getAbsolutePath())
            .resolve("gateway")
            .resolve("users")
            .resolve("alice")
            .resolve("a2a")
            .resolve("sessions.json");
        assertTrue(Files.exists(expected));
    }

    /**
     * Unsafe user ids are rejected (path traversal guard).
     */
    @Test
    public void rejectsUnsafeUserId() {
        try {
            store.storeFile("../evil");
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    /**
     * Unknown users yield empty results, not errors.
     */
    @Test
    public void emptyForUnknownUser() {
        assertTrue(store.listForUser("nobody").isEmpty());
        assertFalse(store.isAgentCallSession("nobody", "agentB", "x"));
    }

    /**
     * The {@code __default__} user (underscores) is a valid path segment.
     */
    @Test
    public void handlesDefaultUserUnderscores() {
        store.record(rec("B1", "A1", "__default__", A2ASessionRecord.STATUS_RUNNING));
        assertTrue(store.isAgentCallSession("__default__", "agentB", "B1"));
    }

    /**
     * Two sub-runs that collide on sub-session id but target different agents stay distinct: neither record
     * overwrites the other and a status update is agent-scoped (goosed session ids are per-instance, not unique).
     */
    @Test
    public void distinguishesCollidingSubSessionIdsByAgent() {
        store.record(new A2ASessionRecord("S1", "A1", "agentA", "agentB", "alice", "2026-06-05T10:00:00Z",
            A2ASessionRecord.STATUS_RUNNING, "to B"));
        store.record(new A2ASessionRecord("S1", "A1", "agentA", "agentC", "alice", "2026-06-05T10:00:00Z",
            A2ASessionRecord.STATUS_RUNNING, "to C"));
        assertEquals("colliding ids under different agents must not overwrite", 2, store.listForUser("alice").size());

        store.updateStatus("alice", "agentB", "S1", A2ASessionRecord.STATUS_COMPLETED);
        long bCompleted = store.listForUser("alice").stream()
            .filter(r -> "agentB".equals(r.targetAgentId()) && A2ASessionRecord.STATUS_COMPLETED.equals(r.status()))
            .count();
        long cStillRunning = store.listForUser("alice").stream()
            .filter(r -> "agentC".equals(r.targetAgentId()) && A2ASessionRecord.STATUS_RUNNING.equals(r.status()))
            .count();
        assertEquals(1, bCompleted);
        assertEquals(1, cStillRunning);
        assertTrue(store.isAgentCallSession("alice", "agentB", "S1"));
        assertTrue(store.isAgentCallSession("alice", "agentC", "S1"));
    }
}
