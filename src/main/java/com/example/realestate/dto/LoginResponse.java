package com.example.realestate.dto;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;
    private boolean success = true;
    private String message;

    public LoginResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
        this.success = true;
    }

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Added getRole method to resolve the error
    public String getRole() {
        return user != null ? user.getRole() : null;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}