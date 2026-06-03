/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.service.SolutionTypeService;

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
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for CRUD operations on solution type definitions.
 *
 * @author x00000000
 * @since 2026-05-30
 */
@RestController
@RestSchema(schemaId = "solutionTypeController")
@RequestMapping("/api/gateway/solution-types")
public class SolutionTypeController {
    private final SolutionTypeService solutionTypeService;

    /**
     * Creates the solution type controller instance.
     *
     * @param solutionTypeService service handling solution type CRUD operations
     */
    public SolutionTypeController(SolutionTypeService solutionTypeService) {
        this.solutionTypeService = solutionTypeService;
    }

    /**
     * Lists all solution type definitions.
     *
     * @param request current HTTP request
     * @return a map with "solutionTypes" list
     */
    @GetMapping
    public Map<String, Object> listSolutionTypes(HttpServletRequest request) {
        List<Map<String, Object>> types = solutionTypeService.listSolutionTypes();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("solutionTypes", types);
        return result;
    }

    /**
     * Gets a solution type by ID.
     *
     * @param id solution type identifier
     * @param request current HTTP request
     * @return ResponseEntity containing the solution type or 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSolutionType(@PathVariable("id") String id,
        HttpServletRequest request) {
        Map<String, Object> st = solutionTypeService.getSolutionType(id);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("solutionType", st);
        return ResponseEntity.ok(body);
    }

    /**
     * Creates a new solution type.
     *
     * @param request request body containing solution type fields
     * @param httpRequest current HTTP request
     * @return ResponseEntity with created solution type or 400
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSolutionType(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) {
        Map<String, Object> st = solutionTypeService.createSolutionType(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("solutionType", st);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Updates a solution type by ID.
     *
     * @param id solution type identifier
     * @param request request body containing updated fields
     * @param httpRequest current HTTP request
     * @return ResponseEntity with updated solution type or 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSolutionType(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        Map<String, Object> st = solutionTypeService.updateSolutionType(id, request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        body.put("solutionType", st);
        return ResponseEntity.ok(body);
    }

    /**
     * Deletes a solution type by ID.
     *
     * @param id solution type identifier
     * @param request current HTTP request
     * @return ResponseEntity with success status or 404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSolutionType(@PathVariable("id") String id,
        HttpServletRequest request) {
        boolean deleted = solutionTypeService.deleteSolutionType(id);
        if (!deleted) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", false);
            body.put("error", "Solution type not found: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        return ResponseEntity.ok(body);
    }
}
