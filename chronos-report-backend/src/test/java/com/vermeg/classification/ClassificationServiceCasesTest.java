package com.vermeg.classification;

import com.vermeg.classification.calendar.WorkingDaysService;
import com.vermeg.classification.dto.AnomalyLine;
import com.vermeg.classification.dto.ClassificationResult;
import com.vermeg.classification.dto.ReportLine;
import com.vermeg.entity.accountingcode.AccountingCode;
import com.vermeg.entity.activitynature.ActivityNature;
import com.vermeg.entity.company.Company;
import com.vermeg.entity.companymember.CompanyMember;
import com.vermeg.entity.employee.Employee;
import com.vermeg.entity.employeebyactivitynature.EmployeeByActivityNature;
import com.vermeg.entity.employeebyproduct.EmployeeByProduct;
import com.vermeg.entity.employeetime.EmployeeTimeProjection;
import com.vermeg.entity.organizationalassignment.OrganizationalAssignment;
import com.vermeg.entity.organizationalunit.OrganizationalUnit;
import com.vermeg.entity.organizationalunitmember.OrganizationalUnitMember;
import com.vermeg.entity.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClassificationServiceCasesTest {

    private static final String MONTH_PERIOD = "1|26"; // Janvier 2026
    private static final int CAPACITY = 22;

    private WorkingDaysService workingDaysService;
    private ClassificationService classificationService;

    private Employee employee;
    private CompanyMember member;

    @BeforeEach
    void setUp() {
        workingDaysService = mock(WorkingDaysService.class);
        when(workingDaysService.countWorkingDays(any(), any(), any())).thenReturn(CAPACITY);

        classificationService = new ClassificationService();
        ReflectionTestUtils.setField(classificationService, "workingDaysService", workingDaysService);

        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Alice");
        employee.setLastName("Martin");
        employee.setIdentifier("EMP001");

        Company company = new Company();
        company.setId(10L);
        company.setName("ACME");
        company.setCountry("FR");

        member = new CompanyMember();
        member.setId(100L);
        member.setEmployee(employee);
        member.setCompany(company);
        member.setRegistrationNumber(999L);
        member.setStartDate(LocalDate.of(2020, 1, 1));
        member.setEndDate(null);
    }

    @Test
    void case1_directAssignment_producesOneLinePerAssignment() {
        OrganizationalUnit unit = organizationalUnit(1L, "APAC", null);
        Product product = product(1L, "AGILE REPORTER");
        AccountingCode ac = accountingCode(1L, "VERM-MGT-APAC", unit, product, activityNature("REGIE"));

        List<OrganizationalAssignment> assignments = List.of(
                organizationalAssignment(unit, product, ac, 65.0),
                organizationalAssignment(unit, product, ac, 35.0));
        ClassificationContext context = context(assignments, List.of(), List.of(), List.of(), List.of());

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertEquals(2, result.reportLines().size());
        assertTrue(result.anomalyLines().isEmpty());
        assertEquals(65.0, result.reportLines().get(0).ratio());
        assertEquals("VERM-MGT-APAC", result.reportLines().get(0).accountingCode());
    }

    @Test
    void case2_noTimesheet_allDefaultsFound_usesParentOrgUnitAndNoTsCode() {
        OrganizationalUnit parent = organizationalUnit(3L, "APAC", null);
        OrganizationalUnit child = organizationalUnit(2L, "APAC SALES", parent);
        ClassificationContext context = defaultsContext(child, product(2L, "SUITE MEGARA"), activityNature("SUPPORT"), List.of());

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertEquals(1, result.reportLines().size());
        assertTrue(result.anomalyLines().isEmpty());
        ReportLine line = result.reportLines().get(0);
        assertEquals("NO_TS_APAC", line.accountingCode());
        assertEquals(100.0, line.ratio());
        assertEquals("APAC", line.organizationalUnit());
    }

    @Test
    void case2_noTimesheet_missingProduct_producesAnomalyWithNotFound() {
        OrganizationalUnit unit = organizationalUnit(2L, "APAC", null);
        ClassificationContext context = context(List.of(),
                List.of(organizationalUnitMember(unit)),
                List.of(),
                List.of(employeeByActivityNature(activityNature("SUPPORT"))),
                List.of());

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertTrue(result.reportLines().isEmpty());
        assertEquals(1, result.anomalyLines().size());
        AnomalyLine anomaly = result.anomalyLines().get(0);
        assertEquals(AnomalyLine.NOT_FOUND, anomaly.product());
        assertEquals("APAC", anomaly.organizationalUnit());
    }

    @Test
    void case3_wholeMonthHolidays_producesNaCode() {
        OrganizationalUnit unit = organizationalUnit(2L, "APAC", null);
        ClassificationContext context = defaultsContext(unit, product(2L, "PRODX"), activityNature("SUPPORT"),
                List.of(employeeTime("HOL-CODE", "APAC", "PRODX", "HOLIDAYS", CAPACITY)));

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertEquals(1, result.reportLines().size());
        ReportLine line = result.reportLines().get(0);
        assertEquals("NA", line.accountingCode());
        assertEquals(100.0, line.ratio());
    }

    @Test
    void case3_partialHolidays_producesNoTsCode() {
        OrganizationalUnit unit = organizationalUnit(2L, "APAC", null);
        ClassificationContext context = defaultsContext(unit, product(2L, "PRODX"), activityNature("SUPPORT"),
                List.of(employeeTime("HOL-CODE", "APAC", "PRODX", "HOLIDAYS", 5.0)));

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertEquals(1, result.reportLines().size());
        ReportLine line = result.reportLines().get(0);
        assertEquals("NO_TS_APAC", line.accountingCode());
        assertEquals(100.0, line.ratio());
    }

    @Test
    void case4_partialTimesheet_producesProjectLinePlusCompletion() {
        OrganizationalUnit unit = organizationalUnit(2L, "APAC", null);
        ClassificationContext context = defaultsContext(unit, product(2L, "PRODX"), activityNature("SUPPORT"),
                List.of(employeeTime("ALPHA-PROJECT", "APAC", "PRODX", "FORFAIT", 20.0)));

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertEquals(2, result.reportLines().size());
        ReportLine projectLine = result.reportLines().stream()
                .filter(l -> l.accountingCode().equals("ALPHA-PROJECT")).findFirst().orElseThrow();
        assertEquals(round(20.0 / CAPACITY * 100.0), projectLine.ratio());

        ReportLine completionLine = result.reportLines().stream()
                .filter(l -> l.accountingCode().equals("NO_TS_APAC")).findFirst().orElseThrow();
        assertEquals(round((CAPACITY - 20.0) / CAPACITY * 100.0), completionLine.ratio());
    }

    @Test
    void case4_fullCoverage_noCompletionLine() {
        OrganizationalUnit unit = organizationalUnit(2L, "APAC", null);
        ClassificationContext context = defaultsContext(unit, product(2L, "PRODX"), activityNature("SUPPORT"),
                List.of(employeeTime("ALPHA-PROJECT", "APAC", "PRODX", "FORFAIT", CAPACITY)));

        ClassificationResult result = classificationService.classify(member, MONTH_PERIOD, context);

        assertEquals(1, result.reportLines().size());
        assertEquals("ALPHA-PROJECT", result.reportLines().get(0).accountingCode());
    }

    // --- helpers ---

    private ClassificationContext defaultsContext(OrganizationalUnit unit, Product product, ActivityNature nature,
                                                   List<EmployeeTimeProjection> timesheets) {
        return context(List.of(),
                List.of(organizationalUnitMember(unit)),
                List.of(employeeByProduct(product)),
                List.of(employeeByActivityNature(nature)),
                timesheets);
    }

    private ClassificationContext context(List<OrganizationalAssignment> assignments,
                                           List<OrganizationalUnitMember> orgUnitMembers,
                                           List<EmployeeByProduct> products,
                                           List<EmployeeByActivityNature> activityNatures,
                                           List<EmployeeTimeProjection> timesheets) {
        return new ClassificationContext(
                Map.of(employee.getId(), assignments),
                Map.of(employee.getId(), orgUnitMembers),
                Map.of(employee.getId(), products),
                Map.of(employee.getId(), activityNatures),
                Map.of(employee.getId(), timesheets));
    }

    private OrganizationalUnit organizationalUnit(Long id, String name, OrganizationalUnit parent) {
        OrganizationalUnit unit = new OrganizationalUnit();
        unit.setId(id);
        unit.setName(name);
        unit.setParent(parent);
        return unit;
    }

    private Product product(Long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        return p;
    }

    private ActivityNature activityNature(String name) {
        ActivityNature nature = new ActivityNature();
        nature.setId((long) Math.abs(name.hashCode()));
        nature.setName(name);
        return nature;
    }

    private AccountingCode accountingCode(Long id, String operationalIdentifier, OrganizationalUnit unit, Product product, ActivityNature nature) {
        AccountingCode ac = new AccountingCode();
        ac.setId(id);
        ac.setOperationalIdentifier(operationalIdentifier);
        ac.setOrganizationalUnit(unit);
        ac.setProduct(product);
        ac.setActivityNature(nature);
        return ac;
    }

    private OrganizationalAssignment organizationalAssignment(OrganizationalUnit unit, Product product, AccountingCode ac, double percentage) {
        OrganizationalAssignment oa = new OrganizationalAssignment();
        oa.setEmployee(employee);
        oa.setOrganizationalUnit(unit);
        oa.setProduct(product);
        oa.setAccountingCode(ac);
        oa.setAllocationPercentage(percentage);
        return oa;
    }

    private OrganizationalUnitMember organizationalUnitMember(OrganizationalUnit unit) {
        OrganizationalUnitMember oum = new OrganizationalUnitMember();
        oum.setEmployee(employee);
        oum.setOrganizationalUnit(unit);
        oum.setStartDate(LocalDate.of(2020, 1, 1));
        oum.setEndDate(null);
        return oum;
    }

    private EmployeeByProduct employeeByProduct(Product product) {
        EmployeeByProduct ebp = new EmployeeByProduct();
        ebp.setEmployee(employee);
        ebp.setProduct(product);
        return ebp;
    }

    private EmployeeByActivityNature employeeByActivityNature(ActivityNature nature) {
        EmployeeByActivityNature ean = new EmployeeByActivityNature();
        ean.setEmployee(employee);
        ean.setActivityNature(nature);
        return ean;
    }

    private EmployeeTimeProjection employeeTime(String accountingCodeOperationalIdentifier, String organizationalUnitName,
                                                 String productName, String activityNatureName, double manDay) {
        return new EmployeeTimeProjection(employee.getId(), LocalDate.of(2026, 1, 10), manDay,
                accountingCodeOperationalIdentifier, activityNatureName, organizationalUnitName, productName);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
