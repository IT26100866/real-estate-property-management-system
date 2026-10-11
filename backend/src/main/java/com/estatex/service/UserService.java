package com.estatex.service;

import com.estatex.dto.LoginRequest;
import com.estatex.dto.LoginResponse;
import com.estatex.dto.RegisterRequest;
import com.estatex.dto.UpdateProfileRequest;
import com.estatex.dto.UserDto;
import com.estatex.dto.ChangePasswordRequest;
import com.estatex.model.Role;
import com.estatex.model.User;
import com.estatex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Layer implementing User Management and Authentication business logic.
 * Encapsulates role identification and credentials validation.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user given username (or email) and password.
     * Identifies the user's role (Customer, Agent, Admin) based on stored role and username format (name@user, name@agent, name@admin),
     * and resolves their destination portal.
     *
     * @param request LoginRequest containing username and password
     * @return LoginResponse with authentication status, user profile, and destination redirect URL
     */
    public LoginResponse authenticate(LoginRequest request) {
        String identifier = request.getUsername().trim();
        String password = request.getPassword();

        // Query database by username or email
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identifier, identifier);

        if (userOpt.isEmpty()) {
            return LoginResponse.failure("No user account found matching username: " + identifier);
        }

        User user = userOpt.get();

        // Check password match and seamlessly upgrade legacy SHA-256 hashes
        if (user.getPassword() != null && user.getPassword().startsWith("$2a$")) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return LoginResponse.failure("Invalid password. Please check your credentials.");
            }
        } else if (isLegacySha256Hash(user.getPassword())) {
            if (user.getPassword().equals(legacyHashPassword(password))) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            } else {
                return LoginResponse.failure("Invalid password. Please check your credentials.");
            }
        } else {
            return LoginResponse.failure("Invalid password format. Please contact support.");
        }

        // Identify role from user record and username format
        Role identifiedRole = user.getRole();
        if (identifiedRole == null) {
            identifiedRole = User.determineRoleFromUsername(user.getUsername());
            user.setRole(identifiedRole);
        }

        String roleName = identifiedRole.name();
        String redirectUrl = user.getDashboardRedirectUrl();

        return new LoginResponse(
                true,
                "Authentication successful. Navigating to " + roleName + " portal.",
                user
        );
    }

    /**
     * Registers a new user account into the SQL database.
     * Automatically formats username with the appropriate suffix (name@user, name@agent, name@admin).
     * Enforces unique password and unique username constraints.
     *
     * @param request RegisterRequest with profile and credential details
     * @return UserDto representation of the created user
     */
    public UserDto register(RegisterRequest request) {
        String rawUsername = request.getUsername().trim();
        String password = request.getPassword();
        String email = request.getEmail().trim();
        // Strip any existing suffix and force @user to prevent privilege escalation
        String baseUsername = rawUsername;
        if (baseUsername.contains("@")) {
            baseUsername = baseUsername.substring(0, baseUsername.indexOf('@')).trim();
        }
        String formattedUsername = baseUsername + "@user";
        Role resolvedRole = Role.CUSTOMER;

        // Enforce unique username
        if (userRepository.existsByUsernameIgnoreCase(formattedUsername)) {
            throw new IllegalArgumentException("Username '" + formattedUsername + "' is already in use.");
        }

        // Enforce unique email
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email address '" + email + "' is already registered.");
        }

        User newUser = new User(
                formattedUsername,
                passwordEncoder.encode(password),
                email,
                request.getFullName().trim(),
                request.getPhone() != null ? request.getPhone().trim() : null,
                resolvedRole
        );

        User savedUser = userRepository.save(newUser);
        return new UserDto(savedUser);
    }

    /**
     * Formats username to ensure it has the appropriate role suffix:
     * - Standard user / customer: name@user
     * - Real estate agent: name@agent
     * - Administrator: name@admin
     */
    public static String formatUsernameWithRoleSuffix(String username, Role role) {
        if (username == null || username.trim().isEmpty()) {
            return username;
        }
        String clean = username.trim();
        String lower = clean.toLowerCase();

        // If it already has an explicit suffix, keep it
        if (lower.endsWith("@admin") || lower.endsWith("@agent") || lower.endsWith("@user")) {
            return clean;
        }

        // If user typed an email-like @domain, strip that part to use as base username
        if (clean.contains("@")) {
            clean = clean.substring(0, clean.indexOf('@')).trim();
        }

        switch (role) {
            case ADMIN:
                return clean + "@admin";
            case AGENT:
                return clean + "@agent";
            case CUSTOMER:
            default:
                return clean + "@user";
        }
    }


    /**
     * Legacy SHA-256 hasher for seamless migration of old users.
     */
    private String legacyHashPassword(String rawPassword) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password securely", e);
        }
    }

    private boolean isLegacySha256Hash(String hash) {
        return hash != null && hash.length() == 44 && hash.endsWith("=");
    }

    /**
     * Retrieve user profile by username.
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .map(UserDto::new);
    }

    /**
     * Updates user profile details.
     */
    public UserDto updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));

        // Ensure email isn't taken by someone else
        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email address '" + request.getEmail() + "' is already registered by another user.");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        return new UserDto(userRepository.save(user));
    }

    /**
     * Check if a username is available.
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    /**
     * List all registered users (useful for Admin user management views).
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Delete user by ID (Customer / Admin account management).
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with ID " + id + " does not exist.");
        }
        userRepository.deleteById(id);
    }

    /**
     * Change user password.
     */
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        // Verify current password
        if (user.getPassword() != null && user.getPassword().startsWith("$2a$")) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }
        } else if (isLegacySha256Hash(user.getPassword())) {
            if (!user.getPassword().equals(legacyHashPassword(request.getCurrentPassword()))) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }
        } else {
            throw new IllegalArgumentException("Current password format is invalid.");
        }

        // Update with new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
