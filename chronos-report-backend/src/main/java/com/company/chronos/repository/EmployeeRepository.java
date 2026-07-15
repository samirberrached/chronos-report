package com.company.chronos.repository;

import com.company.chronos.entity.Company;
import com.company.chronos.entity.Employee;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Employee} entities.
 *
 * <p>Provides lookup by business key and company-scoped pagination. No business
 * logic is contained here — only data access and custom queries.</p>
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Finds an employee by its business key within a company.
     *
     * @param employeeCode the employee code
     * @param company the owning company
     * @return the matching employee, if any
     */
    Optional<Employee> findByEmployeeCodeAndCompany(String employeeCode, Company company);

    /**
     * Returns a page of employees for a given company.
     *
     * @param company the owning company
     * @param pageable pagination metadata
     * @return a page of employees
     */
    Page<Employee> findByCompany(Company company, Pageable pageable);

    /**
     * Returns all employees belonging to a company.
     *
     * @param company the owning company
     * @return the list of employees
     */
    List<Employee> findAllByCompany(Company company);

    /**
     * Checks whether an employee code already exists for a company.
     *
     * @param employeeCode the employee code to check
     * @param companyId the company id
     * @return true if a duplicate exists
     */
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.employeeCode = :code "
            + "AND e.company.id = :companyId")
    boolean existsByEmployeeCodeAndCompanyId(@Param("code") String employeeCode,
                                             @Param("companyId") Long companyId);
}