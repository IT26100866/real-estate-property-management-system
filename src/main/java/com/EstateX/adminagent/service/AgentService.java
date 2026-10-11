package com.EstateX.adminagent.service;

import com.EstateX.adminagent.dto.AgentRequest;
import com.EstateX.adminagent.entity.AccountStatus;
import com.EstateX.adminagent.entity.Agent;
import com.EstateX.adminagent.exception.DuplicateResourceException;
import com.EstateX.adminagent.exception.ResourceNotFoundException;
import com.EstateX.adminagent.repository.AgentRepository;
import com.EstateX.adminagent.repository.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Component 3 - Agent Management (business rules live here). */
@Service
public class AgentService {

    private final AgentRepository agentRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public AgentService(AgentRepository agentRepository,
                        PersonRepository personRepository,
                        PasswordEncoder passwordEncoder) {
        this.agentRepository = agentRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- CREATE ----------
    @Transactional
    public Agent create(AgentRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (personRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if (personRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username is already in use");
        }

        Agent agent = new Agent(
                request.username(),
                request.fullName(),
                request.email(),
                request.phone(),
                request.address(),
                passwordEncoder.encode(request.password()),
                request.status());

        return agentRepository.save(agent);
    }

    // ---------- READ ----------
    public List<Agent> getAll() {
        return agentRepository.findAll();
    }

    public Agent getById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id " + id));
    }

    // ---------- UPDATE ----------
    @Transactional
    public Agent update(Long id, AgentRequest request) {
        Agent agent = getById(id);

        if (personRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if (personRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("Username is already in use");
        }

        agent.setUsername(request.username());
        agent.setFullName(request.fullName());
        agent.setEmail(request.email());
        agent.setPhone(request.phone());
        agent.setAddress(request.address());
        if (request.status() != null) {
            agent.setStatus(request.status());
        }
        if (request.password() != null && !request.password().isBlank()) {
            agent.setPassword(passwordEncoder.encode(request.password()));
        }
        return agentRepository.save(agent);
    }

    @Transactional
    public Agent updateStatus(Long id, AccountStatus status) {
        Agent agent = getById(id);
        agent.setStatus(status);
        return agentRepository.save(agent);
    }

    /** Soft delete: the agent stays in the database, but cannot be used. */
    @Transactional
    public Agent deactivate(Long id) {
        return updateStatus(id, AccountStatus.INACTIVE);
    }

    // ---------- DELETE ----------
    // NOTE: properties.agent_id has ON DELETE CASCADE, so deleting an agent also
    // deletes that agent's properties. Prefer deactivate() in the UI.
    @Transactional
    public void delete(Long id) {
        agentRepository.delete(getById(id));
    }

    /*
     * "View properties managed by an agent": call the property listing member's
     * service with properties.agent_id = this agent's id.
     */
}
