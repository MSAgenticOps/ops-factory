/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test coverage for {@link ProactiveDeliveryMarkers} (delivery.json read/modify/write).
 *
 * @author x00000000
 * @since 2026-06-07
 */
public class ProactiveDeliveryMarkersTest {
    @Test
    public void setDeliver_thenGetDeliver_returnsValue() throws Exception {
        Path file = tempDeliveryFile();

        ProactiveDeliveryMarkers.setDeliver(file, "job-1", ProactiveDeliveryMarkers.DELIVER_IM);

        assertEquals(ProactiveDeliveryMarkers.DELIVER_IM, ProactiveDeliveryMarkers.getDeliver(file, "job-1"));
        assertTrue("file should be created lazily on first write", Files.exists(file));
    }

    @Test
    public void getDeliver_missingFile_returnsNull() throws Exception {
        Path file = Files.createTempDirectory("pdm").resolve(ProactiveDeliveryMarkers.DELIVERY_FILE);

        assertNull(ProactiveDeliveryMarkers.getDeliver(file, "job-1"));
    }

    @Test
    public void getDeliver_unknownSchedule_returnsNull() throws Exception {
        Path file = tempDeliveryFile();
        ProactiveDeliveryMarkers.setDeliver(file, "job-1", ProactiveDeliveryMarkers.DELIVER_IM);

        assertNull(ProactiveDeliveryMarkers.getDeliver(file, "other"));
    }

    @Test
    public void setDeliver_multipleSchedules_areIndependent() throws Exception {
        Path file = tempDeliveryFile();

        ProactiveDeliveryMarkers.setDeliver(file, "a", ProactiveDeliveryMarkers.DELIVER_IM);
        ProactiveDeliveryMarkers.setDeliver(file, "b", ProactiveDeliveryMarkers.DELIVER_IM);

        assertEquals(ProactiveDeliveryMarkers.DELIVER_IM, ProactiveDeliveryMarkers.getDeliver(file, "a"));
        assertEquals(ProactiveDeliveryMarkers.DELIVER_IM, ProactiveDeliveryMarkers.getDeliver(file, "b"));
    }

    @Test
    public void remove_deletesOnlyTargetEntry() throws Exception {
        Path file = tempDeliveryFile();
        ProactiveDeliveryMarkers.setDeliver(file, "a", ProactiveDeliveryMarkers.DELIVER_IM);
        ProactiveDeliveryMarkers.setDeliver(file, "b", ProactiveDeliveryMarkers.DELIVER_IM);

        ProactiveDeliveryMarkers.remove(file, "a");

        assertNull(ProactiveDeliveryMarkers.getDeliver(file, "a"));
        assertEquals(ProactiveDeliveryMarkers.DELIVER_IM, ProactiveDeliveryMarkers.getDeliver(file, "b"));
    }

    @Test
    public void remove_missingEntry_isNoOp() throws Exception {
        Path file = Files.createTempDirectory("pdm").resolve(ProactiveDeliveryMarkers.DELIVERY_FILE);

        ProactiveDeliveryMarkers.remove(file, "never");

        assertNull(ProactiveDeliveryMarkers.getDeliver(file, "never"));
    }

    private static Path tempDeliveryFile() throws Exception {
        return Files.createTempDirectory("pdm").resolve(ProactiveDeliveryMarkers.DELIVERY_FILE);
    }
}
