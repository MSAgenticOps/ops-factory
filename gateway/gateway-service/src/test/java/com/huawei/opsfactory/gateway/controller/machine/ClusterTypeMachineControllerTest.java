/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.gateway.service.ClusterTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ClusterTypeMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class ClusterTypeMachineControllerTest {

    @Mock
    private ClusterTypeService clusterTypeService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        ClusterTypeMachineController controller = new ClusterTypeMachineController(clusterTypeService);

        assertNotNull(controller);
    }
}
