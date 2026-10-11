package com.EstateX.adminagent.dto;

import com.EstateX.adminagent.entity.AdminRole;
import jakarta.validation.constraints.NotNull;

public record RoleRequest(
        @NotNull(message = "Role is required") AdminRole role
) { }
