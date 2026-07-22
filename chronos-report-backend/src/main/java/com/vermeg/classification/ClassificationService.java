package com.vermeg.classification;

import com.vermeg.classification.calendar.WorkingDaysService;
import com.vermeg.classification.dto.AnomalyLine;
import com.vermeg.classification.dto.ClassificationResult;
import com.vermeg.classification.dto.DateRange;
import com.vermeg.classification.dto.ReportLine;
import com.vermeg.entity.company.Company;
import com.vermeg.entity.companymember.CompanyMember;
import com.vermeg.entity.employee.Employee;
import com.vermeg.entity.employeebyactivitynature.EmployeeByActivityNature;
import com.vermeg.entity.employeebyproduct.EmployeeByProduct;
import com.vermeg.entity.employeetime.EmployeeTimeProjection;
import com.vermeg.entity.organizationalassignment.OrganizationalAssignment;
import com.vermeg.entity.organizationalunit.OrganizationalUnit;
import com.vermeg.entity.organizationalunitmember.OrganizationalUnitMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClassificationService {

    private static final String HOLIDAYS_ACTIVITY_NATURE = "HOLIDAYS";
    private static final String NA_ACCOUNTING_CODE = "NA";
    private static final String NO_TS_PREFIX = "NO_TS_";

    @Autowired private WorkingDaysService workingDaysService;

    /**
     * Brique 1 : Reçoit la période en String (ex: "1|26" ou "06|25") et le CompanyMember,
     * convertit le mois, puis applique la Table 1 (La Photo).
     */
    public DateRange calculateAnalysisPeriod(CompanyMember member, String monthPeriodStr) {
        YearMonth yearMonth = parseMonthPeriod(monthPeriodStr);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        LocalDate contractStart = member.getStartDate();
        LocalDate contractEnd = member.getEndDate();

        LocalDate calculatedStart;
        LocalDate calculatedEnd;

        if (contractStart.isBefore(monthStart) || contractStart.isEqual(monthStart)) {
            calculatedStart = monthStart;
            calculatedEnd = (contractEnd == null || !contractEnd.isBefore(monthEnd)) ? monthEnd : contractEnd;
        } else {
            calculatedStart = contractStart;
            calculatedEnd = (contractEnd == null || !contractEnd.isBefore(monthEnd)) ? monthEnd : contractEnd;
        }

        return new DateRange(calculatedStart, calculatedEnd);
    }

    /**
     * Phases 2 et 3 du spec : calcule la capacité, regroupe les timesheets par AccountingCode
     * et applique l'aiguillage des 4 cas pour produire les lignes Report/Anomalies de ce membre.
     * Toutes les données de référence viennent du {@link ClassificationContext} pré-chargé
     * (pas de requête SQL par employé).
     */
    public ClassificationResult classify(CompanyMember member, String monthPeriodStr, ClassificationContext context) {
        DateRange range = calculateAnalysisPeriod(member, monthPeriodStr);
        Employee employee = member.getEmployee();
        Company company = member.getCompany();
        int period = parseMonthPeriod(monthPeriodStr).getMonthValue();

        List<OrganizationalAssignment> assignments = context.assignmentsFor(employee.getId());
        if (!assignments.isEmpty()) {
            return classifyCase1DirectAssignment(employee, member, company, period, assignments);
        }

        int employeeCapacity = workingDaysService.countWorkingDays(company.getCountry(), range.startDate(), range.endDate());

        List<EmployeeTimeProjection> timesheets = context.timesheetsFor(employee.getId()).stream()
                .filter(et -> et.date() != null
                        && !et.date().isBefore(range.startDate())
                        && !et.date().isAfter(range.endDate()))
                .toList();

        double holidayDays = 0;
        double normalDays = 0;
        Map<String, EmployeeTimeProjection> representativeByAccountingCode = new LinkedHashMap<>();
        Map<String, Double> daysByAccountingCode = new LinkedHashMap<>();

        for (EmployeeTimeProjection et : timesheets) {
            double days = et.manDay() != null ? et.manDay() : 0.0;
            boolean isHoliday = HOLIDAYS_ACTIVITY_NATURE.equalsIgnoreCase(et.activityNatureName());

            if (isHoliday) {
                holidayDays += days;
            } else {
                normalDays += days;
                String code = et.accountingCodeOperationalIdentifier();
                if (code != null) {
                    representativeByAccountingCode.putIfAbsent(code, et);
                    daysByAccountingCode.merge(code, days, Double::sum);
                }
            }
        }

        if (normalDays == 0 && holidayDays == 0) {
            return classifyCase2NoTimesheet(employee, member, company, period, range, context);
        }

        if (normalDays == 0) {
            return classifyCase3OnlyHolidays(employee, member, company, period, range, holidayDays, employeeCapacity, context);
        }

        return classifyCase4NormalTimesheets(employee, member, company, period, range,
                representativeByAccountingCode, daysByAccountingCode, normalDays, holidayDays, employeeCapacity, context);
    }

    private ClassificationResult classifyCase1DirectAssignment(Employee employee, CompanyMember member, Company company,
                                                                 int period, List<OrganizationalAssignment> assignments) {
        List<ReportLine> lines = new ArrayList<>();
        for (OrganizationalAssignment oa : assignments) {
            lines.add(new ReportLine(
                    employee.getFirstName(), employee.getLastName(), employee.getIdentifier(),
                    member.getRegistrationNumber(), company.getName(), period,
                    oa.getOrganizationalUnit() != null ? oa.getOrganizationalUnit().getName() : null,
                    oa.getProduct() != null ? oa.getProduct().getName() : null,
                    oa.getAccountingCode() != null ? oa.getAccountingCode().getActivityNature().getName() : null,
                    oa.getAccountingCode() != null ? oa.getAccountingCode().getOperationalIdentifier() : null,
                    oa.getAllocationPercentage() != null ? oa.getAllocationPercentage() : 0.0));
        }
        return new ClassificationResult(lines, List.of());
    }

    private ClassificationResult classifyCase2NoTimesheet(Employee employee, CompanyMember member, Company company,
                                                            int period, DateRange range, ClassificationContext context) {
        return resolveDefaultAllocation(employee, member, company, period, range, 100.0, false, context);
    }

    private ClassificationResult classifyCase3OnlyHolidays(Employee employee, CompanyMember member, Company company,
                                                             int period, DateRange range, double holidayDays,
                                                             int employeeCapacity, ClassificationContext context) {
        boolean wholeMonthOnLeave = holidayDays >= employeeCapacity;
        return resolveDefaultAllocation(employee, member, company, period, range, 100.0, wholeMonthOnLeave, context);
    }

    private ClassificationResult classifyCase4NormalTimesheets(Employee employee, CompanyMember member, Company company,
                                                                int period, DateRange range,
                                                                Map<String, EmployeeTimeProjection> representativeByAccountingCode,
                                                                Map<String, Double> daysByAccountingCode,
                                                                double normalDays, double holidayDays, int employeeCapacity,
                                                                ClassificationContext context) {
        List<ReportLine> reportLines = new ArrayList<>();
        for (Map.Entry<String, Double> entry : daysByAccountingCode.entrySet()) {
            EmployeeTimeProjection representative = representativeByAccountingCode.get(entry.getKey());
            double days = entry.getValue();
            double ratio = employeeCapacity > 0 ? (days / employeeCapacity) * 100.0 : 0.0;

            reportLines.add(new ReportLine(
                    employee.getFirstName(), employee.getLastName(), employee.getIdentifier(),
                    member.getRegistrationNumber(), company.getName(), period,
                    representative.organizationalUnitName(),
                    representative.productName(),
                    representative.activityNatureName(),
                    entry.getKey(),
                    round2(ratio)));
        }

        ClassificationResult stepA = new ClassificationResult(reportLines, List.of());

        double totalSaisi = normalDays + holidayDays;
        if (totalSaisi >= employeeCapacity) {
            return stepA;
        }

        double missingRatio = employeeCapacity > 0 ? ((employeeCapacity - totalSaisi) / employeeCapacity) * 100.0 : 0.0;
        ClassificationResult completion = resolveDefaultAllocation(employee, member, company, period, range, round2(missingRatio), false, context);
        return stepA.merge(completion);
    }

    /**
     * Cherche les 3 valeurs de repli par défaut (OrgUnit/Product/ActivityNature) de l'employé.
     * Si les 3 sont trouvées, produit une ligne REPORT (code NA si congé plein mois, sinon NO_TS_+unité).
     * Sinon, produit une ligne ANOMALIES avec NOT FOUND sur les éléments manquants.
     */
    private ClassificationResult resolveDefaultAllocation(Employee employee, CompanyMember member, Company company,
                                                            int period, DateRange range, double ratio, boolean wholeMonthOnLeave,
                                                            ClassificationContext context) {
        Optional<String> orgUnit = findDefaultOrganizationalUnit(employee.getId(), range, context);
        Optional<String> product = findDefaultProduct(employee.getId(), context);
        Optional<String> activityNature = findDefaultActivityNature(employee.getId(), context);

        if (orgUnit.isPresent() && product.isPresent() && activityNature.isPresent()) {
            String accountingCode = wholeMonthOnLeave ? NA_ACCOUNTING_CODE : NO_TS_PREFIX + orgUnit.get();
            ReportLine line = new ReportLine(
                    employee.getFirstName(), employee.getLastName(), employee.getIdentifier(),
                    member.getRegistrationNumber(), company.getName(), period,
                    orgUnit.get(), product.get(), activityNature.get(), accountingCode, ratio);
            return ClassificationResult.report(line);
        }

        AnomalyLine anomaly = new AnomalyLine(
                employee.getFirstName(), employee.getLastName(), employee.getIdentifier(),
                member.getRegistrationNumber(), company.getName(), period,
                orgUnit.orElse(AnomalyLine.NOT_FOUND),
                product.orElse(AnomalyLine.NOT_FOUND),
                activityNature.orElse(AnomalyLine.NOT_FOUND));
        return ClassificationResult.anomaly(anomaly);
    }

    private Optional<String> findDefaultOrganizationalUnit(Long employeeId, DateRange range, ClassificationContext context) {
        return context.organizationalUnitMembersFor(employeeId).stream()
                .filter(oum -> (oum.getStartDate() == null || !oum.getStartDate().isAfter(range.endDate()))
                        && (oum.getEndDate() == null || !oum.getEndDate().isBefore(range.startDate())))
                .max(Comparator.comparing(OrganizationalUnitMember::getStartDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(oum -> {
                    OrganizationalUnit unit = oum.getOrganizationalUnit();
                    // Le rapport remonte à l'unité PARENTE ; si l'unité n'a pas de parent, on garde l'unité elle-même.
                    OrganizationalUnit parent = unit.getParent();
                    return parent != null ? parent.getName() : unit.getName();
                });
    }

    private Optional<String> findDefaultProduct(Long employeeId, ClassificationContext context) {
        return context.productsFor(employeeId).stream()
                .min(Comparator.comparing(EmployeeByProduct::getId))
                .map(ebp -> ebp.getProduct().getName());
    }

    private Optional<String> findDefaultActivityNature(Long employeeId, ClassificationContext context) {
        return context.activityNaturesFor(employeeId).stream()
                .min(Comparator.comparing(EmployeeByActivityNature::getId))
                .map(ean -> ean.getActivityNature().getName());
    }

    private YearMonth parseMonthPeriod(String monthPeriodStr) {
        String[] parts = monthPeriodStr.split("\\|");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt("20" + parts[1]);
        return YearMonth.of(year, month);
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
