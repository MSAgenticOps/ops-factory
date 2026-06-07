/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test coverage for {@link ChannelTargetKey}.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public class ChannelTargetKeyTest {
    @Test
    public void of_buildsExpectedKey() {
        assertEquals("im:wechat:wechat-main:account-1:conv-1:thread-1",
            ChannelTargetKey.of("wechat", "wechat-main", "account-1", "conv-1", "thread-1"));
    }

    @Test
    public void of_nullThreadId_collapsesToEmptySegment() {
        assertEquals("im:whatsapp:wa-1:default:conv-1:",
            ChannelTargetKey.of("whatsapp", "wa-1", "default", "conv-1", null));
    }
}
