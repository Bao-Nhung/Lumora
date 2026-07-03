package com.vn.Lumora.dto;

public class AuthDto {
    public record RegisterRequest(String name, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record UserResponse(Long id, String name, String email) {}
    public record AuthResponse(String message, boolean success, UserResponse user) {}
}
