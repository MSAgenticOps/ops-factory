/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.service.channel.ChannelAdapterRegistry;
import com.huawei.opsfactory.gateway.service.channel.ChannelConfigService;
import com.huawei.opsfactory.gateway.service.channel.WeChatLoginService;
import com.huawei.opsfactory.gateway.service.channel.WhatsAppMessagePumpService;
import com.huawei.opsfactory.gateway.service.channel.WhatsAppWebLoginService;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelDetail;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelLoginState;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelSelfTestRequest;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelSelfTestResult;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelUpsertRequest;
import com.huawei.opsfactory.gateway.service.channel.model.ChannelVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Admin REST controller for managing external channel configurations and login lifecycle.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/gateway/channels")
public class ChannelAdminController {
    private static final Logger log = LoggerFactory.getLogger(ChannelAdminController.class);

    private final ChannelConfigService channelConfigService;
    private final ChannelAdapterRegistry channelAdapterRegistry;
    private final WhatsAppWebLoginService whatsAppWebLoginService;
    private final WhatsAppMessagePumpService whatsAppMessagePumpService;
    private final WeChatLoginService weChatLoginService;

    /**
     * Creates the channel admin controller instance.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public ChannelAdminController(ChannelConfigService channelConfigService,
                                  ChannelAdapterRegistry channelAdapterRegistry,
                                  WhatsAppWebLoginService whatsAppWebLoginService,
                                  WhatsAppMessagePumpService whatsAppMessagePumpService,
                                  WeChatLoginService weChatLoginService) {
        this.channelConfigService = channelConfigService;
        this.channelAdapterRegistry = channelAdapterRegistry;
        this.whatsAppWebLoginService = whatsAppWebLoginService;
        this.whatsAppMessagePumpService = whatsAppMessagePumpService;
        this.weChatLoginService = weChatLoginService;
    }

    /**
     * Lists all channels for the current admin user.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping
    public Mono<Map<String, Object>> listChannels(ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> Map.<String, Object>of("channels", channelConfigService.listChannels(userId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Gets a channel by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{channelId}")
    public Mono<ResponseEntity<ChannelDetail>> getChannel(@PathVariable String channelId, ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            ChannelDetail detail = channelConfigService.getChannel(channelId, userId);
            if (detail == null) {
                return ResponseEntity.notFound().<ChannelDetail>build();
            }
            return ResponseEntity.ok(detail);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Creates a new channel.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createChannel(@RequestBody ChannelUpsertRequest request,
                                                                   ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String ownerUserId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.createChannel(
                        request,
                        ownerUserId != null ? ownerUserId : "admin"
                );
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.<String, Object>of("success", true, "channel", detail));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Updates a channel by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PutMapping("/{channelId}")
    public Mono<ResponseEntity<Map<String, Object>>> updateChannel(@PathVariable String channelId,
                                                                   @RequestBody ChannelUpsertRequest request,
                                                                   ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.updateChannel(channelId, request, userId);
                return ResponseEntity.ok(Map.<String, Object>of("success", true, "channel", detail));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Enables a channel by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/enable")
    public Mono<ResponseEntity<Map<String, Object>>> enableChannel(@PathVariable String channelId,
                                                                   ServerWebExchange exchange) {
        return setEnabled(channelId, true, exchange);
    }

    /**
     * Disables a channel by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/disable")
    public Mono<ResponseEntity<Map<String, Object>>> disableChannel(@PathVariable String channelId,
                                                                    ServerWebExchange exchange) {
        return setEnabled(channelId, false, exchange);
    }

    /**
     * Deletes a channel by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @DeleteMapping("/{channelId}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteChannel(@PathVariable String channelId,
                                                                   ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                channelConfigService.deleteChannel(channelId);
                return ResponseEntity.ok(Map.<String, Object>of("success", true));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Lists all bindings for a channel.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{channelId}/bindings")
    public Mono<ResponseEntity<Map<String, Object>>> listBindings(@PathVariable String channelId,
                                                                  ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                return ResponseEntity.ok(Map.<String, Object>of(
                        "bindings", channelConfigService.listBindings(channelId, userId)));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Lists all events for a channel.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{channelId}/events")
    public Mono<ResponseEntity<Map<String, Object>>> listEvents(@PathVariable String channelId,
                                                                ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                return ResponseEntity.ok(Map.<String, Object>of(
                        "events", channelConfigService.listEvents(channelId, userId)));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Verifies a channel configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/verify")
    public Mono<ResponseEntity<Map<String, Object>>> verifyChannel(@PathVariable String channelId,
                                                                   ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelVerificationResult result = channelConfigService.verifyChannel(channelId, userId);
                return ResponseEntity.ok(Map.<String, Object>of(
                        "success", result.ok(),
                        "verification", result));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Probes a channel for connectivity status.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/probe")
    public Mono<ResponseEntity<Map<String, Object>>> probeChannel(@PathVariable String channelId,
                                                                  ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> channelConfigService.getChannel(channelId, userId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(detail -> {
                    if (detail == null) {
                        return Mono.just(
                                ResponseEntity.badRequest()
                                        .body(errorBody("Channel '" + channelId + "' not found"))
                        );
                    }
                    return channelAdapterRegistry.require(detail.type()).testConnectivity(channelId, userId)
                            .map(result -> ResponseEntity.ok(Map.<String, Object>of(
                                    "success", result.ok(),
                                    "connectivity", result
                            )));
                });
    }

    /**
     * Gets the current login state for a channel.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{channelId}/login-state")
    public Mono<ResponseEntity<Map<String, Object>>> getLoginState(@PathVariable String channelId,
                                                                   ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.getChannel(channelId, userId);
                if (detail == null) {
                    return ResponseEntity.badRequest().body(errorBody("Channel '" + channelId + "' not found"));
                }
                ChannelLoginState state = switch (detail.type()) {
                    case "wechat" -> weChatLoginService.getLoginState(channelId, userId);
                    case "whatsapp" -> whatsAppWebLoginService.getLoginState(channelId, userId);
                    default -> throw new IllegalArgumentException(detail.type() + " login is not implemented yet");
                };
                return ResponseEntity.ok(Map.<String, Object>of("state", state));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Starts the login process for a channel.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/login")
    public Mono<ResponseEntity<Map<String, Object>>> startLogin(@PathVariable String channelId,
                                                                ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.getChannel(channelId, userId);
                if (detail == null) {
                    return ResponseEntity.badRequest().body(errorBody("Channel '" + channelId + "' not found"));
                }
                ChannelLoginState state = switch (detail.type()) {
                    case "wechat" -> weChatLoginService.startLogin(channelId, userId);
                    case "whatsapp" -> whatsAppWebLoginService.startLogin(channelId, userId);
                    default -> throw new IllegalArgumentException(detail.type() + " login is not implemented yet");
                };
                return ResponseEntity.ok(Map.<String, Object>of("success", true, "state", state));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Logs out from a channel and resets its runtime state.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/logout")
    public Mono<ResponseEntity<Map<String, Object>>> logout(@PathVariable String channelId,
                                                            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.getChannel(channelId, userId);
                if (detail == null) {
                    return ResponseEntity.badRequest().body(errorBody("Channel '" + channelId + "' not found"));
                }
                if ("wechat".equals(detail.type())) {
                    weChatLoginService.logout(channelId, userId);
                } else if ("whatsapp".equals(detail.type())) {
                    whatsAppWebLoginService.logout(channelId, userId);
                } else {
                    return ResponseEntity.badRequest().body(errorBody(detail.type() + " login is not implemented yet"));
                }
                detail = channelConfigService.resetChannelRuntimeState(channelId, userId);
                String disconnectedMessage = "wechat".equals(detail.type())
                        ? "WeChat login required"
                        : "WhatsApp Web login required";
                ChannelLoginState state = new ChannelLoginState(
                        detail.id(),
                        "disconnected",
                        disconnectedMessage,
                        detail.config().authStateDir(),
                        "wechat".equals(detail.type()) ? detail.config().wechatId() : detail.config().selfPhone(),
                        detail.config().lastConnectedAt(),
                        detail.config().lastDisconnectedAt(),
                        detail.config().lastError(),
                        null
                );
                return ResponseEntity.ok(Map.<String, Object>of("success", true, "state", state));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            } catch (IllegalStateException e) {
                log.error("Failed to logout channel {}", channelId, e);
                ChannelDetail detail = null;
                try {
                    detail = channelConfigService.getChannel(channelId, userId);
                    if (detail != null) {
                        detail = channelConfigService.resetChannelRuntimeState(channelId, userId);
                    }
                } catch (IllegalArgumentException resetError) {
                    log.warn("Failed to reset runtime state for channel {} after logout error: {}",
                            channelId, resetError.getMessage());
                }
                if (detail == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(errorBody("Failed to clear channel login state"));
                }
                String disconnectedMessage = "wechat".equals(detail.type())
                        ? "WeChat login required"
                        : "WhatsApp Web login required";
                ChannelLoginState fallbackState = new ChannelLoginState(
                        detail.id(),
                        "disconnected",
                        disconnectedMessage,
                        detail.config().authStateDir(),
                        "wechat".equals(detail.type()) ? detail.config().wechatId() : "",
                        detail.config().lastConnectedAt(),
                        detail.config().lastDisconnectedAt(),
                        "",
                        null
                );
                return ResponseEntity.ok(Map.<String, Object>of("success", true, "state", fallbackState));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Runs a self-test on a channel to verify end-to-end messaging.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/{channelId}/self-test")
    public Mono<ResponseEntity<Map<String, Object>>> runSelfTest(@PathVariable String channelId,
                                                                 @RequestBody ChannelSelfTestRequest request,
                                                                 ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.getChannel(channelId, userId);
                if (detail == null) {
                    return ResponseEntity.badRequest().body(errorBody("Channel '" + channelId + "' not found"));
                }
                if ("wechat".equals(detail.type())) {
                    return ResponseEntity.badRequest().body(errorBody("wechat self-test is not implemented yet"));
                }
                if (!"whatsapp".equals(detail.type())) {
                    return ResponseEntity.badRequest()
                            .body(errorBody(detail.type() + " self-test is not implemented yet"));
                }
                ChannelSelfTestResult result = whatsAppMessagePumpService.runSelfTest(
                        channelId,
                        userId,
                        request.text()
                );
                return ResponseEntity.ok(Map.<String, Object>of("success", true, "result", result));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            } catch (IllegalStateException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<ResponseEntity<Map<String, Object>>> setEnabled(String channelId,
                                                                 boolean enabled,
                                                                 ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        String userId = currentUserId(exchange);
        return Mono.fromCallable(() -> {
            try {
                ChannelDetail detail = channelConfigService.setEnabled(channelId, enabled, userId);
                return ResponseEntity.ok(Map.<String, Object>of("success", true, "channel", detail));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Map<String, Object> errorBody(String error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("error", error);
        return body;
    }

    private String currentUserId(ServerWebExchange exchange) {
        String userId = exchange.getAttribute(UserContextFilter.USER_ID_ATTR);
        return userId == null || userId.isBlank() ? "admin" : userId;
    }
}
