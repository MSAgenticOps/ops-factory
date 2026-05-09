/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for CRUD operations on business service definitions.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/gateway/business-services")
public class BusinessServiceController {
    private static final Logger log = LoggerFactory.getLogger(BusinessServiceController.class);

    private final BusinessServiceService businessServiceService;

    public BusinessServiceController(BusinessServiceService businessServiceService) {
        this.businessServiceService = businessServiceService;
    }

    /**
     * Lists business services, optionally filtered by group, host, or keyword.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping
    public Mono<Map<String, Object>> listBusinessServices(
            @RequestParam(value = "groupId", required = false) String groupId,
            @RequestParam(value = "hostId", required = false) String hostId,
            @RequestParam(value = "keyword", required = false) String keyword,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            List<Map<String, Object>> services;
            if (keyword != null && !keyword.isEmpty()) {
                services = businessServiceService.searchByKeyword(keyword);
            } else {
                services = businessServiceService.listBusinessServices(groupId, hostId);
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("businessServices", services);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Gets a business service by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getBusinessService(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> bs = businessServiceService.getBusinessService(id);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("businessService", bs);
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Gets a business service with its associated hosts resolved.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{id}/resolved")
    public Mono<ResponseEntity<Map<String, Object>>> getResolved(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> resolved = businessServiceService.getWithResolvedHosts(id);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("businessService", resolved);
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Lists hosts associated with a business service.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{id}/hosts")
    public Mono<Map<String, Object>> getHosts(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            List<Map<String, Object>> hosts = businessServiceService.getHostsForBusinessService(id);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("hosts", hosts);
            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Gets the topology data for a business service.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @GetMapping("/{id}/topology")
    public Mono<Map<String, Object>> getTopology(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> businessServiceService.getTopologyForBusinessService(id))
                .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Creates a new business service.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createBusinessService(
            @RequestBody Map<String, Object> request,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> bs = businessServiceService.createBusinessService(request);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("businessService", bs);
                return ResponseEntity.status(HttpStatus.CREATED).body(body);
            } catch (Exception e) {
                log.error("Failed to create business service", e);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Updates a business service by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> updateBusinessService(
            @PathVariable("id") String id,
            @RequestBody Map<String, Object> request,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> bs = businessServiceService.updateBusinessService(id, request);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                body.put("businessService", bs);
                return ResponseEntity.ok(body);
            } catch (IllegalArgumentException e) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            } catch (Exception e) {
                log.error("Failed to update business service {}", id, e);
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Deletes a business service by ID.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteBusinessService(
            @PathVariable("id") String id,
            ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> {
            boolean deleted = businessServiceService.deleteBusinessService(id);
            if (!deleted) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", false);
                body.put("error", "Business service not found: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
            }
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            return ResponseEntity.ok(body);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Migrates business data from the legacy business field to the business service table.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostMapping("/migrate")
    public Mono<Map<String, Object>> migrate(ServerWebExchange exchange) {
        UserContextFilter.requireAdmin(exchange);
        return Mono.fromCallable(() -> businessServiceService.migrateFromBusinessField())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
