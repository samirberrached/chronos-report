package com.company.chronos.repository;

import com.company.chronos.entity.AccountingCode;
import com.company.chronos.entity.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link AccountingCode} entities.
 *
 * <p>Provides lookup by business key and company-scoped retrieval.</p>
 */
@Repository
public interface AccountingCodeRepository extends JpaRepository<AccountingCode, Long> {

    /**
     * Finds an accounting code by its business key within a company.
     *
     * @param code the GL code
     * @param company the owning company
     * @return the matching accounting code, if any
     */
    Optional<AccountingCode> findByCodeAndCompany(String code, Company company);

    /**
     * Returns all accounting codes belonging to a company.
     *
     * @param company the owning company
     * @return the list of accounting codes
     */
    List<AccountingCode> findAllByCompany(Company company);
}