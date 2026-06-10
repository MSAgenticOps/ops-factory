/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.model;

/**
 * Indicator Raw Data.
 *
 * @author x00000000
 * @since 2026-05-11
 */
public class IndicatorRawData extends IndicatorBase {
    private String moType;

    private String neName;

    public String getMoType() {
        return moType;
    }

    public void setMoType(String moType) {
        this.moType = moType;
    }

    public String getNeName() {
        return neName;
    }

    public void setNeName(String neName) {
        this.neName = neName;
    }
}
