package com.company.chronos.authentication;

import com.company.chronos.dto.auth.AuthRequest;
import com.company.chronos.dto.auth.AuthResponse;
import com.company.chronos.dto.auth.RefreshRequest;
import com.company.chronos.dto.auth.RegisterRequest;
import com.company.chronos.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing public authentication endpoints (login, register,
 * refresh). No JWT is required here.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /** Authentication service. */
    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user and returns a token pair.
     *
     * @param request the login request
     * @return the authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    /**
     * Registers a new user and returns a token pair.
     *
     * @param request the registration request
     * @return the authentication response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authenticationService.register(request));
    }

    /**
     * Exchanges a refresh token for a new access token.
     *
     * @param request the refresh request
     * @return the authentication response
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authenticationService.refresh(request));
    }
}