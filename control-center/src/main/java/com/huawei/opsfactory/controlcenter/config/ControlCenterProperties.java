/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.controlcenter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "control-center")
/**
 * Control Center Properties.
 *
 * @author x00000000
 * @since 2026-05-27
 */
public class ControlCenterProperties {

    private String secretKey = "change-me";
    private String corsOrigin = "http://127.0.0.1:5173";
    private int requestTimeoutMs = 5000;
    private String projectRoot = "";
    private List<ServiceTarget> services = new ArrayList<>();
    private Langfuse langfuse = new Langfuse();

    /**
     * Gets the secret key used for authentication.
     *
     * @return the secret key used for authentication
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Sets the secret key used for authentication.
     *
     * @param secretKey the secret key used for authentication
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Gets the allowed CORS origin pattern.
     *
     * @return the allowed CORS origin pattern
     */
    public String getCorsOrigin() {
        return corsOrigin;
    }

    /**
     * Sets the allowed CORS origin pattern.
     *
     * @param corsOrigin the allowed CORS origin pattern
     */
    public void setCorsOrigin(String corsOrigin) {
        this.corsOrigin = corsOrigin;
    }

    /**
     * Gets the request timeout in milliseconds.
     *
     * @return the request timeout in milliseconds
     */
    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    /**
     * Sets the request timeout in milliseconds.
     *
     * @param requestTimeoutMs the request timeout in milliseconds
     */
    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    /**
     * Gets the project root directory.
     *
     * @return the project root directory
     */
    public String getProjectRoot() {
        return projectRoot;
    }

    /**
     * Sets the project root directory.
     *
     * @param projectRoot the project root directory
     */
    public void setProjectRoot(String projectRoot) {
        this.projectRoot = projectRoot;
    }

    /**
     * Gets the list of managed service targets.
     *
     * @return the list of managed service targets
     */
    public List<ServiceTarget> getServices() {
        return services;
    }

    /**
     * Sets the list of managed service targets.
     *
     * @param services the list of managed service targets
     */
    public void setServices(List<ServiceTarget> services) {
        this.services = services;
    }

    /**
     * Gets the Langfuse configuration.
     *
     * @return the Langfuse configuration
     */
    public Langfuse getLangfuse() {
        return langfuse;
    }

    /**
     * Sets the Langfuse configuration.
     *
     * @param langfuse the Langfuse configuration
     */
    public void setLangfuse(Langfuse langfuse) {
        this.langfuse = langfuse;
    }

/**
     * Service Target.
     *
     * @author x00000000
     * @since 2026-05-27
     */
    public static class ServiceTarget {
        private String id;
        private String name;
        private String baseUrl;
        private boolean required = true;
        private String healthPath;
        private String ctlComponent;
        private String configPath = "";
        private String logPath = "";
        private Auth auth = new Auth();

        /**
         * Gets the service identifier.
         *
         * @return the service identifier
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the service identifier.
         *
         * @param id the service identifier
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Gets the service name.
         *
         * @return the service name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the service name.
         *
         * @param name the service name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the base URL of the service.
         *
         * @return the base URL of the service
         */
        public String getBaseUrl() {
            return baseUrl;
        }

        /**
         * Sets the base URL of the service.
         *
         * @param baseUrl the base URL of the service
         */
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        /**
         * Gets whether the service is required.
         *
         * @return whether the service is required
         */
        public boolean isRequired() {
            return required;
        }

        /**
         * Sets whether the service is required.
         *
         * @param required whether the service is required
         */
        public void setRequired(boolean required) {
            this.required = required;
        }

        /**
         * Gets the health check path of the service.
         *
         * @return the health check path of the service
         */
        public String getHealthPath() {
            return healthPath;
        }

        /**
         * Sets the health check path of the service.
         *
         * @param healthPath the health check path of the service
         */
        public void setHealthPath(String healthPath) {
            this.healthPath = healthPath;
        }

        /**
         * Gets the ctl.sh component name.
         *
         * @return the ctl.sh component name
         */
        public String getCtlComponent() {
            return ctlComponent;
        }

        /**
         * Sets the ctl.sh component name.
         *
         * @param ctlComponent the ctl.sh component name
         */
        public void setCtlComponent(String ctlComponent) {
            this.ctlComponent = ctlComponent;
        }

        /**
         * Gets the configuration file path.
         *
         * @return the configuration file path
         */
        public String getConfigPath() {
            return configPath;
        }

        /**
         * Sets the configuration file path.
         *
         * @param configPath the configuration file path
         */
        public void setConfigPath(String configPath) {
            this.configPath = configPath;
        }

        /**
         * Gets the log file path.
         *
         * @return the log file path
         */
        public String getLogPath() {
            return logPath;
        }

        /**
         * Sets the log file path.
         *
         * @param logPath the log file path
         */
        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }

        /**
         * Gets the authentication configuration.
         *
         * @return the authentication configuration
         */
        public Auth getAuth() {
            return auth;
        }

        /**
         * Sets the authentication configuration.
         *
         * @param auth the authentication configuration
         */
        public void setAuth(Auth auth) {
            this.auth = auth;
        }
    }

/**
     * Auth.
     *
     * @author x00000000
     * @since 2026-05-27
     */
    public static class Auth {
        private String type = "none";
        private String secretKey = "";

        /**
         * Gets the authentication type.
         *
         * @return the authentication type
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the authentication type.
         *
         * @param type the authentication type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Gets the secret key for authentication.
         *
         * @return the secret key for authentication
         */
        public String getSecretKey() {
            return secretKey;
        }

        /**
         * Sets the secret key for authentication.
         *
         * @param secretKey the secret key for authentication
         */
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

/**
     * Langfuse.
     *
     * @author x00000000
     * @since 2026-05-27
     */
    public static class Langfuse {
        private String host = "";
        private String publicKey = "";
        private String secretKey = "";

        /**
         * Gets the Langfuse host URL.
         *
         * @return the Langfuse host URL
         */
        public String getHost() {
            return host;
        }

        /**
         * Sets the Langfuse host URL.
         *
         * @param host the Langfuse host URL
         */
        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Gets the Langfuse public key.
         *
         * @return the Langfuse public key
         */
        public String getPublicKey() {
            return publicKey;
        }

        /**
         * Sets the Langfuse public key.
         *
         * @param publicKey the Langfuse public key
         */
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        /**
         * Gets the Langfuse secret key.
         *
         * @return the Langfuse secret key
         */
        public String getSecretKey() {
            return secretKey;
        }

        /**
         * Sets the Langfuse secret key.
         *
         * @param secretKey the Langfuse secret key
         */
        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }
}
