package com.huawei.opsfactory.gateway.service.channel.model;

public record WhatsAppChannelConfig(
        String loginStatus,
        String sessionLabel,
        String selfPhone,
        String authStateDir,
        String lastConnectedAt,
        String lastDisconnectedAt,
        String lastError
) {
}
