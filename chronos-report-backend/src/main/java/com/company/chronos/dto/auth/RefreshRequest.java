package com.company.chronos.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for refreshing an access token using a refresh token.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {

    /** The refresh token issued at login/registration. */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}