package com.jtzj.datasync.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.jtzj.datasync.service.SyncQxbTransferVOrderProgressService;
import com.jtzj.datasync.util.UspLogger;
import com.jtzj.datasync.util.UspLoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SyncQxbTransferVOrderProgressServiceImpl implements SyncQxbTransferVOrderProgressService {
    private static final UspLogger logger = UspLoggerFactory.getLogger(SyncQxbTransferVOrderProgressServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @DS("qxb-transfer")
    @Override
    public List<Map<String, Object>> getSyncRecord(int batchSize, BigDecimal syncVersion) {
        return jdbcTemplate.queryForList(getSyncRecordSql, syncVersion, batchSize);
    }


    @DS("auto-generateData-db")
    @Override
    public void upsertOrderProgress(Map<String, Object> fields) {

        if (fields == null || fields.isEmpty()) return;

        // 1. 验证必需字段
        if (!fields.containsKey("orderItemId")) {
            throw new IllegalArgumentException("主键orderItemId不能为空");
        }
        logger.debug("Update VOrderProgress record", JSON.toJSONString(fields));

        // 2. 动态构建SQL
        String sql = buildMergeSql(fields.keySet());

        // 3. 准备参数数组（按字段名排序）
        Object[] params = fields.keySet().stream()
                .sorted()
                .map(fields::get)
                .toArray();

        // 4. 执行更新
        jdbcTemplate.update(sql, params);
    }

    private String buildMergeSql(Set<String> fieldNames) {
        // 2.1 构建字段列表（排序确保参数顺序一致）
        List<String> sortedFields = new ArrayList<>(fieldNames);
        Collections.sort(sortedFields);

        // 2.2 构建字段SQL片段
        StringJoiner sourceColumns = new StringJoiner(", ");
        StringJoiner insertColumns = new StringJoiner(", ");
        StringJoiner insertValues = new StringJoiner(", ");
        StringJoiner updateSet = new StringJoiner(", ");

        for (String field : sortedFields) {
            String quotedField = "\"" + field + "\"";
            sourceColumns.add("? AS " + quotedField);
            insertColumns.add(quotedField);
            insertValues.add("source." + quotedField);

            // 主键不参与更新
            if (!"orderItemId".equals(field)) {
                updateSet.add("target." + quotedField + " = source." + quotedField);
            }
        }

        // 2.3 构建完整MERGE语句
        return "MERGE INTO qxb_v_order_progress AS target " +
                "USING (SELECT " + sourceColumns + ") AS source " +
                "ON (target.\"orderItemId\" = source.\"orderItemId\") " +
                "WHEN MATCHED THEN " +
                "    UPDATE SET " + updateSet + " " +
                "WHEN NOT MATCHED THEN " +
                "    INSERT (" + insertColumns + ") " +
                "    VALUES (" + insertValues + ");";
    }

    private static final String getSyncRecordSql = "SELECT * FROM  v_order_progress  WHERE sync_version > ? ORDER BY sync_version LIMIT ?";
}
