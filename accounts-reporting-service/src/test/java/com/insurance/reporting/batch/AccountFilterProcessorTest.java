package com.insurance.reporting.batch;

import com.insurance.reporting.domain.AccountRecord;
import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.service.AgentLookupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountFilterProcessorTest {

    @Mock
    private AgentLookupService agentLookupService;

    private AccountFilterProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AccountFilterProcessor(agentLookupService);
    }

    private AccountRecord createValidRecord() {
        AccountRecord record = new AccountRecord();
        record.setBranch("001");
        record.setAgent("ABC123");
        record.setPolicy("POL000001");
        record.setEntryDate(20130101);
        record.setMethodColl("K ");
        record.setEntryType("E");
        return record;
    }

    private AgentControl createActiveAgent() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 5);
        agent.setName("Test Agent");
        return agent;
    }

    @Test
    void shouldPassValidRecord() {
        // T-RPT-008: Active agent + valid date + valid MC1 + valid entry type → passes
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(createActiveAgent()));

        AccountRecord result = processor.process(createValidRecord());
        assertNotNull(result);
    }

    @Test
    void shouldFilterInactiveAgent() {
        AgentControl inactive = createActiveAgent();
        inactive.setStatus("X");
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(inactive));

        assertNull(processor.process(createValidRecord()));
    }

    @Test
    void shouldFilterMissingAgent() {
        when(agentLookupService.lookupAgent(anyString(), anyString()))
                .thenReturn(Optional.empty());

        assertNull(processor.process(createValidRecord()));
    }

    @Test
    void shouldFilterDateGreaterOrEqual20131199() {
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(createActiveAgent()));

        AccountRecord record = createValidRecord();
        record.setEntryDate(20131199);
        assertNull(processor.process(record));

        record.setEntryDate(20140101);
        assertNull(processor.process(record));
    }

    @Test
    void shouldFilterMethodCollR() {
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(createActiveAgent()));

        AccountRecord record = createValidRecord();
        record.setMethodColl("R ");
        assertNull(processor.process(record));
    }

    @Test
    void shouldFilterMethodCollX() {
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(createActiveAgent()));

        AccountRecord record = createValidRecord();
        record.setMethodColl("X ");
        assertNull(processor.process(record));
    }

    @Test
    void shouldFilterMethodCollS() {
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(createActiveAgent()));

        AccountRecord record = createValidRecord();
        record.setMethodColl("S ");
        assertNull(processor.process(record));
    }

    @Test
    void shouldFilterEntryTypeR() {
        when(agentLookupService.lookupAgent("001", "ABC123"))
                .thenReturn(Optional.of(createActiveAgent()));

        AccountRecord record = createValidRecord();
        record.setEntryType("R");
        assertNull(processor.process(record));
    }
}
