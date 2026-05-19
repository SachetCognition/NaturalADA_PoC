package com.insurance.agent.service;

import com.insurance.agent.domain.AgentControl;
import com.insurance.agent.repository.AgentControlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentControlRepository repository;

    private AgentService service;

    @BeforeEach
    void setUp() {
        service = new AgentService(repository);
    }

    @Test
    void shouldFindAgentByKey() {
        AgentControl agent = new AgentControl();
        agent.setMainAgtKey("100013ABC123");
        agent.setStatus("A");
        agent.setInspectNo((short) 5);
        agent.setName("Test Agent");

        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.of(agent));

        Optional<AgentControl> result = service.findByKey("100013ABC123");
        assertTrue(result.isPresent());
        assertEquals("A", result.get().getStatus());
        assertEquals("Test Agent", result.get().getName());
    }

    @Test
    void shouldReturnEmptyForMissingAgent() {
        when(repository.findByMainAgtKey("100013XXXXXX")).thenReturn(Optional.empty());

        Optional<AgentControl> result = service.findByKey("100013XXXXXX");
        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnTrueForActiveAgent() {
        AgentControl agent = new AgentControl();
        agent.setStatus("A");
        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.of(agent));

        assertTrue(service.isActive("100013ABC123"));
    }

    @Test
    void shouldReturnFalseForInactiveAgent() {
        AgentControl agent = new AgentControl();
        agent.setStatus("X");
        when(repository.findByMainAgtKey("100013ABC123")).thenReturn(Optional.of(agent));

        assertFalse(service.isActive("100013ABC123"));
    }

    @Test
    void shouldReturnFalseForNonExistentAgent() {
        when(repository.findByMainAgtKey("100013XXXXXX")).thenReturn(Optional.empty());

        assertFalse(service.isActive("100013XXXXXX"));
    }
}
