package com.school.service.jwt;

import com.school.dto.jwt.AuthResponse;

public interface AuthService {
    AuthResponse login(String username, String password);
}