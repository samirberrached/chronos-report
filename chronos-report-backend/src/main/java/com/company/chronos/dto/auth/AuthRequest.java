package com.company.chronos.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for {@code POST /auth/login} and {@code POST /auth/register}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    /** User email used as the login identifier. */
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    /** Plain-text password (never serialized back to the client). */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;
}