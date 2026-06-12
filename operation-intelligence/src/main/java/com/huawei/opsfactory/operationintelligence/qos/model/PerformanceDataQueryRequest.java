/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Request DTO for fetching performance data from DV service.
 *
 * @author x00000000
 * @since 2026-06-06
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PerformanceDataQueryRequest {

    private String moType;

    private String measUnitKey;

    private List<String> dns;

    private Long startTime;

    private Long endTime;

    /**
     * Gets the MO type.
     *
     * @return the MO type
     */
    public String getMoType() {
        return moType;
    }

    /**
     * Sets the MO type.
     *
     * @param moType the MO type
     */
    public void setMoType(String moType) {
        this.moType = moType;
    }

    /**
     * Gets the measurement unit key.
     *
     * @return the measurement unit key
     */
    public String getMeasUnitKey() {
        return measUnitKey;
    }

    /**
     * Sets the measurement unit key.
     *
     * @param measUnitKey the measurement unit key
     */
    public void setMeasUnitKey(String measUnitKey) {
        this.measUnitKey = measUnitKey;
    }

    /**
     * Gets the list of DN names.
     *
     * @return the list of DN names
     */
    public List<String> getDns() {
        return dns;
    }

    /**
     * Sets the list of DN names.
     *
     * @param dns the list of DN names
     */
    public void setDns(List<String> dns) {
        this.dns = dns;
    }

    /**
     * Gets the start time.
     *
     * @return the start time in milliseconds
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the start time in milliseconds
     */
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time.
     *
     * @return the end time in milliseconds
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime the end time in milliseconds
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
