/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseSopController;
import com.huawei.opsfactory.gateway.service.SopService;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for CRUD operations on SOP (Standard Operating Procedure) definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "sopMachineController")
@RequestMapping("/machine/gateway/sops")
@BasicAuth
public class SopMachineController extends BaseSopController {

    /**
     * Creates the sop controller instance.
     *
     * @param sopService service handling SOP persistence and business logic
     */
    public SopMachineController(SopService sopService) {
        super(sopService);
    }
}
