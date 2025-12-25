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
        // Mock Login: Always success for now or simple check
        String code = request.get("code");

        // Mock User
        String token = jwtTokenProvider.createToken("user1", "USER");
        String refreshToken = jwtTokenProvider.createToken("user1", "USER"); // Simplified refresh token

        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "refreshToken", refreshToken,
                "expiresIn", 3600,
                "user", Map.of(
                        "userId", 1,
                        "username", "user1",
                        "name", "Hong GilDong",
                        "email", "hong@kz.com",
                        "role", "USER")));
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
