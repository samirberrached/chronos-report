package com.vermeg.classification;

import com.vermeg.classification.dto.DateRange;
import com.vermeg.entity.companymember.CompanyMember;
import com.vermeg.entity.companymember.CompanyMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClassificationServiceIntegrationTest {

    @Autowired
    private CompanyMemberRepository companyMemberRepository;

    @Autowired
    private ClassificationService classificationService;

    @Test
    void connectsToDatabaseAndComputesPeriodForRealMember() {
        List<CompanyMember> members = companyMemberRepository.findAll();

        assertFalse(members.isEmpty(),
                "Aucun CompanyMember trouvé en base : vérifier que Postgres tourne et que la table company_member contient des données.");

        // Certaines lignes importées ont des dates de contrat incohérentes (endDate < startDate) ;
        // on choisit une ligne cohérente pour valider la logique métier, sans dépendre de la qualité des données.
        CompanyMember member = members.stream()
                .filter(m -> m.getStartDate() != null
                        && (m.getEndDate() == null || !m.getEndDate().isBefore(m.getStartDate())))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Aucun CompanyMember avec des dates de contrat cohérentes (startDate <= endDate) trouvé parmi "
                                + members.size() + " lignes."));

        DateRange range = classificationService.calculateAnalysisPeriod(member, "1|26");

        assertNotNull(range);
        assertFalse(range.startDate().isAfter(range.endDate()));
    }
}
