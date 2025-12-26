package com.kz.magazine.controller;

import com.kz.magazine.security.JwtTokenProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final com.kz.magazine.repository.UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be empty");
        }

        // String password = request.get("password"); // Mock check if needed

        String role = "USER";
        String name = "Hong GilDong";
        String email = "hong@kz.com";
        String department = "HR";

        if ("admin".equals(username)) {
            role = "ADMIN";
            name = "System Admin";
            email = "admin@kz.com";
            department = "IT";
        }

        String finalRole = role;
        String finalName = name;
        String finalEmail = email;
        String finalDepartment = department;

        // Sync to DB
        com.kz.magazine.entity.User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(com.kz.magazine.entity.User.builder()
                        .username(username)
                        .name(finalName)
                        .email(finalEmail)
                        .role(finalRole)
                        .department(finalDepartment)
                        .isActive(true)
                        .build()));

        String token = jwtTokenProvider.createToken(username, role);
        String refreshToken = jwtTokenProvider.createToken(username, role);

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "refreshToken", refreshToken,
                "expiresIn", 3600,
                "user", Map.of(
                        "userId", user.getUserId(),
                        "username", user.getUsername(),
                        "name", user.getName(),
                        "email", email, // potentially null in DB if not set, but we set it above
                        "role", user.getRole())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsername(refreshToken);
            String newToken = jwtTokenProvider.createToken(username, "USER");
            return ResponseEntity.ok(Map.of("accessToken", newToken));
        }
        return ResponseEntity.status(401).body("Invalid Refresh Token");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(Map.of(
                "userId", 1,
                "username", "user1",
                "name", "Hong GilDong",
                "email", "hong@kz.com",
                "role", "USER"));
    }
}
