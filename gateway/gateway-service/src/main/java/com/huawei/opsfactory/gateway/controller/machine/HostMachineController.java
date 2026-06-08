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
 * REST controller for CRUD operations and connectivity testing on host entries.
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

    @Override
    @GetMapping("/by-ip")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getHostByIp(@RequestParam("ip") String ip, HttpServletRequest request) {
        return super.getHostByIp(ip, request);
    }

    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getHost(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getHost(id, request);
    }

    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createHost(@RequestBody Map<String, Object> requestBody,
        HttpServletRequest request) throws ConflictException, BadRequestException {
        return super.createHost(requestBody, request);
    }

    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateHost(@PathVariable("id") String id,
        @RequestBody Map<String, Object> requestBody, HttpServletRequest request)
        throws NotFoundException, ConflictException, BadRequestException {
        return super.updateHost(id, requestBody, request);
    }

    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteHost(@PathVariable("id") String id, HttpServletRequest request) {
        return super.deleteHost(id, request);
    }

    @Override
    @GetMapping("/tags")
    @BasicAuth
    public Map<String, Object> getTags(HttpServletRequest request) {
        return super.getTags(request);
    }

    @Override
    @PostMapping("/{id}/test")
    @BasicAuth
    public Map<String, Object> testConnectivity(@PathVariable("id") String id, HttpServletRequest request) {
        return super.testConnectivity(id, request);
    }
}
