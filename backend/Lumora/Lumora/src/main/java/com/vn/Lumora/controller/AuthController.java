package com.vn.Lumora.controller;

import com.vn.Lumora.dto.AuthDto.*;
import com.vn.Lumora.model.User;
import com.vn.Lumora.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (request.email() == null || request.email().trim().isEmpty() ||
            request.password() == null || request.password().length() < 6 ||
            request.name() == null || request.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new AuthResponse("Thông tin đăng ký không hợp lệ!", false, null));
        }

        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            return ResponseEntity.ok(new AuthResponse("Email này đã được đăng ký sử dụng!", false, null));
        }

        // Hash password
        String hashedPassword = hashPassword(request.password());

        // Create user
        User user = new User(
            request.name().trim(),
            request.email().trim().toLowerCase(),
            hashedPassword
        );

        User savedUser = userRepository.save(user);

        UserResponse userResponse = new UserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        );

        return ResponseEntity.ok(new AuthResponse("Đăng ký tài khoản thành công!", true, userResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Email hoặc mật khẩu không đúng định dạng!", false, null));
        }

        Optional<User> userOpt = userRepository.findByEmail(request.email().trim().toLowerCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(new AuthResponse("Email hoặc mật khẩu không chính xác!", false, null));
        }

        User user = userOpt.get();
        String inputHashed = hashPassword(request.password());

        if (!user.getPassword().equals(inputHashed)) {
            return ResponseEntity.ok(new AuthResponse("Email hoặc mật khẩu không chính xác!", false, null));
        }

        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail()
        );

        return ResponseEntity.ok(new AuthResponse("Đăng nhập tài khoản thành công!", true, userResponse));
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
