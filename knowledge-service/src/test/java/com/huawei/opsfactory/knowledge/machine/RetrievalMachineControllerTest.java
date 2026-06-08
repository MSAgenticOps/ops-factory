/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.machine;

import com.huawei.opsfactory.knowledge.controller.base.BaseRetrievalController;
import com.huawei.opsfactory.knowledge.service.KnowledgeServiceFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link RetrievalMachineController}.
 *
 * @since 2026-06-06
 */
@ExtendWith(MockitoExtension.class)
class RetrievalMachineControllerTest {

    @Mock
    private KnowledgeServiceFacade facade;

    /**
     * Test controller instantiation.
     */
    @Test
    void testControllerInstantiation() {
        RetrievalMachineController controller = new RetrievalMachineController(facade);

        assertNotNull(controller);
    }

    /**
     * Test search method delegates to facade.
     */
    @Test
    void testSearch() {
        RetrievalMachineController controller = new RetrievalMachineController(facade);
        BaseRetrievalController.SearchRequest request = new BaseRetrievalController.SearchRequest(
            "test query", List.of(), List.of(), null, 10, null, null);
        BaseRetrievalController.SearchResponse expectedResponse = new BaseRetrievalController.SearchResponse(
            "test query", List.of(), 0);
        when(facade.search(any())).thenReturn(expectedResponse);

        BaseRetrievalController.SearchResponse response = controller.search(request);

        assertEquals(expectedResponse, response);
        verify(facade).search(request);
    }

    /**
     * Test compare method delegates to facade.
     */
    @Test
    void testCompare() {
        RetrievalMachineController controller = new RetrievalMachineController(facade);
        var request = new BaseRetrievalController.CompareSearchRequest(
            "test query", List.of(), List.of(), null, null, List.of());
        var expectedResponse = new BaseRetrievalController.CompareSearchResponse(
            "test query", 10, null, null, null);
        when(facade.compare(any())).thenReturn(expectedResponse);

        var response = controller.compare(request);

        assertEquals(expectedResponse, response);
        verify(facade).compare(request);
    }

    /**
     * Test fetch method delegates to facade.
     */
    @Test
    void testFetch() {
        RetrievalMachineController controller = new RetrievalMachineController(facade);
        String chunkId = "test-chunk-id";
        var expectedResponse = new BaseRetrievalController.FetchResponse(
            chunkId, null, null, null, List.of(), null, null, List.of(),
            null, null, null, null, List.of());
        when(facade.fetch(any(), anyBoolean(), anyInt())).thenReturn(expectedResponse);

        var response = controller.fetch(chunkId, false, 1, true, true);

        assertEquals(expectedResponse, response);
        verify(facade).fetch(chunkId, false, 1);
    }

    /**
     * Test retrieve method delegates to facade.
     */
    @Test
    void testRetrieve() {
        RetrievalMachineController controller = new RetrievalMachineController(facade);
        var request = new BaseRetrievalController.RetrieveRequest(
            "test query", List.of(), null, 10, null);
        var expectedResponse = new BaseRetrievalController.RetrieveResponse(
            "test query", List.of());
        when(facade.retrieve(any())).thenReturn(expectedResponse);

        var response = controller.retrieve(request);

        assertEquals(expectedResponse, response);
        verify(facade).retrieve(request);
    }

    /**
     * Test explain method delegates to facade.
     */
    @Test
    void testExplain() {
        RetrievalMachineController controller = new RetrievalMachineController(facade);
        var request = new BaseRetrievalController.ExplainRequest(
            "test query", "chunk-id", List.of(), null);
        var expectedResponse = new BaseRetrievalController.ExplainResponse(
            "test query", "chunk-id", null, null, null);
        when(facade.explain(any())).thenReturn(expectedResponse);

        var response = controller.explain(request);

        assertEquals(expectedResponse, response);
        verify(facade).explain(request);
    }
}
