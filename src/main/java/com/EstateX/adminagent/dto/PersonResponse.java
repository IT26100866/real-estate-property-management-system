package com.EstateX.adminagent.dto;

import com.EstateX.adminagent.entity.AccountStatus;
import com.EstateX.adminagent.entity.Admin;
import com.EstateX.adminagent.entity.Person;

/** Data we send back to the browser. The password is NEVER included. */
public record PersonResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String address,
        String type,
        AccountStatus status,
        String role
) {
    public static PersonResponse from(Person p) {
        String role = null;

        if (p instanceof Admin admin && admin.getAdminRole() != null) {
            role = admin.getAdminRole().name();
        }

        return new PersonResponse(p.getId(), p.getUsername(), p.getFullName(), p.getEmail(),
                p.getPhone(), p.getAddress(), p.getType(), p.getStatus(), role);
    }
}
