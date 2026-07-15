package com.company.chronos.dto.report;

import com.company.chronos.validation.ValidMonthPeriod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for {@code POST /reports/generate}. Triggers (re)generation
 * of the cost-allocation report for a company and reporting month.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {

    /** Owning company id. */
    @NotNull(message = "Company id is required")
    private Long companyId;

    /** Reporting month in {@code yyyy-MM} format. */
    @NotBlank(message = "Month is required")
    @ValidMonthPeriod
    private String month;
}