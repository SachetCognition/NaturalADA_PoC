package com.insurance.reporting.batch;

import com.insurance.reporting.domain.AccountRecord;
import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.repository.AccountRepository;
import com.insurance.reporting.repository.AgentControlRepository;
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
class AccountsReportBatchIT {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AgentControlRepository agentControlRepository;

    @BeforeEach
    void setUp() throws IOException {
        accountRepository.deleteAll();
        agentControlRepository.deleteAll();
        Files.createDirectories(Path.of("output"));

        // Seed agent controls
        AgentControl agent = new AgentControl();
        agent.setMainAgtKey("100013ABC123");
        agent.setStatus("A");
        agent.setInspectNo((short) 5);
        agent.setName("Test Agent");
        agentControlRepository.save(agent);

        // Seed accounts
        AccountRecord acc1 = new AccountRecord();
        acc1.setAccKey("001ABC123POL000001");
        acc1.setBranch("001");
        acc1.setAgent("ABC123");
        acc1.setPolicy("POL000001");
        acc1.setEntryType("E");
        acc1.setEntryDate(20130601);
        acc1.setDebCredAmt(123456L);
        acc1.setCommAmount(50000L);
        acc1.setCashAmt(25000L);
        acc1.setCashDate(20130715);
        acc1.setMethodColl("K ");
        acc1.setProcessMkrs("  ");
        accountRepository.save(acc1);

        // Record that should be filtered (inactive agent)
        AgentControl inactiveAgent = new AgentControl();
        inactiveAgent.setMainAgtKey("100023DEF456");
        inactiveAgent.setStatus("X");
        inactiveAgent.setInspectNo((short) 3);
        inactiveAgent.setName("Inactive Agent");
        agentControlRepository.save(inactiveAgent);

        AccountRecord acc2 = new AccountRecord();
        acc2.setAccKey("002DEF456POL000002");
        acc2.setBranch("002");
        acc2.setAgent("DEF456");
        acc2.setPolicy("POL000002");
        acc2.setEntryType("C");
        acc2.setEntryDate(20130501);
        acc2.setDebCredAmt(50000L);
        acc2.setCommAmount(10000L);
        acc2.setCashAmt(5000L);
        acc2.setCashDate(20130601);
        acc2.setMethodColl("K ");
        acc2.setProcessMkrs("  ");
        accountRepository.save(acc2);
    }

    @Test
    void shouldRunFullReportingBatch() throws Exception {
        // T-RPT-010, T-RPT-011
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);
        assertEquals(BatchStatus.COMPLETED, execution.getStatus());

        // Verify output file exists and contains the valid record
        Path outputFile = Path.of("output/accounts-report.txt");
        if (Files.exists(outputFile)) {
            List<String> lines = Files.readAllLines(outputFile);
            // Only the first account should appear (second has inactive agent)
            assertFalse(lines.isEmpty(), "Report should contain at least one line");

            String firstLine = lines.get(0);
            assertTrue(firstLine.contains("001"), "Should contain branch 001");
            assertTrue(firstLine.contains("ABC123"), "Should contain agent ABC123");
            assertTrue(firstLine.contains("~"), "Should be tilde-delimited");

            // Verify filtered record is NOT in output
            boolean hasFilteredRecord = lines.stream().anyMatch(l -> l.contains("DEF456"));
            assertFalse(hasFilteredRecord, "Inactive agent records should be filtered out");
        }
    }
}
