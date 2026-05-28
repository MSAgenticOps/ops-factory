/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.controller.finops;

import com.huawei.opsfactory.gateway.model.finops.FinOpsUsageSnapshotModels.SnapshotPayload;
import com.huawei.opsfactory.gateway.service.finops.FinOpsUsageSnapshotService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal FinOps endpoint for gateway-owned session usage snapshots.
 *
 * @since 2026-05-28
 */
@RestController
@RequestMapping("/gateway/internal/finops")
public class FinOpsUsageSnapshotController {

    private final FinOpsUsageSnapshotService snapshotService;

    public FinOpsUsageSnapshotController(FinOpsUsageSnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    /**
     * Returns normalized token usage extracted from gateway-managed goosed sessions.
     *
     * @return current usage snapshot
     */
    @GetMapping(value = "/session-usage", produces = MediaType.APPLICATION_JSON_VALUE)
    public SnapshotPayload getSessionUsage() {
        return snapshotService.snapshot();
    }
}
