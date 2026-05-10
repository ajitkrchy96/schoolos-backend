package com.school.service.jwt;

import com.school.dto.jwt.AuthResponse;
import com.school.dto.jwt.AuthUserResponse;
import com.school.model.AppUser;
import com.school.repository.AppUserRepository;
import com.school.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(String username, String password) {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        /*if (!password.equals("password")) {
            throw new RuntimeException("Invalid password");
        }*/
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }


        AuthUserResponse authUser = AuthUserResponse.fromAppUser(user);
        return new AuthResponse(jwtUtil.generateToken(username), authUser);
    }
}