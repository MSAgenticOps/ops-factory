package com.huawei.opsfactory.finops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "finops")
public class FinOpsProperties {

    private String secretKey = "change-me";
    private String corsOrigin = "http://127.0.0.1:5173";
    private String dataRoot = "../gateway/users";
    private String snapshotDir = "./data/snapshots";
    private Scan scan = new Scan();

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCorsOrigin() {
        return corsOrigin;
    }

    public void setCorsOrigin(String corsOrigin) {
        this.corsOrigin = corsOrigin;
    }

    public String getDataRoot() {
        return dataRoot;
    }

    public void setDataRoot(String dataRoot) {
        this.dataRoot = dataRoot;
    }

    public String getSnapshotDir() {
        return snapshotDir;
    }

    public void setSnapshotDir(String snapshotDir) {
        this.snapshotDir = snapshotDir;
    }

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public static class Scan {
        private long refreshIntervalMs = 300000;
        private int maxDbOpenMs = 5000;
        private int retentionDays = 30;

        public long getRefreshIntervalMs() {
            return refreshIntervalMs;
        }

        public void setRefreshIntervalMs(long refreshIntervalMs) {
            this.refreshIntervalMs = refreshIntervalMs;
        }

        public int getMaxDbOpenMs() {
            return maxDbOpenMs;
        }

        public void setMaxDbOpenMs(int maxDbOpenMs) {
            this.maxDbOpenMs = maxDbOpenMs;
        }

        public int getRetentionDays() {
            return retentionDays;
        }

        public void setRetentionDays(int retentionDays) {
            this.retentionDays = retentionDays;
        }
    }
}
