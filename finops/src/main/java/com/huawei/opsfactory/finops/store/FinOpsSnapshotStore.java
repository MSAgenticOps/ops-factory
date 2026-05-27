package com.huawei.opsfactory.finops.store;

import com.huawei.opsfactory.finops.model.FinOpsModels.SessionMessageRecord;
import com.huawei.opsfactory.finops.model.FinOpsModels.SessionUsageRecord;
import com.huawei.opsfactory.finops.model.FinOpsModels.SnapshotStatus;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Component;

@Component
public class FinOpsSnapshotStore {

    private final AtomicReference<Snapshot> current = new AtomicReference<>(
        new Snapshot(List.of(), List.of(), new SnapshotStatus("empty", null, 0, 0, 0, null), "")
    );

    public Snapshot current() {
        return current.get();
    }

    public Snapshot update(SessionDbReader.ScanResult scanResult) {
        Snapshot snapshot = new Snapshot(
            List.copyOf(scanResult.sessions()),
            List.copyOf(scanResult.messages()),
            new SnapshotStatus(
                scanResult.skippedDbCount() > 0 ? "partial" : "ready",
                Instant.now(),
                scanResult.sourceDbCount(),
                scanResult.skippedDbCount(),
                scanResult.sessions().size(),
                scanResult.lastError()
            ),
            scanResult.dataSource()
        );
        current.set(snapshot);
        return snapshot;
    }

    public Snapshot markFailed(Exception ex) {
        Snapshot previous = current.get();
        Snapshot failed = new Snapshot(
            previous.sessions(),
            previous.messages(),
            new SnapshotStatus(
                "stale",
                previous.status().lastRefreshedAt(),
                previous.status().sourceDbCount(),
                previous.status().skippedDbCount(),
                previous.status().sessionCount(),
                ex.getMessage()
            ),
            previous.dataSource()
        );
        current.set(failed);
        return failed;
    }

    public record Snapshot(
        List<SessionUsageRecord> sessions,
        List<SessionMessageRecord> messages,
        SnapshotStatus status,
        String dataSource
    ) {
    }
}
