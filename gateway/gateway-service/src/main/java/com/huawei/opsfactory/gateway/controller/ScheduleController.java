/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.common.model.ManagedInstance;
import com.huawei.opsfactory.gateway.filter.UserContextFilter;
import com.huawei.opsfactory.gateway.process.InstanceManager;
import com.huawei.opsfactory.gateway.proxy.GoosedProxy;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveDeliveryMarkerService;
import com.huawei.opsfactory.gateway.service.proactive.ProactiveDeliveryMarkers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * REST controller for scheduled job management through goosed.
 *
 * @author x00000000
 * @since 2026-05-30
 */
@RestController
@RestSchema(schemaId = "scheduleController")
@RequestMapping("/api/gateway/agents/{agentId}/schedule")
public class ScheduleController {
    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final int MAX_SCHEDULE_ID_LENGTH = 200;

    private static final Pattern SAFE_SCHEDULE_ID = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final InstanceManager instanceManager;

    private final GoosedProxy goosedProxy;

    private final ProactiveDeliveryMarkerService deliveryMarkerService;

    /**
     * Creates the schedule controller instance.
     *
     * @param instanceManager agent instance lifecycle manager
     * @param goosedProxy HTTP proxy for forwarding requests to goosed processes
     * @param deliveryMarkerService persists the per-user "deliver report to IM" marker for a schedule
     */
    public ScheduleController(InstanceManager instanceManager, GoosedProxy goosedProxy,
        ProactiveDeliveryMarkerService deliveryMarkerService) {
        this.instanceManager = instanceManager;
        this.goosedProxy = goosedProxy;
        this.deliveryMarkerService = deliveryMarkerService;
    }

    /**
     * Creates a scheduled job. When {@code deliver=im} is supplied, also records (Gateway-side, keyed by schedule
     * id) that this schedule's report should be pushed to the user's bound IM channels.
     *
     * @param agentId agent identifier
     * @param body request body (forwarded verbatim to goosed: {@code {id, recipe, cron}})
     * @param deliver optional delivery channel; {@code im} marks the schedule for IM push
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSchedule(@PathVariable("agentId") String agentId, @RequestBody String body,
        @RequestParam(value = "deliver", required = false) String deliver, HttpServletRequest request) {
        // Forward the schedule body verbatim to goosed (it only accepts {id, recipe, cron}); the deliver flag is a
        // Gateway-side concern persisted separately, keyed by schedule id, so goosed never sees it.
        ResponseEntity<String> response = jsonProxy(agentId, request, HttpMethod.POST, "/schedule/create", body);
        // Reconcile the Gateway-side delivery marker with the toggle: set it for deliver=im, clear it otherwise so a
        // recreate that turned delivery off does not leave a stale marker.
        reconcileDeliverMarker(agentId, request, extractScheduleId(body), deliver);
        return response;
    }

    /**
     * Lists all scheduled jobs for the agent.
     *
     * @param agentId agent identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listSchedules(@PathVariable("agentId") String agentId, HttpServletRequest request) {
        ResponseEntity<String> response = jsonProxy(agentId, request, HttpMethod.GET, "/schedule/list", null);
        // Annotate each job with its Gateway-side deliver marker so the UI can show the real "deliver to IM" state
        // (the marker lives on the gateway, not in goosed's schedule list).
        String userId = (String) request.getAttribute(UserContextFilter.USER_ID_ATTR);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
            .body(annotateDeliverMarkers(agentId, userId, response.getBody()));
    }

    /**
     * Updates a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param body request body
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSchedule(@PathVariable("agentId") String agentId, @PathVariable("id") String id,
        @RequestBody String body, HttpServletRequest request) {
        return jsonProxy(agentId, request, HttpMethod.PUT, "/schedule/" + encode(id), body);
    }

    /**
     * Deletes a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteSchedule(@PathVariable("agentId") String agentId,
        @PathVariable("id") String id, HttpServletRequest request) {
        ResponseEntity<String> response =
            jsonProxy(agentId, request, HttpMethod.DELETE, "/schedule/delete/" + encode(id), null);
        // Clear any delivery marker for the deleted schedule so it cannot leave a dangling IM-push target.
        String userId = (String) request.getAttribute(UserContextFilter.USER_ID_ATTR);
        if (userId != null) {
            deliveryMarkerService.remove(userId, agentId, id);
        }
        return response;
    }

    /**
     * Runs a scheduled job immediately.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @PostMapping(value = "/{id}/run_now", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> runScheduleNow(@PathVariable("agentId") String agentId,
        @PathVariable("id") String id, HttpServletRequest request) {
        return jsonProxy(agentId, request, HttpMethod.POST, "/schedule/" + encode(id) + "/run_now", null);
    }

    /**
     * Pauses a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @PostMapping(value = "/{id}/pause", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> pauseSchedule(@PathVariable("agentId") String agentId, @PathVariable("id") String id,
        HttpServletRequest request) {
        return jsonProxy(agentId, request, HttpMethod.POST, "/schedule/" + encode(id) + "/pause", null);
    }

    /**
     * Unpauses a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @PostMapping(value = "/{id}/unpause", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> unpauseSchedule(@PathVariable("agentId") String agentId,
        @PathVariable("id") String id, HttpServletRequest request) {
        return jsonProxy(agentId, request, HttpMethod.POST, "/schedule/" + encode(id) + "/unpause", null);
    }

    /**
     * Lists recent sessions started by a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @GetMapping(value = "/{id}/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> listScheduleSessions(@PathVariable("agentId") String agentId,
        @PathVariable("id") String id, HttpServletRequest request) {
        String path = appendQueryString("/schedule/" + encode(id) + "/sessions", request);
        return jsonProxy(agentId, request, HttpMethod.GET, path, null);
    }

    /**
     * Kills the currently running process for a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @PostMapping(value = "/{id}/kill", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> killSchedule(@PathVariable("agentId") String agentId, @PathVariable("id") String id,
        HttpServletRequest request) {
        return jsonProxy(agentId, request, HttpMethod.POST, "/schedule/" + encode(id) + "/kill", null);
    }

    /**
     * Inspects the running state of a scheduled job.
     *
     * @param agentId agent identifier
     * @param id scheduled job identifier
     * @param request current HTTP request
     * @return proxied goosed response
     */
    @GetMapping(value = "/{id}/inspect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> inspectSchedule(@PathVariable("agentId") String agentId,
        @PathVariable("id") String id, HttpServletRequest request) {
        return jsonProxy(agentId, request, HttpMethod.GET, "/schedule/" + encode(id) + "/inspect", null);
    }

