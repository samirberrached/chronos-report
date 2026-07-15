package com.company.chronos.repository;

import com.company.chronos.entity.EmployeeAnomaly;
import com.company.chronos.entity.EmployeeAnomaly.AnomalySeverity;
import com.company.chronos.entity.EmployeeReport;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link EmployeeAnomaly} fact entities.
 *
 * <p>Provides report- and month-scoped lookups plus paginated, filterable
 * listing for the anomalies endpoint.</p>
 */
@Repository
public interface EmployeeAnomalyRepository extends JpaRepository<EmployeeAnomaly, Long> {

    /**
     * Returns all anomalies for a given report.
     *
     * @param report the report
     * @return the list of anomalies
     */
    List<EmployeeAnomaly> findAllByReport(EmployeeReport report);

    /**
     * Returns a page of anomalies for a company, optionally filtered by
     * resolved status, ordered by severity then month.
     *
     * @param companyId the company id
     * @param resolved the resolved flag to filter on (null = no filter)
     * @param pageable pagination metadata
     * @return a page of anomalies
     */
    Page<EmployeeAnomaly> findByReport_Company_IdAndResolved(
            Long companyId, boolean resolved, Pageable pageable);

    /**
     * Returns all unresolved anomalies of a given severity for a company.
     *
     * @param companyId the company id
     * @param severity the severity to filter on
     * @return the list of anomalies
     */
    List<EmployeeAnomaly> findByReport_Company_IdAndSeverityAndResolved(
            Long companyId, AnomalySeverity severity, boolean resolved);
}