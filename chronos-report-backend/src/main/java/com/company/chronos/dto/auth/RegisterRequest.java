package com.company.chronos.dto.auth;

import com.company.chronos.validation.ValidRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for {@code POST /auth/register}. Extends the login
 * credentials with the initial role assigned to the new user.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** User email used as the login identifier and display contact. */
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    /** Plain-text password (never serialized back to the client). */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

    /** Display name of the user. */
    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must be at most 200 characters")
    private String fullName;

    /** Role assigned to the user (ADMIN, FINANCE_ANALYST, VIEWER). */
    @NotBlank(message = "Role is required")
    @ValidRole
    private String role;
}