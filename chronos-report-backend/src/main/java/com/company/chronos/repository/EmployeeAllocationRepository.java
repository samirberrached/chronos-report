package com.company.chronos.repository;

import com.company.chronos.entity.EmployeeAllocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmployeeAllocation} fact entities.
 *
 * <p>Provides month- and company-scoped retrieval used by the report and
 * export services.</p>
 */
@Repository
public interface EmployeeAllocationRepository extends JpaRepository<EmployeeAllocation, Long> {

    /**
     * Returns all allocations for a company in a given reporting month.
     *
     * @param companyId the company id
     * @param month the reporting month (yyyy-MM)
     * @return the list of allocations
     */
    @Query("SELECT a FROM EmployeeAllocation a WHERE a.employee.company.id = :companyId "
            + "AND a.month = :month")
    List<EmployeeAllocation> findByCompanyIdAndMonth(@Param("companyId") Long companyId,
                                                     @Param("month") String month);

    /**
     * Deletes all allocations for a company in a given reporting month so they
     * can be regenerated.
     *
     * @param companyId the company id
     * @param month the reporting month (yyyy-MM)
     * @return the number of rows deleted
     */
    @Query("DELETE FROM EmployeeAllocation a WHERE a.employee.company.id = :companyId "
            + "AND a.month = :month")
    int deleteByCompanyIdAndMonth(@Param("companyId") Long companyId,
                                  @Param("month") String month);
}