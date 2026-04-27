package com.huawei.opsfactory.businessintelligence.api;

import com.huawei.opsfactory.businessintelligence.model.BiModels.Snapshot;
import com.huawei.opsfactory.businessintelligence.model.BiModels.TabContent;
import com.huawei.opsfactory.businessintelligence.model.MetricsModels.DataQueryRequest;
import com.huawei.opsfactory.businessintelligence.service.BusinessIntelligenceMetricsService;
import com.huawei.opsfactory.businessintelligence.service.BusinessIntelligenceService;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business-intelligence")
public class BusinessIntelligenceController {

    private final BusinessIntelligenceService businessIntelligenceService;
    private final BusinessIntelligenceMetricsService metricsService;

    public BusinessIntelligenceController(BusinessIntelligenceService businessIntelligenceService,
                                           BusinessIntelligenceMetricsService metricsService) {
        this.businessIntelligenceService = businessIntelligenceService;
        this.metricsService = metricsService;
    }

    @GetMapping("/overview")
    public Snapshot getOverview(
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return businessIntelligenceService.getOverview(startDate, endDate);
    }

    @PostMapping("/refresh")
    public Snapshot refresh(
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return businessIntelligenceService.refresh(startDate, endDate);
    }

    @GetMapping("/tabs/{tabId}")
    public TabContent getTab(
        @PathVariable("tabId") String tabId,
        @RequestParam(value = "granularity", required = false) String granularity
    ) {
        return businessIntelligenceService.getTab(tabId, granularity);
    }

    @GetMapping("/export.xlsx")
    public ResponseEntity<ByteArrayResource> exportWorkbook() {
        byte[] bytes = businessIntelligenceService.exportCurrentWorkbook();
        String filename = "business-intelligence-" + DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(Instant.now().atZone(java.time.ZoneId.systemDefault())) + ".xlsx";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(new ByteArrayResource(bytes));
    }

    @GetMapping("/metrics/{domain}")
    public Object getMetrics(
        @PathVariable("domain") String domain,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(value = "personLimit", required = false) Integer personLimit
    ) {
        return switch (domain) {
            case "executive" -> metricsService.getExecutiveMetrics(startDate, endDate);
            case "sla" -> metricsService.getSlaMetrics(startDate, endDate);
            case "incidents" -> metricsService.getIncidentMetrics(startDate, endDate);
            case "changes" -> metricsService.getChangeMetrics(startDate, endDate);
            case "requests" -> metricsService.getRequestMetrics(startDate, endDate);
            case "problems" -> metricsService.getProblemMetrics(startDate, endDate);
            case "cross-process" -> metricsService.getCrossProcessMetrics(startDate, endDate);
            case "workforce" -> metricsService.getWorkforceMetrics(startDate, endDate, personLimit != null ? personLimit : 10);
            default -> throw new IllegalArgumentException("Unknown metrics domain: " + domain);
        };
    }

    @PostMapping("/data/{domain}/query")
    public Object queryData(
        @PathVariable("domain") String domain,
        @RequestBody DataQueryRequest request
    ) {
        return metricsService.query(domain, request);
    }

    @GetMapping("/data/{domain}/lineage")
    public Object traceLineage(
        @PathVariable("domain") String domain,
        @RequestParam("ticketId") String ticketId
    ) {
        return metricsService.traceLineage(domain, ticketId);
    }

    @GetMapping("/data/{domain}/trends")
    public Object getTrends(
        @PathVariable("domain") String domain,
        @RequestParam("metric") String metric,
        @RequestParam("interval") String interval,
        @RequestParam(value = "timeRange", required = false) String timeRange,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate
    ) {
        return metricsService.getTrends(domain, metric, interval, timeRange, startDate, endDate);
    }
}
