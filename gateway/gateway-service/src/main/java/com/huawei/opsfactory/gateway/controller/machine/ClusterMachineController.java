/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseClusterController;
import com.huawei.opsfactory.gateway.service.ClusterService;
import com.huawei.opsfactory.gateway.service.HostGroupService;
import com.huawei.opsfactory.gateway.service.HostService;
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
 * REST controller for CRUD operations on cluster definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "clusterMachineController")
@RequestMapping("/machine/gateway/clusters")
public class ClusterMachineController extends BaseClusterController {

    /**
     * Creates the cluster controller instance.
     *
     * @param clusterService the cluster service
     * @param hostService the host service
     * @param hostGroupService the host group service
     */
    public ClusterMachineController(ClusterService clusterService, HostService hostService,
        HostGroupService hostGroupService) {
        super(clusterService, hostService, hostGroupService);
    }

    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listClusters(@RequestParam(value = "groupId", required = false) String groupId,
        @RequestParam(value = "type", required = false) String type,
        @RequestParam(value = "enabledOnly", required = false, defaultValue = "false") boolean enabledOnly,
        HttpServletRequest request) {
        return super.listClusters(groupId, type, enabledOnly, request);
    }

    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getCluster(@PathVariable("id") String id, HttpServletRequest request)
        throws NotFoundException {
        return super.getCluster(id, request);
    }

    @Override
    @GetMapping("/types")
    @BasicAuth
    public Map<String, Object> getClusterTypes(HttpServletRequest request) {
        return super.getClusterTypes(request);
    }

    @Override
    @GetMapping("/{id}/hosts")
    @BasicAuth
    public Map<String, Object> getClusterHosts(@PathVariable("id") String id, HttpServletRequest request) {
        return super.getClusterHosts(id, request);
    }

    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createCluster(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) throws ConflictException {
        return super.createCluster(request, httpRequest);
    }

    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateCluster(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) throws NotFoundException, ConflictException {
        return super.updateCluster(id, request, httpRequest);
    }

    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteCluster(@PathVariable("id") String id,
        @RequestParam(value = "force", required = false, defaultValue = "false") boolean force,
        HttpServletRequest request) throws ConflictException {
        return super.deleteCluster(id, force, request);
    }
}
