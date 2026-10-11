package com.EstateX.adminagent.dto;

import com.EstateX.adminagent.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "Status is required") AccountStatus status
) { }