    private ResponseEntity<String> jsonProxy(String agentId, HttpServletRequest request, HttpMethod method, String path,
        String body) {
        ManagedInstance instance = resolveInstance(agentId, request);
        String result =
            goosedProxy.fetchJson(instance.getPort(), method, path, body, 30, instance.getSecretKey()).block();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(result);
    }

    private void reconcileDeliverMarker(String agentId, HttpServletRequest request, String scheduleId, String deliver) {
        boolean wantsImDelivery = ProactiveDeliveryMarkers.DELIVER_IM.equalsIgnoreCase(deliver);
        String userId = (String) request.getAttribute(UserContextFilter.USER_ID_ATTR);
        if (userId == null || !isValidScheduleId(scheduleId)) {
            if (wantsImDelivery) {
                log.warn("deliver=im requested but schedule id missing/invalid; IM delivery NOT set for agent {}",
                    agentId);
            }
            return;
        }
        if (wantsImDelivery) {
            deliveryMarkerService.setDeliver(userId, agentId, scheduleId, ProactiveDeliveryMarkers.DELIVER_IM);
        } else {
            deliveryMarkerService.remove(userId, agentId, scheduleId);
        }
    }

    private boolean isValidScheduleId(String scheduleId) {
        return scheduleId != null && scheduleId.length() <= MAX_SCHEDULE_ID_LENGTH
            && SAFE_SCHEDULE_ID.matcher(scheduleId).matches();
    }

    private String annotateDeliverMarkers(String agentId, String userId, String listJson) {
        if (userId == null || listJson == null || listJson.isBlank()) {
            return listJson;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(listJson);
            JsonNode jobs = root.get("jobs");
            if (jobs != null && jobs.isArray()) {
                for (JsonNode job : jobs) {
                    JsonNode id = job.get("id");
                    if (job instanceof ObjectNode jobObj && id != null && id.isTextual()) {
                        jobObj.put("deliver", deliveryMarkerService.getDeliver(userId, agentId, id.asText()));
                    }
                }
            }
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            // Unexpected goosed response shape — pass it through unchanged rather than failing the list.
            return listJson;
        }
    }

    private String extractScheduleId(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            JsonNode id = OBJECT_MAPPER.readTree(body).get("id");
            return id != null && id.isTextual() ? id.asText() : null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private ManagedInstance resolveInstance(String agentId, HttpServletRequest request) {
        String userId = (String) request.getAttribute(UserContextFilter.USER_ID_ATTR);
        ManagedInstance instance = instanceManager.getOrSpawn(agentId, userId).block();
        if (instance == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to resolve agent instance: " + agentId);
        }
        return instance;
    }

    private String appendQueryString(String path, HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString == null || queryString.isBlank()) {
            return path;
        }
        return path + "?" + sanitizeForProxyPath(queryString);
    }

    private String sanitizeForProxyPath(String value) {
        return value.replace('\r', '_').replace('\n', '_');
    }

    private String encode(String value) {
        return UriUtils.encodePathSegment(value, StandardCharsets.UTF_8);
    }
}
