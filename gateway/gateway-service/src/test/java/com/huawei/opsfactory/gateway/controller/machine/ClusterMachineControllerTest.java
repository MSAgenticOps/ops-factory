/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link ClusterMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class ClusterMachineControllerTest {

    @Mock
    private ClusterService clusterService;

    @Mock
    private HostService hostService;

    @Mock
    private HostGroupService hostGroupService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        ClusterMachineController controller = new ClusterMachineController(
            clusterService, hostService, hostGroupService);

        assertNotNull(controller);
        assertEquals("/machine/gateway/clusters",
            controller.getClass().getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()[0]);
    }
}
