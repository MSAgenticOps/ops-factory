/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.common.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Test coverage for User Role.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public class UserRoleTest {

    /**
     * Tests user role enum exists.
     */
    @Test
    public void testUserRoleExists() {
        assertNotNull(UserRole.USER);
    }
}
