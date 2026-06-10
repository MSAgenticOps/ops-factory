/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Focused tests for persisted goosed session metadata loading.
 */
public class PersistedSessionReaderTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Reads session metadata from per-agent SQLite stores and injects agentId.
     */
    @Test
    public void listUserSessions_readsSessionDb() throws Exception {
        Path usersDir = tempFolder.getRoot().toPath().resolve("users");
        createSessionDb(usersDir, "alice", "agent-a", "s1", "Persisted Chat");
        PersistedSessionReader reader = new PersistedSessionReader(new TestAgentConfigService(usersDir));

        List<Map<String, Object>> sessions = reader.listUserSessions("alice");

        assertEquals(1, sessions.size());
        assertEquals("s1", sessions.get(0).get("id"));
        assertEquals("Persisted Chat", sessions.get(0).get("name"));
        assertEquals("agent-a", sessions.get(0).get("agentId"));
        assertEquals("user", sessions.get(0).get("session_type"));
    }

    /**
     * Missing user runtime directories should be treated as empty history.
     */
    @Test
    public void listUserSessions_missingUserDirReturnsEmpty() {
        PersistedSessionReader reader =
            new PersistedSessionReader(new TestAgentConfigService(tempFolder.getRoot().toPath().resolve("users")));

        assertTrue(reader.listUserSessions("alice").isEmpty());
    }

    private void createSessionDb(Path usersDir, String userId, String agentId, String sessionId, String name)
        throws Exception {
        Path db = usersDir.resolve(userId).resolve("agents").resolve(agentId).resolve("data").resolve("sessions")
            .resolve("sessions.db");
        Files.createDirectories(db.getParent());
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + db.toAbsolutePath())) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                    create table sessions (
                        id text primary key,
                        name text not null default '',
                        description text not null default '',
                        user_set_name boolean default false,
                        session_type text not null default 'user',
                        working_dir text not null,
                        created_at text,
                        updated_at text,
                        total_tokens integer,
                        input_tokens integer,
                        output_tokens integer,
                        accumulated_total_tokens integer,
                        accumulated_input_tokens integer,
                        accumulated_output_tokens integer,
                        schedule_id text,
                        recipe_json text,
                        user_recipe_values_json text,
                        provider_name text,
                        model_config_json text,
                        goose_mode text not null default 'auto',
                        thread_id text
                    )
                    """);
            }
            try (PreparedStatement statement = connection.prepareStatement("""
                insert into sessions (id, name, session_type, working_dir, created_at, updated_at)
                values (?, ?, 'user', ?, '2026-06-10T10:00:00Z', '2026-06-10T10:00:00Z')
                """)) {
                statement.setString(1, sessionId);
                statement.setString(2, name);
                statement.setString(3, usersDir.resolve(userId).resolve("agents").resolve(agentId).toString());
                statement.executeUpdate();
            }
        }
    }

    private static class TestAgentConfigService extends AgentConfigService {
        private final Path usersDir;

        TestAgentConfigService(Path usersDir) {
            super(new GatewayProperties());
            this.usersDir = usersDir;
        }

        @Override
        public Path getUsersDir() {
            return usersDir;
        }
    }
}
