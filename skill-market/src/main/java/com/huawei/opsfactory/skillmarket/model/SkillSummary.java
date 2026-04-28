package com.huawei.opsfactory.skillmarket.model;

public record SkillSummary(
    String id,
    String name,
    String description,
    String path,
    boolean containsScripts,
    String checksum,
    long sizeBytes,
    int fileCount,
    String createdAt,
    String updatedAt
) {
}
