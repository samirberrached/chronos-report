package com.company.chronos.repository;

import com.company.chronos.entity.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Company} entities.
 *
 * <p>Provides lookup by the unique {@code companyCode} business key.</p>
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Finds a company by its unique business code.
     *
     * @param companyCode the company code
     * @return the matching company, if any
     */
    Optional<Company> findByCompanyCode(String companyCode);

    /**
     * Checks whether a company code already exists.
     *
     * @param companyCode the company code to check
     * @return true if a company with that code exists
     */
    boolean existsByCompanyCode(String companyCode);
}