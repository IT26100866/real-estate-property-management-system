package com.example.realestate.security;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final String jwtSecret = "mySecretKeyForRealEstateAppJwtTokenGenerationAndValidationProcess";
    private final long jwtExpirationMs = 86400000; // 1 day

    // Updated to accept both email and role to match AuthController
    public String generateToken(String email, String role) {
        return "mock-jwt-token-for-" + email + "-with-role-" + role;
    }

    public String getEmailFromToken(String token) {
        return "user@example.com";
    }

    public boolean validateToken(String authToken) {
        return true;
    }
}