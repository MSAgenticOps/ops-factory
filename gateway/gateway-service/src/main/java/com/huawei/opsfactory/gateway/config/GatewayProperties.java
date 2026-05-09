/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Central configuration properties bound to the {@code gateway} prefix in application config.
 *
 * @author x00000000
 * @since 2026-05-09
 */
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayProperties {
    private static final Logger log = LoggerFactory.getLogger(GatewayProperties.class);
    private static final String CONFIG_PATH_KEY = "GATEWAY_CONFIG_PATH";

    private String secretKey = "test";
    private String corsOrigin = "http://127.0.0.1:5173";
    private String goosedBin = "goosed";
    private boolean gooseTls = true;

    private Paths paths = new Paths();
    private Idle idle = new Idle();
    private Upload upload = new Upload();
    private Limits limits = new Limits();
    private Prewarm prewarm = new Prewarm();
    private Sse sse = new Sse();
    private Langfuse langfuse = new Langfuse();
    private OfficePreview officePreview = new OfficePreview();
    private Logging logging = new Logging();
    private String credentialEncryptionKey = "changeit-changeit-changeit-32";
    private RemoteExecution remoteExecution = new RemoteExecution();
    private FileCapsules fileCapsules = new FileCapsules();
    private FileBrowser files = new FileBrowser();
    private SkillMarket skillMarket = new SkillMarket();
    private Knowledge knowledge = new Knowledge();
    private List<String> adminUsers = List.of("admin");

    // ---- Getters / Setters ----

    /**
     * Returns the secret key used for gateway authentication.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets the secret key used for gateway authentication.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Returns the allowed CORS origin pattern.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getCorsOrigin() {
        return corsOrigin;
    }

    /**
     * Sets the allowed CORS origin pattern.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setCorsOrigin(String corsOrigin) {
        this.corsOrigin = corsOrigin;
    }

    /**
     * Returns the path to the goosed binary.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getGoosedBin() {
        return goosedBin;
    }

    /**
     * Sets the path to the goosed binary.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setGoosedBin(String goosedBin) {
        this.goosedBin = goosedBin;
    }

    /**
     * Returns whether TLS is enabled for goosed communication.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public boolean isGooseTls() {
        return gooseTls;
    }

    /**
     * Sets whether TLS is enabled for goosed communication.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setGooseTls(boolean gooseTls) {
        this.gooseTls = gooseTls;
    }

    /**
     * Returns the URL scheme (http or https) based on the TLS setting.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String gooseScheme() {
        return gooseTls ? "https" : "http";
    }

    /**
     * Returns the path configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Paths getPaths() {
        return paths;
    }

    /**
     * Sets the path configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setPaths(Paths paths) {
        this.paths = paths;
    }

    /**
     * Returns the idle timeout configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Idle getIdle() {
        return idle;
    }

    /**
     * Sets the idle timeout configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setIdle(Idle idle) {
        this.idle = idle;
    }

    /**
     * Returns the upload size limit configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Upload getUpload() {
        return upload;
    }

    /**
     * Sets the upload size limit configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    /**
     * Returns the Langfuse observability configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Langfuse getLangfuse() {
        return langfuse;
    }

    /**
     * Sets the Langfuse observability configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setLangfuse(Langfuse langfuse) {
        this.langfuse = langfuse;
    }

    /**
     * Returns the instance limit configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Limits getLimits() {
        return limits;
    }

    /**
     * Sets the instance limit configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setLimits(Limits limits) {
        this.limits = limits;
    }

    /**
     * Returns the prewarm configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Prewarm getPrewarm() {
        return prewarm;
    }

    /**
     * Sets the prewarm configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setPrewarm(Prewarm prewarm) {
        this.prewarm = prewarm;
    }

    /**
     * Returns the SSE timeout configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Sse getSse() {
        return sse;
    }

    /**
     * Sets the SSE timeout configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setSse(Sse sse) {
        this.sse = sse;
    }

    /**
     * Returns the Office preview configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public OfficePreview getOfficePreview() {
        return officePreview;
    }

    /**
     * Sets the Office preview configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setOfficePreview(OfficePreview officePreview) {
        this.officePreview = officePreview;
    }

    /**
     * Returns the logging configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Logging getLogging() {
        return logging;
    }

    /**
     * Sets the logging configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    /**
     * Returns the credential encryption key.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public String getCredentialEncryptionKey() {
        return credentialEncryptionKey;
    }

    /**
     * Sets the credential encryption key.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setCredentialEncryptionKey(String credentialEncryptionKey) {
        this.credentialEncryptionKey = credentialEncryptionKey;
    }

    /**
     * Returns the remote execution configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public RemoteExecution getRemoteExecution() {
        return remoteExecution;
    }

    /**
     * Sets the remote execution configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setRemoteExecution(RemoteExecution remoteExecution) {
        this.remoteExecution = remoteExecution;
    }

    /**
     * Returns the file capsules configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public FileCapsules getFileCapsules() {
        return fileCapsules;
    }

    /**
     * Sets the file capsules configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setFileCapsules(FileCapsules fileCapsules) {
        this.fileCapsules = fileCapsules;
    }

    /**
     * Returns the file browser configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public FileBrowser getFiles() {
        return files;
    }

    /**
     * Sets the file browser configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setFiles(FileBrowser files) {
        this.files = files;
    }

    /**
     * Returns the skill market configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public SkillMarket getSkillMarket() {
        return skillMarket;
    }

    /**
     * Sets the skill market configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setSkillMarket(SkillMarket skillMarket) {
        this.skillMarket = skillMarket;
    }

    /**
     * Returns the knowledge feature configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Knowledge getKnowledge() {
        return knowledge;
    }

    /**
     * Sets the knowledge feature configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
    }

    /**
     * Returns the list of admin user IDs.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public List<String> getAdminUsers() {
        return adminUsers;
    }

    /**
     * Sets the list of admin user IDs.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public void setAdminUsers(List<String> adminUsers) {
        this.adminUsers = adminUsers;
    }

    /**
     * Resolves the absolute path to the gateway configuration file.
     *
     * @author x00000000
     * @since 2026-05-09
     */
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

    /**
     * Returns the directory containing the gateway configuration file.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Path getConfigDirectory() {
        Path configPath = getConfigPath();
        Path parent = configPath.getParent();
        if (parent != null) {
            return parent;
        }
        return Path.of("").toAbsolutePath().normalize();
    }

    /**
     * Resolves the absolute path to the project root directory.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Path getProjectRootPath() {
        Path configuredRoot = Path.of(paths.getProjectRoot());
        if (configuredRoot.isAbsolute()) {
            return configuredRoot.normalize();
        }
        if (configuredConfigPath() != null) {
            return getConfigDirectory().resolve(configuredRoot).normalize();
        }
        return configuredRoot.toAbsolutePath().normalize();
    }

    /**
     * Resolves the absolute path to the gateway root directory.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public Path getGatewayRootPath() {
        if (configuredConfigPath() == null) {
            return getProjectRootPath().resolve("gateway").normalize();
        }
        Path configPath = getConfigPath();
        Path configDir = getConfigDirectory();
        if ("config.yaml".equals(configPath.getFileName() != null ? configPath.getFileName().toString() : "")) {
            return configDir;
        }
        return getProjectRootPath().resolve("gateway").normalize();
    }

    private String configuredConfigPath() {
        String configuredPath = System.getProperty(CONFIG_PATH_KEY);
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv(CONFIG_PATH_KEY);
        }
        return (configuredPath == null || configuredPath.isBlank()) ? null : configuredPath;
    }

    // ---- Nested config classes ----

    /**
     * Path-related gateway configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Paths {
        private String projectRoot = "..";
        private String agentsDir = "agents";
        private String usersDir = "users";

        /**
         * Returns the configured project root path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getProjectRoot() {
            return projectRoot;
        }

        /**
         * Sets the configured project root path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setProjectRoot(String projectRoot) {
            this.projectRoot = projectRoot;
        }

        /**
         * Returns the configured agents directory path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getAgentsDir() {
            return agentsDir;
        }

        /**
         * Sets the configured agents directory path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setAgentsDir(String agentsDir) {
            this.agentsDir = agentsDir;
        }

        /**
         * Returns the configured users directory path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getUsersDir() {
            return usersDir;
        }

        /**
         * Sets the configured users directory path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setUsersDir(String usersDir) {
            this.usersDir = usersDir;
        }
    }

    /**
     * Idle instance lifecycle configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Idle {
        private int timeoutMinutes = 15;
        private long checkIntervalMs = 60000L;
        private int maxRestartAttempts = 3;
        private long restartBaseDelayMs = 5000L;

        /**
         * Returns the idle timeout in minutes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getTimeoutMinutes() {
            return timeoutMinutes;
        }

        /**
         * Sets the idle timeout in minutes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setTimeoutMinutes(int timeoutMinutes) {
            this.timeoutMinutes = timeoutMinutes;
        }

        /**
         * Returns the watchdog check interval in milliseconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public long getCheckIntervalMs() {
            return checkIntervalMs;
        }

        /**
         * Sets the watchdog check interval in milliseconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setCheckIntervalMs(long checkIntervalMs) {
            this.checkIntervalMs = checkIntervalMs;
        }

        /**
         * Returns the maximum restart attempts for one instance.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxRestartAttempts() {
            return maxRestartAttempts;
        }

        /**
         * Sets the maximum restart attempts for one instance.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxRestartAttempts(int maxRestartAttempts) {
            this.maxRestartAttempts = maxRestartAttempts;
        }

        /**
         * Returns the base backoff delay in milliseconds for restarts.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public long getRestartBaseDelayMs() {
            return restartBaseDelayMs;
        }

        /**
         * Sets the base backoff delay in milliseconds for restarts.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setRestartBaseDelayMs(long restartBaseDelayMs) {
            this.restartBaseDelayMs = restartBaseDelayMs;
        }
    }

    /**
     * Upload size limit configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Upload {
        private int maxFileSizeMb = 50;
        private int maxImageSizeMb = 20;

        /**
         * Returns the maximum upload file size in megabytes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxFileSizeMb() {
            return maxFileSizeMb;
        }

        /**
         * Sets the maximum upload file size in megabytes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxFileSizeMb(int maxFileSizeMb) {
            this.maxFileSizeMb = maxFileSizeMb;
        }

        /**
         * Returns the maximum upload image size in megabytes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxImageSizeMb() {
            return maxImageSizeMb;
        }

        /**
         * Sets the maximum upload image size in megabytes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxImageSizeMb(int maxImageSizeMb) {
            this.maxImageSizeMb = maxImageSizeMb;
        }
    }

    /**
     * Langfuse observability integration configuration.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Langfuse {
        private String host = "";
        private String publicKey = "";
        private String secretKey = "";

        /**
         * Returns the Langfuse host URL.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getHost() {
            return host;
        }

        /**
         * Sets the Langfuse host URL.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Returns the Langfuse public key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getPublicKey() {
            return publicKey;
        }

        /**
         * Sets the Langfuse public key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        /**
         * Returns the Langfuse secret key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getSecretKey() {
            return secretKey;
        }

        /**
         * Sets the Langfuse secret key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    /**
     * Instance count limit configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Limits {
        private int maxInstancesPerUser = 5;
        private int maxInstancesGlobal = 50;

        /**
         * Returns the maximum instances allowed per user.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxInstancesPerUser() {
            return maxInstancesPerUser;
        }

        /**
         * Sets the maximum instances allowed per user.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxInstancesPerUser(int maxInstancesPerUser) {
            this.maxInstancesPerUser = maxInstancesPerUser;
        }

        /**
         * Returns the maximum instances allowed globally.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxInstancesGlobal() {
            return maxInstancesGlobal;
        }

        /**
         * Sets the maximum instances allowed globally.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxInstancesGlobal(int maxInstancesGlobal) {
            this.maxInstancesGlobal = maxInstancesGlobal;
        }
    }

    /**
     * Server-sent events timeout configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Sse {
        private int firstByteTimeoutSec = 120;
        private int idleTimeoutSec = 600;
        private int maxDurationSec = 1200;

        /**
         * Returns the first-byte timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getFirstByteTimeoutSec() {
            return firstByteTimeoutSec;
        }

        /**
         * Sets the first-byte timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setFirstByteTimeoutSec(int firstByteTimeoutSec) {
            this.firstByteTimeoutSec = firstByteTimeoutSec;
        }

        /**
         * Returns the idle timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getIdleTimeoutSec() {
            return idleTimeoutSec;
        }

        /**
         * Sets the idle timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setIdleTimeoutSec(int idleTimeoutSec) {
            this.idleTimeoutSec = idleTimeoutSec;
        }

        /**
         * Returns the maximum stream duration in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxDurationSec() {
            return maxDurationSec;
        }

        /**
         * Sets the maximum stream duration in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxDurationSec(int maxDurationSec) {
            this.maxDurationSec = maxDurationSec;
        }
    }

    /**
     * Instance prewarm configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Prewarm {
        private boolean enabled = true;
        private String defaultAgentId = "universal-agent";

        /**
         * Returns whether prewarming is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets whether prewarming is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Returns the default agent identifier used for prewarming.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getDefaultAgentId() {
            return defaultAgentId;
        }

        /**
         * Sets the default agent identifier used for prewarming.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setDefaultAgentId(String defaultAgentId) {
            this.defaultAgentId = defaultAgentId;
        }
    }

    /**
     * Office preview integration configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class OfficePreview {
        private boolean enabled = false;
        private String onlyofficeUrl = "";
        private String fileBaseUrl = "";

        /**
         * Returns whether Office preview is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * Sets whether Office preview is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Returns the OnlyOffice service URL.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getOnlyofficeUrl() {
            return onlyofficeUrl;
        }

        /**
         * Sets the OnlyOffice service URL.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setOnlyofficeUrl(String onlyofficeUrl) {
            this.onlyofficeUrl = onlyofficeUrl;
        }

        /**
         * Returns the base URL used to serve previewable files.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getFileBaseUrl() {
            return fileBaseUrl;
        }

        /**
         * Sets the base URL used to serve previewable files.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setFileBaseUrl(String fileBaseUrl) {
            this.fileBaseUrl = fileBaseUrl;
        }
    }

    /**
     * Logging behavior configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Logging {
        private boolean accessLogEnabled = true;
        private boolean includeUpstreamErrorBody = false;
        private boolean includeSseChunkPreview = false;
        private int sseChunkPreviewMaxChars = 160;

        /**
         * Returns whether access logging is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isAccessLogEnabled() {
            return accessLogEnabled;
        }

        /**
         * Sets whether access logging is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setAccessLogEnabled(boolean accessLogEnabled) {
            this.accessLogEnabled = accessLogEnabled;
        }

        /**
         * Returns whether upstream error bodies are included in logs.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isIncludeUpstreamErrorBody() {
            return includeUpstreamErrorBody;
        }

        /**
         * Sets whether upstream error bodies are included in logs.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setIncludeUpstreamErrorBody(boolean includeUpstreamErrorBody) {
            this.includeUpstreamErrorBody = includeUpstreamErrorBody;
        }

        /**
         * Returns whether SSE chunk previews are included in logs.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isIncludeSseChunkPreview() {
            return includeSseChunkPreview;
        }

        /**
         * Sets whether SSE chunk previews are included in logs.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setIncludeSseChunkPreview(boolean includeSseChunkPreview) {
            this.includeSseChunkPreview = includeSseChunkPreview;
        }

        /**
         * Returns the maximum number of characters kept for SSE chunk previews.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getSseChunkPreviewMaxChars() {
            return sseChunkPreviewMaxChars;
        }

        /**
         * Sets the maximum number of characters kept for SSE chunk previews.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setSseChunkPreviewMaxChars(int sseChunkPreviewMaxChars) {
            this.sseChunkPreviewMaxChars = sseChunkPreviewMaxChars;
        }
    }

    /**
     * Remote execution timeout configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class RemoteExecution {
        private int defaultTimeout = 30;
        private int maxTimeout = 120;

        /**
         * Returns the default remote execution timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getDefaultTimeout() {
            return defaultTimeout;
        }

        /**
         * Sets the default remote execution timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setDefaultTimeout(int defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
        }

        /**
         * Returns the maximum allowed remote execution timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxTimeout() {
            return maxTimeout;
        }

        /**
         * Sets the maximum allowed remote execution timeout in seconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxTimeout(int maxTimeout) {
            this.maxTimeout = maxTimeout;
        }
    }

    /**
     * File capsule feature configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class FileCapsules {
        private List<String> allowedExtensions = List.of(
                "doc", "docx",
                "xls", "xlsx",
                "ppt", "pptx",
                "csv",
                "txt",
                "json",
                "md", "markdown",
                "html", "htm");

        /**
         * Returns the allowed file extensions for file capsules.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public List<String> getAllowedExtensions() {
            return allowedExtensions;
        }

        /**
         * Sets the allowed file extensions for file capsules.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setAllowedExtensions(List<String> allowedExtensions) {
            this.allowedExtensions = allowedExtensions;
        }
    }

    /**
     * File browser scanning configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class FileBrowser {
        private List<FileScanRoot> scanRoots = List.of(
                new FileScanRoot("workingDir", "${userAgentDir}", false),
                new FileScanRoot("output", "${userAgentDir}/output", false));

        /**
         * Returns the configured file browser scan roots.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public List<FileScanRoot> getScanRoots() {
            return scanRoots;
        }

        /**
         * Sets the configured file browser scan roots.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setScanRoots(List<FileScanRoot> scanRoots) {
            this.scanRoots = scanRoots;
        }
    }

    /**
     * Definition of one file browser scan root.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class FileScanRoot {
        private String id = "";
        private String path = "";
        private boolean recursive = false;
        private List<String> excludeDirs = List.of();
        private int maxDepth = 6;
        private int maxFiles = 1000;
        private long scanTimeoutMs = 2000;

        /**
         * Creates an empty file scan root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public FileScanRoot() {
        }

        /**
         * Creates a file scan root with the given identifier, path, and recursion flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public FileScanRoot(String id, String path, boolean recursive) {
            this.id = id;
            this.path = path;
            this.recursive = recursive;
        }

        /**
         * Returns the scan root identifier.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the scan root identifier.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Returns the scan root path template.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getPath() {
            return path;
        }

        /**
         * Sets the scan root path template.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * Returns whether recursive scanning is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isRecursive() {
            return recursive;
        }

        /**
         * Sets whether recursive scanning is enabled.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setRecursive(boolean recursive) {
            this.recursive = recursive;
        }

        /**
         * Returns the excluded directory names for this scan root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public List<String> getExcludeDirs() {
            return excludeDirs;
        }

        /**
         * Sets the excluded directory names for this scan root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setExcludeDirs(List<String> excludeDirs) {
            this.excludeDirs = excludeDirs;
        }

        /**
         * Returns the maximum scan depth.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxDepth() {
            return maxDepth;
        }

        /**
         * Sets the maximum scan depth.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        /**
         * Returns the maximum number of files per scan.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxFiles() {
            return maxFiles;
        }

        /**
         * Sets the maximum number of files per scan.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxFiles(int maxFiles) {
            this.maxFiles = maxFiles;
        }

        /**
         * Returns the scan timeout in milliseconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public long getScanTimeoutMs() {
            return scanTimeoutMs;
        }

        /**
         * Sets the scan timeout in milliseconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setScanTimeoutMs(long scanTimeoutMs) {
            this.scanTimeoutMs = scanTimeoutMs;
        }
    }

    /**
     * Skill market integration configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class SkillMarket {
        private String baseUrl = "http://127.0.0.1:8095";
        private int requestTimeoutMs = 10000;
        private int maxPackageSizeMb = 200;

        /**
         * Returns the skill market base URL.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getBaseUrl() {
            return baseUrl;
        }

        /**
         * Sets the skill market base URL.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        /**
         * Returns the skill market request timeout in milliseconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getRequestTimeoutMs() {
            return requestTimeoutMs;
        }

        /**
         * Sets the skill market request timeout in milliseconds.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setRequestTimeoutMs(int requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        /**
         * Returns the maximum package size in megabytes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxPackageSizeMb() {
            return maxPackageSizeMb;
        }

        /**
         * Sets the maximum package size in megabytes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxPackageSizeMb(int maxPackageSizeMb) {
            this.maxPackageSizeMb = maxPackageSizeMb;
        }
    }

    /**
     * Knowledge feature configuration values.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static class Knowledge {
        private String artifactsRoot = "../knowledge-service/data/artifacts";

        /**
         * Returns the artifacts root directory path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getArtifactsRoot() {
            return artifactsRoot;
        }

        /**
         * Sets the artifacts root directory path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setArtifactsRoot(String artifactsRoot) {
            this.artifactsRoot = artifactsRoot;
        }
    }

    // ---- PostConstruct for logging configuration values ----

    /**
     * Logs the loaded configuration values at startup and normalizes the goosed binary path.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    @PostConstruct
    public void logConfiguration() {
        normalizeGoosedBin();
        log.info("GatewayProperties loaded: gooseTls={}, gooseScheme={}, goosedBin={}",
                gooseTls, gooseScheme(), goosedBin);
    }

    private void normalizeGoosedBin() {
        if (goosedBin == null || goosedBin.isBlank()) {
            return;
        }
        Path rawPath = Path.of(goosedBin);
        if (rawPath.isAbsolute()) {
            return;
        }
        Path candidate = getGatewayRootPath().resolve(goosedBin).normalize();
        if (Files.exists(candidate)) {
            goosedBin = candidate.toString();
        }
    }
}
