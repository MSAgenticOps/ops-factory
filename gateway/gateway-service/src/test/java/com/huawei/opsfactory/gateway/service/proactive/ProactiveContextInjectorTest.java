/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import java.time.Instant;
import java.util.List;

/**
 * Test coverage for {@link ProactiveContextInjector}.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public class ProactiveContextInjectorTest {
    @Test
    public void augment_noFollowups_returnsOriginalText() {
        ProactiveFollowupService followups = mock(ProactiveFollowupService.class);
        when(followups.recentByTargetKey(anyString(), anyString(), anyString(), anyInt())).thenReturn(List.of());
        ProactiveContextInjector injector = new ProactiveContextInjector(followups, 10);

        assertEquals("关吧", injector.augment("alice", "fo-copilot", "im:wechat:c:default:conv:", "关吧"));
    }

    @Test
    public void augment_withFollowups_prependsContextBlockAndKeepsOriginalLast() {
        ProactiveFollowupService followups = mock(ProactiveFollowupService.class);
        when(followups.recentByTargetKey("alice", "fo-copilot", "im:k", 10)).thenReturn(List.of(
            new ProactiveFollowupRecord(Instant.now().toString(), "ticket-watch-loop", "s1", "im:k",
                "INC-1 建议关单,等你确认。")));
        ProactiveContextInjector injector = new ProactiveContextInjector(followups, 10);

        String out = injector.augment("alice", "fo-copilot", "im:k", "关吧");

        assertTrue(out.contains("[最近 FO Copilot 主动跟进]"));
        assertTrue(out.contains("ticket-watch-loop"));
        assertTrue(out.contains("INC-1 建议关单"));
        assertTrue(out.contains("今天"));
        assertTrue(out.contains("[用户当前回复]"));
        assertTrue(out.endsWith("关吧"));
    }

    @Test
    public void augment_flattensSummaryNewlines() {
        ProactiveFollowupService followups = mock(ProactiveFollowupService.class);
        when(followups.recentByTargetKey(anyString(), anyString(), anyString(), anyInt())).thenReturn(List.of(
            new ProactiveFollowupRecord(Instant.now().toString(), "sched", "s1", "im:k", "line1\nline2")));
        ProactiveContextInjector injector = new ProactiveContextInjector(followups, 10);

        String out = injector.augment("alice", "fo-copilot", "im:k", "ok");

        assertTrue(out.contains("line1 line2"));
        assertFalse(out.contains("line1\nline2"));
    }
}
