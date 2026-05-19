package com.insurance.reporting.service;

import com.insurance.common.util.AgentKeyBuilder;
import com.insurance.reporting.domain.AgentControl;
import com.insurance.reporting.repository.AgentControlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgentLookupService {

    private final AgentControlRepository repository;

    public AgentLookupService(AgentControlRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "agents", key = "#branch + '-' + #agent")
    public Optional<AgentControl> lookupAgent(String branch, String agent) {
        String key = AgentKeyBuilder.build(branch, agent);
        return repository.findByMainAgtKey(key);
    }

    public boolean isActiveAgent(String branch, String agent) {
        return lookupAgent(branch, agent)
                .map(ac -> "A".equals(ac.getStatus()))
                .orElse(false);
    }
}
