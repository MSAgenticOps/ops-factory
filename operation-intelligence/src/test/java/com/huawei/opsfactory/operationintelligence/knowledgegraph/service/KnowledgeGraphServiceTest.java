/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.knowledgegraph.service;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.EntityTypeDefinition;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphOntology;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.RelationTypeDefinition;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.store.GraphOntologyStore;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.store.GraphSnapshotStore;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.store.InMemoryGraphStore;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Unit tests for knowledge graph import validation. */
class KnowledgeGraphServiceTest {
    @TempDir
    private Path tempDir;

    @Test
    void importOntology_rejectsDuplicateOntologyId() {
        KnowledgeGraphService service = service();
        service.importOntology(ontology("duplicate-id"));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,
            () -> service.importOntology(ontology("duplicate-id")));

        Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        Assertions.assertTrue(exception.getReason().contains("Ontology ID already exists"));
    }

    @Test
    void importOntology_rejectsConcurrentDuplicateOntologyId() throws InterruptedException, ExecutionException {
        KnowledgeGraphService service = service();
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            List<Future<Boolean>> futures = new ArrayList<>();
            for (int index = 0; index < 2; index++) {
                futures.add(executor.submit(importTask(service, readyLatch, startLatch)));
            }
            readyLatch.await();
            startLatch.countDown();

            int successCount = 0;
            int conflictCount = 0;
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    successCount++;
                } else {
                    conflictCount++;
                }
            }

            Assertions.assertEquals(1, successCount);
            Assertions.assertEquals(1, conflictCount);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void importOntology_rejectsUnsafeOntologyId() {
        KnowledgeGraphService service = service();

        ResponseStatusException exception =
            Assertions.assertThrows(ResponseStatusException.class, () -> service.importOntology(ontology("../bad")));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Assertions.assertTrue(exception.getReason().contains("unsupported path characters"));
    }

    @Test
    void importOntology_rejectsTooLongOntologyId() {
        KnowledgeGraphService service = service();
        String ontologyId = "a".repeat(129);

        ResponseStatusException exception =
            Assertions.assertThrows(ResponseStatusException.class, () -> service.importOntology(ontology(ontologyId)));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        Assertions.assertTrue(exception.getReason().contains("too long"));
    }

    private KnowledgeGraphService service() {
        OperationIntelligenceProperties properties = new OperationIntelligenceProperties();
        properties.setDataRoot(tempDir.toString());
        GraphSchemaRegistry schemaRegistry = new GraphSchemaRegistry();
        InMemoryGraphStore graphStore = new InMemoryGraphStore();
        GraphOntologyStore ontologyStore = new GraphOntologyStore(properties);
        GraphSnapshotStore snapshotStore = new GraphSnapshotStore(properties);
        return new KnowledgeGraphService(properties, schemaRegistry, graphStore, ontologyStore, snapshotStore);
    }

    private Callable<Boolean> importTask(KnowledgeGraphService service, CountDownLatch readyLatch,
        CountDownLatch startLatch) {
        return () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                service.importOntology(ontology("concurrent-duplicate-id"));
                return true;
            } catch (ResponseStatusException exception) {
                Assertions.assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
                return false;
            }
        };
    }

    private GraphOntology ontology(String ontologyId) {
        GraphOntology ontology = new GraphOntology();
        ontology.setOntologyId(ontologyId);
        ontology.setName("Test Ontology");
        ontology.setSourceSystem("test");
        ontology.setEntityTypes(List.of(entityType("Service"), entityType("Host")));
        ontology.setRelationTypes(List.of(relationType("deployed_on")));
        return ontology;
    }

    private EntityTypeDefinition entityType(String type) {
        EntityTypeDefinition definition = new EntityTypeDefinition();
        definition.setType(type);
        definition.setRequiredProperties(List.of());
        return definition;
    }

    private RelationTypeDefinition relationType(String type) {
        RelationTypeDefinition definition = new RelationTypeDefinition();
        definition.setType(type);
        definition.setLayer("runtime");
        definition.setFrom(List.of("Service"));
        definition.setTo(List.of("Host"));
        return definition;
    }
}
