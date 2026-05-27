package com.huawei.opsfactory.finops.service;

import com.huawei.opsfactory.finops.store.FinOpsSnapshotStore;
import com.huawei.opsfactory.finops.store.SessionDbReader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UsageIngestionService {

    private static final Logger log = LoggerFactory.getLogger(UsageIngestionService.class);

    private final SessionDbReader reader;
    private final FinOpsSnapshotStore snapshotStore;

    public UsageIngestionService(SessionDbReader reader, FinOpsSnapshotStore snapshotStore) {
        this.reader = reader;
        this.snapshotStore = snapshotStore;
    }

    @PostConstruct
    public void refreshOnStartup() {
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
            SessionDbReader.ScanResult result = reader.scan();
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
