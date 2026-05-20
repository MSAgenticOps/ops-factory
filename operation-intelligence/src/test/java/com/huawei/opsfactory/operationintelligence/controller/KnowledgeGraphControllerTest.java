/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.controller;

import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphSnapshot;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.service.KnowledgeGraphService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
class KnowledgeGraphControllerTest {
    private static final String SECRET_KEY = "kg-secret";

    private static final Path DATA_ROOT = createTempDataRoot();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("operation-intelligence.secret-key", () -> SECRET_KEY);
        registry.add("operation-intelligence.data-root", DATA_ROOT::toString);
        registry.add("operation-intelligence.qos.enabled", () -> "false");
        registry.add("operation-intelligence.knowledge-graph.enabled", () -> "true");
        registry.add("operation-intelligence.knowledge-graph.max-hops", () -> "4");
        registry.add("operation-intelligence.knowledge-graph.snapshot-retention", () -> "3");
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteRecursively(DATA_ROOT);
    }

    @BeforeEach
    void setUp() throws IOException {
        deleteRecursively(DATA_ROOT);
        Files.createDirectories(DATA_ROOT);
    }

    @Test
    void graphEndpoints_requireSecret() {
        webTestClient.get()
            .uri("/operation-intelligence/graph/entities/biz-prod-604015020?envCode=prod")
            .exchange()
            .expectStatus()
            .isUnauthorized();
    }

    @Test
    void importGraph_upsertsB2BCallChainAndPersistsSnapshot() throws IOException {
        importFixture();

        webTestClient.get()
            .uri("/operation-intelligence/graph/entities/svc-prod-bes-business-common-sysparambs?envCode=prod")
            .header("x-secret-key", SECRET_KEY)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.success")
            .isEqualTo(true)
            .jsonPath("$.result.type")
            .isEqualTo("Service")
            .jsonPath("$.result.properties.serviceName")
            .isEqualTo("bes.business.common.SysParamBS")
            .jsonPath("$.result.properties.operations[0].operationName")
            .isEqualTo("querySysParams");

        Path snapshotDir = DATA_ROOT.resolve("knowledge-graph").resolve("prod");
        try (Stream<Path> paths = Files.list(snapshotDir)) {
            long snapshotCount = paths.filter(path -> path.getFileName().toString().endsWith(".json")).count();
            org.junit.jupiter.api.Assertions.assertEquals(1, snapshotCount);
        }
    }

    @Test
    void querySubgraph_supportsFourHops() throws IOException {
        importFixture();

        webTestClient.post()
            .uri("/operation-intelligence/graph/subgraph")
            .header("x-secret-key", SECRET_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("envCode", "prod", "entityId", "svc-prod-bes-business-common-sysparambs", "maxHops", 4))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.result.entities.length()")
            .isEqualTo(3)
            .jsonPath("$.result.relations[0].type")
            .isEqualTo("deployed_in")
            .jsonPath("$.result.observations.length()")
            .isEqualTo(0);
    }

    @Test
    void serviceLoadsPersistedSnapshotsOnStartupInit() throws IOException {
        importFixture();

        knowledgeGraphService.init();
        GraphSnapshot subgraph =
            knowledgeGraphService.querySubgraph("prod", "svc-prod-bes-business-common-datadictbs", 4);
        org.junit.jupiter.api.Assertions.assertEquals(3, subgraph.getEntities().size());
        org.junit.jupiter.api.Assertions.assertEquals(2, subgraph.getRelations().size());
    }

    private void importFixture() throws IOException {
        String body = new ClassPathResource("knowledgegraph/b2b-callchain-import.json")
            .getContentAsString(StandardCharsets.UTF_8);
        webTestClient.post()
            .uri("/operation-intelligence/graph/admin/import")
            .header("x-secret-key", SECRET_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.success")
            .isEqualTo(true)
            .jsonPath("$.result.entityCount")
            .isEqualTo(4)
            .jsonPath("$.result.relationCount")
            .isEqualTo(2)
            .jsonPath("$.result.observationCount")
            .isEqualTo(2);
    }

    private static Path createTempDataRoot() {
        try {
            return Files.createTempDirectory("operation-intelligence-kg-test-");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create test data root", e);
        }
    }

    private static void deleteRecursively(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed to delete " + path, e);
                }
            });
        }
    }
}
