package com.insurance.agent.controller;

import com.insurance.agent.domain.AgentControl;
import com.insurance.agent.repository.AgentControlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentControlRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        AgentControl agent = new AgentControl();
        agent.setMainAgtKey("100013ABC123");
        agent.setStatus("A");
        agent.setInspectNo((short) 5);
        agent.setName("Test Agent");
        repository.save(agent);
    }

    @Test
    void shouldReturnAgentByKey() throws Exception {
        mockMvc.perform(get("/api/agents/100013ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mainAgtKey").value("100013ABC123"))
                .andExpect(jsonPath("$.status").value("A"))
                .andExpect(jsonPath("$.name").value("Test Agent"));
    }

    @Test
    void shouldReturn404ForMissingAgent() throws Exception {
        mockMvc.perform(get("/api/agents/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnActiveStatus() throws Exception {
        mockMvc.perform(get("/api/agents/100013ABC123/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturnInactiveForMissing() throws Exception {
        mockMvc.perform(get("/api/agents/NONEXISTENT/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
