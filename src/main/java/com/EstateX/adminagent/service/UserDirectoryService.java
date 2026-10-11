package com.EstateX.adminagent.service;

import com.EstateX.adminagent.dto.DashboardStats;
import com.EstateX.adminagent.dto.PersonResponse;
import com.EstateX.adminagent.entity.Person;
import com.EstateX.adminagent.repository.AdminRepository;
import com.EstateX.adminagent.repository.AgentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Feeds the Users page (admins + agents together) and the Dashboard cards. */
@Service
public class UserDirectoryService {

    private final AdminRepository adminRepository;
    private final AgentRepository agentRepository;

    public UserDirectoryService(AdminRepository adminRepository,
                                AgentRepository agentRepository) {
        this.adminRepository = adminRepository;
        this.agentRepository = agentRepository;
    }

    /** type = "admin", "agent" or null (everyone). */
    public List<PersonResponse> getAll(String type) {
        List<Person> people = new ArrayList<>();
        people.addAll(adminRepository.findAll());
        people.addAll(agentRepository.findAll());

        return people.stream()
                .filter(p -> type == null || type.isBlank() || p.getType().equalsIgnoreCase(type))
                .sorted(Comparator.comparing(Person::getId))
                .map(PersonResponse::from)
                .toList();
    }

    public DashboardStats stats() {
        long admins = adminRepository.count();
        long agents = agentRepository.count();
        return new DashboardStats(admins + agents, admins, agents);
    }
}
