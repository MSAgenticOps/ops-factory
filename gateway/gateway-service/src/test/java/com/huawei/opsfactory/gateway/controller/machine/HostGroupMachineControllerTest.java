/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HostGroupMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class HostGroupMachineControllerTest {

    @Mock
    private HostGroupService hostGroupService;

    @Mock
    private ClusterService clusterService;

    @Mock
    private BusinessServiceService businessServiceService;

    @Mock
    private HostService hostService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        HostGroupMachineController controller = new HostGroupMachineController(
            hostGroupService, clusterService, businessServiceService, hostService);

        assertNotNull(controller);
    }
}
