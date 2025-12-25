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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password"); // Mock check if needed

        String role = "USER";
        Long userId = 1L;
        String name = "Hong GilDong";
        String email = "hong@kz.com";

        if ("admin".equals(username)) {
            role = "ADMIN";
            userId = 0L;
            name = "System Admin";
            email = "admin@kz.com";
        }

        String token = jwtTokenProvider.createToken(username, role);
        String refreshToken = jwtTokenProvider.createToken(username, role);

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "refreshToken", refreshToken,
                "expiresIn", 3600,
                "user", Map.of(
                        "userId", userId,
                        "username", username,
                        "name", name,
                        "email", email,
                        "role", role)));
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
