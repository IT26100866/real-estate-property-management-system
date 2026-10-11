package com.EstateX.adminagent.controller;

import com.EstateX.adminagent.dto.AgentRequest;
import com.EstateX.adminagent.dto.PersonResponse;
import com.EstateX.adminagent.dto.StatusRequest;
import com.EstateX.adminagent.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Component 3 - Agent Management REST API. */
@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping                                  // Create (register agent)
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@Valid @RequestBody AgentRequest request) {
        return PersonResponse.from(agentService.create(request));
    }

    @GetMapping                                   // Read all
    public List<PersonResponse> getAll() {
        return agentService.getAll().stream().map(PersonResponse::from).toList();
    }

    @GetMapping("/{id}")                          // Read one
    public PersonResponse getOne(@PathVariable Long id) {
        return PersonResponse.from(agentService.getById(id));
    }

    @PutMapping("/{id}")                          // Update
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody AgentRequest request) {
        return PersonResponse.from(agentService.update(id, request));
    }

    @PatchMapping("/{id}/status")                 // Change status
    public PersonResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return PersonResponse.from(agentService.updateStatus(id, request.status()));
    }

    @PatchMapping("/{id}/deactivate")             // Soft delete
    public PersonResponse deactivate(@PathVariable Long id) {
        return PersonResponse.from(agentService.deactivate(id));
    }

    @DeleteMapping("/{id}")                       // Delete
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
