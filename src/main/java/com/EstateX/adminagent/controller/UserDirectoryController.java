package com.EstateX.adminagent.controller;

import com.EstateX.adminagent.dto.DashboardStats;
import com.EstateX.adminagent.dto.PersonResponse;
import com.EstateX.adminagent.service.UserDirectoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Data for the Users page and the Dashboard cards. */
@RestController
@RequestMapping("/api")
public class UserDirectoryController {

    private final UserDirectoryService directoryService;

    public UserDirectoryController(UserDirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    // GET /api/users            -> admins + agents
    // GET /api/users?type=agent -> agents only
    @GetMapping("/users")
    public List<PersonResponse> users(@RequestParam(required = false) String type) {
        return directoryService.getAll(type);
    }

    @GetMapping("/dashboard/stats")
    public DashboardStats stats() {
        return directoryService.stats();
    }
}
