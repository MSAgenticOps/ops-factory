package com.huawei.opsfactory.gateway.controller;

import com.huawei.opsfactory.gateway.service.QosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/gateway/qos")
public class QosController {

    private static final Logger log = LoggerFactory.getLogger(QosController.class);

    private final QosService qosService;

    public QosController(QosService qosService) {
        this.qosService = qosService;
    }

    @PostMapping("/getHealthIndicator")
    public Mono<Map<String, Object>> getHealthIndicator(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String envCode = (String) req.get("envCode");
            long startTime = toLong(req.get("startTime"));
            long endTime = toLong(req.get("endTime"));
            List<Map<String, Object>> results = qosService.getHealthIndicator(envCode, startTime, endTime);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("results", results);
            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/getAvailableIndicatorDetail")
    public Mono<Map<String, Object>> getAvailableIndicatorDetail(@RequestBody Map<String, Object> req) {
        return getIndicatorDetail(req, "A");
    }

    @PostMapping("/getPerformanceIndicatorDetail")
    public Mono<Map<String, Object>> getPerformanceIndicatorDetail(@RequestBody Map<String, Object> req) {
        return getIndicatorDetail(req, "P");
    }

    @PostMapping("/getResourceIndicatorDetail")
    public Mono<Map<String, Object>> getResourceIndicatorDetail(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String envCode = (String) req.get("envCode");
            long startTime = toLong(req.get("startTime"));
            long endTime = toLong(req.get("endTime"));
            List<?> results = qosService.getResourceNormalize(envCode, startTime, endTime);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("results", results);
            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/getContributionData")
    public Mono<Map<String, Object>> getContributionData(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String envCode = (String) req.get("envCode");
            long startTime = toLong(req.get("startTime"));
            long endTime = toLong(req.get("endTime"));
            List<Map<String, Object>> results = qosService.getContributionData(envCode, startTime, endTime);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("results", results);
            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/getAlarmIndicatorDetail")
    public Mono<Map<String, Object>> getAlarmIndicatorDetail(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String envCode = (String) req.get("envCode");
            long startTime = toLong(req.get("startTime"));
            long endTime = toLong(req.get("endTime"));
            int pageIndex = req.containsKey("pageIndex") ? toInt(req.get("pageIndex")) : 1;
            int pageSize = req.containsKey("pageSize") ? toInt(req.get("pageSize")) : 10;
            return qosService.getAlarmDetail(envCode, startTime, endTime, pageIndex, pageSize);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/getProductConfigRule")
    public Mono<ResponseEntity<Map<String, Object>>> getProductConfigRule(@RequestBody Map<String, Object> req) {
        return Mono.fromCallable(() -> {
            String agentSolutionType = (String) req.get("agentSolutionType");
            Map<String, Object> rule = new LinkedHashMap<>();
            rule.put("result", qosService.getProductConfigRule(agentSolutionType));
            return ResponseEntity.ok(rule);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/getEnvironments")
    public Mono<Map<String, Object>> getEnvironments() {
        return Mono.fromCallable(() -> {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("results", qosService.getEnvironments());
            return response;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Map<String, Object>> getIndicatorDetail(Map<String, Object> req, String type) {
        return Mono.fromCallable(() -> {
            String envCode = (String) req.get("envCode");
            long startTime = toLong(req.get("startTime"));
            long endTime = toLong(req.get("endTime"));
            int pageIndex = req.containsKey("pageIndex") ? toInt(req.get("pageIndex")) : 1;
            int pageSize = req.containsKey("pageSize") ? toInt(req.get("pageSize")) : 10;
            return qosService.getIndicatorDetail(envCode, type, startTime, endTime, pageIndex, pageSize);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private static long toLong(Object val) {
        if (val instanceof Number) return ((Number) val).longValue();
        if (val instanceof String) return Long.parseLong((String) val);
        return 0;
    }

    private static int toInt(Object val) {
        if (val instanceof Number) return ((Number) val).intValue();
        if (val instanceof String) return Integer.parseInt((String) val);
        return 1;
    }
}
