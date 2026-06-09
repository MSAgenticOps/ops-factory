/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the {@code gateway.proactive-delivery} configuration so {@link ProactiveDeliveryService} takes a single
 * config object instead of loose {@code @Value} constructor parameters (keeps the constructor within the parameter
 * budget). The {@code poll-interval-ms} key stays an annotation SpEL string on {@code @Scheduled} and is not bound
 * here.
 *
 * @author x00000000
 * @since 2026-06-09
 */
@Component
@ConfigurationProperties(prefix = "gateway.proactive-delivery")
public class ProactiveDeliveryProperties {
    /** Whether proactive IM delivery is active. */
    private boolean enabled = true;

    /** Ignore scheduled runs older than this many minutes (avoids startup backfill of historical reports). */
    private long maxAgeMinutes = 60;

    /**
     * Returns whether proactive IM delivery is active.
     *
     * @return {@code true} if delivery is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether proactive IM delivery is active.
     *
     * @param enabled {@code true} to enable delivery
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the maximum age, in minutes, of a scheduled run still eligible for delivery.
     *
     * @return the max age in minutes
     */
    public long getMaxAgeMinutes() {
        return maxAgeMinutes;
    }

    /**
     * Sets the maximum age, in minutes, of a scheduled run still eligible for delivery.
     *
     * @param maxAgeMinutes the max age in minutes
     */
    public void setMaxAgeMinutes(long maxAgeMinutes) {
        this.maxAgeMinutes = maxAgeMinutes;
    }
}
