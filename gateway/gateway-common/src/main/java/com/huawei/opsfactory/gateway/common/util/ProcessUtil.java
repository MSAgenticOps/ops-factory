/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.common.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Process management utility using JDK 21 APIs.
 *
 * @author x00000000
 * @since 2026-05-09
 */
public final class ProcessUtil {
    private ProcessUtil() {
    }

    /**
     * Reads up to the given number of bytes from the process output stream.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static String readOutput(Process process, int maxBytes) {
        try {
            byte[] bytes = process.getInputStream().readNBytes(maxBytes);
            return new String(bytes, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            return "(failed to read output: " + e.getMessage() + ")";
        }
    }

    /**
     * Returns the process identifier for the given process.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static long getPid(Process process) {
        return process.pid();
    }

    /**
     * Checks whether the given process is still alive.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static boolean isAlive(Process process) {
        return process.isAlive();
    }

    /**
     * Stops a process gracefully before forcing termination if needed.
     *
     * @author x00000000
     * @since 2026-05-09
     */
    public static void stopGracefully(Process process, long graceMs) {
        if (!process.isAlive()) {
            return;
        }
        process.destroy();
        try {
            Thread.sleep(graceMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }
}
