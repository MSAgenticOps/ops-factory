/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.callchainsubgraph.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base fields shared by subgraph result and history items.
 *
 * @author x00000000
 * @since 2026-06-08
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallChainSubgraphBase {
    private String subgraphId;

    private String menuId;

    private String envCode;

    private String solutionType;

    private String solutionId;

    private String ontologyId;

    private String generatedAt;

    private String expiresAt;

    private Map<String, Object> summary = new LinkedHashMap<>();

    public String getSubgraphId() {
        return subgraphId;
    }

    public void setSubgraphId(String subgraphId) {
        this.subgraphId = subgraphId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getSolutionType() {
        return solutionType;
    }

    public void setSolutionType(String solutionType) {
        this.solutionType = solutionType;
    }

    public String getSolutionId() {
        return solutionId;
    }

    public void setSolutionId(String solutionId) {
        this.solutionId = solutionId;
    }

    public String getOntologyId() {
        return ontologyId;
    }

    public void setOntologyId(String ontologyId) {
        this.ontologyId = ontologyId;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Map<String, Object> getSummary() {
        return new LinkedHashMap<>(summary);
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary == null ? new LinkedHashMap<>() : new LinkedHashMap<>(summary);
    }
}
