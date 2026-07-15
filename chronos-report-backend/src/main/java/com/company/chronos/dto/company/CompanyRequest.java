package com.company.chronos.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for creating or updating a {@code Company}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    /** Business key: short unique company code. */
    @NotBlank(message = "Company code is required")
    @Size(max = 30, message = "Company code must be at most 30 characters")
    private String companyCode;

    /** Legal name. */
    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    /** ISO-4217 currency code. */
    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO-4217 code")
    private String currency;

    /** Whether the company is active. */
    private boolean active;
}