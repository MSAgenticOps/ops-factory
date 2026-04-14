package com.huawei.opsfactory.businessintelligence.api;

import com.huawei.opsfactory.businessintelligence.model.BiModels.Snapshot;
import com.huawei.opsfactory.businessintelligence.model.BiModels.TabContent;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business-intelligence")
public class BusinessIntelligenceController {

    private final BusinessIntelligenceService businessIntelligenceService;

    public BusinessIntelligenceController(BusinessIntelligenceService businessIntelligenceService) {
        this.businessIntelligenceService = businessIntelligenceService;
    }

    @GetMapping("/overview")
    public Snapshot getOverview() {
        return businessIntelligenceService.getOverview();
    }

    @PostMapping("/refresh")
    public Snapshot refresh() {
        return businessIntelligenceService.refresh();
    }

    @GetMapping("/tabs/{tabId}")
    public TabContent getTab(@PathVariable("tabId") String tabId) {
        return businessIntelligenceService.getTab(tabId);
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
}
