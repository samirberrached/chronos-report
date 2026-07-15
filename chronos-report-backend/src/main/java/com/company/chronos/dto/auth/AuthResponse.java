package com.company.chronos.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload returned after a successful authentication or registration.
 * Carries both the access and refresh JWTs plus basic identity information.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** Access token (short-lived) used to authorize API requests. */
    private String accessToken;

    /** Refresh token (long-lived) used to obtain a new access token. */
    private String refreshToken;

    /** Token type, always "Bearer". */
    private String tokenType;

    /** Access token lifetime in milliseconds. */
    private long expiresIn;

    /** Authenticated user's email. */
    private String email;

    /** Authenticated user's assigned role. */
    private String role;
}