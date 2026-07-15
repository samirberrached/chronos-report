package com.company.chronos.dto.employee;

import com.company.chronos.validation.ValidAllocationPercentage;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for creating or updating an {@code Employee}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    /** Business key: employee code within the company. */
    @NotBlank(message = "Employee code is required")
    @Size(max = 50, message = "Employee code must be at most 50 characters")
    private String employeeCode;

    /** Full display name. */
    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must be at most 200 characters")
    private String fullName;

    /** Email address. */
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    /** Monthly gross cost. */
    @NotNull(message = "Monthly cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly cost must be positive")
    private BigDecimal monthlyCost;

    /** Standard contracted hours per month. */
    @NotNull(message = "Standard monthly hours is required")
    @Positive(message = "Standard monthly hours must be positive")
    private Integer standardMonthlyHours;

    /** Owning company id. */
    @NotNull(message = "Company id is required")
    private Long companyId;

    /** Organization unit id. */
    @NotNull(message = "Organization id is required")
    private Long organizationId;

    /** Default allocation percentage (0-100) used as a fallback. */
    @ValidAllocationPercentage
    private BigDecimal defaultAllocationPercentage;
}