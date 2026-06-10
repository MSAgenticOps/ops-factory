/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2026-2026. All rights reserved.
 */

package com.huawei.opsfactory.knowledge.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huawei.opsfactory.knowledge.common.util.Jsons;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * The ProfileRepository.
 * @author x00000000
 * @since 2026-05-26
 */

@Repository
public class ProfileRepository {

    private static final String INDEX_TABLE = "index_profile";
    private static final String RETRIEVAL_TABLE = "retrieval_profile";
    private static final Set<String> ALLOWED_TABLES = Set.of(INDEX_TABLE, RETRIEVAL_TABLE);

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final RowMapper<ProfileRecord> indexMapper = (rs, rowNum) -> map(rs, "index");
    private final RowMapper<ProfileRecord> retrievalMapper = (rs, rowNum) -> map(rs, "retrieval");

    public ProfileRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public void insertIndex(ProfileRecord record) {
        insert(INDEX_TABLE, record);
    }

    public void insertRetrieval(ProfileRecord record) {
        insert(RETRIEVAL_TABLE, record);
    }

    public List<ProfileRecord> findAllIndex() {
        return jdbcTemplate.query("select * from " + INDEX_TABLE + " order by created_at desc", indexMapper);
    }

    public List<ProfileRecord> findAllRetrieval() {
        return jdbcTemplate.query("select * from " + RETRIEVAL_TABLE + " order by created_at desc", retrievalMapper);
    }

    public Optional<ProfileRecord> findIndexById(String id) {
        return findById(INDEX_TABLE, indexMapper, id);
    }

    public Optional<ProfileRecord> findRetrievalById(String id) {
        return findById(RETRIEVAL_TABLE, retrievalMapper, id);
    }

    public Optional<ProfileRecord> findIndexByName(String name) {
        return findByName(INDEX_TABLE, indexMapper, name);
    }

    public Optional<ProfileRecord> findRetrievalByName(String name) {
        return findByName(RETRIEVAL_TABLE, retrievalMapper, name);
    }

    public Optional<ProfileRecord> findIndexByOwnerSourceId(String sourceId) {
        return findByOwnerSourceId(INDEX_TABLE, indexMapper, sourceId);
    }

    public Optional<ProfileRecord> findRetrievalByOwnerSourceId(String sourceId) {
        return findByOwnerSourceId(RETRIEVAL_TABLE, retrievalMapper, sourceId);
    }

    public void updateIndex(ProfileRecord record) {
        update(INDEX_TABLE, record);
    }

    public void updateRetrieval(ProfileRecord record) {
        update(RETRIEVAL_TABLE, record);
    }

    public void deleteIndex(String id) {
        delete(INDEX_TABLE, id);
    }

    public void deleteRetrieval(String id) {
        delete(RETRIEVAL_TABLE, id);
    }

    private void insert(String table, ProfileRecord record) {
        validateTable(table);
        String sql = "insert into " + table
            + " (id, name, config_json, owner_source_id, readonly, derived_from_profile_id, created_at, updated_at)"
            + " values (?,?,?,?,?,?,?,?)";
        jdbcTemplate.update(
            sql,
            record.id(), record.name(), Jsons.write(objectMapper, record.config()), record.ownerSourceId(),
            record.readonly() ? 1 : 0, record.derivedFromProfileId(), record.createdAt().toString(),
            record.updatedAt().toString()
        );
    }

    private void update(String table, ProfileRecord record) {
        validateTable(table);
        String sql = "update " + table
            + " set name=?, config_json=?, owner_source_id=?, readonly=?, derived_from_profile_id=?, updated_at=?"
            + " where id=?";
        jdbcTemplate.update(
            sql,
            record.name(), Jsons.write(objectMapper, record.config()), record.ownerSourceId(), record.readonly() ? 1 : 0,
            record.derivedFromProfileId(), record.updatedAt().toString(), record.id()
        );
    }

    private void delete(String table, String id) {
        validateTable(table);
        jdbcTemplate.update("delete from " + table + " where id = ?", id);
    }

    private Optional<ProfileRecord> findById(String table, RowMapper<ProfileRecord> mapper, String id) {
        validateTable(table);
        return jdbcTemplate.query("select * from " + table + " where id = ?", mapper, id).stream().findFirst();
    }

    private Optional<ProfileRecord> findByName(String table, RowMapper<ProfileRecord> mapper, String name) {
        validateTable(table);
        return jdbcTemplate.query("select * from " + table + " where name = ?", mapper, name).stream().findFirst();
    }

    private Optional<ProfileRecord> findByOwnerSourceId(
        String table, RowMapper<ProfileRecord> mapper, String sourceId
    ) {
        validateTable(table);
        return jdbcTemplate.query("select * from " + table + " where owner_source_id = ?", mapper, sourceId)
            .stream()
            .findFirst();
    }

    private void validateTable(String table) {
        if (!ALLOWED_TABLES.contains(table)) {
            throw new IllegalArgumentException("Invalid profile table: " + table);
        }
    }

    private ProfileRecord map(ResultSet rs, String type) throws SQLException {
        return new ProfileRecord(
            rs.getString("id"),
            rs.getString("name"),
            Jsons.readMap(objectMapper, rs.getString("config_json")),
            type,
            rs.getString("owner_source_id"),
            rs.getInt("readonly") != 0,
            rs.getString("derived_from_profile_id"),
            Instant.parse(rs.getString("created_at")),
            Instant.parse(rs.getString("updated_at"))
        );
    }

    public record ProfileRecord(
        String id,
        String name,
        Map<String, Object> config,
        String type,
        String ownerSourceId,
        boolean readonly,
        String derivedFromProfileId,
        Instant createdAt,
        Instant updatedAt
    ) {
    }
}
