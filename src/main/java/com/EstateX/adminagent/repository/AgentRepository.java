package com.EstateX.adminagent.repository;

import com.EstateX.adminagent.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
}
