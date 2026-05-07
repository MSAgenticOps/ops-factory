package com.huawei.opsfactory.gateway.qos.dv;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DvEnvironmentInfo {
    private String envCode;
    private String agentSolutionType;
    private String serverUrl;
    private String utmUser;
    private String utmPassword;
    private String crtContent;
    private String crtFileName;
    private String dns;
    private boolean strictSsl = true;

    public DvEnvironmentInfo() {}

    public DvEnvironmentInfo(String envCode, String agentSolutionType, String serverUrl,
            String utmUser, String utmPassword, String crtContent, String crtFileName) {
        this.envCode = envCode;
        this.agentSolutionType = agentSolutionType;
        this.serverUrl = serverUrl;
        this.utmUser = utmUser;
        this.utmPassword = utmPassword;
        this.crtContent = crtContent;
        this.crtFileName = crtFileName;
    }

    public DvEnvironmentInfo(String envCode, String agentSolutionType, String serverUrl,
            String utmUser, String utmPassword, String crtContent, String crtFileName, String dns) {
        this(envCode, agentSolutionType, serverUrl, utmUser, utmPassword, crtContent, crtFileName);
        this.dns = dns;
    }

    public String getEnvCode() { return envCode; }
    public void setEnvCode(String envCode) { this.envCode = envCode; }
    public String getAgentSolutionType() { return agentSolutionType; }
    public void setAgentSolutionType(String agentSolutionType) { this.agentSolutionType = agentSolutionType; }
    public String getServerUrl() { return serverUrl; }
    public void setServerUrl(String serverUrl) { this.serverUrl = serverUrl; }
    public String getUtmUser() { return utmUser; }
    public void setUtmUser(String utmUser) { this.utmUser = utmUser; }
    @JsonIgnore
    public String getUtmPassword() { return utmPassword; }
    public void setUtmPassword(String utmPassword) { this.utmPassword = utmPassword; }
    @JsonIgnore
    public String getCrtContent() { return crtContent; }
    public void setCrtContent(String crtContent) { this.crtContent = crtContent; }
    public String getCrtFileName() { return crtFileName; }
    public void setCrtFileName(String crtFileName) { this.crtFileName = crtFileName; }
    public String getDns() { return dns; }
    public void setDns(String dns) { this.dns = dns; }
    public boolean isStrictSsl() { return strictSsl; }
    public void setStrictSsl(boolean strictSsl) { this.strictSsl = strictSsl; }

    @Override
    public String toString() {
        return "DvEnvironmentInfo{envCode='" + envCode + "', serverUrl='" + serverUrl
                + "', utmUser='" + utmUser + "', utmPassword='*****'}";
    }
}
