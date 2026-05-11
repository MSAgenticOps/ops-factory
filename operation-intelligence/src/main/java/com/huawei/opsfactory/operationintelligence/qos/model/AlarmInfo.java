
package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmInfo {
    private String alarmId;

    private String alarmName;

    private String severity;

    private String dn;

    private String meName;

    private Long occurUtc;

    private Integer count;

    private String moi;

    private String additionalInformation;

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getMeName() {
        return meName;
    }

    public void setMeName(String meName) {
        this.meName = meName;
    }

    public Long getOccurUtc() {
        return occurUtc;
    }

    public void setOccurUtc(Long occurUtc) {
        this.occurUtc = occurUtc;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getMoi() {
        return moi;
    }

    public void setMoi(String moi) {
        this.moi = moi;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
