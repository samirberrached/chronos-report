package com.company.chronos.repository;

import com.company.chronos.entity.Employee;
import com.company.chronos.entity.EmployeeTime;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmployeeTime} fact entities.
 *
 * <p>Provides month-scoped aggregation queries used by the report builders to
 * compute allocations and detect anomalies.</p>
 */
@Repository
public interface EmployeeTimeRepository extends JpaRepository<EmployeeTime, Long> {

    /**
     * Returns all time entries for an employee in a given reporting month.
     *
     * @param employee the employee
     * @param month the reporting month (yyyy-MM)
     * @return the list of time entries
     */
    List<EmployeeTime> findByEmployeeAndMonth(Employee employee, String month);

    /**
     * Returns all time entries for a company in a given reporting month.
     *
     * @param companyId the company id
     * @param month the reporting month (yyyy-MM)
     * @return the list of time entries
     */
    @Query("SELECT t FROM EmployeeTime t WHERE t.employee.company.id = :companyId "
            + "AND t.month = :month")
    List<EmployeeTime> findByCompanyIdAndMonth(@Param("companyId") Long companyId,
                                               @Param("month") String month);

    /**
     * Returns the total logged hours for an employee in a given month.
     *
     * @param employeeId the employee id
     * @param month the reporting month (yyyy-MM)
     * @return the total hours, or zero if none
     */
    @Query("SELECT COALESCE(SUM(t.hours), 0) FROM EmployeeTime t "
            + "WHERE t.employee.id = :employeeId AND t.month = :month")
    BigDecimal sumHoursByEmployeeAndMonth(@Param("employeeId") Long employeeId,
                                          @Param("month") String month);
}