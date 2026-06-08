/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.apache.servicecomb.provider.rest.common.RestSchema;

/**
 * Status Controller.
 *
 * @author x00000000
 * @since 2026-05-27
 */
@RestController("controlCenterStatusController")
@RestSchema(schemaId = "statusController")
@RequestMapping("/api/control-center")
public class StatusController {

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("status", "ok");
    }
}
