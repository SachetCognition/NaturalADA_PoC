package com.insurance.reporting.repository;

import com.insurance.reporting.domain.AgentControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentControlRepository extends JpaRepository<AgentControl, Long> {

    Optional<AgentControl> findByMainAgtKey(String mainAgtKey);
}
