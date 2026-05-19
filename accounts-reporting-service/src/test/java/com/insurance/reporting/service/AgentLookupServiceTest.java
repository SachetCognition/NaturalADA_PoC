package com.insurance.reporting.service;

import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.repository.AgentControlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentLookupServiceTest {

    @Mock
    private AgentControlRepository repository;

    private AgentLookupService service;

    @BeforeEach
    void setUp() {
        service = new AgentLookupService(repository);
    }

    @Test
    void shouldConstructKeyAndLookup() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        agent.setInspectNo((short) 5);
        agent.setName("Test Agent");

        // Key should be "10" + "001" + "3" + "ABC123" = "100013ABC123"
        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.of(agent));

        Optional<AgentControl> result = service.lookupAgent("001", "ABC123");
        assertTrue(result.isPresent());
        assertEquals("A", result.get().getStatus());
    }

    @Test
    void shouldReturnTrueForActiveAgent() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.of(agent));

        assertTrue(service.isActiveAgent("001", "ABC123"));
    }

    @Test
    void shouldReturnFalseForInactiveAgent() {
        AgentControl agent = new AgentControl();
        agent.setStatus("X");
        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.of(agent));

        assertFalse(service.isActiveAgent("001", "ABC123"));
    }

    @Test
    void shouldReturnFalseForMissingAgent() {
        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.empty());

        assertFalse(service.isActiveAgent("001", "ABC123"));
    }
}
