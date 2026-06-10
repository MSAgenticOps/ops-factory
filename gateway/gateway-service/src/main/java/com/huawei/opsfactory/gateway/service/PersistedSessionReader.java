/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads persisted goosed session metadata from per-user agent SQLite stores.
 */
@Service
public class PersistedSessionReader {
    private static final Logger log = LoggerFactory.getLogger(PersistedSessionReader.class);

    private static final int MAX_DB_OPEN_SECONDS = 5;

    private final AgentConfigService agentConfigService;

    /**
     * Creates the persisted session reader.
     *
     * @param agentConfigService gateway agent configuration service
     */
    public PersistedSessionReader(AgentConfigService agentConfigService) {
        this.agentConfigService = agentConfigService;
    }

    /**
     * Lists persisted sessions for all agents owned by the user.
     *
     * @param userId user identifier
     * @return session maps shaped like the goosed session list response, with agentId injected
     */
    public List<Map<String, Object>> listUserSessions(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        Path agentsDir = agentConfigService.getUsersDir().resolve(userId).resolve("agents").toAbsolutePath()
            .normalize();
        if (!Files.isDirectory(agentsDir)) {
            return List.of();
        }

        List<Map<String, Object>> sessions = new ArrayList<>();
        for (Path db : findSessionDbs(agentsDir)) {
            try {
                sessions.addAll(readDb(agentsDir, db));
            } catch (SQLException ex) {
                log.warn("[SESSION-LIST] skip persisted session db {}: {}", db, ex.getMessage());
            }
        }
        return sessions;
    }

    private List<Path> findSessionDbs(Path agentsDir) {
        try (var stream = Files.find(agentsDir, 4, (path, attrs) -> attrs.isRegularFile() && isSessionDb(path))) {
            return stream.sorted().toList();
        } catch (IOException ex) {
            log.warn("[SESSION-LIST] failed to scan persisted session dbs under {}: {}", agentsDir, ex.getMessage());
            return List.of();
        }
    }

    private boolean isSessionDb(Path path) {
        String name = path.getFileName().toString();
        if (!"sessions.db".equals(name)) {
            return false;
        }
        String normalized = path.toString().replace('\\', '/');
        return normalized.endsWith("/data/sessions/sessions.db") || normalized.endsWith("/data/sessions.db");
    }

    private List<Map<String, Object>> readDb(Path agentsDir, Path db) throws SQLException {
        Path relative = agentsDir.relativize(db);
        if (relative.getNameCount() < 2) {
            return List.of();
        }
        String agentId = relative.getName(0).toString();
        String url = "jdbc:sqlite:file:" + db.toAbsolutePath() + "?mode=ro";
        try (Connection connection = DriverManager.getConnection(url)) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(MAX_DB_OPEN_SECONDS);
                statement.execute("PRAGMA query_only = true");
            }
            if (!tableExists(connection, "sessions")) {
                return List.of();
            }
            return readSessions(connection, agentId);
        }
    }

    private List<Map<String, Object>> readSessions(Connection connection, String agentId) throws SQLException {
        List<Map<String, Object>> sessions = new ArrayList<>();
        String sql = """
            select id, name, description, user_set_name, session_type, working_dir,
                   created_at, updated_at, total_tokens, input_tokens, output_tokens,
                   accumulated_total_tokens, accumulated_input_tokens, accumulated_output_tokens,
                   schedule_id, recipe_json, user_recipe_values_json, provider_name,
                   model_config_json, goose_mode, thread_id
            from sessions
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> session = readSessionRow(rs, agentId);
                if (session != null) {
                    sessions.add(session);
                }
            }
        }
        return sessions;
    }

    private Map<String, Object> readSessionRow(ResultSet rs, String agentId) throws SQLException {
        String id = rs.getString("id");
        if (id == null || id.isBlank()) {
            return null;
        }

        Map<String, Object> session = new LinkedHashMap<>();
        put(session, "id", id);
        put(session, "name", rs.getString("name"));
        put(session, "description", rs.getString("description"));
        session.put("user_set_name", rs.getBoolean("user_set_name"));
        put(session, "session_type", rs.getString("session_type"));
        put(session, "working_dir", rs.getString("working_dir"));
        put(session, "created_at", rs.getString("created_at"));
        put(session, "updated_at", rs.getString("updated_at"));
        put(session, "total_tokens", rs.getObject("total_tokens"));
        put(session, "input_tokens", rs.getObject("input_tokens"));
        put(session, "output_tokens", rs.getObject("output_tokens"));
        put(session, "accumulated_total_tokens", rs.getObject("accumulated_total_tokens"));
        put(session, "accumulated_input_tokens", rs.getObject("accumulated_input_tokens"));
        put(session, "accumulated_output_tokens", rs.getObject("accumulated_output_tokens"));
        put(session, "schedule_id", rs.getString("schedule_id"));
        put(session, "recipe_json", rs.getString("recipe_json"));
        put(session, "user_recipe_values_json", rs.getString("user_recipe_values_json"));
        put(session, "provider_name", rs.getString("provider_name"));
        put(session, "model_config_json", rs.getString("model_config_json"));
        put(session, "goose_mode", rs.getString("goose_mode"));
        put(session, "thread_id", rs.getString("thread_id"));
        session.put("agentId", agentId);
        return session;
    }

    private void put(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (PreparedStatement statement =
            connection.prepareStatement("select name from sqlite_master where type='table' and name=?")) {
            statement.setString(1, tableName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
}
