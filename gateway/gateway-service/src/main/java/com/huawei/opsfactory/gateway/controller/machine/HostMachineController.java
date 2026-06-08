/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseHostController;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;
import com.huawei.opsfactory.gateway.exception.BadRequestException;
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

import java.util.Map;

/**
 * Machine-to-machine REST controller for host CRUD operations and connectivity testing.
 * <p>
 * Requires Basic authentication for all endpoints.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "hostMachineController")
@RequestMapping("/machine/gateway/hosts")
public class HostMachineController extends BaseHostController {

    /**
     * Creates the host controller instance.
     *
     * @param hostService the host service
     * @param clusterService the cluster service
     * @param businessServiceService the business service service
     * @param hostGroupService the host group service
     */
    public HostMachineController(HostService hostService, ClusterService clusterService,
        BusinessServiceService businessServiceService, HostGroupService hostGroupService) {
        super(hostService, clusterService, businessServiceService, hostGroupService);
    }

    /**
     * Lists hosts, optionally filtered by tags, cluster, group, business service, or enabled status.
     *
     * @param tags tags
     * @param clusterId cluster identifier
     * @param groupId group identifier
     * @param businessServiceId business service id
     * @param enabledOnly enabled-only filter flag
     * @param request current HTTP request
     * @return a map with "hosts" list
     * @throws NotFoundException if referenced entity not found
     */
    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listHosts(@RequestParam(value = "tags", required = false) String tags,
        @RequestParam(value = "clusterId", required = false) String clusterId,
        @RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "businessServiceId", required = false) String businessServiceId,
        @RequestParam(value = "enabledOnly", required = false, defaultValue = "false") boolean enabledOnly,
        HttpServletRequest request) throws NotFoundException {
        return super.listHosts(tags, clusterId, groupId, businessServiceId, enabledOnly, request);
    }

    /**
     * Gets a host by its IP address.
     *
     * @param ip ip address
     * @param request current HTTP request
     * @return a response entity with the host, or 404 if not found
     */
    @Override
    @GetMapping("/by-ip")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getHostByIp(@RequestParam("ip") String ip, HttpServletRequest request) {
        return super.getHostByIp(ip, request);
    }

    /**
     * Gets a host by ID.
     *
     * @param id entity identifier
     * @param request current HTTP request
     * @return a response entity with the host, or 404 if not found
     * @throws NotFoundException if host not found
     */
    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getHost(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getHost(id, request);
    }

    /**
     * Creates a new host.
     *
     * @param requestBody HTTP request body containing host fields
     * @param request current HTTP request
     * @return response entity with created host
     * @throws ConflictException if host already exists
     * @throws BadRequestException if validation fails
     */
    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createHost(@RequestBody Map<String, Object> requestBody,
        HttpServletRequest request) throws ConflictException, BadRequestException {
        return super.createHost(requestBody, request);
    }

    /**
     * Updates a host by ID.
     *
     * @param id host identifier
     * @param requestBody request body containing updated fields
     * @param request current HTTP request
     * @return response entity with updated host, or 404 if not found
     * @throws NotFoundException if host not found
     * @throws ConflictException if update causes a conflict
     * @throws BadRequestException if validation fails
     */
    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateHost(@PathVariable("id") String id,
        @RequestBody Map<String, Object> requestBody, HttpServletRequest request)
        throws NotFoundException, ConflictException, BadRequestException {
        return super.updateHost(id, requestBody, request);
    }

    /**
     * Deletes a host by ID.
     *
     * @param id entity identifier
     * @param request current HTTP request
     * @return response entity with success status, or 404 if not found
     */
    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteHost(@PathVariable("id") String id, HttpServletRequest request) {
        return super.deleteHost(id, request);
    }

    /**
     * Returns all unique host tags.
     *
     * @param request current HTTP request
     * @return a map with "tags" list
     */
    @Override
    @GetMapping("/tags")
    @BasicAuth
    public Map<String, Object> getTags(HttpServletRequest request) {
        return super.getTags(request);
    }

    /**
     * Tests SSH connectivity to a host.
     *
     * @param id host identifier
     * @param request current HTTP request
     * @return a map with test results
     */
    @Override
    @PostMapping("/{id}/test")
    @BasicAuth
    public Map<String, Object> testConnectivity(@PathVariable("id") String id, HttpServletRequest request) {
        return super.testConnectivity(id, request);
    }
}
