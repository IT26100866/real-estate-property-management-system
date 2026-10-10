package com.EstateX.adminagent.dto;

import com.EstateX.adminagent.entity.AccountStatus;
import jakarta.validation.constraints.*;

/** Data coming from the Register / Update Agent form. */
public record AgentRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be 3 to 50 characters")
        String username,

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must be under 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "0\\d{9}", message = "Phone must be 10 digits starting with 0")
        String phone,

        @Size(max = 500, message = "Address is too long")
        String address,

        // required when creating, optional when updating (checked in the service)
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        AccountStatus status
) { }
