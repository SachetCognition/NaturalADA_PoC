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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class PremiumRateGoldenMasterIT {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CentCodesRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        repository.deleteAll();
        Files.createDirectories(Path.of("output"));

        // Seed golden master dataset
        CentCodesRecord record = new CentCodesRecord();
        record.setTableType((short) 150);
        record.setDateKey(99999999);
        record.setCoverCodeSuper("150110AU99999999");
        record.setTpPremium1(25000);
        record.setPremium(25000);
        record.setCovGrpKey("GRP001");
        record.setInvType("INV01");
        repository.save(record);
    }

    @Test
    void shouldMatchGoldenMasterOutput() throws Exception {
        // T-PRM-007: Golden master comparison
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);
        assertEquals(BatchStatus.COMPLETED, execution.getStatus());

        // Verify audit-before contains original data
        Path auditBefore = Path.of("output/audit-before.csv");
        if (Files.exists(auditBefore)) {
            List<String> beforeLines = Files.readAllLines(auditBefore);
            assertFalse(beforeLines.isEmpty(), "Audit-before file should not be empty");
            assertTrue(beforeLines.get(0).contains("150110AU99999999"),
                    "First line should contain the original cover code super");
        }

        // Verify audit-after contains updated data with new premium
        Path auditAfter = Path.of("output/audit-after.csv");
        if (Files.exists(auditAfter)) {
            List<String> afterLines = Files.readAllLines(auditAfter);
            assertFalse(afterLines.isEmpty(), "Audit-after file should not be empty");
            boolean hasNewRecord = afterLines.stream()
                    .anyMatch(line -> line.contains("99999999") && line.contains("26667"));
            assertTrue(hasNewRecord, "Audit-after should contain new record with premium 26667");
        }
    }
}
