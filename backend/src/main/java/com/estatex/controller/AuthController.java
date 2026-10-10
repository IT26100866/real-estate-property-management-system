package com.estatex.controller;

import com.estatex.dto.ApiResponse;
import com.estatex.dto.LoginRequest;
import com.estatex.dto.LoginResponse;
import com.estatex.dto.RegisterRequest;
import com.estatex.dto.UserDto;
import com.estatex.model.Role;
import com.estatex.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.estatex.security.JwtUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for User Authentication & Registration.
 * Handles HTTP requests from the sign-in and create account pages.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Sign-in endpoint: Authenticates username & password against the database.
     * Identifies whether user is a standard user, agent, or admin and returns appropriate redirect URL.
     *
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.authenticate(request);

        if (response.isSuccess()) {
            String token = jwtUtil.generateToken(response.getUser().getUsername(), response.getRole());
            response.setToken(token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Create Account / Registration endpoint.
     * Stores new user in the database.
     *
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDto createdUser = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse(true, "Account created successfully for " + createdUser.getUsername(), createdUser)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error("Failed to complete registration: " + e.getMessage())
            );
        }
    }

    /**
     * Check if a username is available.
     *
     * GET /api/auth/check-username?username=dilshan
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of(
                "username", username,
                "available", available
        ));
    }

    /**
     * List user roles available in the system.
     *
     * GET /api/auth/roles
     */
    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, String>>> getRoles() {
        List<Map<String, String>> roles = Arrays.stream(Role.values())
                .map(r -> Map.of(
                        "name", r.name(),
                        "displayName", r.getDisplayName(),
                        "dashboardUrl", r.getDefaultDashboardUrl()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
