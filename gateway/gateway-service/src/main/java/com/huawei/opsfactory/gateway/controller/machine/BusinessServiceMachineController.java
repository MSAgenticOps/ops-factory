/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseBusinessServiceController;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for CRUD operations on business service definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "businessServiceMachineController")
@RequestMapping("/machine/gateway/business-services")
public class BusinessServiceMachineController extends BaseBusinessServiceController {

    /**
     * Creates the business service controller instance.
     *
     * @param businessServiceService service handling business service CRUD operations
     */
    public BusinessServiceMachineController(BusinessServiceService businessServiceService) {
        super(businessServiceService);
    }

    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listBusinessServices(@RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "hostId", required = false) String hostId,
        @RequestParam(value = "keyword", required = false) String keyword, HttpServletRequest request) {
        return super.listBusinessServices(groupId, hostId, keyword, request);
    }

    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getBusinessService(@PathVariable("id") String id,
        HttpServletRequest request) throws NotFoundException {
        return super.getBusinessService(id, request);
    }

    @Override
    @GetMapping("/{id}/resolved")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getResolved(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getResolved(id, request);
    }

    @Override
    @GetMapping("/{id}/hosts")
    @BasicAuth
    public Map<String, Object> getHosts(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getHosts(id, request);
    }

    @Override
    @GetMapping("/{id}/topology")
    @BasicAuth
    public Map<String, Object> getTopology(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getTopology(id, request);
    }

    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createBusinessService(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) throws ConflictException {
        return super.createBusinessService(request, httpRequest);
    }

    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateBusinessService(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) throws NotFoundException, ConflictException {
        return super.updateBusinessService(id, request, httpRequest);
    }

    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteBusinessService(@PathVariable("id") String id,
        HttpServletRequest request) {
        return super.deleteBusinessService(id, request);
    }

    @Override
    @PostMapping("/migrate")
    @BasicAuth
    public Map<String, Object> migrate(HttpServletRequest request) {
        return super.migrate(request);
    }
}
