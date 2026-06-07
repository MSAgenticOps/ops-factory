/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared file-safety helpers for the per-user proactive stores: a per-path monitor so read-modify-write cycles on
 * the same file serialize (no lost updates), and an atomic write (temp file + atomic rename) so readers never see a
 * partially written file.
 *
 * @author x00000000
 * @since 2026-06-07
 */
final class ProactiveFiles {
    private static final ConcurrentHashMap<String, Object> LOCKS = new ConcurrentHashMap<>();

    private ProactiveFiles() {
    }

    /**
     * Returns a process-wide monitor for a file path; callers synchronize their read-modify-write on it.
     *
     * @param path target file
     * @return the monitor object for that path
     */
    static Object lockFor(Path path) {
        return LOCKS.computeIfAbsent(path.toAbsolutePath().normalize().toString(), key -> new Object());
    }

    /**
     * Writes content atomically: write to a sibling temp file, then atomically rename over the target.
     *
     * @param file target file
     * @param content content to write
     * @throws IOException if the write or rename fails
     */
    static void atomicWrite(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
        Files.writeString(tmp, content, StandardCharsets.UTF_8);
        try {
            Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
