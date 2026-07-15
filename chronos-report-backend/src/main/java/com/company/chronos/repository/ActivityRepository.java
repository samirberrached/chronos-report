package com.company.chronos.repository;

import com.company.chronos.entity.Activity;
import com.company.chronos.entity.Company;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Activity} entities.
 *
 * <p>Provides lookup by business key and company-scoped retrieval.</p>
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Finds an activity by its business key within a company.
     *
     * @param activityCode the activity code
     * @param company the owning company
     * @return the matching activity, if any
     */
    Optional<Activity> findByActivityCodeAndCompany(String activityCode, Company company);

    /**
     * Returns all activities belonging to a company.
     *
     * @param company the owning company
     * @return the list of activities
     */
    List<Activity> findAllByCompany(Company company);
}