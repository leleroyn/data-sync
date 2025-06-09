package com.jtzj.datasync.service;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.math.BigDecimal;

public interface DataSyncConfigService {
    BigDecimal getCurrSyncVersion(String tableName);
    void updateConfig(BigDecimal syncVersion, String tableName);
}
