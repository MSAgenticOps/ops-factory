/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Test coverage for {@link ProactiveFollowups} (records.jsonl append / query / compaction).
 *
 * @author x00000000
 * @since 2026-06-07
 */
public class ProactiveFollowupsTest {
    @Test
    public void append_thenExistsForSession() throws Exception {
        Path file = tempRecords();

        ProactiveFollowups.append(file, record("s1", "im:wechat:c:default:conv:"));

        assertTrue(ProactiveFollowups.existsForSession(file, "s1"));
        assertFalse(ProactiveFollowups.existsForSession(file, "s2"));
    }

    @Test
    public void recentByTargetKey_filtersByKeyAndReturnsOldestFirst() throws Exception {
        Path file = tempRecords();
        ProactiveFollowups.append(file, record("s1", "im:k:a"));
        ProactiveFollowups.append(file, record("s2", "im:k:b"));
        ProactiveFollowups.append(file, record("s3", "im:k:a"));

        List<ProactiveFollowupRecord> recent = ProactiveFollowups.recentByTargetKey(file, "im:k:a", 10);

        assertEquals(2, recent.size());
        assertEquals("s1", recent.get(0).sessionId());
        assertEquals("s3", recent.get(1).sessionId());
    }

    @Test
    public void recentByTargetKey_respectsLimit() throws Exception {
        Path file = tempRecords();
        ProactiveFollowups.append(file, record("s1", "im:k:a"));
        ProactiveFollowups.append(file, record("s2", "im:k:a"));
        ProactiveFollowups.append(file, record("s3", "im:k:a"));

        List<ProactiveFollowupRecord> recent = ProactiveFollowups.recentByTargetKey(file, "im:k:a", 2);

        assertEquals(2, recent.size());
        assertEquals("s2", recent.get(0).sessionId());
        assertEquals("s3", recent.get(1).sessionId());
    }

    @Test
    public void append_compactsRecordsOlderThanMaxAge() throws Exception {
        Path file = tempRecords();
        String staleTime = Instant.now().minus(ProactiveFollowups.MAX_AGE_DAYS + 1, ChronoUnit.DAYS).toString();
        ProactiveFollowups.append(file, new ProactiveFollowupRecord(staleTime, "sched", "stale", "im:k:a", "old"));

        // A later append triggers compaction of the now-expired record.
        ProactiveFollowups.append(file, record("fresh", "im:k:a"));

        assertFalse(ProactiveFollowups.existsForSession(file, "stale"));
        assertTrue(ProactiveFollowups.existsForSession(file, "fresh"));
    }

    private static ProactiveFollowupRecord record(String sessionId, String targetKey) {
        return new ProactiveFollowupRecord(Instant.now().toString(), "sched", sessionId, targetKey, "report");
    }

    private static Path tempRecords() throws Exception {
        return Files.createTempDirectory("pfu").resolve(ProactiveStorage.RECORDS_FILE);
    }
}
