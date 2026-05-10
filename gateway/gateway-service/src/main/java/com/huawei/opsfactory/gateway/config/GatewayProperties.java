/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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

    public static class Paths {
        private String projectRoot = "..";
        private String agentsDir = "agents";
        private String usersDir = "users";

        /**
         * Gets the project root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getProjectRoot() {
            return projectRoot;
        }
        /**
         * Sets the project root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setProjectRoot(String projectRoot) {
            this.projectRoot = projectRoot;
        }
        /**
         * Gets the agents dir.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getAgentsDir() {
            return agentsDir;
        }
        /**
         * Sets the agents dir.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setAgentsDir(String agentsDir) {
            this.agentsDir = agentsDir;
        }
        /**
         * Gets the users dir.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getUsersDir() {
            return usersDir;
        }
        /**
         * Sets the users dir.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setUsersDir(String usersDir) {
            this.usersDir = usersDir;
        }
    }

    public static class Idle {
        private int timeoutMinutes = 15;
        private long checkIntervalMs = 60000L;
        private int maxRestartAttempts = 3;
        private long restartBaseDelayMs = 5000L;

        /**
         * Gets the timeout minutes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getTimeoutMinutes() {
            return timeoutMinutes;
        }
        /**
         * Sets the timeout minutes.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setTimeoutMinutes(int timeoutMinutes) {
            this.timeoutMinutes = timeoutMinutes;
        }
        /**
         * Gets the check interval ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public long getCheckIntervalMs() {
            return checkIntervalMs;
        }
        /**
         * Sets the check interval ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setCheckIntervalMs(long checkIntervalMs) {
            this.checkIntervalMs = checkIntervalMs;
        }
        /**
         * Gets the max restart attempts.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxRestartAttempts() {
            return maxRestartAttempts;
        }
        /**
         * Sets the max restart attempts.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxRestartAttempts(int maxRestartAttempts) {
            this.maxRestartAttempts = maxRestartAttempts;
        }
        /**
         * Gets the restart base delay ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public long getRestartBaseDelayMs() {
            return restartBaseDelayMs;
        }
        /**
         * Sets the restart base delay ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setRestartBaseDelayMs(long restartBaseDelayMs) {
            this.restartBaseDelayMs = restartBaseDelayMs;
        }
    }

    public static class Upload {
        private int maxFileSizeMb = 50;
        private int maxImageSizeMb = 20;

        /**
         * Gets the max file size mb.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxFileSizeMb() {
            return maxFileSizeMb;
        }
        /**
         * Sets the max file size mb.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxFileSizeMb(int maxFileSizeMb) {
            this.maxFileSizeMb = maxFileSizeMb;
        }
        /**
         * Gets the max image size mb.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxImageSizeMb() {
            return maxImageSizeMb;
        }
        /**
         * Sets the max image size mb.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxImageSizeMb(int maxImageSizeMb) {
            this.maxImageSizeMb = maxImageSizeMb;
        }
    }

    public static class Langfuse {
        private String host = "";
        private String publicKey = "";
        private String secretKey = "";

        /**
         * Gets the host.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getHost() {
            return host;
        }
        /**
         * Sets the host.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setHost(String host) {
            this.host = host;
        }
        /**
         * Gets the public key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getPublicKey() {
            return publicKey;
        }
        /**
         * Sets the public key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
        /**
         * Gets the secret key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getSecretKey() {
            return secretKey;
        }
        /**
         * Sets the secret key.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static class Limits {
        private int maxInstancesPerUser = 5;
        private int maxInstancesGlobal = 50;

        /**
         * Gets the max instances per user.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxInstancesPerUser() {
            return maxInstancesPerUser;
        }
        /**
         * Sets the max instances per user.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxInstancesPerUser(int maxInstancesPerUser) {
            this.maxInstancesPerUser = maxInstancesPerUser;
        }
        /**
         * Gets the max instances global.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxInstancesGlobal() {
            return maxInstancesGlobal;
        }
        /**
         * Sets the max instances global.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxInstancesGlobal(int maxInstancesGlobal) {
            this.maxInstancesGlobal = maxInstancesGlobal;
        }
    }

    public static class Sse {
        private int firstByteTimeoutSec = 120;
        private int idleTimeoutSec = 600;
        private int maxDurationSec = 1200;

        /**
         * Gets the first byte timeout sec.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getFirstByteTimeoutSec() {
            return firstByteTimeoutSec;
        }
        /**
         * Sets the first byte timeout sec.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setFirstByteTimeoutSec(int firstByteTimeoutSec) {
            this.firstByteTimeoutSec = firstByteTimeoutSec;
        }
        /**
         * Gets the idle timeout sec.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getIdleTimeoutSec() {
            return idleTimeoutSec;
        }
        /**
         * Sets the idle timeout sec.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setIdleTimeoutSec(int idleTimeoutSec) {
            this.idleTimeoutSec = idleTimeoutSec;
        }
        /**
         * Gets the max duration sec.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxDurationSec() {
            return maxDurationSec;
        }
        /**
         * Sets the max duration sec.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxDurationSec(int maxDurationSec) {
            this.maxDurationSec = maxDurationSec;
        }
    }

    public static class Prewarm {
        private boolean enabled = true;
        private String defaultAgentId = "universal-agent";

        /**
         * Returns the enabled flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isEnabled() {
            return enabled;
        }
        /**
         * Updates the enabled flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        /**
         * Gets the default agent id.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getDefaultAgentId() {
            return defaultAgentId;
        }
        /**
         * Sets the default agent id.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setDefaultAgentId(String defaultAgentId) {
            this.defaultAgentId = defaultAgentId;
        }
    }

    public static class OfficePreview {
        private boolean enabled = false;
        private String onlyofficeUrl = "";
        private String fileBaseUrl = "";

        /**
         * Returns the enabled flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isEnabled() {
            return enabled;
        }
        /**
         * Updates the enabled flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        /**
         * Gets the onlyoffice url.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getOnlyofficeUrl() {
            return onlyofficeUrl;
        }
        /**
         * Sets the onlyoffice url.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setOnlyofficeUrl(String onlyofficeUrl) {
            this.onlyofficeUrl = onlyofficeUrl;
        }
        /**
         * Gets the file base url.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getFileBaseUrl() {
            return fileBaseUrl;
        }
        /**
         * Sets the file base url.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setFileBaseUrl(String fileBaseUrl) {
            this.fileBaseUrl = fileBaseUrl;
        }
    }

    public static class Logging {
        private boolean accessLogEnabled = true;
        private boolean includeUpstreamErrorBody = false;
        private boolean includeSseChunkPreview = false;
        private int sseChunkPreviewMaxChars = 160;

        /**
         * Returns the access log enabled flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isAccessLogEnabled() {
            return accessLogEnabled;
        }
        /**
         * Updates the access log enabled flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setAccessLogEnabled(boolean accessLogEnabled) {
            this.accessLogEnabled = accessLogEnabled;
        }
        /**
         * Returns the include upstream error body flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isIncludeUpstreamErrorBody() {
            return includeUpstreamErrorBody;
        }
        /**
         * Updates the include upstream error body flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setIncludeUpstreamErrorBody(boolean includeUpstreamErrorBody) {
            this.includeUpstreamErrorBody = includeUpstreamErrorBody;
        }
        /**
         * Returns the include sse chunk preview flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isIncludeSseChunkPreview() {
            return includeSseChunkPreview;
        }
        /**
         * Updates the include sse chunk preview flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setIncludeSseChunkPreview(boolean includeSseChunkPreview) {
            this.includeSseChunkPreview = includeSseChunkPreview;
        }
        /**
         * Gets the sse chunk preview max chars.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getSseChunkPreviewMaxChars() {
            return sseChunkPreviewMaxChars;
        }
        /**
         * Sets the sse chunk preview max chars.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setSseChunkPreviewMaxChars(int sseChunkPreviewMaxChars) {
            this.sseChunkPreviewMaxChars = sseChunkPreviewMaxChars;
        }
    }

    public static class RemoteExecution {
        private int defaultTimeout = 30;
        private int maxTimeout = 120;

        /**
         * Gets the default timeout.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getDefaultTimeout() {
            return defaultTimeout;
        }
        /**
         * Sets the default timeout.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setDefaultTimeout(int defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
        }
        /**
         * Gets the max timeout.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxTimeout() {
            return maxTimeout;
        }
        /**
         * Sets the max timeout.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxTimeout(int maxTimeout) {
            this.maxTimeout = maxTimeout;
        }
    }

    public static class FileCapsules {
        private List<String> allowedExtensions = List.of(
                "doc", "docx",
                "xls", "xlsx",
                "ppt", "pptx",
                "pdf",
                "csv",
                "txt",
                "json",
                "md", "markdown",
                "html", "htm");

        /**
         * Gets the allowed extensions.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public List<String> getAllowedExtensions() {
            return allowedExtensions;
        }
        /**
         * Sets the allowed extensions.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setAllowedExtensions(List<String> allowedExtensions) {
            this.allowedExtensions = allowedExtensions;
        }
    }

    public static class FileBrowser {
        private List<FileScanRoot> scanRoots = List.of(
                new FileScanRoot("workingDir", "${userAgentDir}", false),
                new FileScanRoot("output", "${userAgentDir}/output", false));

        /**
         * Gets the scan roots.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public List<FileScanRoot> getScanRoots() {
            return scanRoots;
        }
        /**
         * Sets the scan roots.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setScanRoots(List<FileScanRoot> scanRoots) {
            this.scanRoots = scanRoots;
        }
    }

    public static class FileScanRoot {
        private String id = "";
        private String path = "";
        private boolean recursive = false;
        private List<String> excludeDirs = List.of();
        private int maxDepth = 6;
        private int maxFiles = 1000;
        private long scanTimeoutMs = 2000;

        public FileScanRoot() {
        }

        public FileScanRoot(String id, String path, boolean recursive) {
            this.id = id;
            this.path = path;
            this.recursive = recursive;
        }

        /**
         * Gets the id.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getId() {
            return id;
        }
        /**
         * Sets the id.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setId(String id) {
            this.id = id;
        }
        /**
         * Gets the path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getPath() {
            return path;
        }
        /**
         * Sets the path.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setPath(String path) {
            this.path = path;
        }
        /**
         * Returns the recursive flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public boolean isRecursive() {
            return recursive;
        }
        /**
         * Updates the recursive flag.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setRecursive(boolean recursive) {
            this.recursive = recursive;
        }
        /**
         * Gets the exclude dirs.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public List<String> getExcludeDirs() {
            return excludeDirs;
        }
        /**
         * Sets the exclude dirs.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setExcludeDirs(List<String> excludeDirs) {
            this.excludeDirs = excludeDirs;
        }
        /**
         * Gets the max depth.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxDepth() {
            return maxDepth;
        }
        /**
         * Sets the max depth.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
        }
        /**
         * Gets the max files.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxFiles() {
            return maxFiles;
        }
        /**
         * Sets the max files.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxFiles(int maxFiles) {
            this.maxFiles = maxFiles;
        }
        /**
         * Gets the scan timeout ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public long getScanTimeoutMs() {
            return scanTimeoutMs;
        }
        /**
         * Sets the scan timeout ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setScanTimeoutMs(long scanTimeoutMs) {
            this.scanTimeoutMs = scanTimeoutMs;
        }
    }

    public static class SkillMarket {
        private String baseUrl = "http://127.0.0.1:8095";
        private int requestTimeoutMs = 10000;
        private int maxPackageSizeMb = 200;

        /**
         * Gets the base url.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getBaseUrl() {
            return baseUrl;
        }
        /**
         * Sets the base url.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        /**
         * Gets the request timeout ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getRequestTimeoutMs() {
            return requestTimeoutMs;
        }
        /**
         * Sets the request timeout ms.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setRequestTimeoutMs(int requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }
        /**
         * Gets the max package size mb.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public int getMaxPackageSizeMb() {
            return maxPackageSizeMb;
        }
        /**
         * Sets the max package size mb.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setMaxPackageSizeMb(int maxPackageSizeMb) {
            this.maxPackageSizeMb = maxPackageSizeMb;
        }
    }

    public static class Knowledge {
        private String artifactsRoot = "../knowledge-service/data/artifacts";

        /**
         * Gets the artifacts root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public String getArtifactsRoot() {
            return artifactsRoot;
        }
        /**
         * Sets the artifacts root.
         *
         * @author x00000000
         * @since 2026-05-09
         */
        public void setArtifactsRoot(String artifactsRoot) {
            this.artifactsRoot = artifactsRoot;
        }
    }

    @Override
    public String toString() {
        return "GatewayProperties{"
                + "secretKey='***'"
                + ", corsOrigin='" + corsOrigin + '\''
                + ", gooseTls=" + gooseTls
                + ", gooseScheme='" + gooseScheme() + '\''
                + ", goosedBin='" + goosedBin + '\''
                + '}';
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
