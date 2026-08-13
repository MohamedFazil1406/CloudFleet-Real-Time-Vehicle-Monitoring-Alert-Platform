package com.fazil.production_backend.service;

import com.fazil.production_backend.dto.LoginRequest;
import com.fazil.production_backend.dto.LoginResponse;
import com.fazil.production_backend.dto.RegisterRequest;
import com.fazil.production_backend.entity.User;
import com.fazil.production_backend.enums.UserRole;
import com.fazil.production_backend.repository.UserRepository;
import com.fazil.production_backend.config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /* ========================================================= */
    /* REGISTER                                                   */
    /* ========================================================= */

    @Transactional
    public LoginResponse register(
            RegisterRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (userRepository.existsByEmail(email)) {

            throw new IllegalArgumentException(
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(UserRole.OPERATOR)
                .active(true)
                .build();

        User savedUser =
                userRepository.save(user);

        String token =
                jwtService.generateToken(
                        savedUser.getEmail()
                );

        return buildLoginResponse(
                savedUser,
                token
        );
    }

    /* ========================================================= */
    /* LOGIN                                                      */
    /* ========================================================= */

    public LoginResponse login(
            LoginRequest request
    ) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid email or password"
                                )
                        );

        if (!Boolean.TRUE.equals(
                user.getActive()
        )) {

            throw new IllegalArgumentException(
                    "User account is inactive"
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return buildLoginResponse(
                user,
                token
        );
    }

    /* ========================================================= */
    /* RESPONSE                                                   */
    /* ========================================================= */

    private LoginResponse buildLoginResponse(
            User user,
            String token
    ) {

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}