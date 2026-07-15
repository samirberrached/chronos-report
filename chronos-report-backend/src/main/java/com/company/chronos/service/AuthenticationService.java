package com.company.chronos.service;

import com.company.chronos.dto.auth.AuthRequest;
import com.company.chronos.dto.auth.AuthResponse;
import com.company.chronos.dto.auth.RefreshRequest;
import com.company.chronos.dto.auth.RegisterRequest;

/**
 * Service contract for authentication: login, registration and token refresh.
 */
public interface AuthenticationService {

    /**
     * Authenticates a user and returns a fresh token pair.
     *
     * @param request the login request
     * @return the authentication response with access/refresh tokens
     */
    AuthResponse login(AuthRequest request);

    /**
     * Registers a new user and returns a token pair.
     *
     * @param request the registration request
     * @return the authentication response with access/refresh tokens
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Exchanges a refresh token for a new access token.
     *
     * @param request the refresh request
     * @return the authentication response with a new access token
     */
    AuthResponse refresh(RefreshRequest request);
}