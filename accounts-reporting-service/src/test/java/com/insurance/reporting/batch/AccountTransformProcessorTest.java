package com.insurance.reporting.batch;

import com.insurance.reporting.domain.AccountRecord;
import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.dto.ReportLine;
import com.insurance.reporting.service.AgentLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountTransformProcessorTest {

    @Mock
    private AgentLookupService agentLookupService;

    private AccountTransformProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AccountTransformProcessor(agentLookupService);
    }

    private AccountRecord createRecord() {
        AccountRecord record = new AccountRecord();
        record.setBranch("001");
        record.setAgent("ABC123");
        record.setPolicy("POL000001");
        record.setEntryDate(20130601);
        record.setCashDate(20130715);
        record.setEntryType("E");
        record.setMethodColl("K ");
        record.setDebCredAmt(123456L);
        record.setCommAmount(-50000L);
        record.setCashAmt(0L);
        return record;
    }

    @Test
    void shouldTransformWithPositiveAmounts() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 5);
        agent.setName("Test Agent");
        when(agentLookupService.lookupAgent("001", "ABC123")).thenReturn(Optional.of(agent));

        ReportLine line = processor.process(createRecord());

        assertNotNull(line);
        assertEquals("001", line.branch());
        assertEquals("ABC123", line.agent());
        assertEquals("POL000001", line.policy());
        assertEquals((short) 5, line.inspectNo());
        assertEquals("Test Agent", line.agentName());
        assertEquals("ADDL", line.type());
        assertEquals("UNMATCHED CASH", line.type2());
        assertTrue(line.emPrem().contains("1234.56"));
    }

    @Test
    void shouldFormatNegativeAmounts() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 1);
        agent.setName("Agent");
        when(agentLookupService.lookupAgent("001", "ABC123")).thenReturn(Optional.of(agent));

        ReportLine line = processor.process(createRecord());

        assertNotNull(line);
        // Comm is -500.00
        assertTrue(line.emComm().startsWith("-"));
        assertTrue(line.emComm().contains("500.00"));
    }

    @Test
    void shouldSanitizeDates() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 1);
        agent.setName("Agent");
        when(agentLookupService.lookupAgent("001", "ABC123")).thenReturn(Optional.of(agent));

        AccountRecord record = createRecord();
        record.setEntryDate(1800000);

        ReportLine line = processor.process(record);
        assertNotNull(line);
        assertEquals(0, line.entryDate());
    }

    @Test
    void shouldMapEntryTypes() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 1);
        agent.setName("Agent");
        when(agentLookupService.lookupAgent("001", "ABC123")).thenReturn(Optional.of(agent));

        AccountRecord record = createRecord();
        record.setEntryType("C");

        ReportLine line = processor.process(record);
        assertNotNull(line);
        assertEquals("NEW", line.type());
    }

    @Test
    void shouldHandleZeroAmounts() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 1);
        agent.setName("Agent");
        when(agentLookupService.lookupAgent("001", "ABC123")).thenReturn(Optional.of(agent));

        AccountRecord record = createRecord();
        record.setDebCredAmt(0L);
        record.setCommAmount(0L);
        record.setCashAmt(0L);

        ReportLine line = processor.process(record);
        assertNotNull(line);
        assertTrue(line.emPrem().contains("0.00"));
    }
}
