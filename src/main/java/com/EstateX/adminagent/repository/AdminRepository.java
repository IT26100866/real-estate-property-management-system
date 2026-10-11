package com.EstateX.adminagent.repository;

import com.EstateX.adminagent.entity.Admin;
import com.EstateX.adminagent.entity.AdminRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    long countByAdminRole(AdminRole adminRole);
}
