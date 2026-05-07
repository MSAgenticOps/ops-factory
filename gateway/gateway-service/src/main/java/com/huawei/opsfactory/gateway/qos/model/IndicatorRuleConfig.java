package com.huawei.opsfactory.gateway.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatorRuleConfig {
    private String indicatorCode;
    private Integer baseLine;
    private Integer upperLimit;
    private BigDecimal weight;

    public String getIndicatorCode() { return indicatorCode; }
    public void setIndicatorCode(String indicatorCode) { this.indicatorCode = indicatorCode; }
    public Integer getBaseLine() { return baseLine; }
    public void setBaseLine(Integer baseLine) { this.baseLine = baseLine; }
    public Integer getUpperLimit() { return upperLimit; }
    public void setUpperLimit(Integer upperLimit) { this.upperLimit = upperLimit; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
