/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.infrastructure.db;

import java.util.List;

/**
 * Supported database dialects for the knowledge service.
 *
 * @author x00000000
 * @since 2026-05-26
 */

public enum DatabaseDialect {

    /**
     * SQLite database dialect.
     */
    SQLITE("sqlite", "org.sqlite.JDBC", List.of("classpath:db/migration/common")),

    /**
     * PostgreSQL database dialect.
     */
    POSTGRESQL("postgresql", "org.postgresql.Driver", List.of("classpath:db/migration/common"));

    private final String type;

    private final String defaultDriverClassName;

    private final List<String> flywayLocations;

    /**
     * Creates a database dialect descriptor.
     *
     * @param type the configured dialect type
     * @param defaultDriverClassName the default JDBC driver class
     * @param flywayLocations Flyway migration locations
     */
    DatabaseDialect(String type, String defaultDriverClassName, List<String> flywayLocations) {
        this.type = type;
        this.defaultDriverClassName = defaultDriverClassName;
        this.flywayLocations = flywayLocations;
    }

    /**
     * Returns the configured dialect type.
     *
     * @return the dialect type
     */
    public String type() {
        return type;
    }

    /**
     * Returns the default JDBC driver class name.
     *
     * @return the driver class name
     */
    public String defaultDriverClassName() {
        return defaultDriverClassName;
    }

    /**
     * Returns the Flyway migration locations.
     *
     * @return the Flyway migration locations
     */
    public List<String> flywayLocations() {
        return flywayLocations;
    }
}
