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
 * Machine-to-machine REST controller for SOP CRUD operations.
 * <p>
 * Requires Basic authentication for all endpoints.
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

    /**
     * Lists all SOP definitions.
     *
     * @param request the current HTTP request
     * @return a map containing the list of all SOP definitions under the {@code sops} key
     */
    @Override
    @GetMapping
    @BasicAuth
    public Map<String, Object> listSops(HttpServletRequest request) {
        return super.listSops(request);
    }

    /**
     * Gets an SOP by ID.
     *
     * @param id the unique identifier of the SOP to retrieve
     * @param request the current HTTP request
     * @return a response entity with the SOP details, or 404 if not found
     */
    @Override
    @GetMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> getSop(@PathVariable("id") String id, HttpServletRequest request) {
        return super.getSop(id, request);
    }

    /**
     * Creates a new SOP definition.
     *
     * @param request the SOP definition fields to create, provided as a JSON request body
     * @param httpRequest the current HTTP request
     * @return a response entity with the created SOP and 201 status,
     *         or 409 if a duplicate name already exists
     */
    @Override
    @PostMapping
    @BasicAuth
    public ResponseEntity<Map<String, Object>> createSop(@RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) {
        return super.createSop(request, httpRequest);
    }

    /**
     * Updates an SOP by ID.
     *
     * @param id the unique identifier of the SOP to update
     * @param request the SOP fields to modify, provided as a JSON request body
     * @param httpRequest the current HTTP request
     * @return a response entity with the updated SOP, 404 if not found,
     *         or 409 if the update causes a name conflict
     */
    @Override
    @PutMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> updateSop(@PathVariable("id") String id,
        @RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        return super.updateSop(id, request, httpRequest);
    }

    /**
     * Deletes an SOP by ID.
     *
     * @param id the unique identifier of the SOP to delete
     * @param request the current HTTP request
     * @return a response entity with a success flag, or 404 if the SOP does not exist
     */
    @Override
    @DeleteMapping("/{id}")
    @BasicAuth
    public ResponseEntity<Map<String, Object>> deleteSop(@PathVariable("id") String id, HttpServletRequest request) {
        return super.deleteSop(id, request);
    }
}
