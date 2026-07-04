package com.vn.Lumora.controller;

import com.vn.Lumora.dto.UserDto.*;
import com.vn.Lumora.model.User;
import com.vn.Lumora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserDetailResponse>> getAllUsers() {
        List<UserDetailResponse> users = userRepository.findAll().stream()
                .map(user -> new UserDetailResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        if (request.name() == null || request.name().trim().isEmpty() ||
            request.email() == null || request.email().trim().isEmpty() ||
            request.password() == null || request.password().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tên, email và mật khẩu không được để trống!"));
        }

        String emailNormalized = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(emailNormalized)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email đã tồn tại trên hệ thống!"));
        }

        String hashedPassword = hashPassword(request.password());
        User newUser = new User(request.name().trim(), emailNormalized, hashedPassword);
        User savedUser = userRepository.save(newUser);

        return ResponseEntity.ok(new UserDetailResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (request.name() == null || request.name().trim().isEmpty() ||
            request.email() == null || request.email().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Tên và email không được để trống!"));
        }

        User user = userOpt.get();
        String emailNormalized = request.email().trim().toLowerCase();

        // Check if email belongs to another user
        if (!user.getEmail().equals(emailNormalized) && userRepository.existsByEmail(emailNormalized)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email đã tồn tại trên hệ thống!"));
        }

        user.setName(request.name().trim());
        user.setEmail(emailNormalized);

        if (request.password() != null && !request.password().trim().isEmpty()) {
            user.setPassword(hashPassword(request.password()));
        }

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserDetailResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getRole(),
                updatedUser.getCreatedAt()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestParam(required = false) Long loggedInUserId) {
        if (loggedInUserId != null && loggedInUserId.equals(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Không thể xóa chính tài khoản đang đăng nhập!"));
        }

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Xóa người dùng thành công!"));
    }

    // SHA-256 Hashing helper
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
