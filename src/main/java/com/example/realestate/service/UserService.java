package com.example.realestate.service;

import com.example.realestate.dto.LoginRequest;
import com.example.realestate.dto.LoginResponse;
import com.example.realestate.dto.RegisterRequest;
import com.example.realestate.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public LoginResponse authenticate(LoginRequest request) {
        UserDto user = new UserDto(1L, "Test User", request.getEmail(), "USER");
        return new LoginResponse("mock-jwt-token", user);
    }

    public UserDto register(RegisterRequest request) {
        return new UserDto(1L, request.getName(), request.getEmail(), request.getRole());
    }

    public boolean isUsernameAvailable(String email) {
        return true;
    }
}