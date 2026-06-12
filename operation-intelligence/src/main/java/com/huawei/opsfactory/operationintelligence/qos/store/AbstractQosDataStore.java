/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.operationintelligence.qos.store;

import com.huawei.opsfactory.operationintelligence.config.OperationIntelligenceProperties;

import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.file.Path;
import java.util.List;

/**
 * Abstract base class for QoS data stores that share common file store configuration.
 *
 * @param <T> the data type stored
 * @author x00000000
 * @since 2026-06-08
 */
public abstract class AbstractQosDataStore<T> {

    private final JsonFileStore<T> store;

    /**
     * Creates a new data store with the given configuration.
     *
     * @param properties the application properties
     * @param subDir the subdirectory under qos/ (e.g., "raw", "detail", "normalize")
     * @param filePrefix the file prefix for the JSON store
     * @param typeRef the type reference for deserialization
     * @param retentionDaysProperty the number of days to retain data
     */
    protected AbstractQosDataStore(OperationIntelligenceProperties properties, String subDir, String filePrefix,
        TypeReference<List<T>> typeRef, long retentionDaysProperty) {
        Path dir = properties.resolveDataRoot().resolve("qos").resolve(subDir);
        long rotationMs = properties.getQos().getRotationIntervalMs();
        long retentionMs = retentionDaysProperty * 86400_000L;
        this.store = new JsonFileStore<>(dir, filePrefix, typeRef, true, rotationMs, retentionMs);
        this.store.init();
    }

    /**
     * Loads data within the specified time range.
     *
     * @param startMs the start time in milliseconds
     * @param endMs the end time in milliseconds
     * @return the list of data items
     */
    public List<T> loadRange(long startMs, long endMs) {
        return store.loadRange(startMs, endMs);
    }

    /**
     * Appends a single item.
     *
     * @param item the item to append
     */
    public void append(T item) {
        store.append(item);
    }

    /**
     * Appends all items.
     *
     * @param items the items to append
     */
    public void appendAll(List<T> items) {
        store.appendAll(items);
    }

    /**
     * Cleans up expired data files.
     */
    public void cleanup() {
        store.cleanup();
    }
}
