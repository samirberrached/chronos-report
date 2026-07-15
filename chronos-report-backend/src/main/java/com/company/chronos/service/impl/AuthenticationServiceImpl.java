package com.company.chronos.service.impl;

import com.company.chronos.dto.auth.AuthRequest;
import com.company.chronos.dto.auth.AuthResponse;
import com.company.chronos.dto.auth.RefreshRequest;
import com.company.chronos.dto.auth.RegisterRequest;
import com.company.chronos.entity.User;
import com.company.chronos.entity.User.Role;
import com.company.chronos.exception.BusinessRuleViolationException;
import com.company.chronos.exception.DuplicateResourceException;
import com.company.chronos.repository.UserRepository;
import com.company.chronos.security.JwtTokenProvider;
import com.company.chronos.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AuthenticationService}. Handles login,
 * registration and refresh using the JWT provider and BCrypt encoder.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    /** User repository. */
    private final UserRepository userRepository;

    /** Spring Security authentication manager. */
    private final AuthenticationManager authenticationManager;

    /** Password encoder (BCrypt). */
    private final PasswordEncoder passwordEncoder;

    /** JWT token provider. */
    private final JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildResponse(userDetails);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.valueOf(request.getRole()))
                .enabled(true)
                .build();
        userRepository.save(user);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
        return buildResponse(userDetails);
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();
        if (!tokenProvider.validateToken(token)) {
            throw new BusinessRuleViolationException("Invalid or expired refresh token");
        }
        String email = tokenProvider.getUsername(token);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("")
                .authorities("ROLE_" + tokenProvider.getRole(token))
                .build();
        return buildResponse(userDetails);
    }

    /**
     * Builds an {@link AuthResponse} with a fresh access/refresh token pair.
     */
    private AuthResponse buildResponse(UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("VIEWER");
        String access = tokenProvider.generateAccessToken(userDetails, role);
        String refresh = tokenProvider.generateRefreshToken(userDetails);
        return AuthResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpirationMs())
                .role(role)
                .build();
    }
}