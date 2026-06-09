/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.gateway.service.proactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stateless read/append helpers for the per-user follow-up record file ({@code proactive-followups/records.jsonl}),
 * one {@link ProactiveFollowupRecord} per line. Operating on an explicit {@link Path} keeps it trivially testable
 * against a temp directory.
 *
 * <p>Append performs light compaction (PRD §7.1): records older than {@link #MAX_AGE_DAYS} are dropped and the file
 * is capped to the most recent {@link #MAX_RECORDS}. Malformed lines are skipped, never fatal.
 *
 * @author x00000000
 * @since 2026-06-07
 */
public final class ProactiveFollowups {
    /** Maximum records retained per file. */
    public static final int MAX_RECORDS = 1000;

    /** Maximum record age retained, in days. */
    public static final long MAX_AGE_DAYS = 30;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(ProactiveFollowups.class);

    private ProactiveFollowups() {
    }

    /**
     * Appends a record, then compacts: records older than {@link #MAX_AGE_DAYS} are dropped and the file is capped
     * to the most recent {@link #MAX_RECORDS}. This rewrites the whole file atomically; concurrent appends to the
     * same file are serialized on a per-path monitor so none are lost.
     *
     * @param recordsFile path to the per-user {@code records.jsonl}
     * @param record record to append
     * @throws IOException if the file cannot be read or written
     */
    public static void append(Path recordsFile, ProactiveFollowupRecord record) throws IOException {
        synchronized (ProactiveFiles.lockFor(recordsFile)) {
            List<ProactiveFollowupRecord> kept = compactByAge(read(recordsFile));
            kept.add(record);
            if (kept.size() > MAX_RECORDS) {
                kept = new ArrayList<>(kept.subList(kept.size() - MAX_RECORDS, kept.size()));
            }
            write(recordsFile, kept);
        }
    }

    /**
     * Returns whether any record was already written for the given session id (delivery idempotency).
     *
     * @param recordsFile path to the per-user {@code records.jsonl}
     * @param sessionId goosed session id
     * @return {@code true} if a record with that session id exists
     * @throws IOException if the file cannot be read
     */
    public static boolean existsForSession(Path recordsFile, String sessionId) throws IOException {
        if (sessionId == null) {
            return false;
        }
        for (ProactiveFollowupRecord r : read(recordsFile)) {
            if (sessionId.equals(r.sessionId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns up to {@code limit} most recent records for a target key, oldest-first (append order).
     *
     * @param recordsFile path to the per-user {@code records.jsonl}
     * @param targetKey conversation target key to match
     * @param limit maximum records to return
     * @return matching records (most recent {@code limit}), oldest-first
     * @throws IOException if the file cannot be read
     */
    public static List<ProactiveFollowupRecord> recentByTargetKey(Path recordsFile, String targetKey, int limit)
        throws IOException {
        List<ProactiveFollowupRecord> matched = new ArrayList<>();
        if (targetKey == null || limit <= 0) {
            return matched;
        }
        for (ProactiveFollowupRecord r : read(recordsFile)) {
            if (targetKey.equals(r.targetKey())) {
                matched.add(r);
            }
        }
        if (matched.size() > limit) {
            return new ArrayList<>(matched.subList(matched.size() - limit, matched.size()));
        }
        return matched;
    }

    private static List<ProactiveFollowupRecord> read(Path recordsFile) throws IOException {
        List<ProactiveFollowupRecord> out = new ArrayList<>();
        if (recordsFile == null || Files.notExists(recordsFile)) {
            return out;
        }
        for (String line : Files.readAllLines(recordsFile, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }
            try {
                out.add(MAPPER.readValue(line, ProactiveFollowupRecord.class));
            } catch (JsonProcessingException malformed) {
                // Skip a corrupt line rather than failing the whole read (best-effort store), but surface it.
                LOG.warn("Skipping corrupt follow-up record in {}: {}", recordsFile, malformed.getOriginalMessage());
            }
        }
        return out;
    }

    private static List<ProactiveFollowupRecord> compactByAge(List<ProactiveFollowupRecord> records) {
        Instant cutoff = Instant.now().minus(Duration.ofDays(MAX_AGE_DAYS));
        List<ProactiveFollowupRecord> kept = new ArrayList<>();
        for (ProactiveFollowupRecord r : records) {
            Instant time = parseInstant(r.time());
            if (time == null || !time.isBefore(cutoff)) {
                kept.add(r);
            }
        }
        return kept;
    }

    private static Instant parseInstant(String iso) {
        if (iso == null) {
            return null;
        }
        try {
            return Instant.parse(iso);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void write(Path recordsFile, List<ProactiveFollowupRecord> records) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (ProactiveFollowupRecord r : records) {
            sb.append(MAPPER.writeValueAsString(r)).append('\n');
        }
        ProactiveFiles.atomicWrite(recordsFile, sb.toString());
    }
}
