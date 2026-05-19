package com.insurance.premium.batch;

import com.insurance.premium.domain.CentCodesRecord;
import com.insurance.premium.repository.CentCodesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class PremiumRateBatchIT {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CentCodesRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        seedTestData();
    }

    private void seedTestData() {
        CentCodesRecord record1 = new CentCodesRecord();
        record1.setTableType((short) 150);
        record1.setDateKey(99999999);
        record1.setCoverCodeSuper("150110AU99999999");
        record1.setTpPremium1(25000);
        record1.setPremium(25000);
        record1.setCovGrpKey("GRP001");
        record1.setInvType("INV01");
        repository.save(record1);

        CentCodesRecord record2 = new CentCodesRecord();
        record2.setTableType((short) 150);
        record2.setDateKey(99999999);
        record2.setCoverCodeSuper("150110US99999999");
        record2.setTpPremium1(30000);
        record2.setPremium(30000);
        record2.setCovGrpKey("GRP002");
        record2.setInvType("INV02");
        repository.save(record2);
    }

    @Test
    void shouldRunFullBatchPipeline() throws Exception {
        // T-PRM-005, T-PRM-006: Run all 4 steps and validate
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        assertEquals(BatchStatus.COMPLETED, execution.getStatus());

        // Verify R2: records with dateKey=99999999 are now 20150331
        List<CentCodesRecord> expired = repository
                .findByCoverCodeSuperStartingWithAndTableTypeAndDateKey("150110", (short) 150, 20150331);
        assertFalse(expired.isEmpty(), "Should have expired records with dateKey=20150331");

        // Verify R3: new records exist with dateKey=99999999 and premium=26667
        List<CentCodesRecord> newActive = repository
                .findByCoverCodeSuperStartingWithAndTableTypeAndDateKey("150110", (short) 150, 99999999);
        assertFalse(newActive.isEmpty(), "Should have new active records");
        for (CentCodesRecord record : newActive) {
            assertEquals(26667, record.getPremium());
            assertEquals(26667, record.getTpPremium1());
        }
    }
}
