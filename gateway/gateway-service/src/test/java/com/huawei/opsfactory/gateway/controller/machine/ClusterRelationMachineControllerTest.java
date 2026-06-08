/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.gateway.service.ClusterRelationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ClusterRelationMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class ClusterRelationMachineControllerTest {

    @Mock
    private ClusterRelationService clusterRelationService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        ClusterRelationMachineController controller = new ClusterRelationMachineController(clusterRelationService);

        assertNotNull(controller);
    }
}
