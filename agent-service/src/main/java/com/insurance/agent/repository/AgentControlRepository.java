package com.insurance.agent.repository;

import com.insurance.agent.domain.AgentControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentControlRepository extends JpaRepository<AgentControl, Long> {

    Optional<AgentControl> findByMainAgtKey(String mainAgtKey);
}
