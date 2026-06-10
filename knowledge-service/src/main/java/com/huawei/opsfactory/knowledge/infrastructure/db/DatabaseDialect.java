/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.infrastructure.db;

import java.util.List;

/**
 * Supported database dialects for the knowledge service.
 * @author x00000000
 * @since 2026-05-26
 */

public enum DatabaseDialect {

    SQLITE("sqlite", "org.sqlite.JDBC", List.of("classpath:db/migration/common")),
    POSTGRESQL("postgresql", "org.postgresql.Driver", List.of("classpath:db/migration/common"));

    private final String type;
    private final String defaultDriverClassName;
    private final List<String> flywayLocations;

    DatabaseDialect(String type, String defaultDriverClassName, List<String> flywayLocations) {
        this.type = type;
        this.defaultDriverClassName = defaultDriverClassName;
        this.flywayLocations = flywayLocations;
    }

    public String type() {
        return type;
    }

    public String defaultDriverClassName() {
        return defaultDriverClassName;
    }

    public List<String> flywayLocations() {
        return flywayLocations;
    }
}
