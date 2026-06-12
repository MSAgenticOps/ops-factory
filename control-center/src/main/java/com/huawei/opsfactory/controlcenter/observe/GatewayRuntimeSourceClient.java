/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.observe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.opsfactory.controlcenter.config.ControlCenterProperties;
import com.huawei.opsfactory.controlcenter.registry.ManagedServiceRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
/**
 * Gateway Runtime Source Client.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class GatewayRuntimeSourceClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpSupport httpSupport;
    private final ControlCenterProperties.ServiceTarget gateway;

    public GatewayRuntimeSourceClient(HttpSupport httpSupport, ManagedServiceRegistry registry) {
        this.httpSupport = httpSupport;
        this.gateway = registry.require("gateway");
    }

    public Map<String, Object> getSystem() {
        return getMap("/api/gateway/runtime-source/system");
    }

    public Map<String, Object> getInstances() {
        return getMap("/api/gateway/runtime-source/instances");
    }

    public Map<String, Object> getAgents() {
        return getMap("/api/gateway/agents");
    }

    public Map<String, Object> getMetrics() {
        return getMap("/api/gateway/runtime-source/metrics");
    }

    /**
     * Triggers a lifecycle action (start, stop, restart) on a single goosed instance.
     *
     * @param agentId agent identifier owning the instance
     * @param userId user identifier owning the instance
     * @param action lifecycle action name
     * @return the gateway response body
     */
    public Map<String, Object> postInstanceAction(String agentId, String userId, String action) {
        return postMap("/api/gateway/runtime-source/instances/" + encodeSegment(agentId) + "/"
                + encodeSegment(userId) + "/" + action);
    }

    private static String encodeSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    @FunctionalInterface
    private interface HttpCall {
        HttpResponse<String> execute() throws IOException, InterruptedException;
    }

    private Map<String, Object> getMap(String path) {
        return send(path, () -> httpSupport.get(gateway.getBaseUrl() + path, buildHeaders()));
    }

    private Map<String, Object> postMap(String path) {
        return send(path, () -> httpSupport.post(gateway.getBaseUrl() + path, buildHeaders()));
    }

    private Map<String, Object> send(String path, HttpCall call) {
        try {
            HttpResponse<String> response = call.execute();
            return parseResponse(response.statusCode(), response.body());
        } catch (InterruptedException e) {
            // Restore the interrupt flag so callers up the stack can observe the interruption.
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling gateway runtime source " + path, e);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed to call gateway runtime source " + path + ": " + e.getMessage(), e);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-secret-key", gateway.getAuth().getSecretKey());
        headers.set("x-user-id", "admin");
        return headers;
    }

    private static Map<String, Object> parseResponse(int statusCode, String body) throws JsonProcessingException {
        if (statusCode < 200 || statusCode >= 300) {
            throw new IllegalStateException("Gateway runtime source returned HTTP " + statusCode);
        }
        return MAPPER.readValue(body, new TypeReference<Map<String, Object>>() {});
    }
}
