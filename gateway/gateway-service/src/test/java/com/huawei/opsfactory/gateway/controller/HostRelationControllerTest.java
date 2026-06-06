/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.opsfactory.gateway.service.BusinessServiceService;
import com.huawei.opsfactory.gateway.service.HostRelationService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Focused tests for HostRelationController business service enrichment.
 */
public class HostRelationControllerTest {

    /**
     * Verifies graph responses are enriched with matching business services and business-entry edges.
     */
    @Test
    public void getGraph_addsBusinessServiceNodesAndEdgesForMatchingHosts() {
        HostRelationService hostRelationService = mock(HostRelationService.class);
        BusinessServiceService businessServiceService = mock(BusinessServiceService.class);
        HostRelationController controller = new HostRelationController(hostRelationService, businessServiceService);

        List<Map<String, Object>> nodes = new ArrayList<>();
        nodes.add(new LinkedHashMap<>(Map.of("id", "host-1", "name", "Host One")));
        Map<String, Object> graph = new LinkedHashMap<>();
        graph.put("nodes", nodes);
        graph.put("edges", new ArrayList<Map<String, Object>>());
        when(hostRelationService.getGraphData(null, null)).thenReturn(graph);
        when(businessServiceService.listBusinessServices(null, null)).thenReturn(List.of(
            new LinkedHashMap<>(Map.of("id", "bs-1", "name", "Business One", "groupId", "group-1", "hostIds",
                List.of("host-1", "host-2")))));
        when(hostRelationService.listRelations(null, null, null, "business-service", "bs-1"))
            .thenReturn(List.of(new LinkedHashMap<>(Map.of("targetHostId", "host-1", "description", "entry"))));

        Map<String, Object> result = controller.getGraph(null, null, null);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultNodes = (List<Map<String, Object>>) result.get("nodes");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> resultEdges = (List<Map<String, Object>>) result.get("edges");
        assertEquals(2, resultNodes.size());
        assertEquals("business-service", resultNodes.get(1).get("nodeType"));
        assertEquals(1, resultEdges.size());
        assertEquals("bs-1", resultEdges.get(0).get("source"));
        assertEquals("host-1", resultEdges.get(0).get("target"));
        assertEquals("business-entry", resultEdges.get(0).get("type"));
        assertTrue(resultEdges.get(0).containsKey("description"));
    }
}
