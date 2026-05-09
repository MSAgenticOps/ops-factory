package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DnCluster {
    private String clusterDn;
    private List<DnElement> elements;

    public String getClusterDn() { return clusterDn; }
    public void setClusterDn(String clusterDn) { this.clusterDn = clusterDn; }
    public List<DnElement> getElements() { return elements; }
    public void setElements(List<DnElement> elements) { this.elements = elements; }
}
