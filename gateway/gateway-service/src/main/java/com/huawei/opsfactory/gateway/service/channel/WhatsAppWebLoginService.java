/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.channel;

import com.huawei.opsfactory.gateway.service.channel.model.ChannelConnectionConfig;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelLoginState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages WhatsApp Web channel login lifecycle including QR code login, logout, and runtime state file management.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class WhatsAppWebLoginService {
    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebLoginService.class);

    private final ChannelConfigService channelConfigService;

    private final ChannelRuntimeStorageService runtimeStorageService;

    /**
     * Creates the whats app web login service instance.
     */
    public WhatsAppWebLoginService(ChannelConfigService channelConfigService,
        ChannelRuntimeStorageService runtimeStorageService) {
        this.channelConfigService = channelConfigService;
        this.runtimeStorageService = runtimeStorageService;
    }

    /**
     * Gets the current login state for a WhatsApp channel using the default owner user ID.
     *
     * @param channelId channel identifier
     * @return the current login state for a WhatsApp channel using the default owner user ID
     */
    public ChannelLoginState getLoginState(String channelId) {
        return getLoginState(channelId, "admin");
    }

    /**
     * Gets the current login state for a WhatsApp channel, merging configuration and runtime state.
     *
     * @param channelId channel identifier
     * @param ownerUserId owner user id
     * @return the current login state for a WhatsApp channel, merging configuration and runtime state
     */
    public ChannelLoginState getLoginState(String channelId, String ownerUserId) {
        ChannelDetail channel = requireChannel(channelId, ownerUserId);
        ChannelConnectionConfig config = channel.config();
        Map<String, Object> runtimeState = ChannelProcessHelper.readRuntimeState(loginStateFile(channel));
        String status = ChannelProcessHelper.normalizeStatus(config.loginStatus());
        if (runtimeState.get("status") instanceof String runtimeStatus && !runtimeStatus.isBlank()) {
            status = ChannelProcessHelper.normalizeStatus(runtimeStatus);
        }
        String message = switch (status) {
            case "connected":
                yield "WhatsApp Web session connected";
            case "pending":
                yield "Login pending. QR runtime will be attached next.";
            case "error":
                yield config.lastError() == null || config.lastError().isBlank() ? "WhatsApp Web connection error"
                    : config.lastError();
            default:
                yield "WhatsApp Web login required";
        };

        String stateMessage = ChannelProcessHelper.asString(runtimeState.get("message"));
        if (stateMessage != null && !stateMessage.isBlank()) {
            message = stateMessage;
        }
        String stateSelfPhone = ChannelProcessHelper.asString(runtimeState.get("selfPhone"));
        String stateConnectedAt = ChannelProcessHelper.asString(runtimeState.get("lastConnectedAt"));
        String stateDisconnectedAt = ChannelProcessHelper.asString(runtimeState.get("lastDisconnectedAt"));
        String stateError = ChannelProcessHelper.asString(runtimeState.get("lastError"));
        String stateQr = ChannelProcessHelper.asString(runtimeState.get("qrCodeDataUrl"));

        return new ChannelLoginState(channel.id(), status, message, config.authStateDir(),
            stateSelfPhone != null ? stateSelfPhone : config.selfPhone(),
            stateConnectedAt != null ? stateConnectedAt : config.lastConnectedAt(),
            stateDisconnectedAt != null ? stateDisconnectedAt : config.lastDisconnectedAt(),
            stateError != null ? stateError : config.lastError(), stateQr);
    }

    /**
     * Starts the WhatsApp Web QR login flow using the default owner user ID.
     *
     * @param channelId channel identifier
     * @return the starts the WhatsApp Web QR login flow using the default owner user ID
     */
    public ChannelLoginState startLogin(String channelId) {
        return startLogin(channelId, "admin");
    }

    /**
     * Starts the WhatsApp Web QR login flow, preparing the auth directory and launching the helper process.
     *
     * @param channelId channel identifier
     *        process
     * @param ownerUserId owner user id
     *        process
     * @return the starts the WhatsApp Web QR login flow, preparing the auth directory and launching the helper process
     */
    public ChannelLoginState startLogin(String channelId, String ownerUserId) {
        ChannelDetail channel = requireChannel(channelId, ownerUserId);
        Path authDir = runtimeStorageService.authDirectory(channel);
        Path stateFile = loginStateFile(channel);
        Path pidFile = pidFile(channel);
        Path logFile = logFile(channel);
        Path inbox = runtimeStorageService.inboxDirectory(channel);
        Path outboxPending = runtimeStorageService.outboxPendingDirectory(channel);
        Path outboxSent = runtimeStorageService.outboxSentDirectory(channel);
        Path outboxError = runtimeStorageService.outboxErrorDirectory(channel);
        ChannelProcessHelper.killIfRunning(pidFile);
        try {
            Files.createDirectories(authDir);
            Files.createDirectories(inbox);
            Files.createDirectories(outboxPending);
            Files.createDirectories(outboxSent);
            Files.createDirectories(outboxError);
            Files.createDirectories(logFile.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create WhatsApp auth directory", e);
        }

        writeInitialStateFile(channel, stateFile);
        startHelperProcess(channel, authDir, stateFile, pidFile, logFile, inbox, outboxPending, outboxSent,
            outboxError);
        channelConfigService.recordEvent(channelId, ownerUserId, "info", "whatsapp.login_requested",
            "WhatsApp Web login requested; auth directory prepared at " + authDir);

        return getLoginState(channelId, ownerUserId);
    }

    /**
     * Logs out of a WhatsApp channel using the default owner user ID.
     *
     * @param channelId logs out of a WhatsApp channel using the default owner user ID
     * @return the logs out of a WhatsApp channel using the default owner user ID
     */
    public ChannelLoginState logout(String channelId) {
        return logout(channelId, "admin");
    }

    /**
     * Logs out of a WhatsApp channel, stopping the helper process and clearing auth state.
     *
     * @param channelId logs out of a WhatsApp channel, stopping the helper process and clearing auth state
     * @param ownerUserId logs out of a WhatsApp channel, stopping the helper process and clearing auth state
     * @return the logs out of a WhatsApp channel, stopping the helper process and clearing auth state
     */
    public ChannelLoginState logout(String channelId, String ownerUserId) {
        ChannelDetail channel = requireChannel(channelId, ownerUserId);
        Path authDir = runtimeStorageService.authDirectory(channel);
        Path stateFile = loginStateFile(channel);
        Path pidFile = pidFile(channel);
        try {
            ChannelProcessHelper.killIfRunning(pidFile);
        } catch (IllegalStateException e) {
            log.debug("Failed to stop existing WhatsApp helper for {}", channelId, e);
        }
        try {
            ChannelProcessHelper.clearDirectory(authDir);
        } catch (IllegalStateException e) {
            log.debug("Failed to clear WhatsApp auth dir for {}", channelId, e);
        }
        ChannelProcessHelper.deleteQuietly(stateFile);

        writeDisconnectedStateFile(channel, stateFile);
        channelConfigService.recordEvent(channelId, ownerUserId, "info", "whatsapp.logged_out",
            "Cleared WhatsApp Web auth state");
        ChannelDetail updated = channelConfigService.getChannel(channelId, ownerUserId);

        return new ChannelLoginState(updated.id(), "disconnected", "WhatsApp Web login required",
            updated.config().authStateDir(), updated.config().selfPhone(), updated.config().lastConnectedAt(),
            updated.config().lastDisconnectedAt(), updated.config().lastError(), null);
    }

    private ChannelDetail requireChannel(String channelId, String ownerUserId) {
        ChannelDetail channel = channelConfigService.getChannel(channelId, ownerUserId);
        if (channel == null) {
            throw new IllegalArgumentException("Channel '" + channelId + "' not found");
        }
        if (!"whatsapp".equals(channel.type())) {
            throw new IllegalArgumentException("Channel '" + channelId + "' is not a WhatsApp channel");
        }
        return channel;
    }

    private Path loginStateFile(ChannelDetail channel) {
        return runtimeStorageService.loginStateFile(channel);
    }

    private Path pidFile(ChannelDetail channel) {
        return runtimeStorageService.pidFile(channel);
    }

    private Path logFile(ChannelDetail channel) {
        return runtimeStorageService.logFile(channel);
    }

    private void writeInitialStateFile(ChannelDetail channel, Path stateFile) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("channelId", channel.id());
        payload.put("status", "pending");
        payload.put("message", "Initializing WhatsApp Web helper...");
        payload.put("authStateDir", channel.config().authStateDir());
        payload.put("selfPhone", channel.config().selfPhone());
        payload.put("lastConnectedAt", channel.config().lastConnectedAt());
        payload.put("lastDisconnectedAt", channel.config().lastDisconnectedAt());
        payload.put("lastError", "");
        payload.put("qrCodeDataUrl", null);
        try {
            String json = ChannelProcessHelper.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            Files.writeString(stateFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write WhatsApp login state file", e);
        }
    }

    private void writeDisconnectedStateFile(ChannelDetail channel, Path stateFile) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("channelId", channel.id());
        payload.put("status", "disconnected");
        payload.put("message", "WhatsApp Web login required");
        payload.put("authStateDir", channel.config().authStateDir());
        payload.put("selfPhone", "");
        payload.put("lastConnectedAt", channel.config().lastConnectedAt());
        payload.put("lastDisconnectedAt", Instant.now().toString());
        payload.put("lastError", "");
        payload.put("qrCodeDataUrl", null);
        try {
            Files.createDirectories(stateFile.getParent());
            String json = ChannelProcessHelper.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            Files.writeString(stateFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write WhatsApp login state file", e);
        }
    }

    private void startHelperProcess(ChannelDetail channel, Path authDir, Path stateFile, Path pidFile, Path logFile,
        Path inboxDir, Path outboxPendingDir, Path outboxSentDir, Path outboxErrorDir) {
        Path helperDir = channelConfigService.getGatewayRoot().resolve("tools").resolve("whatsapp-web-helper");
        Path helperEntry = helperDir.resolve("index.js");
        if (!Files.exists(helperEntry)) {
            throw new IllegalStateException("WhatsApp Web helper not found: " + helperEntry);
        }

        List<String> command = new ArrayList<>();
        command.add("node");
        command.add(helperEntry.toString());
        command.add("--command");
        command.add("login");
        command.add("--channel-id");
        command.add(channel.id());
        command.add("--state-file");
        command.add(stateFile.toString());
        command.add("--pid-file");
        command.add(pidFile.toString());
        command.add("--auth-dir");
        command.add(authDir.toString());
        command.add("--inbox-dir");
        command.add(inboxDir.toString());
        command.add("--outbox-pending-dir");
        command.add(outboxPendingDir.toString());
        command.add("--outbox-sent-dir");
        command.add(outboxSentDir.toString());
        command.add("--outbox-error-dir");
        command.add(outboxErrorDir.toString());

        if (channel.config().selfPhone() != null && !channel.config().selfPhone().isBlank()) {
            command.add("--self-phone");
            command.add(channel.config().selfPhone());
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(helperDir.toFile());
        builder.redirectErrorStream(true);
        builder.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile.toFile()));
        try {
            builder.start();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start WhatsApp Web helper", e);
        }
    }
}
