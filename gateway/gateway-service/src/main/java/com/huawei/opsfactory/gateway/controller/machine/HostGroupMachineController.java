/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseHostGroupController;
import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;
import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Machine-to-machine REST controller for host group CRUD operations.
 * <p>
 * Requires Basic authentication for all endpoints.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "hostGroupMachineController")
@RequestMapping("/machine/gateway/host-groups")
public class HostGroupMachineController extends BaseHostGroupController {

    /**
     * Creates the host group controller instance.
     *
     * @param hostGroupService the host group service
     * @param clusterService the cluster service
     * @param businessServiceService the business service service
     * @param hostService the host service
     */
    public HostGroupMachineController(HostGroupService hostGroupService, ClusterService clusterService,
        BusinessServiceService businessServiceService, HostService hostService) {
        super(hostGroupService, clusterService, businessServiceService, hostService);
    }

    /**
     * Lists host groups, optionally filtered by enabled status.
     *
     * @param enabledOnly enabled-only filter flag
     * @param request current HTTP request
     * @return a map with "groups" list
     */
    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listGroups(
        @RequestParam(value = "enabledOnly", required = false, defaultValue = "false") boolean enabledOnly,
        HttpServletRequest request) {
        return super.listGroups(enabledOnly, request);
    }

    /**
     * Returns the hierarchical tree of groups, clusters, and business services.
     *
     * @param enabledOnly returns the hierarchical tree of groups, clusters, and business services
     * @param request current HTTP request
     * @return the hierarchical tree of groups, clusters, and business services
     */
    @Override
    @GetMapping("/tree")
    @BasicAuth
    public Map<String, Object> getTree(
        @RequestParam(value = "enabledOnly", required = false, defaultValue = "false") boolean enabledOnly,
        HttpServletRequest request) {
        return super.getTree(enabledOnly, request);
    }

    /**
     * Gets a host group by ID.
     *
     * @param id entity identifier
     * @param request current HTTP request
     * @return a response entity with the host group, or 404 if not found
     * @throws NotFoundException if host group not found
     */
    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getGroup(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getGroup(id, request);
    }

    /**
     * Creates a new host group.
     *
     * @param request HTTP request body containing host group fields
     * @param httpRequest current HTTP request
     * @return response entity with created host group
     * @throws BadRequestException if validation fails
     * @throws ConflictException if host group already exists
     */
    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createGroup(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) throws BadRequestException, ConflictException {
        return super.createGroup(request, httpRequest);
    }

    /**
     * Updates a host group by ID.
     *
     * @param id host group identifier
     * @param request request body containing updated fields
     * @param httpRequest current HTTP request
     * @return response entity with updated host group
     * @throws NotFoundException if host group not found
     * @throws BadRequestException if validation fails
     * @throws ConflictException if update causes a conflict
     */
    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateGroup(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest)
        throws NotFoundException, BadRequestException, ConflictException {
        return super.updateGroup(id, request, httpRequest);
    }

    /**
     * Deletes a host group by ID, optionally forcing deletion of associated resources.
     *
     * @param id entity identifier
     * @param force whether to force the operation
     * @param request current HTTP request
     * @return response entity with success status or 404
     * @throws ConflictException if conflict during deletion
     */
    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable("id") String id,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force,
        HttpServletRequest request) throws ConflictException {
        return super.deleteGroup(id, force, request);
    }
}
