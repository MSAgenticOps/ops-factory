/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.controller.machine;

import com.huawei.opsfactory.operationintelligence.knowledgegraph.model.GraphOntology;
import com.huawei.opsfactory.operationintelligence.knowledgegraph.service.KnowledgeGraphService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link KnowledgeGraphMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class KnowledgeGraphMachineControllerTest {

    @Mock
    private KnowledgeGraphService knowledgeGraphService;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        KnowledgeGraphMachineController controller = new KnowledgeGraphMachineController(knowledgeGraphService);

        assertNotNull(controller);
    }

    /**
     * Test importOntology method.
     */
    @Test
    void testImportOntology() {
        KnowledgeGraphMachineController controller = new KnowledgeGraphMachineController(knowledgeGraphService);
        GraphOntology ontology = new GraphOntology();
        ontology.setOntologyId("test-ontology");
        GraphOntology expectedResult = new GraphOntology();
        expectedResult.setOntologyId("test-ontology");
        when(knowledgeGraphService.importOntology(any(GraphOntology.class))).thenReturn(expectedResult);

        Map<String, Object> result = controller.importOntology(ontology);

        assertNotNull(result);
        assertTrue(result.containsKey("result"));
        assertEquals(expectedResult, result.get("result"));
        verify(knowledgeGraphService).importOntology(ontology);
    }

    /**
     * Test listOntologies method.
     */
    @Test
    void testListOntologies() {
        KnowledgeGraphMachineController controller = new KnowledgeGraphMachineController(knowledgeGraphService);

        GraphOntology o1 = new GraphOntology();
        o1.setOntologyId("1");
        GraphOntology o2 = new GraphOntology();
        o2.setOntologyId("2");
        List<GraphOntology> expectedList = List.of(o1, o2);

        when(knowledgeGraphService.listOntologies()).thenReturn(expectedList);

        Map<String, Object> result = controller.listOntologies();

        assertNotNull(result);
        assertTrue(result.containsKey("result"));
        assertEquals(expectedList, result.get("result"));
        verify(knowledgeGraphService).listOntologies();
    }

    /**
     * Test getOntology method.
     */
    @Test
    void testGetOntology() {
        KnowledgeGraphMachineController controller = new KnowledgeGraphMachineController(knowledgeGraphService);
        String ontologyId = "test-id";

        GraphOntology expectedOntology = new GraphOntology();
        expectedOntology.setOntologyId(ontologyId);
        expectedOntology.setName("Test");

        when(knowledgeGraphService.getOntology(ontologyId)).thenReturn(expectedOntology);

        Map<String, Object> result = controller.getOntology(ontologyId);

        assertNotNull(result);
        assertTrue(result.containsKey("result"));
        assertEquals(expectedOntology, result.get("result"));
        verify(knowledgeGraphService).getOntology(ontologyId);
    }
}
