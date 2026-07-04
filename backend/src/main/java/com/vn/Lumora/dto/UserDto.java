package com.vn.Lumora.dto;

import java.time.LocalDateTime;

public class UserDto {
    public record UserDetailResponse(Long id, String name, String email, String role, LocalDateTime createdAt) {}
    public record CreateUserRequest(String name, String email, String password) {}
    public record UpdateUserRequest(String name, String email, String password) {}
    public record MessageResponse(String message) {}
}
