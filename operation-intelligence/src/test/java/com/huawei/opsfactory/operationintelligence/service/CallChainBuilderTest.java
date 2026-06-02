/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.CallChainTree;
import com.huawei.opsfactory.operationintelligence.qos.model.FlowNode;
import com.huawei.opsfactory.operationintelligence.qos.model.TraceLogRecord;
import com.huawei.opsfactory.operationintelligence.qos.parser.AppendInfoParser;
import com.huawei.opsfactory.operationintelligence.qos.parser.TraceLogParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Call Chain Builder Test.
 *
 * @author x00000000
 * @since 2026-05-14
 */
class CallChainBuilderTest {

    private CallChainBuilder builder;

    private CallChainStatistics statistics;

    @BeforeEach
    void setUp() {
        TraceLogParser parser = new TraceLogParser(new AppendInfoParser());
        statistics = new CallChainStatistics(parser);
        OperationIntelligenceProperties properties = new OperationIntelligenceProperties();
        builder = new CallChainBuilder(statistics, properties);
    }

    @Test
    void testBuildSimpleFlow() {
        // Create test data with same traceId and multiple logs
        List<TraceLogRecord> logs = new ArrayList<>();

        TraceLogRecord log1 = new TraceLogRecord();
        log1.setTraceId("BES1234567890");
        log1.setSeqNo("1");
        log1.setUrl("/api/v1/test");
        log1.setIp("10.0.0.1");
        log1.setCluster("TestCluster");
        log1.setCost(100L);
        log1.setLogMessage("ER");
        log1.setMenuId("604015020");
        logs.add(log1);

        TraceLogRecord log2 = new TraceLogRecord();
        log2.setTraceId("BES1234567890");
        log2.setSeqNo("1.1");
        log2.setUrl("/api/v1/test2");
        log2.setIp("10.0.0.1");
        log2.setCluster("TestCluster");
        log2.setCost(50L);
        log2.setLogMessage("ER");
        logs.add(log2);

        TraceLogRecord log3 = new TraceLogRecord();
        log3.setTraceId("BES1234567890");
        log3.setSeqNo("2");
        log3.setUrl("/api/v1/test3");
        log3.setIp("10.0.0.1");
        log3.setCluster("TestCluster");
        log3.setCost(75L);
        log3.setLogMessage("ER");
        logs.add(log3);

        // Build call chain - use 1 for total count (only 1 traceId)
        CallChainTree tree = builder.build("BES", "menuId", "604015020", logs, 1L, "TRACE");

        assertNotNull(tree);
        assertEquals("BES", tree.getChainType());
        assertEquals(1L, tree.getTotalCount());
        assertNotNull(tree.getFlows());
        assertEquals(1, tree.getFlows().size());

        // Verify node-level success statistics
        List<FlowNode> nodes = tree.getFlows().get(0).getNodes();
        assertEquals(3, nodes.size());
        for (FlowNode node : nodes) {
            assertEquals(1L, node.getCallCount(), "Node " + node.getSeqNo() + " callCount should be 1");
            assertEquals(1L, node.getSuccessCount(), "Node " + node.getSeqNo() + " successCount should be 1");
            assertEquals(100.0, node.getSuccessPercent(), 0.001,
                "Node " + node.getSeqNo() + " successPercent should be 100.0");
        }
    }

