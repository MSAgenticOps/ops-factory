package com.huawei.opsfactory.operationintelligence.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QosControllerTest {

    @Test
    void toLong_numberInput() {
        assertEquals(123L, QosController.toLong(123));
    }

    @Test
    void toLong_stringInput() {
        assertEquals(456L, QosController.toLong("456"));
    }

    @Test
    void toLong_nullInput_returnsZero() {
        assertEquals(0L, QosController.toLong(null));
    }

    @Test
    void toInt_numberInput() {
        assertEquals(10, QosController.toInt(10));
    }

    @Test
    void toInt_stringInput() {
        assertEquals(20, QosController.toInt("20"));
    }

    @Test
    void toInt_nullInput_returnsOne() {
        assertEquals(1, QosController.toInt(null));
    }
}
