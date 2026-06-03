/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.exception.BadRequestException;
import com.huawei.opsfactory.gateway.exception.ConflictException;
import com.huawei.opsfactory.gateway.exception.NotFoundException;
import com.huawei.opsfactory.gateway.service.AgentSkillInstallService;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for installing and uninstalling skills on agent instances.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RestController
@RestSchema(schemaId = "agentSkillController")
@RequestMapping("/api/gateway/agents")
public class AgentSkillController {
    private final AgentSkillInstallService installService;

    /**
     * Creates the agent skill controller instance.
     *
     * @param installService service handling skill install/uninstall operations
     */
    public AgentSkillController(AgentSkillInstallService installService) {
        this.installService = installService;
    }

    /**
     * Installs a skill on the specified agent instance.
     *
     * @param agentId agent instance identifier
     * @param body request body containing "skillId"
     * @param request current HTTP request
     * @return ResponseEntity with installation result
     */
    @PostMapping("/{agentId}/skills/install")
    public ResponseEntity<Map<String, Object>> installSkill(@PathVariable("agentId") String agentId,
        @RequestBody Map<String, String> body, HttpServletRequest request)
        throws NotFoundException, BadRequestException, ConflictException {
        String skillId = body.get("skillId");
        return ResponseEntity.ok(installService.install(agentId, skillId));
    }

    /**
     * Uninstalls a skill from the specified agent instance.
     *
     * @param agentId agent instance identifier
     * @param skillId skill identifier to remove
     * @param request current HTTP request
     * @return ResponseEntity with uninstallation result
     */
    @DeleteMapping("/{agentId}/skills/{skillId}")
    public ResponseEntity<Map<String, Object>> uninstallSkill(@PathVariable("agentId") String agentId,
        @PathVariable("skillId") String skillId, HttpServletRequest request)
        throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(installService.uninstall(agentId, skillId));
    }

}