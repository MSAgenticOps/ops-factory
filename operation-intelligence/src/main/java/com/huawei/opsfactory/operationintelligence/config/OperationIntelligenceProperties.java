
package com.huawei.opsfactory.operationintelligence.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.util.List;

@ConfigurationProperties(prefix = "operation-intelligence")
public class OperationIntelligenceProperties {

    private static final Logger log = LoggerFactory.getLogger(OperationIntelligenceProperties.class);

    private static final String CONFIG_PATH_KEY = "OI_CONFIG_PATH";

    private String secretKey = "test";

    private String corsOrigin = "*";

    private String dataRoot = "";

    private Qos qos = new Qos();

    private Logging logging = new Logging();

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

    public Qos getQos() {
        return qos;
    }

    public void setQos(Qos qos) {
        this.qos = qos;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    public Path resolveDataRoot() {
        if (dataRoot != null && !dataRoot.isBlank()) {
            Path configured = Path.of(dataRoot);
            if (configured.isAbsolute()) {
                return configured.normalize();
            }
            return getConfigDirectory().resolve(configured).normalize();
        }
        return getConfigDirectory().resolve("data").normalize();
    }

    public Path getConfigPath() {
        String configuredPath = configuredConfigPath();
        if (configuredPath == null || configuredPath.isBlank()) {
            return Path.of("config.yaml").toAbsolutePath().normalize();
        }
        Path configPath = Path.of(configuredPath);
        if (configPath.isAbsolute()) {
            return configPath.normalize();
        }
        return Path.of("").toAbsolutePath().resolve(configPath).normalize();
    }

    public Path getConfigDirectory() {
        Path configPath = getConfigPath();
        Path parent = configPath.getParent();
        if (parent != null) {
            return parent;
        }
        return Path.of("").toAbsolutePath().normalize();
    }

    /**
     * Resolve the runtime config file path from the same OI_CONFIG_PATH source used by
     * Spring's {@code spring.config.import} in application.yml. This method does NOT load
     * or parse the config file (Spring handles that); it only resolves the filesystem
     * location so that {@link #getConfigDirectory()} and {@link #resolveDataRoot()} can
     * place the data directory relative to the config file.
     */
    private String configuredConfigPath() {
        String configuredPath = System.getProperty(CONFIG_PATH_KEY);
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv(CONFIG_PATH_KEY);
        }
        return (configuredPath == null || configuredPath.isBlank()) ? null : configuredPath;
    }

    public static class Qos {
        private boolean enabled = true;

        private long collectionIntervalMs = 300000;

        private long rotationIntervalMs = 3600000;

        private long rawDataRetentionDays = 7;

        private long detailDataRetentionDays = 30;

        private long normalizeDataRetentionDays = 90;

        private Weights weights = new Weights();

        private Thresholds thresholds = new Thresholds();

        private List<DvEnvironment> dvEnvironments = List.of();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getCollectionIntervalMs() {
            return collectionIntervalMs;
        }

        public void setCollectionIntervalMs(long collectionIntervalMs) {
            this.collectionIntervalMs = collectionIntervalMs;
        }

        public long getRotationIntervalMs() {
            return rotationIntervalMs;
        }

        public void setRotationIntervalMs(long rotationIntervalMs) {
            this.rotationIntervalMs = rotationIntervalMs;
        }

        public long getRawDataRetentionDays() {
            return rawDataRetentionDays;
        }

        public void setRawDataRetentionDays(long rawDataRetentionDays) {
            this.rawDataRetentionDays = rawDataRetentionDays;
        }

        public long getDetailDataRetentionDays() {
            return detailDataRetentionDays;
        }

        public void setDetailDataRetentionDays(long detailDataRetentionDays) {
            this.detailDataRetentionDays = detailDataRetentionDays;
        }

        public long getNormalizeDataRetentionDays() {
            return normalizeDataRetentionDays;
        }

