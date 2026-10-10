package com.estatex.controller;

import com.estatex.dto.ApiResponse;
import com.estatex.dto.UpdateProfileRequest;
import com.estatex.dto.UserDto;
import com.estatex.dto.ChangePasswordRequest;
import com.estatex.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User Management operations (viewing profile, listing all users).
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * List all users in system (used by Admin Dashboard User Management table).
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieve authenticated user profile.
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update authenticated user profile.
     * PUT /api/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            UserDto updatedUser = userService.updateProfile(username, request);
            return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully.", updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete user account by ID.
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User account deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Change authenticated user password.
     * PUT /api/users/me/password
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse> changeMyPassword(@Valid @RequestBody ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            userService.changePassword(username, request);
            return ResponseEntity.ok(ApiResponse.success("Password updated successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
