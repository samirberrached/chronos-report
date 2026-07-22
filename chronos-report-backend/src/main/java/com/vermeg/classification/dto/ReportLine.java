package com.vermeg.classification.dto;

public record ReportLine(
        String firstName,
        String lastName,
        String identifier,
        Long registrationNumber,
        String company,
        int period,
        String organizationalUnit,
        String product,
        String activityNature,
        String accountingCode,
        double ratio
) {}
