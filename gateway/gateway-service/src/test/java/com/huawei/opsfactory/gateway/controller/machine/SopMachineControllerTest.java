/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.huawei.opsfactory.gateway.service.SopService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link SopMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class SopMachineControllerTest {

    @Mock
    private SopService sopService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        SopMachineController controller = new SopMachineController(sopService);

        assertNotNull(controller);
    }
}
