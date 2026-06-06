/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.finops;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.model.finops.UsageSnapshotModels.SessionUsageRecord;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;

/**
 * Focused tests for UsageSnapshotService session parsing helpers.
 */
public class UsageSnapshotServiceTest {
    private UsageSnapshotService usageSnapshotService;

    /**
     * Sets up the test fixture.
     *
     */
    @Before
    public void setUp() {
        usageSnapshotService = new UsageSnapshotService(mock(AgentConfigService.class), new ObjectMapper());
    }

    /**
     * Verifies readSessionRow skips blank IDs and normalizes recipe/model data into a record.
     *
     * @throws Exception if invocation fails
     */
    @Test
    public void readSessionRow_readsValidSessionAndBuildsDerivedFields() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn("session-1");
        when(rs.getString("name")).thenReturn("New Chat");
        when(rs.getString("session_type")).thenReturn("CHAT");
        when(rs.getString("working_dir")).thenReturn("/tmp/work");
        when(rs.getObject("created_at")).thenReturn("2026-06-01T10:00:00Z");
        when(rs.getObject("updated_at")).thenReturn("2026-06-01T10:10:00Z");
        when(rs.getObject("total_tokens")).thenReturn(30L);
        when(rs.getObject("input_tokens")).thenReturn(10L);
        when(rs.getObject("output_tokens")).thenReturn(20L);
        when(rs.getObject("accumulated_total_tokens")).thenReturn(300L);
        when(rs.getObject("accumulated_input_tokens")).thenReturn(100L);
        when(rs.getObject("accumulated_output_tokens")).thenReturn(200L);
        when(rs.getString("schedule_id")).thenReturn("");
        when(rs.getString("recipe_json")).thenReturn("{\"title\":\"Recipe Title\",\"description\":\"Recipe Description\"}");
        when(rs.getString("provider_name")).thenReturn("");
        when(rs.getString("model_config_json")).thenReturn("{\"model_name\":\"gpt-test\"}");
        when(rs.getString("goose_mode")).thenReturn("standard");
        when(rs.getString("thread_id")).thenReturn("thread-1");

        UsageSnapshotService.UserAgent userAgent = new UsageSnapshotService.UserAgent("alice", "agent-a");
        SessionUsageRecord session = usageSnapshotService.readSessionRow(rs, userAgent);

        assertEquals("session-1", session.id());
        assertEquals("alice", session.userId());
        assertEquals("agent-a", session.agentId());
        assertEquals("chat", session.sessionType());
        assertEquals("gpt-test", session.modelName());
        assertEquals("Recipe Title - Recipe Description", session.label());
    }

    /**
     * Verifies readSessionRow returns null for blank session IDs.
     *
     * @throws Exception if invocation fails
     */
    @Test
    public void readSessionRow_returnsNullForBlankId() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("id")).thenReturn(" ");

        Object result = usageSnapshotService.readSessionRow(rs, new UsageSnapshotService.UserAgent("alice", "agent-a"));

        assertEquals(null, result);
    }
}
