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
 * Request payload for exporting a report. Specifies the company, month and
 * desired export format (csv or excel).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {

    /** Owning company id. */
    @NotNull(message = "Company id is required")
    private Long companyId;

    /** Reporting month in {@code yyyy-MM} format. */
    @NotBlank(message = "Month is required")
    @ValidMonthPeriod
    private String month;

    /** Export format: "csv" or "excel". */
    @NotBlank(message = "Format is required")
    private String format;
}