/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BusinessServiceMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class BusinessServiceMachineControllerTest {

    @Mock
    private BusinessServiceService businessServiceService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        BusinessServiceMachineController controller = new BusinessServiceMachineController(businessServiceService);

        assertNotNull(controller);
        assertEquals("/machine/gateway/business-services",
            controller.getClass().getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()[0]);
    }
}
