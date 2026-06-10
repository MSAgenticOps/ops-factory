/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.parser;

import com.huawei.opsfactory.operationintelligence.qos.model.TraceLogRecord;
import com.huawei.opsfactory.operationintelligence.qos.util.JsonNodeHelper;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Trace Log Parser.
 * Parses DV tracelog API responses into TraceLogRecord objects.
 *
 * @author call-chain
 * @since 2026-05-14
 */
@Component
public class TraceLogParser {

    private static final Logger log = LoggerFactory.getLogger(TraceLogParser.class);

    private final AppendInfoParser appendInfoParser;

    /**
     * Trace Log Parser.
     *
     * @param appendInfoParser the append info parser
     */
    public TraceLogParser(AppendInfoParser appendInfoParser) {
        this.appendInfoParser = appendInfoParser;
    }

    /**
     * Parse a single tracelog entry.
     *
     * @param logEntry the log entry JSON node
     * @return the parsed trace log record
     */
    public TraceLogRecord parse(JsonNode logEntry) {
        TraceLogRecord record = new TraceLogRecord();
        record.setTraceId(JsonNodeHelper.textVal(logEntry, "TraceID"));
        record.setIp(JsonNodeHelper.textVal(logEntry, "ServerIP"));
        record.setCluster(JsonNodeHelper.textVal(logEntry, "ClusterType"));
        record.setClusterId(JsonNodeHelper.textVal(logEntry, "ClusterId"));
        record.setLogMessage(JsonNodeHelper.textVal(logEntry, "LogMessage"));
        record.setLogTime(JsonNodeHelper.textVal(logEntry, "Time"));
        record.setCost(JsonNodeHelper.parseCost(JsonNodeHelper.textVal(logEntry, "cost")));
        record.setMoi(JsonNodeHelper.textVal(logEntry, "moi"));

        // Field priority: url > serviceName > topic/eventName
        String url = JsonNodeHelper.safeValue(JsonNodeHelper.textVal(logEntry, "url"));
        String serviceName = JsonNodeHelper.safeValue(JsonNodeHelper.textVal(logEntry, "serviceName"));

        if (url != null) {
            record.setUrl(url);
        } else if (serviceName != null) {
            record.setServiceName(serviceName);
            record.setOperationName(JsonNodeHelper.textVal(logEntry, "operationName"));
        }

        // Parse fields from AppendInfo
        String appendInfo = JsonNodeHelper.textVal(logEntry, "AppendInfo");
        if (appendInfo != null) {
            parseAppendInfo(record, appendInfo);
        }

        return record;
    }

    /**
     * Parse AppendInfo fields into the record.
     *
     * @param record the record to populate
     * @param appendInfo the append info string
     */
    private void parseAppendInfo(TraceLogRecord record, String appendInfo) {
        try {
            record.setSeqNo(appendInfoParser.parseSeqNo(appendInfo));
            record.setMenuId(appendInfoParser.parseField(appendInfo, "menuId"));
            record.setBusiCode(appendInfoParser.parseField(appendInfo, "busiCode"));
            record.setJobDefinedId(appendInfoParser.parseField(appendInfo, "jobDefinedId"));
            record.setOperatorId(appendInfoParser.parseField(appendInfo, "operatorId"));
            record.setProcessName(appendInfoParser.parseField(appendInfo, "processName"));
            record.setElementName(appendInfoParser.parseField(appendInfo, "elementName"));
            record.setElementType(appendInfoParser.parseField(appendInfo, "elementType"));

            // If url and serviceName are both null, extract MQ info from AppendInfo
            if (record.getUrl() == null && record.getServiceName() == null) {
                String type = appendInfoParser.parseField(appendInfo, "type");
                if ("consumer".equalsIgnoreCase(type) || "producer".equalsIgnoreCase(type)) {
                    record.setTopic(appendInfoParser.parseField(appendInfo, "topic"));
                    record.setEventName(appendInfoParser.parseField(appendInfo, "eventName"));
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse AppendInfo: {}", e.getMessage());
        }
    }

    /**
     * Check if a trace log record represents a successful call.
     *
     * @param record the trace log record
     * @return true if successful, false otherwise
     */
    public boolean isSuccess(TraceLogRecord record) {
        String logMessage = record.getLogMessage();
        if (logMessage == null) {
            return false;
        }
        String upper = logMessage.toUpperCase();
        return upper.startsWith("E") || upper.startsWith("END") || upper.startsWith("ER") || upper.startsWith("EBS")
            || upper.startsWith("EMQ") || upper.startsWith("EBPMP") || upper.startsWith("EBPMA");
    }
}
