/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.machine;

import com.huawei.opsfactory.common.aop.BasicAuth;
import com.huawei.opsfactory.gateway.controller.base.BaseClusterTypeController;
import com.huawei.opsfactory.gateway.service.ClusterTypeService;
import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

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
 * REST controller for CRUD operations on cluster type definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "clusterTypeMachineController")
@RequestMapping("/machine/gateway/cluster-types")
public class ClusterTypeMachineController extends BaseClusterTypeController {

    /**
     * Creates the cluster type machine controller instance.
     *
     * @param clusterTypeService service handling cluster type CRUD operations
     */
    public ClusterTypeMachineController(ClusterTypeService clusterTypeService) {
        super(clusterTypeService);
    }

    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listClusterTypes(HttpServletRequest request) {
        return super.listClusterTypes(request);
    }

    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getClusterType(@PathVariable("id") String id,
        HttpServletRequest request) throws NotFoundException {
        return super.getClusterType(id, request);
    }

    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createClusterType(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) {
        return super.createClusterType(request, httpRequest);
    }

    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateClusterType(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest)
        throws NotFoundException, BadRequestException {
        return super.updateClusterType(id, request, httpRequest);
    }

    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteClusterType(@PathVariable("id") String id,
        HttpServletRequest request) {
        return super.deleteClusterType(id, request);
    }
}
