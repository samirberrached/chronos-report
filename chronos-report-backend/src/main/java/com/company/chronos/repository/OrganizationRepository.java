package com.company.chronos.repository;

import com.company.chronos.entity.Company;
import com.company.chronos.entity.Organization;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Organization} entities.
 *
 * <p>Supports company-scoped lookups and retrieval of the organization
 * hierarchy (root nodes and children of a given parent).</p>
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    /**
     * Returns all organizations belonging to a company.
     *
     * @param company the owning company
     * @return the list of organizations
     */
    List<Organization> findAllByCompany(Company company);

    /**
     * Returns the root organizations (no parent) for a company.
     *
     * @param company the owning company
     * @return the list of root organizations
     */
    List<Organization> findByCompanyAndParentIsNull(Company company);

    /**
     * Returns the direct children of a given parent organization.
     *
     * @param parent the parent organization
     * @return the list of child organizations
     */
    List<Organization> findByParent(Organization parent);
}