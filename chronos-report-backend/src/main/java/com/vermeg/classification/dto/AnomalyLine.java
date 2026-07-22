package com.vermeg.classification.dto;

public record AnomalyLine(
        String firstName,
        String lastName,
        String identifier,
        Long registrationNumber,
        String company,
        int period,
        String organizationalUnit,
        String product,
        String activityNature
) {
    public static final String NOT_FOUND = "NOT FOUND";
}
