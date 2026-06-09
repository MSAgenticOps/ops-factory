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

/**
 * Shared file-safety helpers for the per-user proactive stores: a per-path monitor so read-modify-write cycles on
 * the same file serialize (no lost updates), and an atomic write (temp file + atomic rename) so readers never see a
 * partially written file.
 *
 * @author x00000000
 * @since 2026-06-07
 */
final class ProactiveFiles {
    /*
     * A fixed pool of monitors striped by path hash, NOT an unbounded per-path map. This bounds memory for the life
     * of the process, and — unlike an evicting cache — a given path always maps to the same monitor, so mutual
     * exclusion can never be broken by evicting a lock another thread is currently holding. Two distinct paths may
     * share a monitor (benign false contention); the proactive stores are low-traffic, so that is negligible.
     */
    private static final int LOCK_STRIPES = 64;

    private static final Object[] LOCKS = newLocks(LOCK_STRIPES);

    private ProactiveFiles() {
    }

    private static Object[] newLocks(int count) {
        Object[] locks = new Object[count];
        for (int i = 0; i < count; i++) {
            locks[i] = new Object();
        }
        return locks;
    }

    /**
     * Returns a process-wide monitor for a file path; callers synchronize their read-modify-write on it. The same
     * path always returns the same monitor (striped over a fixed pool, so the pool never grows).
     *
     * @param path target file
     * @return the monitor object for that path
     */
    static Object lockFor(Path path) {
        String key = path.toAbsolutePath().normalize().toString();
        return LOCKS[(key.hashCode() & Integer.MAX_VALUE) % LOCK_STRIPES];
    }

    /**
     * Writes content atomically: write to a sibling temp file, then atomically rename over the target.
     *
     * @param file target file
     * @param content content to write
     * @throws IOException if the write or rename fails
     */
    static void atomicWrite(Path file, String content) throws IOException {
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path tmp = file.resolveSibling(file.getFileName().toString() + ".tmp");
        Files.writeString(tmp, content, StandardCharsets.UTF_8);
        boolean moved = false;
        try {
            try {
                Files.move(tmp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unsupported) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
        } finally {
            if (!moved) {
                // Best-effort cleanup of the stray temp file; never let it mask the original move failure.
                try {
                    Files.deleteIfExists(tmp);
                } catch (IOException cleanupFailure) {
                    // ignore: the original IOException from the failed move propagates instead
                }
            }
        }
    }
}
