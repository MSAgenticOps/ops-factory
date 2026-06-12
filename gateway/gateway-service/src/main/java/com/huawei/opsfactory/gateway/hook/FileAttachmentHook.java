/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.hook;

import com.huawei.opsfactory.gateway.service.AgentConfigService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request hook that validates file paths referenced in user messages against the user's agent directory.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Component
@Order(2)
public class FileAttachmentHook implements RequestHook {
    private static final Logger log = LoggerFactory.getLogger(FileAttachmentHook.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final AgentConfigService agentConfigService;

    /**
     * Creates the file attachment hook instance.
     *
     * @param agentConfigService service for resolving user directory paths
     */
    public FileAttachmentHook(AgentConfigService agentConfigService) {
        this.agentConfigService = agentConfigService;
    }

    /**
     * Validates file paths referenced in user messages against the user's agent directory.
     *
     * @param ctx hook context containing the request body with user message
     * @return Mono emitting the unchanged context, or an error if a file path is invalid
     */
    @Override
    public Mono<HookContext> process(HookContext ctx) {
        try {
            JsonNode content = parseContentItems(ctx.getBody());
            if (content == null) {
                return Mono.just(ctx);
            }
            validateReferencedPaths(ctx, content);
            return Mono.just(ctx);
        } catch (ResponseStatusException e) {
            return Mono.error(e);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.error("Error in FileAttachmentHook", e);
            return Mono.just(ctx);
        }
    }

    private JsonNode parseContentItems(String body) throws JsonProcessingException {
        JsonNode userMessage = objectMapper.readTree(body).path("user_message");
        if (userMessage.isMissingNode()) {
            return null;
        }
        JsonNode content = userMessage.path("content");
        return content.isArray() ? content : null;
    }

    private void validateReferencedPaths(HookContext ctx, JsonNode content) {
        Path usersDir = agentConfigService.getUsersDir();
        Pattern pathPattern = buildUsersPathPattern(usersDir);
        Path userAgentsDir = buildUserAgentsDir(usersDir, ctx.getUserId());
        for (JsonNode item : content) {
            validateTextItemPaths(item, pathPattern, userAgentsDir);
        }
    }

    private Pattern buildUsersPathPattern(Path usersDir) {
        String usersDirStr = usersDir.toAbsolutePath().normalize().toString();
        return Pattern.compile(Pattern.quote(usersDirStr) + "[/\\\\][^\\s\"']+");
    }

    private Path buildUserAgentsDir(Path usersDir, String userId) {
        return usersDir.resolve(userId).resolve("agents").toAbsolutePath().normalize();
    }

    private void validateTextItemPaths(JsonNode item, Pattern pathPattern, Path userAgentsDir) {
        if (!"text".equals(item.path("type").asText())) {
            return;
        }
        for (String filePath : extractPaths(pathPattern, item.path("text").asText(""))) {
            validateReferencedPath(filePath, userAgentsDir);
        }
    }

    private void validateReferencedPath(String filePath, Path userAgentsDir) {
        Path resolved = Path.of(filePath).toAbsolutePath().normalize();
        if (!resolved.startsWith(userAgentsDir)) {
            log.warn("Path escapes user directory: {}", filePath);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: file path outside user directory");
        }
        if (Files.exists(resolved)) {
            return;
        }
        log.warn("Referenced file does not exist: {}", filePath);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Referenced file not found: " + resolved.getFileName());
    }

    private List<String> extractPaths(Pattern pattern, String text) {
        List<String> paths = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            paths.add(matcher.group());
        }
        return paths;
    }
}
