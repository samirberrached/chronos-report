package com.vermeg.classification;

import com.vermeg.classification.dto.DateRange;
import com.vermeg.entity.companymember.CompanyMember;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClassificationServiceTest {

    private final ClassificationService service = new ClassificationService();

    private CompanyMember member(LocalDate startDate, LocalDate endDate) {
        CompanyMember m = new CompanyMember();
        m.setStartDate(startDate);
        m.setEndDate(endDate);
        return m;
    }

    @Test
    void contractCoversWholeMonth_returnsFullMonth() {
        CompanyMember m = member(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 12, 31));

        DateRange range = service.calculateAnalysisPeriod(m, "1|26");

        assertEquals(LocalDate.of(2026, 1, 1), range.startDate());
        assertEquals(LocalDate.of(2026, 1, 31), range.endDate());
    }

    @Test
    void contractStartsMidMonth_returnsContractStart() {
        CompanyMember m = member(LocalDate.of(2026, 1, 15), null);

        DateRange range = service.calculateAnalysisPeriod(m, "1|26");

        assertEquals(LocalDate.of(2026, 1, 15), range.startDate());
        assertEquals(LocalDate.of(2026, 1, 31), range.endDate());
    }

    @Test
    void contractEndsMidMonth_returnsContractEnd() {
        CompanyMember m = member(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 20));

        DateRange range = service.calculateAnalysisPeriod(m, "1|26");

        assertEquals(LocalDate.of(2026, 1, 1), range.startDate());
        assertEquals(LocalDate.of(2026, 1, 20), range.endDate());
    }

    @Test
    void nullEndDate_treatedAsStillActive() {
        CompanyMember m = member(LocalDate.of(2020, 1, 1), null);

        DateRange range = service.calculateAnalysisPeriod(m, "6|25");

        assertEquals(LocalDate.of(2025, 6, 1), range.startDate());
        assertEquals(LocalDate.of(2025, 6, 30), range.endDate());
    }

    @Test
    void singleDigitMonth_isParsedCorrectly() {
        CompanyMember m = member(LocalDate.of(2020, 1, 1), null);

        DateRange range = service.calculateAnalysisPeriod(m, "06|25");

        assertEquals(LocalDate.of(2025, 6, 1), range.startDate());
        assertEquals(LocalDate.of(2025, 6, 30), range.endDate());
    }
}
