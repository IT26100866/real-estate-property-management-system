package com.EstateX.adminagent.controller;

import com.EstateX.adminagent.dto.AdminRequest;
import com.EstateX.adminagent.dto.PersonResponse;
import com.EstateX.adminagent.dto.RoleRequest;
import com.EstateX.adminagent.dto.StatusRequest;
import com.EstateX.adminagent.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Component 5 - Admin Management REST API. */
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping                                  // Create
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@Valid @RequestBody AdminRequest request) {
        return PersonResponse.from(adminService.create(request));
    }

    @GetMapping                                   // Read all
    public List<PersonResponse> getAll() {
        return adminService.getAll().stream().map(PersonResponse::from).toList();
    }

    @GetMapping("/{id}")                          // Read one
    public PersonResponse getOne(@PathVariable Long id) {
        return PersonResponse.from(adminService.getById(id));
    }

    @PutMapping("/{id}")                          // Update
    public PersonResponse update(@PathVariable Long id, @Valid @RequestBody AdminRequest request) {
        return PersonResponse.from(adminService.update(id, request));
    }

    @PatchMapping("/{id}/role")                   // Assign role
    public PersonResponse assignRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return PersonResponse.from(adminService.assignRole(id, request.role()));
    }

    @PatchMapping("/{id}/status")                 // Change status
    public PersonResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return PersonResponse.from(adminService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")                       // Delete
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
