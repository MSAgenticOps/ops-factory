/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseBusinessServiceController;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for CRUD operations on business service definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "businessServiceMachineController")
@RequestMapping("/machine/gateway/business-services")
@BasicAuth
public class BusinessServiceMachineController extends BaseBusinessServiceController {

    /**
     * Creates the business service controller instance.
     *
     * @param businessServiceService service handling business service CRUD operations
     */
    public BusinessServiceMachineController(BusinessServiceService businessServiceService) {
        super(businessServiceService);
    }
}
