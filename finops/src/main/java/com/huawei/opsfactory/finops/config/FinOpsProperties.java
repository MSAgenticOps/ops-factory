package com.huawei.opsfactory.finops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the FinOps service.
 *
 * @since 2026-05-28
 */
@ConfigurationProperties(prefix = "finops")
public class FinOpsProperties {

    private String secretKey = "";
    private String corsOrigin = "http://127.0.0.1:5173";
    private Gateway gateway = new Gateway();
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

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }

    public Scan getScan() {
        return scan;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    /**
     * Snapshot refresh settings.
     */
    public static class Scan {
        private long refreshIntervalMs = 300000;
        private boolean refreshOnStartup = true;

        public long getRefreshIntervalMs() {
            return refreshIntervalMs;
        }

        public void setRefreshIntervalMs(long refreshIntervalMs) {
            this.refreshIntervalMs = refreshIntervalMs;
        }

        public boolean isRefreshOnStartup() {
            return refreshOnStartup;
        }

        public void setRefreshOnStartup(boolean refreshOnStartup) {
            this.refreshOnStartup = refreshOnStartup;
        }
    }

    /**
     * Gateway connection settings used to read usage snapshots.
     */
    public static class Gateway {
        private String baseUrl = "http://127.0.0.1:3000";
        private String secretKey = "";
        private long timeoutMs = 30000;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public long getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(long timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }
}
