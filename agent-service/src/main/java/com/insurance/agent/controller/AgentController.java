package com.insurance.agent.controller;

import com.insurance.agent.domain.AgentControl;
import com.insurance.agent.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/{mainAgtKey}")
    public ResponseEntity<AgentControl> getAgent(@PathVariable String mainAgtKey) {
        return agentService.findByKey(mainAgtKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{mainAgtKey}/active")
    public ResponseEntity<Map<String, Boolean>> isActive(@PathVariable String mainAgtKey) {
        boolean active = agentService.isActive(mainAgtKey);
        return ResponseEntity.ok(Map.of("active", active));
    }
}
