/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class KnowledgeSourceValidationIntegrationTest extends KnowledgeApiIntegrationTestSupport {

    @BeforeEach
    void setUp() throws IOException {
        resetRuntimeState();
    }

    @Test
    void createSourceRejectsInvalidNameCharacters() throws Exception {
        JsonNode response = readJson(mockMvc.perform(post("/api/knowledge/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "bad/name",
                      "description": "invalid source"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andReturn());

        assertThat(response.path("code").asText()).isEqualTo("VALIDATION_FAILED");
    }

    @Test
    void createSourceRejectsDuplicateName() throws Exception {
        createSource("duplicate-source", null, null);

        JsonNode response = readJson(mockMvc.perform(post("/api/knowledge/sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "duplicate-source",
                      "description": "duplicate source"
                    }
                    """))
            .andExpect(status().isConflict())
            .andReturn());

        assertThat(response.path("code").asText()).isEqualTo("SOURCE_NAME_ALREADY_EXISTS");
    }

    @Test
    void updateSourceRejectsBlankNameAfterTrim() throws Exception {
        String sourceId = createSource();

        JsonNode response = readJson(mockMvc.perform(patch("/api/knowledge/sources/{sourceId}", sourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "   "
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andReturn());

        assertThat(response.path("message").asText()).contains("required");
    }

    @Test
    void updateSourceRejectsInvalidNameCharacters() throws Exception {
        String sourceId = createSource();

        JsonNode response = readJson(mockMvc.perform(patch("/api/knowledge/sources/{sourceId}", sourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "invalid/name"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andReturn());

        assertThat(response.path("code").asText()).isEqualTo("VALIDATION_FAILED");
    }

    @Test
    void updateSourceRejectsDuplicateName() throws Exception {
        String sourceA = createSource("source-a", null, null);
        createSource("source-b", null, null);

        JsonNode response = readJson(mockMvc.perform(patch("/api/knowledge/sources/{sourceId}", sourceA)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "source-b"
                    }
                    """))
            .andExpect(status().isConflict())
            .andReturn());

        assertThat(response.path("code").asText()).isEqualTo("SOURCE_NAME_ALREADY_EXISTS");
    }

    @Test
    void updateSourceTrimsNameAndDescription() throws Exception {
        String sourceId = createSource("source-a", null, null);

        JsonNode updated = readJson(mockMvc.perform(patch("/api/knowledge/sources/{sourceId}", sourceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "  source-renamed  ",
                      "description": "  updated description  "
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn());

        assertThat(updated.path("name").asText()).isEqualTo("source-renamed");
        assertThat(updated.path("description").asText()).isEqualTo("updated description");

        JsonNode reloaded = readJson(mockMvc.perform(get("/api/knowledge/sources/{sourceId}", sourceId))
            .andExpect(status().isOk())
            .andReturn());
        assertThat(reloaded.path("name").asText()).isEqualTo("source-renamed");
    }
}
