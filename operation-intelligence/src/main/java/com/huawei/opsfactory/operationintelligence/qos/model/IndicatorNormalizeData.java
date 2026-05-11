
package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatorNormalizeData {
    private Long code;

    private String envCode;

    private String type;

    private BigDecimal indicatorValue;

    private Long timestamp;

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getIndicatorValue() {
        return indicatorValue;
    }

    public void setIndicatorValue(BigDecimal indicatorValue) {
        this.indicatorValue = indicatorValue;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
