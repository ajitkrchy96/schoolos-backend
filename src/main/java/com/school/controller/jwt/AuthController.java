package com.school.controller.jwt;

import com.school.dto.jwt.AuthResponse;
import com.school.service.jwt.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> request) {
        AuthResponse authResponse = authService.login(
                request.get("username"),
                request.get("password")
        );

        log.info("Login successful for username='{}', role='{}', schoolId={}",
                authResponse.getUser().getUsername(),
                authResponse.getUser().getRole(),
                authResponse.getUser().getSchoolId()
        );

        return ResponseEntity.ok(authResponse);
    }
    @GetMapping("/password/{password}")
    public ResponseEntity<String> getPassword(@PathVariable String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return ResponseEntity.ok(encodedPassword);
    }
}