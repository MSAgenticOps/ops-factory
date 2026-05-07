package com.huawei.opsfactory.gateway.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DnElement {
    private String dn;
    private String name;

    public String getDn() { return dn; }
    public void setDn(String dn) { this.dn = dn; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
