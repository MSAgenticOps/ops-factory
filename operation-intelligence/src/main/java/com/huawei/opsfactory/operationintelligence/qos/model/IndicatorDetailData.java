/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

import java.math.BigDecimal;

/**
 * Indicator Detail Data.
 *
 * @author x00000000
 * @since 2026-05-11
 */
public class IndicatorDetailData extends IndicatorBase {
    private String indicatorCode;

    private String indicatorName;

    private String type;

    private BigDecimal dnIndicatorValue;

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getDnIndicatorValue() {
        return dnIndicatorValue;
    }

    public void setDnIndicatorValue(BigDecimal dnIndicatorValue) {
        this.dnIndicatorValue = dnIndicatorValue;
    }
}
