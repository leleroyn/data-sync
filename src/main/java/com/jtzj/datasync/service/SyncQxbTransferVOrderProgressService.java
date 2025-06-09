package com.jtzj.datasync.service;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SyncQxbTransferVOrderProgressService {
    List<Map<String,Object>> getSyncRecord(int batchSize, BigDecimal syncVersion);
    void upsertOrderProgress(Map<String, Object> fields);
}
