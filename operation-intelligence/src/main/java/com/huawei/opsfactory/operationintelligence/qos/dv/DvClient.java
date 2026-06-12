/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.dv;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;
import com.huawei.opsfactory.operationintelligence.qos.model.AlarmInfo;
import com.huawei.opsfactory.operationintelligence.qos.model.AlarmQueryRequest;
import com.huawei.opsfactory.operationintelligence.qos.model.PerformanceDataQueryRequest;
import com.huawei.opsfactory.operationintelligence.qos.model.PerformanceDataResult;
import com.huawei.opsfactory.operationintelligence.qos.model.TraceLogRecord;
import com.huawei.opsfactory.operationintelligence.qos.util.JsonNodeHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import javax.net.ssl.SSLContext;

/**
 * DV Client for querying data from DV (DataView) downstream service.
 *
 * @author x00000000
 * @since 2026-05-11
 */
@Component
public class DvClient {

    private static final Logger log = LoggerFactory.getLogger(DvClient.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final int MAX_RETRIES = 3;

    private static final String PATH_MOS = "/rest/eammimservice/v1/openapi/mit/mos";

    private static final String PATH_PERFORMANCE = "/rest/dvpmservice/v1/openapi/monitor/history/data";

    private static final String PATH_ALARMS = "/rest/fault/v1/current-alarms/scroll";

    private static final String PATH_TRACELOG = "/cmp/api/logmatrix/v1/logdata/tracelog";

    private static final int ALARM_BATCH_SIZE = 500;

    private final DvAuthService authService;

    private final DvSslContextFactory sslFactory;

    private final OperationIntelligenceProperties properties;

    private final int maxConnections;

    private final int connectTimeoutMs;

    private final int requestTimeoutMs;

    private final int queryLimit;

    private final ConcurrentHashMap<String, RestClient> clientCache = new ConcurrentHashMap<>();

    /**
     * Dv Client.
     *
     * @param authService the authService
     * @param sslFactory the sslFactory
     * @param properties the properties
     */
    public DvClient(DvAuthService authService, DvSslContextFactory sslFactory,
        OperationIntelligenceProperties properties) {
        this.authService = authService;
        this.sslFactory = sslFactory;
        this.properties = properties;

        this.maxConnections = 10;
        this.connectTimeoutMs = 10000;
        this.requestTimeoutMs = 60000;
        this.queryLimit = properties.getCallChain().getQueryLimit();
    }

    /**
     * Extract text value from JSON node by field name.
     *
     * @param node the JSON node
     * @param field the field name
     * @return the text value, or null if not present or null
     */
    private static String textVal(JsonNode node, String field) {
        return JsonNodeHelper.textVal(node, field);
    }

    // --- MO 查询（性能数据前置接口） ---

    /**
     * Get DV environment by solution type (envCode).
     *
     * @param solutionType the solution type (envCode)
     * @return the DV environment info, or null if not found
     */
    private DvEnvironmentInfo getDvEnvironment(String solutionType) {
        var environments = properties.getQos().getDvEnvironments();
        if (environments == null || environments.isEmpty()) {
            return null;
        }

        for (var config : environments) {
            if (solutionType.equals(config.getAgentSolutionType())) {
                return DvEnvironmentInfo.fromConfig(config);
            }
        }

        return null;
    }

    /**
     * fetch Mos.
     *
     * @param env the env
     * @param dns the dns
     * @return the result
     */
    public List<String> fetchMos(DvEnvironmentInfo env, List<String> dns) {
        return executeWithRetry(() -> doFetchMos(env, dns), "fetchMos[" + env.getEnvCode() + "]");
    }

    // --- 11.3.1 性能指标查询 ---

    /**
     * Internal implementation of fetching MOs.
     *
     * @param env the DV environment info
     * @param dns the list of DN names
     * @return the list of child MO names
     */
    private List<String> doFetchMos(DvEnvironmentInfo env, List<String> dns) {
        try {
            String jsonBody = MAPPER.writeValueAsString(dns);
            String response = executePost(env, PATH_MOS, jsonBody);
            if (response == null || response.isBlank()) {
                return Collections.emptyList();
            }
            return parseChildren(response);
        } catch (IOException | IllegalStateException e) {
            throw new IllegalStateException("Failed to fetch MOs from " + env.getServerUrl() + ": " + e.getMessage(),
                e);
        }
    }

    /**
     * Fetches performance data from DV service.
     *
     * @param env the DV environment info
     * @param request the performance data query request
     * @return the list of performance data results
     */
    public List<PerformanceDataResult> fetchPerformanceData(DvEnvironmentInfo env,
        PerformanceDataQueryRequest request) {
        return executeWithRetry(() -> doFetchPerformanceData(env, request),
            "fetchPerformanceData[" + env.getEnvCode() + "]");
    }

    // --- 11.3.2 当前告警查询（scroll） ---

    /**
     * Internal implementation of fetching performance data.
     *
     * @param env the DV environment info
     * @param request the performance data query request
     * @return the list of performance data results
     */
    private List<PerformanceDataResult> doFetchPerformanceData(DvEnvironmentInfo env,
        PerformanceDataQueryRequest request) {
        try {
            Map<String, Object> timeRanges = new LinkedHashMap<>();
            timeRanges.put(String.valueOf(request.getStartTime()), request.getEndTime());

            Map<String, Object> dnMap = new LinkedHashMap<>();
            for (String dn : request.getDns()) {
                dnMap.put(dn, Map.of());
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("moType", request.getMoType());
            body.put("measUnitKey", request.getMeasUnitKey());
            body.put("timeRanges", timeRanges);
            body.put("dnOriginalValueMeasTypeCalTypes", dnMap);

            String jsonBody = MAPPER.writeValueAsString(body);
            String response = executePost(env, PATH_PERFORMANCE, jsonBody);

            if (response == null || response.isBlank()) {
                return Collections.emptyList();
            }

            return parsePerformanceResult(response);
        } catch (IOException | IllegalStateException e) {
            throw new IllegalStateException("Failed to fetch performance data from " + env.getServerUrl() + " moType="
                + request.getMoType() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Fetches current alarms from DV service.
     *
     * @param env the DV environment info
     * @param request the alarm query request
     * @return the list of alarm info
     */
    public List<AlarmInfo> fetchCurrentAlarms(DvEnvironmentInfo env, AlarmQueryRequest request) {
        return executeWithRetry(() -> doFetchCurrentAlarms(env, request),
            "fetchCurrentAlarms[" + env.getEnvCode() + "]");
    }

    /**
     * Internal implementation of fetching current alarms.
     *
     * @param env the DV environment info
     * @param request the alarm query request
     * @return the list of alarm info
     */
    private List<AlarmInfo> doFetchCurrentAlarms(DvEnvironmentInfo env, AlarmQueryRequest request) {
        try {
            String jsonBody = MAPPER.writeValueAsString(buildAlarmQuery(request));
            String response = executePost(env, PATH_ALARMS, jsonBody);

            if (response == null || response.isBlank()) {
                return Collections.emptyList();
            }

            return parseAlarms(response);
        } catch (IOException | IllegalStateException e) {
            throw new IllegalStateException("Failed to fetch alarms from " + env.getServerUrl() + ": " + e.getMessage(),
                e);
        }
    }

    // --- 11.8 通用重试机制 ---

    /**
     * Sleeps for the specified delay before retrying.
     *
     * @param delayMs the delay in milliseconds
     * @throws InterruptedException if the sleep is interrupted
     */
    void sleepBeforeRetry(long delayMs) throws InterruptedException {
        Thread.sleep(delayMs);
    }

    // --- 内部工具方法 ---

    /**
     * Executes an action with retry logic.
     *
     * @param <T> the return type
     * @param action the action to execute
     * @param operationName the operation name for logging
     * @return the result of the action
     */
    <T> T executeWithRetry(Supplier<T> action, String operationName) {
        int retryCount = 0;
        Exception lastException = null;
        while (retryCount <= MAX_RETRIES) {
            try {
                return action.get();
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    break;
                }
                long delayMs = (1L << retryCount) * 1000;
                log.warn("{} failed, retry {}/{} in {}ms: {}", operationName, retryCount, MAX_RETRIES, delayMs,
                    e.getMessage());
                try {
                    sleepBeforeRetry(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(operationName + " interrupted during retry", ie);
                }
            }
        }
        log.error("{} failed after {} retries: {}", operationName, MAX_RETRIES, lastException.getMessage());
        throw new IllegalStateException(operationName + " failed after " + MAX_RETRIES + " retries", lastException);
    }

    /**
     * Get or create RestClient for the DV environment.
     *
     * @param env the DV environment info
     * @return the RestClient instance
     */
    private RestClient getOrCreateRestClient(DvEnvironmentInfo env) {
        return clientCache.computeIfAbsent(env.getServerUrl(), url -> {
            ClientHttpRequestFactory requestFactory;
            try {
                SSLContext sslContext =
                    sslFactory.createSslContext(env.getCrtContent(), env.getCrtFileName(), env.isStrictSsl());
                requestFactory = new SimpleClientHttpRequestFactory() {
                    @Override
                    protected void prepareConnection(java.net.HttpURLConnection conn, String httpMethod)
                        throws IOException {
                        super.prepareConnection(conn, httpMethod);
                        if (conn instanceof javax.net.ssl.HttpsURLConnection httpsConn) {
                            httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
                            httpsConn.setConnectTimeout(connectTimeoutMs);
                            httpsConn.setReadTimeout(requestTimeoutMs);
                            httpsConn.setHostnameVerifier((hostname, session) -> true);
                        }
                    }
                };
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Failed to create SSL context for " + url, e);
            }
            return RestClient.builder().requestFactory(requestFactory).baseUrl(url).build();
        });
    }

    /**
     * shutdown.
     */
    @PreDestroy
    public void shutdown() {
        clientCache.clear();
    }

    /**
     * Executes a POST request to the DV server and returns the response body.
     *
     * @param env the DV environment info
     * @param path the URL path (appended to server URL)
     * @param jsonBody the JSON request body
     * @return the response body string, or null if empty
     * @throws IOException if the request fails
     */
    private String executePost(DvEnvironmentInfo env, String path, String jsonBody) throws IOException {
        RestClient webClient = getOrCreateRestClient(env);
        Map<String, String> headers = authService.buildAuthHeaders(env);
        return webClient.post()
            .uri(env.getServerUrl() + path)
            .headers(h -> headers.forEach(h::add))
            .body(jsonBody)
            .retrieve()
            .body(String.class);
    }

    /**
     * Executes a tracelog POST request, serializing the body map.
     *
     * @param env the DV environment info
     * @param body the query body map
     * @return the response body string, or null if empty
     * @throws IOException if serialization or request fails
     */
    private String executeTraceLogPost(DvEnvironmentInfo env, Map<String, Object> body) throws IOException {
        String jsonBody = MAPPER.writeValueAsString(body);
        return executePost(env, PATH_TRACELOG, jsonBody);
    }

    /**
     * Build alarm query request body.
     *
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param severities the list of severity levels
     * @param dns the list of DN names
     * @return the alarm query request body map
     */
    private Map<String, Object> buildAlarmQuery(AlarmQueryRequest request) {
        List<Map<String, Object>> filters = new ArrayList<>();

        Map<String, Object> timeFilter = new LinkedHashMap<>();
        timeFilter.put("name", "OCCURUTC");
        timeFilter.put("field", "OCCURUTC");
        timeFilter.put("operator", "BETWEEN");
        timeFilter.put("values", List.of(request.getStartTime(), request.getEndTime()));
        filters.add(timeFilter);

        if (request.getSeverities() != null && !request.getSeverities().isEmpty()) {
            Map<String, Object> severityFilter = new LinkedHashMap<>();
            severityFilter.put("name", "SEVERITY");
            severityFilter.put("field", "SEVERITY");
            severityFilter.put("operator", "IN");
            severityFilter.put("values", request.getSeverities());
            filters.add(severityFilter);
        }

        if (request.getDns() != null && !request.getDns().isEmpty()) {
            Map<String, Object> dnFilter = new LinkedHashMap<>();
            dnFilter.put("name", "NATIVEMEDN");
            dnFilter.put("field", "NATIVEMEDN");
            dnFilter.put("operator", "IN");
            dnFilter.put("values", request.getDns());
            filters.add(dnFilter);
        }

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("filters", filters);
        query.put("expression", "and");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", query);
        body.put("sort", List.of(Collections.singletonMap("field", "CSN")));
        body.put("fields", List.of("alarmId", "alarmName", "severity", "nativeMeDn", "meName", "occurUtc", "count",
            "moi", "additionalInformation"));
        body.put("size", ALARM_BATCH_SIZE);
        return body;
    }

    /**
     * Parses performance data result from response string.
     *
     * @param response the response string
     * @return the list of performance data results
     */
    List<PerformanceDataResult> parsePerformanceResult(String response) {
        List<PerformanceDataResult> results = new ArrayList<>();
        if (response == null)
            return results;
        try {
            JsonNode root = MAPPER.readTree(response);
            JsonNode resultNode = root.has("result") ? root.get("result") : root;
            JsonNode datas = resultNode.has("datas") ? resultNode.get("datas") : resultNode;
            if (datas.isArray()) {
                for (JsonNode item : datas) {
                    results.add(parsePerformanceDataItem(item));
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse performance result: {}", e.getMessage());
        }
        return results;
    }

    /**
     * Parses a single performance data JSON item into a PerformanceDataResult.
     *
     * @param item the JSON node representing one performance data entry
     * @return the parsed PerformanceDataResult
     */
    private PerformanceDataResult parsePerformanceDataItem(JsonNode item) {
        PerformanceDataResult r = new PerformanceDataResult();
        r.setDn(textVal(item, "dn"));
        r.setMoType(textVal(item, "neName"));
        r.setNeName(textVal(item, "neName"));
        r.setPeriod(item.has("period") ? item.get("period").asInt() : 0);
        if (item.has("values") && item.get("values").isObject()) {
            r.setValues(parseValuesMap(item.get("values")));
        }
        return r;
    }

    /**
     * Converts a JSON object node into a map of string key-value pairs.
     *
     * @param valuesNode the JSON object node containing value entries
     * @return a map of field names to their text values
     */
    private Map<String, String> parseValuesMap(JsonNode valuesNode) {
        Map<String, String> vals = new LinkedHashMap<>();
        for (Map.Entry<String, JsonNode> entry : valuesNode.properties()) {
            vals.put(entry.getKey(), entry.getValue().asText());
        }
        return vals;
    }

    /**
     * Parses alarm info list from response string.
     *
     * @param response the response string
     * @return the list of alarm info
     */
    List<AlarmInfo> parseAlarms(String response) {
        List<AlarmInfo> alarms = new ArrayList<>();
        if (response == null)
            return alarms;
        try {
            JsonNode root = MAPPER.readTree(response);
            JsonNode hits = root.get("hits");
            if (hits == null || !hits.isArray())
                return alarms;
            for (JsonNode hit : hits) {
                AlarmInfo alarm = new AlarmInfo();
                alarm.setAlarmId(textVal(hit, "alarmId"));
                alarm.setAlarmName(textVal(hit, "alarmName"));
                alarm.setSeverity(textVal(hit, "severity"));
                alarm.setDn(textVal(hit, "nativeMeDn"));
                alarm.setMeName(textVal(hit, "meName"));
                alarm.setOccurUtc(hit.has("occurUtc") ? hit.get("occurUtc").asLong() : null);
                alarm.setCount(hit.has("count") ? hit.get("count").asInt() : 1);
                alarm.setMoi(textVal(hit, "moi"));
                alarm.setAdditionalInformation(textVal(hit, "additionalInformation"));
                alarms.add(alarm);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse alarm response: {}", e.getMessage());
        }
        return alarms;
    }

    /**
     * Parses MO children from response string.
     *
     * @param response the response string
     * @return the list of child text values
     */
    List<String> parseChildren(String response) {
        List<String> children = new ArrayList<>();
        if (response == null)
            return children;
        try {
            JsonNode root = MAPPER.readTree(response);
            JsonNode resultNode = root.has("result") ? root.get("result") : root;
            if (resultNode.isArray()) {
                for (JsonNode item : resultNode) {
                    collectChildTexts(item, children);
                }
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse MO children response: {}", e.getMessage());
        }
        return children;
    }

    /**
     * Extracts text values from the "children" array of a JSON node.
     *
     * @param item the JSON node that may contain a "children" array
     * @param children the list to append child text values to
     */
    private void collectChildTexts(JsonNode item, List<String> children) {
        if (!item.has("children") || !item.get("children").isArray()) {
            return;
        }
        for (JsonNode child : item.get("children")) {
            children.add(child.asText());
        }
    }

    // --- Call Chain: TraceLog Support ---

    /**
     * Fetch TraceLog entries with time range splitting support.
     *
     * @param solutionType the solution type (envCode)
     * @param solutionId the solution id (for DV must filter)
     * @param chainType the chain type (BES/API/BPM/JOB)
     * @param conditionKey the primary condition key (for backward compatibility)
     * @param conditions the list of conditions (each with conditionKey and conditionValue)
     * @param config the chain type configuration
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return list of trace log records
     */
    public List<TraceLogRecord> fetchTraceLogEntries(String solutionType, String solutionId, String chainType,
        String conditionKey, List<Map<String, String>> conditions,
        com.huawei.opsfactory.operationintelligence.qos.model.ChainTypeConfig config, long startTime, long endTime,
        int querySize) {
        DvEnvironmentInfo env = getDvEnvironment(solutionType);
        if (env == null) {
            throw new IllegalArgumentException("No DV environment found for solutionType: " + solutionType);
        }
        return executeWithRetry(() -> doFetchTraceLogEntries(env, solutionId, chainType, conditionKey, conditions,
            config, startTime, endTime, querySize), "fetchTraceLogEntries[" + env.getEnvCode() + "]");
    }

    /**
     * Fetch TraceLog by TraceID.
     *
     * @param solutionType the solution type (envCode)
     * @param solutionId the solution id (for DV must filter)
     * @param traceId the trace ID
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return list of trace log records
     */
    public List<TraceLogRecord> fetchByTraceId(String solutionType, String solutionId, String traceId, long startTime,
        long endTime, int querySize) {
        DvEnvironmentInfo env = getDvEnvironment(solutionType);
        if (env == null) {
            throw new IllegalArgumentException("No DV environment found for solutionType: " + solutionType);
        }
        return executeWithRetry(() -> doFetchByTraceId(env, solutionId, traceId, startTime, endTime, querySize),
            "fetchByTraceId[" + env.getEnvCode() + "]");
    }

    /**
     * Internal implementation of fetching trace log entries.
     *
     * @param env the DV environment info
     * @param solutionId the solution id (for DV must filter)
     * @param chainType the chain type
     * @param conditionKey the primary condition key
     * @param conditions the list of conditions
     * @param config the chain type configuration
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return list of trace log records
     */
    private List<TraceLogRecord> doFetchTraceLogEntries(DvEnvironmentInfo env, String solutionId, String chainType,
        String conditionKey, List<Map<String, String>> conditions,
        com.huawei.opsfactory.operationintelligence.qos.model.ChainTypeConfig config, long startTime, long endTime,
        int querySize) {
        try {
            Map<String, Object> body = buildTraceLogQuery(chainType, conditionKey, conditions, config,
                env.getAgentSolutionType(), solutionId, startTime, endTime, querySize);
            String response = executeTraceLogPost(env, body);
            if (response == null || response.isBlank()) {
                return Collections.emptyList();
            }
            return parseTraceLogResponse(response);
        } catch (IOException | IllegalStateException e) {
            throw new IllegalStateException(
                "Failed to fetch tracelog from " + env.getServerUrl() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Internal implementation of fetching by trace ID.
     *
     * @param env the DV environment info
     * @param solutionId the solution id (for DV must filter)
     * @param traceId the trace ID
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return list of trace log records
     */
    private List<TraceLogRecord> doFetchByTraceId(DvEnvironmentInfo env, String solutionId, String traceId,
        long startTime, long endTime, int querySize) {
        try {
            Map<String, Object> body = buildTraceLogQueryByTraceId(traceId, env.getAgentSolutionType(), solutionId,
                startTime, endTime, querySize);
            String jsonBody = MAPPER.writeValueAsString(body);

            log.info("[TraceLog Request] URL: {}{}, TraceId: {}", env.getServerUrl(), PATH_TRACELOG, traceId);

            String response = executePost(env, PATH_TRACELOG, jsonBody);

            log.info("[TraceLog Response] TraceId: {}, Status: {}", traceId,
                response != null && !response.isBlank() ? "OK" : "Empty");

            if (response == null || response.isBlank()) {
                return Collections.emptyList();
            }

            return parseTraceLogResponse(response);
        } catch (IOException | IllegalStateException e) {
            throw new IllegalStateException(
                "Failed to fetch tracelog by traceId from " + env.getServerUrl() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Build tracelog query request body.
     *
     * @param chainType the chain type
     * @param conditionKey the primary condition key
     * @param conditions the list of conditions
     * @param config the chain type configuration
     * @param agentSolutionType the agent solution type
     * @param solutionId the solution id (for DV must filter)
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return the query request body map
     */
    private Map<String, Object> buildTraceLogQuery(String chainType, String conditionKey,
        List<Map<String, String>> conditions,
        com.huawei.opsfactory.operationintelligence.qos.model.ChainTypeConfig config, String agentSolutionType,
        String solutionId, long startTime, long endTime, int querySize) {
        Map<String, Object> must = new LinkedHashMap<>();

        // TraceID prefix filter
        must.put("TraceID", chainType + "*");

        // SolutionId filter
        must.put("SolutionId", solutionId);

        // Build AppendInfo filter for entry logs
        // Start with seqNo=1, then append conditions based on conditionKeyOnAppendInfo config
        StringBuilder appendInfoFilter = new StringBuilder("*seqNo=1*");

        // Get condition keys that should be appended to AppendInfo
        String conditionKeyOnAppendInfo = config != null ? config.getConditionKeyOnAppendInfo() : null;
        List<String> appendInfoKeys = new ArrayList<>();
        if (conditionKeyOnAppendInfo != null && !conditionKeyOnAppendInfo.isEmpty()) {
            String[] keys = conditionKeyOnAppendInfo.split(",");
            for (String key : keys) {
                appendInfoKeys.add(key.trim());
            }
        }

        // Add all conditions to must clause
        if (conditions != null && !conditions.isEmpty()) {
            for (Map<String, String> condition : conditions) {
                String key = condition.get("conditionKey");
                String value = condition.get("conditionValue");
                if (key != null && value != null && !value.isEmpty()) {
                    // If this key should be appended to AppendInfo filter
                    if (appendInfoKeys.contains(key)) {
                        appendInfoFilter.append(key).append("=").append(value).append("*");
                    } else {
                        // Otherwise add as separate must clause
                        must.put(key, value);
                    }
                }
            }
        }

        // Set AppendInfo filter
        must.put("AppendInfo", appendInfoFilter.toString());

        return buildTraceLogBody(agentSolutionType, must, startTime, endTime, querySize);
    }

    /**
     * Build tracelog query request body by TraceID.
     *
     * @param traceId the trace ID
     * @param agentSolutionType the agent solution type
     * @param solutionId the solution id (for DV must filter)
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return the query request body map
     */
    private Map<String, Object> buildTraceLogQueryByTraceId(String traceId, String agentSolutionType, String solutionId,
        long startTime, long endTime, int querySize) {
        Map<String, Object> must = new LinkedHashMap<>();

        // Exact TraceID match
        must.put("TraceID", traceId);

        // SolutionId filter
        must.put("SolutionId", solutionId);

        return buildTraceLogBody(agentSolutionType, must, startTime, endTime, querySize);
    }

    /**
     * Builds the common tracelog query body structure shared by both query variants.
     *
     * @param agentSolutionType the agent solution type
     * @param must the must-condition map (TraceID, SolutionId, etc.)
     * @param startTime the start time in milliseconds
     * @param endTime the end time in milliseconds
     * @param querySize the query page size
     * @return the query request body map
     */
    private Map<String, Object> buildTraceLogBody(String agentSolutionType, Map<String, Object> must, long startTime,
        long endTime, int querySize) {
        List<Map<String, Object>> sort = List.of(Map.of("fieldName", "Time", "order", "desc"));

        // Use LinkedHashMap to preserve field order
        Map<String, Object> customIndexItem = new LinkedHashMap<>();
        customIndexItem.put("logtype", "tracelog");
        customIndexItem.put("solutionType", agentSolutionType);
        List<Map<String, Object>> customIndex = List.of(customIndexItem);

        Map<String, Object> fieldCondition = new LinkedHashMap<>();
        Map<String, Object> boolCondition = new LinkedHashMap<>();
        boolCondition.put("must", must);
        fieldCondition.put("boolCondition", boolCondition);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sort", sort);
        body.put("solutionType", agentSolutionType);
        body.put("customIndex", customIndex);
        body.put("from", "0");
        body.put("size", String.valueOf(querySize));
        body.put("beginTime", formatTime(startTime));
        body.put("endTime", formatTime(endTime));
        body.put("timePattern", "yyyy-MM-dd HH:mm:ss.SSS");
        body.put("fieldCondition", fieldCondition);

        return body;
    }

    /**
     * Format timestamp to DV time format.
     *
     * @param timestamp the timestamp in milliseconds
     * @return the formatted time string
     */
    private String formatTime(long timestamp) {
        java.time.Instant instant = java.time.Instant.ofEpochMilli(timestamp);
        return java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(java.time.ZoneOffset.UTC)
            .format(instant);
    }

    /**
     * Parse tracelog API response.
     *
     * @param response the response string
     * @return the list of trace log records
     */
    private List<TraceLogRecord> parseTraceLogResponse(String response) {
        List<TraceLogRecord> results = new ArrayList<>();
        if (response == null) {
            return results;
        }
        try {
            JsonNode root = MAPPER.readTree(response);
            JsonNode logsNode = root.get("logs");
            if (logsNode == null || !logsNode.isArray()) {
                log.warn("No logs array found in tracelog response");
                return results;
            }

            for (JsonNode item : logsNode) {
                TraceLogRecord record = new TraceLogRecord();
                record.setTraceId(textVal(item, "TraceID"));
                record.setIp(textVal(item, "ServerIP"));
                record.setCluster(textVal(item, "ClusterType"));
                record.setClusterId(textVal(item, "ClusterId"));
                record.setLogMessage(textVal(item, "LogMessage"));
                record.setLogTime(textVal(item, "Time"));
                record.setCost(parseCost(textVal(item, "cost")));
                record.setMoi(textVal(item, "moi"));

                // Field priority: url > serviceName
                String url = safeValue(textVal(item, "url"));
                String serviceName = safeValue(textVal(item, "serviceName"));

                if (url != null) {
                    record.setUrl(url);
                } else if (serviceName != null) {
                    record.setServiceName(serviceName);
                    record.setOperationName(textVal(item, "operationName"));
                }

                // Parse top-level fields
                record.setMenuId(textVal(item, "menuId"));
                record.setOperatorId(textVal(item, "operatorId"));
                record.setJobDefinedId(textVal(item, "jobDefinedId"));

                // Parse AppendInfo
                String appendInfo = textVal(item, "AppendInfo");
                if (appendInfo != null) {
                    parseAppendInfo(record, appendInfo);
                }

                results.add(record);
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse tracelog response: {}", e.getMessage());
        }
        return results;
    }

    /**
     * Parse AppendInfo into record.
     *
     * @param record the trace log record to populate
     * @param appendInfo the AppendInfo string
     */
    private void parseAppendInfo(TraceLogRecord record, String appendInfo) {
        String[] parts = appendInfo.split(",");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();

                switch (key) {
                    case "seqNo" -> record.setSeqNo(value);
                    case "menuId" -> record.setMenuId(value);
                    case "busiCode" -> record.setBusiCode(value);
                    case "jobDefinedId" -> record.setJobDefinedId(value);
                    case "operatorId" -> record.setOperatorId(value);
                    case "processName" -> record.setProcessName(value);
                    case "elementName" -> record.setElementName(value);
                    case "elementType" -> record.setElementType(value);
                    case "topic" -> record.setTopic(value);
                    case "eventName" -> record.setEventName(value);
                    case "serviceName" -> {
                        if (record.getServiceName() == null) {
                            record.setServiceName(value);
                        }
                    }
                    case "operationName" -> {
                        if (record.getOperationName() == null) {
                            record.setOperationName(value);
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse cost string to Long.
     *
     * @param cost the cost string
     * @return the parsed cost value, or null if invalid
     */
    private Long parseCost(String cost) {
        return JsonNodeHelper.parseCost(cost);
    }

    /**
     * Return null if value is "null" string.
     *
     * @param value the input value
     * @return the value, or null if the input is "null" string
     */
    private String safeValue(String value) {
        return JsonNodeHelper.safeValue(value);
    }
}
