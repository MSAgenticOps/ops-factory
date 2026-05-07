package com.huawei.opsfactory.gateway.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceIndicatorScope {
    private String agentSolutionType;
    private String indicatorCode;
    private String indicatorName;
    private String type;
    private String moType;
    private String measUnitKey;
    private String measObject;
    private String measTypeKeys;
    private String thresholds;
    private BigDecimal weight;

    public String getAgentSolutionType() { return agentSolutionType; }
    public void setAgentSolutionType(String agentSolutionType) { this.agentSolutionType = agentSolutionType; }
    public String getIndicatorCode() { return indicatorCode; }
    public void setIndicatorCode(String indicatorCode) { this.indicatorCode = indicatorCode; }
    public String getIndicatorName() { return indicatorName; }
    public void setIndicatorName(String indicatorName) { this.indicatorName = indicatorName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMoType() { return moType; }
    public void setMoType(String moType) { this.moType = moType; }
    public String getMeasUnitKey() { return measUnitKey; }
    public void setMeasUnitKey(String measUnitKey) { this.measUnitKey = measUnitKey; }
    public String getMeasObject() { return measObject; }
    public void setMeasObject(String measObject) { this.measObject = measObject; }
    public String getMeasTypeKeys() { return measTypeKeys; }
    public void setMeasTypeKeys(String measTypeKeys) { this.measTypeKeys = measTypeKeys; }
    public String getThresholds() { return sortThresholds(thresholds); }
    public void setThresholds(String thresholds) { this.thresholds = thresholds; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    private String sortThresholds(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 分割并排序
        return Arrays.stream(input.split(";"))
                .map(entry -> entry.split(":"))
                .filter(parts -> parts.length == 2) // 确保有key和value
                .sorted(Comparator.comparingDouble(a -> Double.parseDouble(a[0])))
                .map(parts -> parts[0] + ":" + parts[1])
                .collect(Collectors.joining(";"));
    }
}