    @Test
    void testBuildServiceModeWithNodeMerging() {
        // Create test data where multiple seqNos share the same serviceName
        List<TraceLogRecord> logs = new ArrayList<>();

        TraceLogRecord log1 = new TraceLogRecord();
        log1.setTraceId("BES111");
        log1.setSeqNo("1");
        log1.setServiceName("OrderService");
        log1.setOperationName("createOrder");
        log1.setIp("10.0.0.1");
        log1.setCluster("TestCluster");
        log1.setCost(100L);
        log1.setLogMessage("ER");
        log1.setMenuId("604015020");
        logs.add(log1);

        TraceLogRecord log2 = new TraceLogRecord();
        log2.setTraceId("BES111");
        log2.setSeqNo("1.1");
        log2.setServiceName("OrderService");
        log2.setOperationName("validateOrder");
        log2.setIp("10.0.0.1");
        log2.setCluster("TestCluster");
        log2.setCost(50L);
        log2.setLogMessage("FAIL");
        log2.setMenuId("604015020");
        logs.add(log2);

        TraceLogRecord log3 = new TraceLogRecord();
        log3.setTraceId("BES111");
        log3.setSeqNo("2");
        log3.setServiceName("PaymentService");
        log3.setOperationName("processPayment");
        log3.setIp("10.0.0.2");
        log3.setCluster("TestCluster");
        log3.setCost(75L);
        log3.setLogMessage("ER");
        log3.setMenuId("604015020");
        logs.add(log3);

        // Build in service mode - triggers mergeFlowsByService() and mergeServiceNodes()
        CallChainTree tree = builder.build("BES", "menuId", "604015020", logs, 1L, "service");

        assertNotNull(tree);
        assertEquals(1, tree.getFlows().size());

        // Verify nodes are merged by serviceName (3 raw nodes -> 2 merged nodes)
        List<FlowNode> nodes = tree.getFlows().get(0).getNodes();
        assertEquals(2, nodes.size(), "Nodes with same serviceName should be merged");

        // Verify OrderService node aggregates call/success counts from seqNo 1 and 1.1
        FlowNode orderNode = nodes.stream()
            .filter(n -> "OrderService".equals(n.getServiceName()))
            .findFirst()
            .orElse(null);
        assertNotNull(orderNode);
        assertEquals(2L, orderNode.getCallCount(),
            "OrderService callCount should aggregate both seqNo 1 and 1.1");
        assertEquals(1L, orderNode.getSuccessCount(),
            "OrderService successCount should count only successful calls (ER)");
        assertEquals(50.0, orderNode.getSuccessPercent(), 0.001,
            "OrderService successPercent should be 50% (1 success / 2 calls)");

        // Verify operationNames are merged (deduplicated and joined with comma)
        assertNotNull(orderNode.getOperationName());
        assertTrue(orderNode.getOperationName().contains("createOrder"));
        assertTrue(orderNode.getOperationName().contains("validateOrder"));

        // Verify PaymentService node is not merged (only one occurrence)
        FlowNode paymentNode = nodes.stream()
            .filter(n -> "PaymentService".equals(n.getServiceName()))
            .findFirst()
            .orElse(null);
        assertNotNull(paymentNode);
        assertEquals(1L, paymentNode.getCallCount());
        assertEquals(1L, paymentNode.getSuccessCount());
        assertEquals(100.0, paymentNode.getSuccessPercent(), 0.001);
    }

    @Test
    void testBuildWithEmptyLogs() {
        List<TraceLogRecord> logs = new ArrayList<>();

        CallChainTree tree = builder.build("BES", "menuId", "test", logs, 0L, "TRACE");

        assertNotNull(tree);
        assertTrue(tree.getFlows().isEmpty());
        assertEquals(0L, tree.getTotalCount());
    }

    @Test
    void testSeqNoComparison() {
        // Test seqNo comparison
        int result1 = builder.compareSeqNo("1", "2");
        assertTrue(result1 < 0);

        int result2 = builder.compareSeqNo("1.1", "1.2");
        assertTrue(result2 < 0);

        int result3 = builder.compareSeqNo("1.1", "1.10");
        assertTrue(result3 < 0);

        int result4 = builder.compareSeqNo("1", "1");
        assertEquals(0, result4);
    }

    private List<TraceLogRecord> createTestLogs() {
        List<TraceLogRecord> logs = new ArrayList<>();

        TraceLogRecord log1 = new TraceLogRecord();
        log1.setTraceId("BES1234567890");
        log1.setSeqNo("1");
        log1.setUrl("/api/v1/test");
        log1.setIp("10.0.0.1");
        log1.setCluster("TestCluster");
        log1.setCost(100L);
        log1.setLogMessage("ER");
        log1.setMenuId("604015020");
        logs.add(log1);

        TraceLogRecord log2 = new TraceLogRecord();
        log2.setTraceId("BES1234567890");
        log2.setSeqNo("1.1");
        log2.setServiceName("TestService");
        log2.setOperationName("testMethod");
        log2.setIp("10.0.0.1");
        log2.setCluster("TestCluster");
        log2.setCost(50L);
        log2.setLogMessage("EBS");
        logs.add(log2);

        return logs;
    }
}
