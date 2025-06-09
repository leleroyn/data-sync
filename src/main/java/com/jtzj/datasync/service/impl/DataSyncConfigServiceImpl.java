package com.jtzj.datasync.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.jtzj.datasync.service.DataSyncConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DataSyncConfigServiceImpl implements DataSyncConfigService {
    private final JdbcTemplate jdbcTemplate;

    @DS("auto-generateData-db")
    @Override
    public BigDecimal getCurrSyncVersion(String tableName) {
        try {
            return jdbcTemplate.queryForObject(getCurrSyncVersionSQL, BigDecimal.class, tableName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @DS("auto-generateData-db")
    @Override
    public void updateConfig(BigDecimal syncVersion, String tableName) {
        jdbcTemplate.update("Update data_sync_config set sync_version = ?,update_at=? WHERE table_name = ?  AND status = 1", syncVersion, new Date(), tableName);
    }

    private static final String getCurrSyncVersionSQL = "SELECT sync_version FROM data_sync_config WHERE table_name = ?  AND status = 1";

}