        public void setNormalizeDataRetentionDays(long normalizeDataRetentionDays) {
            this.normalizeDataRetentionDays = normalizeDataRetentionDays;
        }

        public Weights getWeights() {
            return weights;
        }

        public void setWeights(Weights weights) {
            this.weights = weights;
        }

        public Thresholds getThresholds() {
            return thresholds;
        }

        public void setThresholds(Thresholds thresholds) {
            this.thresholds = thresholds;
        }

        public List<DvEnvironment> getDvEnvironments() {
            return dvEnvironments;
        }

        public void setDvEnvironments(List<DvEnvironment> dvEnvironments) {
            this.dvEnvironments = dvEnvironments;
        }

        public static class Weights {
            private double availability = 0.4;

            private double performance = 0.4;

            private double resource = 0.2;

            public double getAvailability() {
                return availability;
            }

            public void setAvailability(double availability) {
                this.availability = availability;
            }

            public double getPerformance() {
                return performance;
            }

            public void setPerformance(double performance) {
                this.performance = performance;
            }

            public double getResource() {
                return resource;
            }

            public void setResource(double resource) {
                this.resource = resource;
            }
        }

        public static class Thresholds {
            private double good = 0.9;

            private double warning = 0.7;

            private double bad = 0.5;

            public double getGood() {
                return good;
            }

            public void setGood(double good) {
                this.good = good;
            }

            public double getWarning() {
                return warning;
            }

            public void setWarning(double warning) {
                this.warning = warning;
            }

            public double getBad() {
                return bad;
            }

            public void setBad(double bad) {
                this.bad = bad;
            }
        }

        public static class DvEnvironment {
            private String envCode;

            private String envName;

            private String agentSolutionType;

            private String productTypeName;

            private String serverUrl;

            private String utmUser;

            private String utmPassword;

            private String crtContent;

            private String crtFileName;

            private String dns;

            private boolean strictSsl = true;

            public String getEnvCode() {
                return envCode;
            }

            public void setEnvCode(String envCode) {
                this.envCode = envCode;
            }

            public String getEnvName() {
                return envName;
            }

            public void setEnvName(String envName) {
                this.envName = envName;
            }

            public String getAgentSolutionType() {
                return agentSolutionType;
            }

            public void setAgentSolutionType(String agentSolutionType) {
                this.agentSolutionType = agentSolutionType;
            }

            public String getProductTypeName() {
                return productTypeName;
            }

            public void setProductTypeName(String productTypeName) {
                this.productTypeName = productTypeName;
            }

            public String getServerUrl() {
                return serverUrl;
            }

            public void setServerUrl(String serverUrl) {
                this.serverUrl = serverUrl;
            }

            public String getUtmUser() {
                return utmUser;
            }

            public void setUtmUser(String utmUser) {
                this.utmUser = utmUser;
            }

            @com.fasterxml.jackson.annotation.JsonIgnore
            public String getUtmPassword() {
                return utmPassword;
            }

            public void setUtmPassword(String utmPassword) {
                this.utmPassword = utmPassword;
            }

            public String getCrtContent() {
                return crtContent;
            }

            public void setCrtContent(String crtContent) {
                this.crtContent = crtContent;
            }

            public String getCrtFileName() {
                return crtFileName;
            }

            public void setCrtFileName(String crtFileName) {
                this.crtFileName = crtFileName;
            }

            public String getDns() {
                return dns;
            }

            public void setDns(String dns) {
                this.dns = dns;
            }

            public boolean isStrictSsl() {
                return strictSsl;
            }

            public void setStrictSsl(boolean strictSsl) {
                this.strictSsl = strictSsl;
            }
        }
    }

    public static class Logging {
        private boolean accessLogEnabled = true;

        public boolean isAccessLogEnabled() {
            return accessLogEnabled;
        }

        public void setAccessLogEnabled(boolean accessLogEnabled) {
            this.accessLogEnabled = accessLogEnabled;
        }
    }
}
