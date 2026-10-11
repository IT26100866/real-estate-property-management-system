package com.EstateX.adminagent.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Person {

    // new column: SUPER_ADMIN / MODERATOR / SUPPORT
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_role", length = 20)
    private AdminRole adminRole;

    protected Admin() { }

    public Admin(String username, String fullName, String email, String phone, String address,
                 String password, AccountStatus status, AdminRole adminRole) {
        super(username, fullName, email, phone, address, password, status);
        this.adminRole = adminRole;
    }

    @Override
    public String getType() { return "Admin"; }

    public AdminRole getAdminRole() { return adminRole; }
    public void setAdminRole(AdminRole adminRole) { this.adminRole = adminRole; }
}
