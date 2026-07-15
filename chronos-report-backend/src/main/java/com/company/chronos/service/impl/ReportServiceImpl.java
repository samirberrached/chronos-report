package com.company.chronos.service.impl;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.report.GenerateReportRequest;
import com.company.chronos.dto.report.ReportResponse;
import com.company.chronos.entity.AccountingCode;
import com.company.chronos.entity.Company;
import com.company.chronos.entity.Employee;
import com.company.chronos.entity.EmployeeAllocation;
import com.company.chronos.entity.EmployeeAnomaly;
import com.company.chronos.entity.EmployeeReport;
import com.company.chronos.entity.EmployeeReport.ReportStatus;
import com.company.chronos.entity.EmployeeTime;
import com.company.chronos.exception.BusinessRuleViolationException;
import com.company.chronos.exception.ResourceNotFoundException;
import com.company.chronos.mapper.ReportMapper;
import com.company.chronos.report.AnalyticEmployeeTimeReportBuilder;
import com.company.chronos.report.EmployeeAnomalyReportBuilder;
import com.company.chronos.repository.AccountingCodeRepository;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.repository.EmployeeAllocationRepository;
import com.company.chronos.repository.EmployeeAnomalyRepository;
import com.company.chronos.repository.EmployeeReportRepository;
import com.company.chronos.repository.EmployeeRepository;
import com.company.chronos.repository.EmployeeTimeRepository;
import com.company.chronos.service.ReportService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ReportService}. Orchestrates report
 * generation: clears prior allocations, builds new ones via the domain
 * builder, detects anomalies, and persists the report header.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportServiceImpl implements ReportService {

    /** Company repository. */
    private final CompanyRepository companyRepository;

    /** Employee repository. */
    private final EmployeeRepository employeeRepository;

    /** Employee time repository. */
    private final EmployeeTimeRepository employeeTimeRepository;

    /** Accounting code repository. */
    private final AccountingCodeRepository accountingCodeRepository;

    /** Allocation repository. */
    private final EmployeeAllocationRepository allocationRepository;

    /** Report repository. */
    private final EmployeeReportRepository reportRepository;

    /** Anomaly repository. */
    private final EmployeeAnomalyRepository anomalyRepository;

    /** Report mapper. */
    private final ReportMapper reportMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> getReports(Long companyId, Pageable pageable) {
        Company company = requireCompany(companyId);
        Page<EmployeeReport> page =
                reportRepository.findByCompanyOrderByMonthDesc(company, pageable);
        return PageResponse.<ReportResponse>builder()
                .content(page.getContent().stream().map(reportMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportResponse getReport(Long companyId, String month) {
        Company company = requireCompany(companyId);
        EmployeeReport report = reportRepository.findByCompanyAndMonth(company, month)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Report", "company/month", companyId + "/" + month));
        return reportMapper.toResponse(report);
    }

    @Override
    public ReportResponse generateReport(GenerateReportRequest request) {
        Company company = requireCompany(request.getCompanyId());
        String month = request.getMonth();

        EmployeeReport report = reportRepository
                .findByCompanyAndMonth(company, month)
                .orElseGet(() -> EmployeeReport.builder()
                        .company(company).month(month)
                        .status(ReportStatus.PENDING).build());

        try {
            allocationRepository.deleteByCompanyIdAndMonth(company.getId(), month);
            anomalyRepository.findAllByReport(report).forEach(anomalyRepository::delete);

            List<Employee> employees = employeeRepository.findAllByCompany(company);
            AccountingCode code = resolveAccountingCode(company);

            List<EmployeeAllocation> allocations = new ArrayList<>();
            List<EmployeeAnomaly> anomalies = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (Employee employee : employees) {
                List<EmployeeTime> times =
                        employeeTimeRepository.findByEmployeeAndMonth(employee, month);
                if (times.isEmpty()) {
                    continue;
                }
                allocations.addAll(AnalyticEmployeeTimeReportBuilder.buildAllocations(
                        employee, times, code, month));
                anomalies.addAll(EmployeeAnomalyReportBuilder.buildAnomalies(
                        employee, times, report, month));
            }

            allocationRepository.saveAll(allocations);
            total = allocations.stream()
                    .map(EmployeeAllocation::getAllocatedCost)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            report.setTotalAllocatedCost(total.setScale(2, java.math.RoundingMode.HALF_UP));
            report.setEmployeeCount(employees.size());
            report.setStatus(ReportStatus.GENERATED);
            report.setNote(null);
            report = reportRepository.save(report);

            for (EmployeeAnomaly anomaly : anomalies) {
                anomaly.setReport(report);
            }
            anomalyRepository.saveAll(anomalies);

            return reportMapper.toResponse(report);
        } catch (BusinessRuleViolationException ex) {
            report.setStatus(ReportStatus.FAILED);
            report.setNote(ex.getMessage());
            reportRepository.save(report);
            throw ex;
        } catch (Exception ex) {
            log.error("Report generation failed for {}/{}", company.getId(), month, ex);
            report.setStatus(ReportStatus.FAILED);
            report.setNote("Unexpected error: " + ex.getMessage());
            reportRepository.save(report);
            throw new BusinessRuleViolationException(
                    "Report generation failed: " + ex.getMessage());
        }
    }

    /**
     * Resolves the accounting code used to book allocations for a company.
     */
    private AccountingCode resolveAccountingCode(Company company) {
        return accountingCodeRepository.findAllByCompany(company).stream()
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "No accounting code configured for company "
                                + company.getName()));
    }

    /**
     * Loads a company by id or throws.
     */
    private Company requireCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }
}