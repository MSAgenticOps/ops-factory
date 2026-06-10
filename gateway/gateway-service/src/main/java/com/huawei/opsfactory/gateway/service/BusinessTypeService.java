/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages business type definitions persisted as JSON files under the gateway data directory.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class BusinessTypeService extends JsonFileEntityStore {

    private final GatewayProperties properties;

    /**
     * Creates the business type service instance.
     *
     * @param properties properties
     */
    public BusinessTypeService(GatewayProperties properties) {
        super("business-type");
        this.properties = properties;
    }

    /**
     * Initializes the business types data directory at startup.
     */
    @PostConstruct
    public void init() {
        initDataDir(properties.getGatewayRootPath().resolve("data"), "business-types");
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all business types.
     *
     * @return the result
     */
    public List<Map<String, Object>> listBusinessTypes() {
        return listEntities();
    }

    /**
     * Gets a business type by its ID.
     *
     * @param id entity identifier
     * @return a business type by its ID
     */
    public Map<String, Object> getBusinessType(String id) throws NotFoundException {
        Map<String, Object> bt = readFile(resolveEntityFile(id));
        if (bt == null) {
            throw new NotFoundException("Business type not found");
        }
        return bt;
    }

    /**
     * Creates a new business type from the provided field map.
     *
     * @param body request body
     * @return the result
     */
    public Map<String, Object> createBusinessType(Map<String, Object> body) {
        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> bt = new LinkedHashMap<>();
        bt.put("id", id);
        bt.put("name", body.getOrDefault("name", ""));
        bt.put("code", body.getOrDefault("code", ""));
        bt.put("description", body.getOrDefault("description", ""));
        bt.put("color", body.getOrDefault("color", "#6366f1"));
        bt.put("knowledge", body.getOrDefault("knowledge", ""));
        bt.put("createdAt", now);
        bt.put("updatedAt", now);

        writeEntityFile(id, bt);
        log.info("Created business type: id={}, name={}, code={}", id, bt.get("name"), bt.get("code"));
        return bt;
    }

    /**
     * Updates an existing business type with the provided field map.
     *
     * @param id an existing business type with the provided field map
     * @param body an existing business type with the provided field map
     * @return the result
     */
    public Map<String, Object> updateBusinessType(String id, Map<String, Object> body) throws NotFoundException {
        Map<String, Object> bt = readFile(resolveEntityFile(id));
        if (bt == null) {
            throw new NotFoundException("Business type not found");
        }

        if (body.containsKey("name")) {
            bt.put("name", body.get("name"));
        }
        if (body.containsKey("code")) {
            bt.put("code", body.get("code"));
        }
        if (body.containsKey("description")) {
            bt.put("description", body.get("description"));
        }
        if (body.containsKey("color")) {
            bt.put("color", body.get("color"));
        }
        if (body.containsKey("knowledge")) {
            bt.put("knowledge", body.get("knowledge"));
        }

        bt.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, bt);
        log.info("Updated business type: id={}", id);
        return bt;
    }

    /**
     * Deletes a business type by its ID.
     *
     * @param id entity identifier
     * @return the result
     */
    public boolean deleteBusinessType(String id) {
        return deleteEntityFile(id);
    }
}
