package com.huawei.opsfactory.gateway.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatorRawData {
    private Long code;
    private String envCode;
    private String dn;
    private String moType;
    private String neName;
    private Map<String, String> values;
    private Long timestamp;

    public Long getCode() { return code; }
    public void setCode(Long code) { this.code = code; }
    public String getEnvCode() { return envCode; }
    public void setEnvCode(String envCode) { this.envCode = envCode; }
    public String getDn() { return dn; }
    public void setDn(String dn) { this.dn = dn; }
    public String getMoType() { return moType; }
    public void setMoType(String moType) { this.moType = moType; }
    public String getNeName() { return neName; }
    public void setNeName(String neName) { this.neName = neName; }
    public Map<String, String> getValues() { return values; }
    public void setValues(Map<String, String> values) { this.values = values; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
