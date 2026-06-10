/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service;

import com.huawei.opsfactory.gateway.config.GatewayProperties;

import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cluster type definitions including mode, command prefix, and environment variables.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Service
public class ClusterTypeService extends JsonFileEntityStore {

    private final GatewayProperties properties;

    private final SolutionTypeService solutionTypeService;

    /**
     * Creates the cluster type service instance.
     */
    public ClusterTypeService(GatewayProperties properties, SolutionTypeService solutionTypeService) {
        super("cluster-type");
        this.properties = properties;
        this.solutionTypeService = solutionTypeService;
    }

    /**
     * Initializes the cluster types data directory at startup.
     */
    @PostConstruct
    public void init() {
        initDataDir(properties.getGatewayRootPath().resolve("data"), "cluster-types");
    }

    // ── CRUD Operations ──────────────────────────────────────────────

    /**
     * Lists all cluster types.
     *
     * @return the result
     */
    public List<Map<String, Object>> listClusterTypes() {
        return listEntities();
    }

    /**
     * Gets a cluster type by its ID.
     *
     * @param id entity identifier
     * @return a cluster type by its ID
     */
    public Map<String, Object> getClusterType(String id) throws NotFoundException {
        Map<String, Object> ct = readFile(resolveEntityFile(id));
        if (ct == null) {
            throw new NotFoundException("Cluster type not found");
        }
        return ct;
    }

    /**
     * Creates a new cluster type from the provided field map.
     *
     * @param body request body
     * @return the result
     */
    public Map<String, Object> createClusterType(Map<String, Object> body) {
        String id = UUID.randomUUID().toString();
        String now = Instant.now().toString();

        Map<String, Object> ct = new LinkedHashMap<>();
        ct.put("id", id);
        ct.put("name", body.getOrDefault("name", ""));
        ct.put("code", body.getOrDefault("code", ""));
        ct.put("description", body.getOrDefault("description", ""));
        ct.put("color", body.getOrDefault("color", "#10b981"));
        ct.put("knowledge", body.getOrDefault("knowledge", ""));
        ct.put("commandPrefix", body.getOrDefault("commandPrefix", null));
        ct.put("envVariables", body.getOrDefault("envVariables", null));
        ct.put("mode", body.getOrDefault("mode", "peer"));
        ct.put("solutionType", solutionTypeService.validateSolutionTypeReference(
            body.getOrDefault("solutionType", "universal")));
        ct.put("createdAt", now);
        ct.put("updatedAt", now);

        writeEntityFile(id, ct);
        log.info("Created cluster type: id={}, name={}, code={}", id, ct.get("name"), ct.get("code"));
        return ct;
    }

    /**
     * Updates an existing cluster type with the provided field map.
     *
     * @param id an existing cluster type with the provided field map
     * @param body an existing cluster type with the provided field map
     * @return the result
     */
    public Map<String, Object> updateClusterType(String id, Map<String, Object> body)
            throws NotFoundException, BadRequestException {
        Map<String, Object> ct = readFile(resolveEntityFile(id));
        if (ct == null) {
            throw new NotFoundException("Cluster type not found");
        }

        if (body.containsKey("name")) {
            ct.put("name", body.get("name"));
        }
        if (body.containsKey("code")) {
            ct.put("code", body.get("code"));
        }
        if (body.containsKey("description")) {
            ct.put("description", body.get("description"));
        }
        if (body.containsKey("color")) {
            ct.put("color", body.get("color"));
        }
        if (body.containsKey("knowledge")) {
            ct.put("knowledge", body.get("knowledge"));
        }
        if (body.containsKey("commandPrefix")) {
            ct.put("commandPrefix", body.get("commandPrefix"));
        }
        if (body.containsKey("envVariables")) {
            ct.put("envVariables", body.get("envVariables"));
        }
        if (body.containsKey("mode")) {
            String mode = (String) body.get("mode");
            if (!"peer".equals(mode) && !"primary-backup".equals(mode)) {
                throw new BadRequestException("Invalid mode. Must be 'peer' or 'primary-backup'");
            }
            ct.put("mode", mode);
        }
        if (body.containsKey("solutionType")) {
            ct.put("solutionType", solutionTypeService.validateSolutionTypeReference(body.get("solutionType")));
        }

        ct.put("updatedAt", Instant.now().toString());
        writeEntityFile(id, ct);
        log.info("Updated cluster type: id={}", id);
        return ct;
    }

    /**
     * Deletes a cluster type by its ID.
     *
     * @param id entity identifier
     * @return the result
     */
    public boolean deleteClusterType(String id) {
        return deleteEntityFile(id);
    }

    // ── Validation ──────────────────────────────────────────────────
}
