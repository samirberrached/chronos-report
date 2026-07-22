package com.vermeg.entity.employeetime;

import java.time.LocalDate;

/**
 * Projection légère d'EmployeeTime ne portant que les champs nécessaires à la classification,
 * pour éviter l'hydratation coûteuse de graphes d'entités JPA sur de gros volumes (~50k+ lignes/mois).
 */
public record EmployeeTimeProjection(
        Long employeeId,
        LocalDate date,
        Double manDay,
        String accountingCodeOperationalIdentifier,
        String activityNatureName,
        String organizationalUnitName,
        String productName
) {}
