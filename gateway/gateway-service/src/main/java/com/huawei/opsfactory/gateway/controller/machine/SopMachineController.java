/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseSopController;
import com.huawei.opsfactory.gateway.service.SopService;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for CRUD operations on SOP (Standard Operating Procedure) definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "sopMachineController")
@RequestMapping("/machine/gateway/sops")
public class SopMachineController extends BaseSopController {

    /**
     * Creates the sop controller instance.
     *
     * @param sopService service handling SOP persistence and business logic
     */
    public SopMachineController(SopService sopService) {
        super(sopService);
    }

    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listSops(HttpServletRequest request) {
        return super.listSops(request);
    }

    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getSop(@PathVariable("id") String id, HttpServletRequest request) {
        return super.getSop(id, request);
    }

    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createSop(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) {
        return super.createSop(request, httpRequest);
    }

    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateSop(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        return super.updateSop(id, request, httpRequest);
    }

    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteSop(@PathVariable("id") String id, HttpServletRequest request) {
        return super.deleteSop(id, request);
    }
}
