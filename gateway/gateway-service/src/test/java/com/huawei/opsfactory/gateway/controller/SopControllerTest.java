/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.huawei.opsfactory.gateway.config.BaseControllerTestConfig;
import com.huawei.opsfactory.gateway.config.GatewayProperties;
import com.huawei.opsfactory.gateway.filter.AuthWebFilter;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.process.PrewarmService;
import com.huawei.opsfactory.gateway.service.SolutionTypeService;
import com.huawei.opsfactory.gateway.service.SopService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Test coverage for Sop Controller.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@RunWith(SpringRunner.class)
@WebMvcTest(SopController.class)
@Import({GatewayProperties.class, AuthWebFilter.class, UserContextFilter.class, BaseControllerTestConfig.class})
/**
 * Sop Controller Test.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class SopControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SopService sopService;

    @MockBean
    private SolutionTypeService solutionTypeService;

    @MockBean
    private PrewarmService prewarmService;

    /**
     * Helper method to create a long string for testing max length validation.
     */
    private String createLongString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("A");
        }
        return sb.toString();
    }

    // ── listSops ─────────────────────────────────────────────────

    /**
     * Tests list sops empty.
     */
    @Test
    public void testListSops_empty() throws Exception {
        when(sopService.listSops()).thenReturn(List.of());

        mockMvc.perform(get("/api/gateway/sops/").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sops").isArray())
            .andExpect(jsonPath("$.sops").isEmpty());
    }

    /**
     * Tests list sops with data.
     */
    @Test
    public void testListSops_withData() throws Exception {
        Map<String, Object> sop = new LinkedHashMap<>();
        sop.put("id", "sop-1");
        sop.put("name", "RCPA诊断");
        when(sopService.listSops()).thenReturn(List.of(sop));

        mockMvc.perform(get("/api/gateway/sops/").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sops[0].id").value("sop-1"))
            .andExpect(jsonPath("$.sops[0].name").value("RCPA诊断"));
    }

    // ── getSop ───────────────────────────────────────────────────

    /**
     * Tests get sop existing.
     */
    @Test
    public void testGetSop_existing() throws Exception {
        Map<String, Object> sop = new LinkedHashMap<>();
        sop.put("id", "sop-1");
        sop.put("name", "TestSOP");
        sop.put("nodes", List.of());
        when(sopService.getSop("sop-1")).thenReturn(sop);

        mockMvc.perform(get("/api/gateway/sops/sop-1").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.sop.id").value("sop-1"));
    }

    /**
     * Tests get sop not found.
     */
    @Test
    public void testGetSop_notFound() throws Exception {
        when(sopService.getSop("nonexistent")).thenThrow(new IllegalArgumentException("SOP not found: nonexistent"));

        mockMvc.perform(get("/api/gateway/sops/nonexistent").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isBadRequest());
    }

    // ── createSop ────────────────────────────────────────────────

    /**
     * Tests create sop success.
     */
    @Test
    public void testCreateSop_success() throws Exception {
        Map<String, Object> created = new LinkedHashMap<>();
        created.put("id", "new-id");
        created.put("name", "NewSOP");
        when(sopService.createSop(any())).thenReturn(created);

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"NewSOP\", \"description\": \"Test\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.sop.id").value("new-id"));
    }

    /**
     * Tests create sop error.
     */
    @Test
    public void testCreateSop_error() throws Exception {
        when(sopService.createSop(any())).thenThrow(new RuntimeException("Write failed"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"SOP\"}"))
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Internal server error"));
    }

    // ── updateSop ────────────────────────────────────────────────

    /**
     * Tests update sop success.
     */
    @Test
    public void testUpdateSop_success() throws Exception {
        Map<String, Object> updated = new LinkedHashMap<>();
        updated.put("id", "sop-1");
        updated.put("name", "UpdatedSOP");
        when(sopService.updateSop(eq("sop-1"), any())).thenReturn(updated);

        mockMvc
            .perform(put("/api/gateway/sops/sop-1").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"UpdatedSOP\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.sop.name").value("UpdatedSOP"));
    }

    /**
     * Tests update sop not found.
     */
    @Test
    public void testUpdateSop_notFound() throws Exception {
        when(sopService.updateSop(eq("nonexistent"), any())).thenReturn(null);

        mockMvc.perform(put("/api/gateway/sops/nonexistent").header("x-secret-key", "test")
            .header("x-user-id", "admin")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Updated\"}")).andExpect(status().isNotFound());
    }

    // ── deleteSop ────────────────────────────────────────────────

    /**
     * Tests delete sop success.
     */
    @Test
    public void testDeleteSop_success() throws Exception {
        when(sopService.deleteSop("sop-1")).thenReturn(true);

        mockMvc.perform(delete("/api/gateway/sops/sop-1").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Tests delete sop not found.
     */
    @Test
    public void testDeleteSop_notFound() throws Exception {
        when(sopService.deleteSop("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/gateway/sops/nonexistent").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Tests create sop duplicate name returns conflict.
     */
    @Test
    public void testCreateSop_duplicateName_returnsConflict() throws Exception {
        when(sopService.createSop(any())).thenThrow(new IllegalArgumentException("SOP name already exists: TestSOP"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("SOP name already exists: TestSOP"));
    }

    // ── Auth tests ───────────────────────────────────────────────

    /**
     * Tests list sops unauthorized no key.
     */
    @Test
    public void testListSops_unauthorized_noKey() throws Exception {
        mockMvc.perform(get("/api/gateway/sops/").header("x-user-id", "admin")).andExpect(status().isUnauthorized());
    }

    /**
     * Tests list sops succeeds for any authenticated user.
     */
    @Test
    public void testListSops_succeeds_forAnyUser() throws Exception {
        when(sopService.listSops()).thenReturn(List.of());

        mockMvc.perform(get("/api/gateway/sops/").header("x-secret-key", "test").header("x-user-id", "regular-user"))
            .andExpect(status().isOk());
    }

    /**
     * Tests create sop succeeds for any authenticated user.
     */
    @Test
    public void testCreateSop_succeeds_forAnyUser() throws Exception {
        Map<String, Object> created = new LinkedHashMap<>();
        created.put("id", "new-id");
        created.put("name", "SOP");
        when(sopService.createSop(any())).thenReturn(created);

        mockMvc.perform(post("/api/gateway/sops/").header("x-secret-key", "test")
            .header("x-user-id", "regular-user")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"SOP\"}")).andExpect(status().isCreated());
    }

    // ── Validation tests ──────────────────────────────────────────────

    /**
     * Tests create SOP name exceeds max length (100).
     */
    @Test
    public void testCreateSop_nameExceedsMaxLength() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("SOP name exceeds maximum length of 100"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"" + createLongString(101) + "\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("SOP name exceeds maximum length of 100"));
    }

    /**
     * Tests create SOP missing required name.
     */
    @Test
    public void testCreateSop_missingRequiredName() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("SOP name is required"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\": \"Test\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("SOP name is required"));
    }

    /**
     * Tests create SOP XSS characters in name.
     */
    @Test
    public void testCreateSop_xssCharactersInName() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("SOP name contains invalid characters (< > \" ' & ` /)"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test<script>alert(1)</script>\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("SOP name contains invalid characters (< > \" ' & ` /)"));
    }

    /**
     * Tests create SOP version exceeds max length (50).
     */
    @Test
    public void testCreateSop_versionExceedsMaxLength() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Version exceeds maximum length of 50"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"version\": \"" + createLongString(51) + "\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Version exceeds maximum length of 50"));
    }

    /**
     * Tests create SOP description exceeds max length (500).
     */
    @Test
    public void testCreateSop_descriptionExceedsMaxLength() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Description exceeds maximum length of 500"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"description\": \"" + createLongString(501) + "\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Description exceeds maximum length of 500"));
    }

    /**
     * Tests create SOP triggerCondition exceeds max length (500).
     */
    @Test
    public void testCreateSop_triggerConditionExceedsMaxLength() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Trigger condition exceeds maximum length of 500"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"triggerCondition\": \"" + createLongString(501) + "\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Trigger condition exceeds maximum length of 500"));
    }

    /**
     * Tests create SOP stepsDescription exceeds max length (1000).
     */
    @Test
    public void testCreateSop_stepsDescriptionExceedsMaxLength() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Steps description exceeds maximum length of 1000"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"stepsDescription\": \"" + createLongString(1001) + "\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Steps description exceeds maximum length of 1000"));
    }

    /**
     * Tests create SOP invalid enabled value.
     */
    @Test
    public void testCreateSop_invalidEnabledValue() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Enabled must be either TRUE or FALSE"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"enabled\": \"INVALID\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Enabled must be either TRUE or FALSE"));
    }

    /**
     * Tests create SOP invalid mode value.
     */
    @Test
    public void testCreateSop_invalidModeValue() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Mode must be either 'structured' or 'natural_language'"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"mode\": \"invalid_mode\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Mode must be either 'structured' or 'natural_language'"));
    }

    /**
     * Tests create SOP solution type not found.
     */
    @Test
    public void testCreateSop_solutionTypeNotFound() throws Exception {
        when(sopService.createSop(any()))
            .thenThrow(new IllegalArgumentException("Solution type not found: nonexistent_solution"));

        mockMvc
            .perform(post("/api/gateway/sops/").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestSOP\", \"targetSolution\": \"nonexistent_solution\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Solution type not found: nonexistent_solution"));
    }
}
