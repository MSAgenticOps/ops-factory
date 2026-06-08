/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Request DTO for fetching current alarms from DV service.
 *
 * @author x00000000
 * @since 2026-06-06
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlarmQueryRequest {

    private Long startTime;

    private Long endTime;

    private List<String> severities;

    private List<String> dns;

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

    /**
     * Gets the list of severity levels.
     *
     * @return the list of severity levels
     */
    public List<String> getSeverities() {
        return severities;
    }

    /**
     * Sets the list of severity levels.
     *
     * @param severities the list of severity levels
     */
    public void setSeverities(List<String> severities) {
        this.severities = severities;
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
}
