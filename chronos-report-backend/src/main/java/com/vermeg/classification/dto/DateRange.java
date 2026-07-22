package com.vermeg.classification.dto;

import java.time.LocalDate;

/**
 * Conteneur pour transporter la période d'analyse calculée (Brique 1).
 */
public record DateRange(LocalDate startDate, LocalDate endDate) {}