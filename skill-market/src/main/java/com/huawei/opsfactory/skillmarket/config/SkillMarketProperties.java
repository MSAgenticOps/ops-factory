package com.huawei.opsfactory.skillmarket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "skill-market")
public class SkillMarketProperties {

    private String corsOrigin = "*";
    private Runtime runtime = new Runtime();
    private PackageSettings pack = new PackageSettings();
    private Logging logging = new Logging();

    public String getCorsOrigin() {
        return corsOrigin;
    }

    public void setCorsOrigin(String corsOrigin) {
        this.corsOrigin = corsOrigin;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public PackageSettings getPackage() {
        return pack;
    }

    public void setPackage(PackageSettings pack) {
        this.pack = pack;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    public static class Runtime {
        private String baseDir = "./data";

        public String getBaseDir() {
            return baseDir;
        }

        public void setBaseDir(String baseDir) {
            this.baseDir = baseDir;
        }
    }

    public static class PackageSettings {
        private int maxUploadSizeMb = 50;
        private int maxUnpackedSizeMb = 200;
        private int maxFileCount = 1000;
        private int maxSingleFileSizeMb = 20;
        private boolean exposeFileList = true;
        private boolean allowScripts = true;

        public int getMaxUploadSizeMb() {
            return maxUploadSizeMb;
        }

        public void setMaxUploadSizeMb(int maxUploadSizeMb) {
            this.maxUploadSizeMb = maxUploadSizeMb;
        }

        public int getMaxUnpackedSizeMb() {
            return maxUnpackedSizeMb;
        }

        public void setMaxUnpackedSizeMb(int maxUnpackedSizeMb) {
            this.maxUnpackedSizeMb = maxUnpackedSizeMb;
        }

        public int getMaxFileCount() {
            return maxFileCount;
        }

        public void setMaxFileCount(int maxFileCount) {
            this.maxFileCount = maxFileCount;
        }

        public int getMaxSingleFileSizeMb() {
            return maxSingleFileSizeMb;
        }

        public void setMaxSingleFileSizeMb(int maxSingleFileSizeMb) {
            this.maxSingleFileSizeMb = maxSingleFileSizeMb;
        }

        public boolean isExposeFileList() {
            return exposeFileList;
        }

        public void setExposeFileList(boolean exposeFileList) {
            this.exposeFileList = exposeFileList;
        }

        public boolean isAllowScripts() {
            return allowScripts;
        }

        public void setAllowScripts(boolean allowScripts) {
            this.allowScripts = allowScripts;
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
