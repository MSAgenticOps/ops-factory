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
 * Machine-to-machine REST controller for business service CRUD operations.
 * <p>
 * Requires Basic authentication for all endpoints.
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

    /**
     * Lists business services, optionally filtered by group, host, or keyword.
     *
     * @param groupId optional group identifier filter
     * @param hostId optional host identifier filter
     * @param keyword optional keyword for full-text search
     * @param request current HTTP request
     * @return a map with "businessServices" list
     */
    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listBusinessServices(@RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "hostId", required = false) String hostId,
        @RequestParam(value = "keyword", required = false) String keyword, HttpServletRequest request) {
        return super.listBusinessServices(groupId, hostId, keyword, request);
    }

    /**
     * Gets a business service by ID.
     *
     * @param id business service identifier
     * @param request current HTTP request
     * @return ResponseEntity containing the business service or 404
     * @throws NotFoundException if business service not found
     */
    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getBusinessService(@PathVariable("id") String id,
        HttpServletRequest request) throws NotFoundException {
        return super.getBusinessService(id, request);
    }

    /**
     * Gets a business service with its associated hosts resolved.
     *
     * @param id business service identifier
     * @param request current HTTP request
     * @return ResponseEntity containing the resolved business service or 404
     * @throws NotFoundException if business service not found
     */
    @Override
    @GetMapping("/{id}/resolved")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getResolved(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getResolved(id, request);
    }

    /**
     * Lists hosts associated with a business service.
     *
     * @param id business service identifier
     * @param request current HTTP request
     * @return a map with "hosts" list
     * @throws NotFoundException if business service not found
     */
    @Override
    @GetMapping("/{id}/hosts")
    @BasicAuth
    public Map<String, Object> getHosts(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getHosts(id, request);
    }

    /**
     * Gets the topology data for a business service.
     *
     * @param id business service identifier
     * @param request current HTTP request
     * @return a map with topology data
     * @throws NotFoundException if business service not found
     */
    @Override
    @GetMapping("/{id}/topology")
    @BasicAuth
    public Map<String, Object> getTopology(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getTopology(id, request);
    }

    /**
     * Creates a new business service.
     *
     * @param request request body containing business service fields
     * @param httpRequest current HTTP request
     * @return ResponseEntity with created business service
     * @throws ConflictException if validation fails (e.g., duplicate name)
     */
    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createBusinessService(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) throws ConflictException {
        return super.createBusinessService(request, httpRequest);
    }

    /**
     * Updates a business service by ID.
     *
     * @param id business service identifier
     * @param request request body containing updated fields
     * @param httpRequest current HTTP request
     * @return ResponseEntity with updated business service
     * @throws NotFoundException if business service not found
     * @throws ConflictException if validation fails (e.g., duplicate name)
     */
    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateBusinessService(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) throws NotFoundException, ConflictException {
        return super.updateBusinessService(id, request, httpRequest);
    }

    /**
     * Deletes a business service by ID.
     *
     * @param id business service identifier
     * @param request current HTTP request
     * @return ResponseEntity with success status or 404
     */
    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteBusinessService(@PathVariable("id") String id,
        HttpServletRequest request) {
        return super.deleteBusinessService(id, request);
    }

    /**
     * Migrates business data from the legacy business field to the business service table.
     *
     * @param request current HTTP request
     * @return a map with migration result counts
     */
    @Override
    @PostMapping("/migrate")
    @BasicAuth
    public Map<String, Object> migrate(HttpServletRequest request) {
        return super.migrate(request);
    }
}
