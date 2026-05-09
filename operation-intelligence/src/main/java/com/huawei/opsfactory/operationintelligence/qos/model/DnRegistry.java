package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DnRegistry {
    private String envCode;
    private List<DnCluster> clusters;

    public String getEnvCode() { return envCode; }
    public void setEnvCode(String envCode) { this.envCode = envCode; }
    public List<DnCluster> getClusters() { return clusters; }
    public void setClusters(List<DnCluster> clusters) { this.clusters = clusters; }
}
