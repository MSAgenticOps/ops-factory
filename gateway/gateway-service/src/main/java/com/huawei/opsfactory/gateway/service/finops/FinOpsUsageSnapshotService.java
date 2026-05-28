/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.finops;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.opsfactory.gateway.model.finops.FinOpsUsageSnapshotModels.SessionMessageRecord;
import com.huawei.opsfactory.gateway.model.finops.FinOpsUsageSnapshotModels.SessionUsageRecord;
import com.huawei.opsfactory.gateway.model.finops.FinOpsUsageSnapshotModels.SnapshotPayload;
import com.huawei.opsfactory.gateway.service.AgentConfigService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Builds FinOps usage snapshots from gateway-managed goosed session stores.
 *
 * @since 2026-05-28
 */
@Service
public class FinOpsUsageSnapshotService {

    private static final Logger log = LoggerFactory.getLogger(FinOpsUsageSnapshotService.class);
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final int MAX_DB_OPEN_SECONDS = 5;
    private static final int MESSAGE_TEXT_LIMIT = 12_000;
    private static final int MESSAGE_PREVIEW_LIMIT = 280;
    private static final Pattern TOOL_NAME_PATTERN = Pattern.compile("\\btool(?:Request|Response|_use|_result)\\s+([A-Za-z0-9_.:-]+)");
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    );

    private final AgentConfigService agentConfigService;
    private final ObjectMapper objectMapper;

    public FinOpsUsageSnapshotService(AgentConfigService agentConfigService, ObjectMapper objectMapper) {
        this.agentConfigService = agentConfigService;
        this.objectMapper = objectMapper;
    }

    /**
     * Returns a read-only usage snapshot for every goosed session database under the gateway users directory.
     *
     * @return normalized usage snapshot payload
     */
    public SnapshotPayload snapshot() {
        Path dataRoot = agentConfigService.getUsersDir().toAbsolutePath().normalize();
        if (!Files.isDirectory(dataRoot)) {
            log.warn("FinOps data root does not exist or is not a directory: {}", dataRoot);
            return new SnapshotPayload(List.of(), List.of(), 0, 0, dataRoot.toString(), "Data root not found: " + dataRoot);
        }

        List<Path> dbs;
        try (var stream = Files.find(dataRoot, 6, (path, attrs) -> attrs.isRegularFile() && isSessionDb(path))) {
            dbs = stream.sorted().toList();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to scan FinOps data root " + dataRoot, ex);
        }

        List<SessionUsageRecord> sessions = new ArrayList<>();
        List<SessionMessageRecord> messages = new ArrayList<>();
        int skipped = 0;
        String lastError = null;
        for (Path db : dbs) {
            try {
                DbReadResult result = readDb(dataRoot, db);
                sessions.addAll(result.sessions());
                messages.addAll(result.messages());
            } catch (SQLException ex) {
                skipped++;
                lastError = db + ": " + ex.getMessage();
                log.warn("Skipping unreadable FinOps session DB {}", db, ex);
            }
        }
        return new SnapshotPayload(sessions, messages, dbs.size(), skipped, dataRoot.toString(), lastError);
    }

    private boolean isSessionDb(Path path) {
        String name = path.getFileName().toString();
        if (!"sessions.db".equals(name)) {
            return false;
        }
        String normalized = path.toString().replace('\\', '/');
        return normalized.contains("/agents/") && (
            normalized.endsWith("/data/sessions/sessions.db") ||
            normalized.endsWith("/data/sessions.db")
        );
    }

    private DbReadResult readDb(Path dataRoot, Path db) throws SQLException {
        Optional<UserAgent> userAgent = resolveUserAgent(dataRoot, db);
        if (userAgent.isEmpty()) {
            return new DbReadResult(List.of(), List.of());
        }

        String url = "jdbc:sqlite:file:" + db.toAbsolutePath() + "?mode=ro";
        try (Connection connection = DriverManager.getConnection(url)) {
            try (Statement statement = connection.createStatement()) {
                statement.setQueryTimeout(MAX_DB_OPEN_SECONDS);
                statement.execute("PRAGMA query_only = true");
            }
            Map<String, MessageStats> messageStats = readMessageStats(connection);
            return new DbReadResult(
                readSessions(connection, userAgent.get(), messageStats),
                readMessages(connection, userAgent.get())
            );
        }
    }

    private Optional<UserAgent> resolveUserAgent(Path dataRoot, Path db) {
        Path relative = dataRoot.relativize(db);
        if (relative.getNameCount() < 4 || !"agents".equals(relative.getName(1).toString())) {
            return Optional.empty();
        }
        return Optional.of(new UserAgent(relative.getName(0).toString(), relative.getName(2).toString()));
    }

    private Map<String, MessageStats> readMessageStats(Connection connection) {
        if (!tableExists(connection, "messages")) {
            return Map.of();
        }

        Map<String, MutableMessageStats> stats = new HashMap<>();
        String sql = """
            select session_id, role, content_json, created_timestamp, timestamp
            from messages
            order by coalesce(created_timestamp, timestamp, 0) asc
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String sessionId = rs.getString("session_id");
                if (sessionId == null || sessionId.isBlank()) {
                    continue;
                }
                MutableMessageStats item = stats.computeIfAbsent(sessionId, ignored -> new MutableMessageStats());
                String role = nullToEmpty(rs.getString("role"));
                String content = rs.getString("content_json");
                item.messageCount++;
                if ("user".equalsIgnoreCase(role)) {
                    item.userMessageCount++;
                    if (item.firstUserText == null) {
                        item.firstUserText = extractFirstText(content);
                    }
                } else if ("assistant".equalsIgnoreCase(role)) {
                    item.assistantMessageCount++;
                }
                if (content != null && content.contains("toolResponse")) {
                    item.toolResponseCount++;
                }
            }
        } catch (SQLException ex) {
            log.warn("Failed to read FinOps message stats", ex);
        }
        Map<String, MessageStats> result = new HashMap<>();
        stats.forEach((key, value) -> result.put(key, value.toRecord()));
        return result;
    }

    private List<SessionUsageRecord> readSessions(Connection connection, UserAgent userAgent, Map<String, MessageStats> messageStats) throws SQLException {
        if (!tableExists(connection, "sessions")) {
            return List.of();
        }
        List<SessionUsageRecord> records = new ArrayList<>();
        String sql = """
            select id, name, session_type, working_dir, created_at, updated_at,
                   total_tokens, input_tokens, output_tokens,
                   accumulated_total_tokens, accumulated_input_tokens, accumulated_output_tokens,
                   schedule_id, recipe_json, provider_name, model_config_json, goose_mode, thread_id
            from sessions
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String sessionId = rs.getString("id");
                if (sessionId == null || sessionId.isBlank()) {
                    continue;
                }
                String recipeJson = rs.getString("recipe_json");
                String modelConfigJson = rs.getString("model_config_json");
                Map<String, Object> recipe = parseMap(recipeJson);
                Map<String, Object> modelConfig = parseMap(modelConfigJson);
                MessageStats stats = messageStats.getOrDefault(sessionId, MessageStats.EMPTY);
                String name = rs.getString("name");
                records.add(new SessionUsageRecord(
                    sessionId,
                    userAgent.userId(),
                    userAgent.agentId(),
                    name,
                    normalizeSessionType(rs.getString("session_type"), rs.getString("schedule_id")),
                    rs.getString("working_dir"),
                    parseInstant(rs.getObject("created_at")),
                    parseInstant(rs.getObject("updated_at")),
                    longValue(rs.getObject("total_tokens")),
                    longValue(rs.getObject("input_tokens")),
                    longValue(rs.getObject("output_tokens")),
                    longValue(rs.getObject("accumulated_total_tokens")),
                    longValue(rs.getObject("accumulated_input_tokens")),
                    longValue(rs.getObject("accumulated_output_tokens")),
                    rs.getString("schedule_id"),
                    blankToUnknown(rs.getString("provider_name")),
                    extractModelName(modelConfig),
                    rs.getString("goose_mode"),
                    rs.getString("thread_id"),
                    stats.messageCount(),
                    stats.userMessageCount(),
                    stats.assistantMessageCount(),
                    stats.toolResponseCount(),
                    buildLabel(name, recipe, stats.firstUserText()),
                    modelConfig,
                    recipe
                ));
            }
        }
        return records;
    }

    private List<SessionMessageRecord> readMessages(Connection connection, UserAgent userAgent) {
        if (!tableExists(connection, "messages")) {
            return List.of();
        }

        List<SessionMessageRecord> records = new ArrayList<>();
        String sql = """
            select id, message_id, session_id, role, content_json, created_timestamp, timestamp, tokens, metadata_json
            from messages
            order by session_id asc, coalesce(created_timestamp, timestamp, 0) asc, id asc
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                String sessionId = rs.getString("session_id");
                if (sessionId == null || sessionId.isBlank()) {
                    continue;
                }
                String contentJson = rs.getString("content_json");
                MessageContentSummary content = summarizeContent(contentJson);
                MessageMetadata metadata = parseMetadata(rs.getString("metadata_json"));
                records.add(new SessionMessageRecord(
                    sessionId,
                    userAgent.userId(),
                    userAgent.agentId(),
                    blankToNull(rs.getString("message_id")),
                    longValue(rs.getObject("id")),
                    nullToEmpty(rs.getString("role")),
                    parseInstant(rs.getObject("created_timestamp")),
                    parseInstant(rs.getObject("timestamp")),
                    nullableLong(rs.getObject("tokens")),
                    content.contentLength(),
                    content.preview(),
                    content.text(),
                    content.truncated(),
                    content.toolRequest(),
                    content.toolResponse(),
                    content.toolName(),
                    content.error(),
                    metadata.userVisible(),
                    metadata.agentVisible()
                ));
            }
        } catch (SQLException ex) {
            log.warn("Failed to read FinOps message details", ex);
        }
        return records;
    }

    private boolean tableExists(Connection connection, String tableName) {
        try (PreparedStatement statement = connection.prepareStatement("select name from sqlite_master where type='table' and name=?")) {
            statement.setString(1, tableName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            log.debug("FinOps table lookup failed for {}", tableName, ex);
            return false;
        }
    }

    private Map<String, Object> parseMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JsonProcessingException ex) {
            log.debug("Ignoring invalid FinOps JSON map", ex);
            return Map.of();
        }
    }

    private String extractModelName(Map<String, Object> modelConfig) {
        for (String key : List.of("model_name", "modelName", "model")) {
            Object value = modelConfig.get(key);
            if (value != null && !value.toString().isBlank()) {
                return value.toString();
            }
        }
        return "unknown";
    }

    private String buildLabel(String name, Map<String, Object> recipe, String firstUserText) {
        if (name != null && !name.isBlank() && !"New Chat".equalsIgnoreCase(name.trim())) {
            return truncate(name.trim(), 120);
        }
        String recipeTitle = stringValue(recipe.get("title"));
        if (!recipeTitle.isBlank()) {
            String description = stringValue(recipe.get("description"));
            return truncate(description.isBlank() ? recipeTitle : recipeTitle + " - " + description, 120);
        }
        return truncate(firstUserText == null || firstUserText.isBlank() ? "Session" : firstUserText.trim(), 120);
    }

    private String extractFirstText(String contentJson) {
        if (contentJson == null || contentJson.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(contentJson);
            return findText(root);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private MessageContentSummary summarizeContent(String contentJson) {
        if (contentJson == null || contentJson.isBlank()) {
            return new MessageContentSummary(0, "", "", false, false, false, null, false);
        }
        try {
            JsonNode root = objectMapper.readTree(contentJson);
            StringBuilder text = new StringBuilder();
            ContentSignals signals = new ContentSignals();
            collectContent(root, text, signals);
            String normalized = normalizeWhitespace(text.isEmpty() ? contentJson : text.toString());
            detectTextualToolSignals(normalized, signals);
            int length = normalized.length();
            boolean truncated = length > MESSAGE_TEXT_LIMIT;
            String contentText = truncated ? normalized.substring(0, MESSAGE_TEXT_LIMIT) : normalized;
            return new MessageContentSummary(
                length,
                truncate(normalized, MESSAGE_PREVIEW_LIMIT),
                contentText,
                truncated,
                signals.toolRequest,
                signals.toolResponse,
                signals.toolName,
                signals.error
            );
        } catch (JsonProcessingException ex) {
            String normalized = normalizeWhitespace(contentJson);
            int length = normalized.length();
            boolean truncated = length > MESSAGE_TEXT_LIMIT;
            boolean toolRequest = containsToolRequest(normalized);
            boolean toolResponse = containsToolResponse(normalized);
            return new MessageContentSummary(
                length,
                truncate(normalized, MESSAGE_PREVIEW_LIMIT),
                truncated ? normalized.substring(0, MESSAGE_TEXT_LIMIT) : normalized,
                truncated,
                toolRequest,
                toolResponse,
                extractToolName(normalized),
                normalized.toLowerCase(Locale.ROOT).contains("error")
            );
        }
    }

    private void collectContent(JsonNode node, StringBuilder text, ContentSignals signals) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            String type = textValue(node.get("type"));
            if ("text".equals(type) || "thinking".equals(type)) {
                appendText(text, textValue(node.get("text")));
                appendText(text, textValue(node.get("thinking")));
                return;
            }
            if (node.has("toolRequest") || "tool_use".equals(type)) {
                signals.toolRequest = true;
                JsonNode tool = node.has("toolRequest") ? node.get("toolRequest") : node;
                signals.toolName = firstNonBlank(signals.toolName, textValue(tool.get("name")), textValue(tool.get("toolName")));
                appendText(text, summarizeJson(tool));
            }
            if (node.has("toolResponse") || "tool_result".equals(type)) {
                signals.toolResponse = true;
                JsonNode tool = node.has("toolResponse") ? node.get("toolResponse") : node;
                signals.toolName = firstNonBlank(signals.toolName, textValue(tool.get("name")), textValue(tool.get("toolName")));
                appendText(text, summarizeJson(tool));
            }
            if (node.has("error") || "error".equalsIgnoreCase(textValue(node.get("status")))) {
                signals.error = true;
            }
            for (JsonNode child : node) {
                collectContent(child, text, signals);
            }
            return;
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                collectContent(child, text, signals);
            }
            return;
        }
        if (node.isTextual()) {
            appendText(text, node.asText());
        }
    }

    private void detectTextualToolSignals(String value, ContentSignals signals) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (containsToolRequest(value)) {
            signals.toolRequest = true;
        }
        if (containsToolResponse(value)) {
            signals.toolResponse = true;
        }
        signals.toolName = firstNonBlank(signals.toolName, extractToolName(value));
        if (value.toLowerCase(Locale.ROOT).contains("error")) {
            signals.error = true;
        }
    }

    private boolean containsToolRequest(String value) {
        String text = value == null ? "" : value;
        return text.contains("toolRequest")
            || text.contains("tool_request")
            || text.contains("tool_use")
            || text.startsWith("toolRequest call_")
            || text.startsWith("tool_use call_");
    }

    private boolean containsToolResponse(String value) {
        String text = value == null ? "" : value;
        return text.contains("toolResponse")
            || text.contains("tool_response")
            || text.contains("tool_result")
            || text.startsWith("toolResponse call_")
            || text.startsWith("tool_result call_");
    }

    private String extractToolName(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        Matcher matcher = TOOL_NAME_PATTERN.matcher(value);
        return matcher.find() ? matcher.group(1) : null;
    }

    private MessageMetadata parseMetadata(String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return new MessageMetadata(true, true);
        }
        try {
            JsonNode root = objectMapper.readTree(metadataJson);
            return new MessageMetadata(
                booleanValue(root.get("userVisible"), true),
                booleanValue(root.get("agentVisible"), true)
            );
        } catch (JsonProcessingException ex) {
            return new MessageMetadata(true, true);
        }
    }

    private String findText(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isObject()) {
            JsonNode type = node.get("type");
            JsonNode text = node.get("text");
            if (type != null && "text".equals(type.asText()) && text != null && text.isTextual()) {
                return text.asText();
            }
            for (JsonNode child : node) {
                String result = findText(child);
                if (result != null && !result.isBlank()) {
                    return result;
                }
            }
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                String result = findText(child);
                if (result != null && !result.isBlank()) {
                    return result;
                }
            }
        }
        return null;
    }

    private String normalizeSessionType(String type, String scheduleId) {
        if (scheduleId != null && !scheduleId.isBlank()) {
            return "scheduled";
        }
        if (type == null || type.isBlank()) {
            return "manual";
        }
        return type.toLowerCase(Locale.ROOT);
    }

    private Instant parseInstant(Object value) {
        if (value == null) {
            return Instant.EPOCH;
        }
        if (value instanceof Number number) {
            long raw = number.longValue();
            return raw > 9_999_999_999L ? Instant.ofEpochMilli(raw) : Instant.ofEpochSecond(raw);
        }
        String text = value.toString();
        if (text.isBlank()) {
            return Instant.EPOCH;
        }
        Optional<Instant> parsedInstant = parseIsoInstant(text);
        if (parsedInstant.isPresent()) {
            return parsedInstant.get();
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            Optional<Instant> localDateTime = parseLocalDateTime(text, formatter);
            if (localDateTime.isPresent()) {
                return localDateTime.get();
            }
        }
        Optional<Long> epoch = parseLong(text);
        return epoch.map(raw -> raw > 9_999_999_999L ? Instant.ofEpochMilli(raw) : Instant.ofEpochSecond(raw))
            .orElse(Instant.EPOCH);
    }

    private Optional<Instant> parseIsoInstant(String text) {
        try {
            return Optional.of(Instant.parse(text));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private Optional<Instant> parseLocalDateTime(String text, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDateTime.parse(text, formatter).toInstant(ZoneOffset.UTC));
        } catch (DateTimeParseException ex) {
            return Optional.empty();
        }
    }

    private Optional<Long> parseLong(String text) {
        try {
            return Optional.of(Long.parseLong(text));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private long longValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String stringValue(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String blankToUnknown(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Long nullableLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean booleanValue(JsonNode node, boolean fallback) {
        return node == null || node.isNull() ? fallback : node.asBoolean(fallback);
    }

    private String textValue(JsonNode node) {
        return node == null || node.isNull() ? "" : node.asText("");
    }

    private void appendText(StringBuilder target, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (!target.isEmpty()) {
            target.append('\n');
        }
        target.append(value);
    }

    private String summarizeJson(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }
        String text = findText(node);
        if (text != null && !text.isBlank()) {
            return text;
        }
        return node.toString();
    }

    private String normalizeWhitespace(String value) {
        return value == null ? "" : value.replaceAll("[\\t\\r ]+", " ").replaceAll("\\n{3,}", "\n\n").trim();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String truncate(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }

    private record DbReadResult(List<SessionUsageRecord> sessions, List<SessionMessageRecord> messages) {
    }

    private record UserAgent(String userId, String agentId) {
    }

    private record MessageContentSummary(
        int contentLength,
        String preview,
        String text,
        boolean truncated,
        boolean toolRequest,
        boolean toolResponse,
        String toolName,
        boolean error
    ) {
    }

    private record MessageMetadata(boolean userVisible, boolean agentVisible) {
    }

    private static final class ContentSignals {
        private boolean toolRequest;
        private boolean toolResponse;
        private String toolName;
        private boolean error;
    }

    private record MessageStats(
        int messageCount,
        int userMessageCount,
        int assistantMessageCount,
        int toolResponseCount,
        String firstUserText
    ) {
        static final MessageStats EMPTY = new MessageStats(0, 0, 0, 0, null);
    }

    private static final class MutableMessageStats {
        private int messageCount;
        private int userMessageCount;
        private int assistantMessageCount;
        private int toolResponseCount;
        private String firstUserText;

        private MessageStats toRecord() {
            return new MessageStats(messageCount, userMessageCount, assistantMessageCount, toolResponseCount, firstUserText);
        }
    }
}
