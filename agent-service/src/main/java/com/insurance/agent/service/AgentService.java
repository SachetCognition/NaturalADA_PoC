package com.insurance.agent.service;

import com.insurance.agent.domain.AgentControl;
import com.insurance.agent.repository.AgentControlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgentService {

    private final AgentControlRepository repository;

    public AgentService(AgentControlRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "agents", key = "#mainAgtKey")
    public Optional<AgentControl> findByKey(String mainAgtKey) {
        return repository.findByMainAgtKey(mainAgtKey);
    }

    public boolean isActive(String mainAgtKey) {
        return findByKey(mainAgtKey)
                .map(agent -> "A".equals(agent.getStatus()))
                .orElse(false);
    }
}
