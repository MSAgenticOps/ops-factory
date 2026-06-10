/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.channel;

import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelReplyResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Scheduled service that polls the WeChat inbox directory, deduplicates inbound messages, and dispatches them to agent
 * sessions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class WeChatMessagePumpService {
    private static final Logger log = LoggerFactory.getLogger(WeChatMessagePumpService.class);

    private final ChannelConfigService channelConfigService;

    private final ChannelRuntimeStorageService runtimeStorageService;

    private final ChannelDedupService channelDedupService;

    private final SessionBridgeService sessionBridgeService;

    /**
     * Creates the we chat message pump service instance.
     */
    public WeChatMessagePumpService(ChannelConfigService channelConfigService,
        ChannelRuntimeStorageService runtimeStorageService, ChannelDedupService channelDedupService,
        SessionBridgeService sessionBridgeService) {
        this.channelConfigService = channelConfigService;
        this.runtimeStorageService = runtimeStorageService;
        this.channelDedupService = channelDedupService;
        this.sessionBridgeService = sessionBridgeService;
    }

    /**
     * Periodically polls the WeChat inbox directory for new messages and processes them.
     */
    @Scheduled(fixedDelay = 2000)
    public void pumpInbox() {
        for (ChannelDetail detail : channelConfigService.listRuntimeChannels("wechat")) {
            if (!detail.enabled()) {
                continue;
            }
            processChannel(detail);
        }
    }

    private void processChannel(ChannelDetail detail) {
        Path inboxDir = inboxDir(detail);
        if (!Files.isDirectory(inboxDir)) {
            return;
        }

        try (var stream = Files.list(inboxDir)) {
            stream.filter(path -> path.getFileName().toString().endsWith(".json"))
                .sorted()
                .forEach(path -> processInboundFile(detail, path));
        } catch (IOException e) {
            log.warn("Failed to scan WeChat inbox for {}: {}", detail.id(), e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void processInboundFile(ChannelDetail channel, Path file) {
        Map<String, Object> payload;
        try {
            payload = ChannelProcessHelper.mapper().readValue(Files.readString(file, StandardCharsets.UTF_8), Map.class);
        } catch (IOException e) {
            channelConfigService.recordEvent(channel.id(), channel.ownerUserId(), "warning", "wechat.inbox_invalid",
                "Failed to parse inbound WeChat file " + file.getFileName());
            ChannelProcessHelper.moveToProcessed(processedInboxDir(channel), file, "invalid");
            return;
        }

        String messageId = ChannelProcessHelper.asString(payload.get("messageId"));
        String peerId = ChannelProcessHelper.asString(payload.get("peerId"));
        String conversationId = ChannelProcessHelper.asString(payload.get("conversationId"));
        String text = ChannelProcessHelper.asString(payload.get("text"));
        String contextToken = ChannelProcessHelper.asString(payload.get("contextToken"));
        if (messageId == null || peerId == null || conversationId == null || text == null || text.isBlank()) {
            channelConfigService.recordEvent(channel.id(), channel.ownerUserId(), "warning", "wechat.inbox_invalid",
                "Inbound WeChat file missing required fields");
            ChannelProcessHelper.moveToProcessed(processedInboxDir(channel), file, "invalid");
            return;
        }

        if (!channelDedupService.markIfNew(channel.id(), channel.ownerUserId(), messageId)) {
            ChannelProcessHelper.moveToProcessed(processedInboxDir(channel), file, "duplicate");
            return;
        }

        try {
            ChannelReplyResult reply = sessionBridgeService
                .sendConversationText(channel.id(), channel.ownerUserId(), "default", peerId, conversationId, null,
                    "direct", text)
                .block(Duration.ofMinutes(5));

            if (reply != null && reply.replyText() != null && !reply.replyText().isBlank()) {
                writeOutboxCommand(channel, peerId, reply.replyText(), contextToken);
            }
            ChannelProcessHelper.moveToProcessed(processedInboxDir(channel), file, "processed");
        } catch (IllegalArgumentException | IllegalStateException e) {
            channelConfigService.recordEvent(channel.id(), channel.ownerUserId(), "warning", "wechat.inbox_failed",
                "Failed to process inbound WeChat message: " + e.getMessage());
            ChannelProcessHelper.moveToProcessed(processedInboxDir(channel), file, "error");
        }
    }

    private void writeOutboxCommand(ChannelDetail channel, String peerId, String text, String contextToken) {
        Path pendingDir = outboxPendingDir(channel);
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", UUID.randomUUID().toString());
        payload.put("to", peerId);
        payload.put("text", text);
        payload.put("contextToken", contextToken);
        payload.put("createdAt", Instant.now().toString());
        Path file = pendingDir.resolve(payload.get("id") + ".json");
        try {
            Files.createDirectories(pendingDir);
            Files.writeString(file, ChannelProcessHelper.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(payload),
                StandardCharsets.UTF_8);
            channelConfigService.recordEvent(channel.id(), channel.ownerUserId(), "info", "wechat.outbox_enqueued",
                "Queued WeChat reply for " + peerId);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write WeChat outbox command", e);
        }
    }

    private Path inboxDir(ChannelDetail channel) {
        return runtimeStorageService.inboxDirectory(channel);
    }

    private Path processedInboxDir(ChannelDetail channel) {
        return runtimeStorageService.processedInboxDirectory(channel);
    }

    private Path outboxPendingDir(ChannelDetail channel) {
        return runtimeStorageService.outboxPendingDirectory(channel);
    }

}
