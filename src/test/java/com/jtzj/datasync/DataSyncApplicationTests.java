package com.jtzj.datasync;

import com.jtzj.datasync.job.SyncQxbTransferVOrderProgressJob;
import com.jtzj.datasync.util.SpringContextUtil;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataSyncApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSyncQxbTransferVOrderProgressJob() throws Exception {
        SyncQxbTransferVOrderProgressJob syncQxbTransferVOrderProgressJob = SpringContextUtil.getBean(SyncQxbTransferVOrderProgressJob.class);
        syncQxbTransferVOrderProgressJob.execute("100");
    }
}
