package com.jtzj.datasync.job;

import com.alibaba.fastjson.JSON;
import com.jtzj.datasync.service.DataSyncConfigService;
import com.jtzj.datasync.service.SyncQxbTransferVOrderProgressService;
import com.jtzj.datasync.util.UspLogger;
import com.jtzj.datasync.util.UspLoggerFactory;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.RequiredArgsConstructor;
import lombok.var;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

@Component
@JobHandler("SyncQxbTransferVOrderProgressJob")
@RequiredArgsConstructor
public class SyncQxbTransferVOrderProgressJob extends IJobHandler {
    private static final UspLogger logger = UspLoggerFactory.getLogger(SyncQxbTransferVOrderProgressJob.class);
    private final SyncQxbTransferVOrderProgressService syncQxbTransferVOrderProgressService;
    private final DataSyncConfigService dataSyncConfigService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        BigDecimal syncVersion = dataSyncConfigService.getCurrSyncVersion("v_order_progress");
        if (syncVersion == null) {
            logger.error("SyncQxbTransferVOrderProgressJob 同步异常：" + "未找到v_order_progress的同步配置.");
            throw new Exception("未找到v_order_progress的同步配置.");
        }
        var syncRecords = syncQxbTransferVOrderProgressService.getSyncRecord(syncVersion);
        if (!CollectionUtils.isEmpty(syncRecords)) {
            logger.debug(String.format("待同步数量:%s,当前版本：%s", syncRecords.size(), syncVersion));
            syncRecords.forEach(o -> {
                        try {
                            syncQxbTransferVOrderProgressService.upsertOrderProgress(o);
                        } catch (Exception ex) {
                            logger.error("同步v_order_progress record 失败", JSON.toJSONString(o));
                        }
                    }
            );
            var curVersion = new BigDecimal(syncRecords.get(syncRecords.size() - 1).get("sync_version").toString());
            dataSyncConfigService.updateConfig(curVersion, "v_order_progress");
        }
        return ReturnT.SUCCESS;
    }
}
