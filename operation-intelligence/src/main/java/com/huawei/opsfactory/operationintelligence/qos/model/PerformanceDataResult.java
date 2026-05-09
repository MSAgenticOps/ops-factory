package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceDataResult {
    private String dn;
    private String moType;
    private String neName;
    private int period;
    private Map<String, String> values;

    public String getDn() { return dn; }
    public void setDn(String dn) { this.dn = dn; }
    public String getMoType() { return moType; }
    public void setMoType(String moType) { this.moType = moType; }
    public String getNeName() { return neName; }
    public void setNeName(String neName) { this.neName = neName; }
    public int getPeriod() { return period; }
    public void setPeriod(int period) { this.period = period; }
    public Map<String, String> getValues() { return values; }
    public void setValues(Map<String, String> values) { this.values = values; }
}
