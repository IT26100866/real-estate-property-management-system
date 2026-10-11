package com.EstateX.adminagent.service;

import com.EstateX.adminagent.dto.AdminRequest;
import com.EstateX.adminagent.entity.AccountStatus;
import com.EstateX.adminagent.entity.Admin;
import com.EstateX.adminagent.entity.AdminRole;
import com.EstateX.adminagent.exception.DuplicateResourceException;
import com.EstateX.adminagent.exception.ResourceNotFoundException;
import com.EstateX.adminagent.repository.AdminRepository;
import com.EstateX.adminagent.repository.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Component 5 - Admin Management (business rules live here). */
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        PersonRepository personRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- CREATE ----------
    @Transactional
    public Admin create(AdminRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (personRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if (personRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username is already in use");
        }

        Admin admin = new Admin(
                request.username(),
                request.fullName(),
                request.email(),
                request.phone(),
                request.address(),
                passwordEncoder.encode(request.password()),
                request.status(),
                request.role());

        return adminRepository.save(admin);
    }

    // ---------- READ ----------
    public List<Admin> getAll() {
        return adminRepository.findAll();
    }

    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id " + id));
    }

    // ---------- UPDATE ----------
    @Transactional
    public Admin update(Long id, AdminRequest request) {
        Admin admin = getById(id);

        if (personRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if (personRepository.existsByUsernameAndIdNot(request.username(), id)) {
            throw new DuplicateResourceException("Username is already in use");
        }
        if (admin.getAdminRole() == AdminRole.SUPER_ADMIN && request.role() != AdminRole.SUPER_ADMIN) {
            ensureNotLastSuperAdmin();
        }

        admin.setUsername(request.username());
        admin.setFullName(request.fullName());
        admin.setEmail(request.email());
        admin.setPhone(request.phone());
        admin.setAddress(request.address());
        admin.setAdminRole(request.role());
        if (request.status() != null) {
            admin.setStatus(request.status());
        }
        if (request.password() != null && !request.password().isBlank()) {
            admin.setPassword(passwordEncoder.encode(request.password()));
        }
        return adminRepository.save(admin);
    }

    /** Assign an admin role / permission. */
    @Transactional
    public Admin assignRole(Long id, AdminRole newRole) {
        Admin admin = getById(id);

        if (admin.getAdminRole() == AdminRole.SUPER_ADMIN && newRole != AdminRole.SUPER_ADMIN) {
            ensureNotLastSuperAdmin();
        }
        admin.setAdminRole(newRole);
        return adminRepository.save(admin);
    }

    @Transactional
    public Admin updateStatus(Long id, AccountStatus status) {
        Admin admin = getById(id);
        admin.setStatus(status);
        return adminRepository.save(admin);
    }

    // ---------- DELETE ----------
    @Transactional
    public void delete(Long id) {
        Admin admin = getById(id);

        if (admin.getAdminRole() == AdminRole.SUPER_ADMIN) {
            ensureNotLastSuperAdmin();
        }
        adminRepository.delete(admin);
    }

    // ---------- business rule ----------
    private void ensureNotLastSuperAdmin() {
        if (adminRepository.countByAdminRole(AdminRole.SUPER_ADMIN) <= 1) {
            throw new IllegalStateException("The last Super Admin cannot be removed or demoted");
        }
    }

    /*
     * Admin also needs to:
     *  - view / update user accounts       -> call UserService      (other member)
     *  - view / update / remove listings   -> call PropertyListingService (other member)
     * Inject those services here once they exist.
     */
}
