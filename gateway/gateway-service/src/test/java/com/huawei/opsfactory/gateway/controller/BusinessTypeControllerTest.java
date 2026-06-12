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
import com.huawei.opsfactory.gateway.service.BusinessTypeService;

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
 * Test coverage for BusinessType Controller with validation tests.
 *
 * @author x00000000
 * @since 2026-06-09
 */
@RunWith(SpringRunner.class)
@WebMvcTest(BusinessTypeController.class)
@Import({GatewayProperties.class, AuthWebFilter.class, UserContextFilter.class, BaseControllerTestConfig.class})
public class BusinessTypeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessTypeService businessTypeService;

    @MockBean
    private com.huawei.opsfactory.gateway.process.PrewarmService prewarmService;

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

    // ── listBusinessTypes ────────────────────────────────────────────

    /**
     * Tests list business types empty.
     */
    @Test
    public void testListBusinessTypes_empty() throws Exception {
        when(businessTypeService.listBusinessTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/gateway/business-types").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessTypes").isArray())
            .andExpect(jsonPath("$.businessTypes").isEmpty());
    }

    /**
     * Tests list business types with data.
     */
    @Test
    public void testListBusinessTypes_withData() throws Exception {
        Map<String, Object> businessType = new LinkedHashMap<>();
        businessType.put("id", "bt-1");
        businessType.put("name", "Web Application");
        businessType.put("code", "WEB_APP");
        when(businessTypeService.listBusinessTypes()).thenReturn(List.of(businessType));

        mockMvc.perform(get("/api/gateway/business-types").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessTypes[0].id").value("bt-1"))
            .andExpect(jsonPath("$.businessTypes[0].name").value("Web Application"));
    }

    // ── getBusinessType ───────────────────────────────────────────────

    /**
     * Tests get business type existing.
     */
    @Test
    public void testGetBusinessType_existing() throws Exception {
        Map<String, Object> businessType = new LinkedHashMap<>();
        businessType.put("id", "bt-1");
        businessType.put("name", "TestBusinessType");
        businessType.put("code", "TEST_BT");
        when(businessTypeService.getBusinessType("bt-1")).thenReturn(businessType);

        mockMvc.perform(get("/api/gateway/business-types/bt-1").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.businessType.id").value("bt-1"));
    }

    /**
     * Tests get business type not found.
     */
    @Test
    public void testGetBusinessType_notFound() throws Exception {
        when(businessTypeService.getBusinessType("nonexistent"))
            .thenThrow(new com.huawei.opsfactory.gateway.exception.NotFoundException("Business type not found"));

        mockMvc.perform(get("/api/gateway/business-types/nonexistent").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isNotFound());
    }

    // ── createBusinessType ────────────────────────────────────────────

    /**
     * Tests create business type success.
     */
    @Test
    public void testCreateBusinessType_success() throws Exception {
        Map<String, Object> created = new LinkedHashMap<>();
        created.put("id", "new-id");
        created.put("name", "NewBusinessType");
        created.put("code", "NEW_BT");
        when(businessTypeService.createBusinessType(any())).thenReturn(created);

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"NewBusinessType\", \"code\": \"NEW_BT\", \"description\": \"Test\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.businessType.id").value("new-id"));
    }

    /**
     * Tests create business type name exceeds max length.
     */
    @Test
    public void testCreateBusinessType_nameExceedsMaxLength() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type name exceeds maximum length of 100"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"" + createLongString(101) + "\", \"code\": \"TEST\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type name exceeds maximum length of 100"));
    }

    /**
     * Tests create business type code exceeds max length.
     */
    @Test
    public void testCreateBusinessType_codeExceedsMaxLength() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type code exceeds maximum length of 50"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test\", \"code\": \"" + createLongString(51) + "\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type code exceeds maximum length of 50"));
    }

    /**
     * Tests create business type missing required name.
     */
    @Test
    public void testCreateBusinessType_missingRequiredName() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type name is required"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\": \"TEST\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type name is required"));
    }

    /**
     * Tests create business type missing required code.
     */
    @Test
    public void testCreateBusinessType_missingRequiredCode() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type code is required"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type code is required"));
    }

    /**
     * Tests create business type XSS characters in name.
     */
    @Test
    public void testCreateBusinessType_xssCharactersInName() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type name contains invalid characters (< > \" ' & ` /)"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test<script>alert(1)</script>\", \"code\": \"TEST\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type name contains invalid characters (< > \" ' & ` /)"));
    }

    /**
     * Tests create business type duplicate name.
     */
    @Test
    public void testCreateBusinessType_duplicateName() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type name already exists: TestType"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"TestType\", \"code\": \"NEW_CODE\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type name already exists: TestType"));
    }

    /**
     * Tests create business type duplicate code.
     */
    @Test
    public void testCreateBusinessType_duplicateCode() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Business type code already exists: TEST_CODE"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"NewType\", \"code\": \"TEST_CODE\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type code already exists: TEST_CODE"));
    }

    /**
     * Tests create business type description exceeds max length.
     */
    @Test
    public void testCreateBusinessType_descriptionExceedsMaxLength() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Description exceeds maximum length of 500"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test\", \"code\": \"TEST\", \"description\": \"" + createLongString(501) + "\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Description exceeds maximum length of 500"));
    }

    /**
     * Tests create business type knowledge exceeds max length.
     */
    @Test
    public void testCreateBusinessType_knowledgeExceedsMaxLength() throws Exception {
        when(businessTypeService.createBusinessType(any()))
            .thenThrow(new IllegalArgumentException("Knowledge exceeds maximum length of 2000"));

        mockMvc
            .perform(post("/api/gateway/business-types").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test\", \"code\": \"TEST\", \"knowledge\": \"" + createLongString(2001) + "\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Knowledge exceeds maximum length of 2000"));
    }

    // ── updateBusinessType ────────────────────────────────────────────

    /**
     * Tests update business type success.
     */
    @Test
    public void testUpdateBusinessType_success() throws Exception {
        Map<String, Object> updated = new LinkedHashMap<>();
        updated.put("id", "bt-1");
        updated.put("name", "UpdatedBusinessType");
        when(businessTypeService.updateBusinessType(eq("bt-1"), any())).thenReturn(updated);

        mockMvc
            .perform(put("/api/gateway/business-types/bt-1").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"UpdatedBusinessType\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.businessType.name").value("UpdatedBusinessType"));
    }

    /**
     * Tests update business type cannot modify code.
     */
    @Test
    public void testUpdateBusinessType_cannotModifyCode() throws Exception {
        when(businessTypeService.updateBusinessType(eq("bt-1"), any()))
            .thenThrow(new IllegalArgumentException("Business type code cannot be modified after creation"));

        mockMvc
            .perform(put("/api/gateway/business-types/bt-1").header("x-secret-key", "test")
                .header("x-user-id", "admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\": \"MODIFIED_CODE\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error").value("Business type code cannot be modified after creation"));
    }

    /**
     * Tests update business type not found.
     */
    @Test
    public void testUpdateBusinessType_notFound() throws Exception {
        when(businessTypeService.updateBusinessType(eq("nonexistent"), any()))
            .thenThrow(new com.huawei.opsfactory.gateway.exception.NotFoundException("Business type not found"));

        mockMvc.perform(put("/api/gateway/business-types/nonexistent").header("x-secret-key", "test")
            .header("x-user-id", "admin")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"Updated\"}"))
            .andExpect(status().isNotFound());
    }

    // ── deleteBusinessType ────────────────────────────────────────────

    /**
     * Tests delete business type success.
     */
    @Test
    public void testDeleteBusinessType_success() throws Exception {
        when(businessTypeService.deleteBusinessType("bt-1")).thenReturn(true);

        mockMvc.perform(delete("/api/gateway/business-types/bt-1").header("x-secret-key", "test").header("x-user-id", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Tests delete business type not found.
     */
    @Test
    public void testDeleteBusinessType_notFound() throws Exception {
        when(businessTypeService.deleteBusinessType("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/gateway/business-types/nonexistent").header("x-secret-key", "test")
            .header("x-user-id", "admin"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ── Auth tests ───────────────────────────────────────────────────

    /**
     * Tests list business types unauthorized no key.
     */
    @Test
    public void testListBusinessTypes_unauthorized_noKey() throws Exception {
        mockMvc.perform(get("/api/gateway/business-types").header("x-user-id", "admin"))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Tests list business types succeeds for any authenticated user.
     */
    @Test
    public void testListBusinessTypes_succeeds_forAnyUser() throws Exception {
        when(businessTypeService.listBusinessTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/gateway/business-types").header("x-secret-key", "test").header("x-user-id", "regular-user"))
            .andExpect(status().isOk());
    }

    /**
     * Tests create business type succeeds for any authenticated user.
     */
    @Test
    public void testCreateBusinessType_succeeds_forAnyUser() throws Exception {
        Map<String, Object> created = new LinkedHashMap<>();
        created.put("id", "new-id");
        created.put("name", "BusinessType");
        when(businessTypeService.createBusinessType(any())).thenReturn(created);

        mockMvc.perform(post("/api/gateway/business-types").header("x-secret-key", "test")
            .header("x-user-id", "regular-user")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\": \"BusinessType\", \"code\": \"BT\"}"))
            .andExpect(status().isCreated());
    }
}
