package com.company.chronos.repository;

import com.company.chronos.entity.Company;
import com.company.chronos.entity.EmployeeReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmployeeReport} fact entities.
 *
 * <p>Provides company- and month-scoped lookups plus paginated listing.</p>
 */
@Repository
public interface EmployeeReportRepository extends JpaRepository<EmployeeReport, Long> {

    /**
     * Finds a report for a company and reporting month.
     *
     * @param company the company
     * @param month the reporting month (yyyy-MM)
     * @return the matching report, if any
     */
    Optional<EmployeeReport> findByCompanyAndMonth(Company company, String month);

    /**
     * Returns a page of reports for a company ordered by month descending.
     *
     * @param company the company
     * @param pageable pagination metadata
     * @return a page of reports
     */
    Page<EmployeeReport> findByCompanyOrderByMonthDesc(Company company, Pageable pageable);

    /**
     * Returns all reports for a company.
     *
     * @param company the company
     * @return the list of reports
     */
    List<EmployeeReport> findAllByCompany(Company company);

    /**
     * Finds failed reports created before a given date (for cleanup).
     *
     * @param status the report status to match
     * @param createdAtBefore the cutoff date
     * @return the matching reports
     */
    List<EmployeeReport> findByStatusAndCreatedAtBefore(
            EmployeeReport.ReportStatus status, java.time.LocalDate createdAtBefore);
}
