package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductConfigRule {
    private String agentSolutionType;
    private String excludeAlarmCode;
    private String alarmWeight;
    private Integer alarmScoreMax;
    private String healthWeight;

    public String getAgentSolutionType() { return agentSolutionType; }
    public void setAgentSolutionType(String agentSolutionType) { this.agentSolutionType = agentSolutionType; }
    public String getExcludeAlarmCode() { return excludeAlarmCode; }
    public void setExcludeAlarmCode(String excludeAlarmCode) { this.excludeAlarmCode = excludeAlarmCode; }
    public String getAlarmWeight() { return alarmWeight; }
    public void setAlarmWeight(String alarmWeight) { this.alarmWeight = alarmWeight; }
    public Integer getAlarmScoreMax() { return alarmScoreMax; }
    public void setAlarmScoreMax(Integer alarmScoreMax) { this.alarmScoreMax = alarmScoreMax; }
    public String getHealthWeight() { return healthWeight; }
    public void setHealthWeight(String healthWeight) { this.healthWeight = healthWeight; }
}
