package com.huawei.opsfactory.finops.service;

import com.huawei.opsfactory.finops.config.FinOpsProperties;
import com.huawei.opsfactory.finops.model.FinOpsModels.UsageSnapshotPayload;
import com.huawei.opsfactory.finops.store.FinOpsSnapshotStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Refreshes the in-memory FinOps snapshot from the configured gateway.
 *
 * @since 2026-05-28
 */
@Service
public class UsageIngestionService {

    private static final Logger log = LoggerFactory.getLogger(UsageIngestionService.class);

    private final FinOpsProperties properties;
    private final GatewayUsageSnapshotClient snapshotClient;
    private final FinOpsSnapshotStore snapshotStore;

    public UsageIngestionService(FinOpsProperties properties,
                                 GatewayUsageSnapshotClient snapshotClient,
                                 FinOpsSnapshotStore snapshotStore) {
        this.properties = properties;
        this.snapshotClient = snapshotClient;
        this.snapshotStore = snapshotStore;
    }

    @PostConstruct
    public void refreshOnStartup() {
        if (!properties.getScan().isRefreshOnStartup()) {
            return;
        }
        refresh();
    }

    @Scheduled(
        fixedDelayString = "${finops.scan.refresh-interval-ms:300000}",
        initialDelayString = "${finops.scan.refresh-interval-ms:300000}"
    )
    public void scheduledRefresh() {
        refresh();
    }

    public FinOpsSnapshotStore.Snapshot refresh() {
        try {
            UsageSnapshotPayload result = snapshotClient.fetchSnapshot();
            FinOpsSnapshotStore.Snapshot snapshot = snapshotStore.update(result);
            log.info(
                "FinOps snapshot refreshed sessions={} sourceDbs={} skippedDbs={}",
                snapshot.status().sessionCount(),
                snapshot.status().sourceDbCount(),
                snapshot.status().skippedDbCount()
            );
            return snapshot;
        } catch (RuntimeException ex) {
            log.warn("FinOps snapshot refresh failed; preserving previous snapshot", ex);
            return snapshotStore.markFailed(ex);
        }
    }
}
