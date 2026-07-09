package com.vermeg.service;

import com.vermeg.model.*;
import com.vermeg.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmployeeStructureImportService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private ActivityNatureRepository activityNatureRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrganizationalUnitRepository ouRepository;

    @Autowired private CompanyMemberRepository companyMemberRepository;
    @Autowired private EmployeeByActivityNatureRepository empNatureRepository;
    @Autowired private EmployeeByProductRepository empProductRepository;
    @Autowired private OrganizationalUnitMemberRepository ouMemberRepository;

    private static final String FILE_PATH =
            "C:\\Users\\berra\\OneDrive\\Bureau\\chronos-report\\data-cleaning\\employee_time_clean.csv";

    // Configuration exacte pour ton format de date "dd/MM/yyyy"
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void importEmployeeStructures() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("🗑️ [Niveau 1 - Employés] Nettoyage ciblé et remise à zéro des IDs...");

        jdbcTemplate.execute("TRUNCATE TABLE company_member, employee_by_activity_nature, employee_by_product, organizational_unit_member CASCADE;");

        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS company_member_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS employee_by_activity_nature_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS employee_by_product_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS organizational_unit_member_id_seq RESTART WITH 1;");

        System.out.println("🧠 Chargement des dictionnaires du Niveau 0 en mémoire...");

        Map<String, Employee> empMap = new HashMap<>();
        employeeRepository.findAll().forEach(e -> {
            if (e.getIdentifier() != null) empMap.put(e.getIdentifier().trim(), e);
        });

        Map<String, Company> companyMap = new HashMap<>();
        companyRepository.findAll().forEach(c -> {
            if (c.getName() != null) companyMap.put(c.getName().trim(), c);
        });

        Map<String, ActivityNature> natureMap = new HashMap<>();
        activityNatureRepository.findAll().forEach(n -> {
            if (n.getName() != null) natureMap.put(n.getName().trim(), n);
        });

        Map<String, Product> productMap = new HashMap<>();
        productRepository.findAll().forEach(p -> {
            if (p.getName() != null) productMap.put(p.getName().trim(), p);
        });

        Map<String, OrganizationalUnit> ouMap = new HashMap<>();
        ouRepository.findAll().forEach(ou -> {
            if (ou.getName() != null) ouMap.put(ou.getName().trim(), ou);
        });

        List<CompanyMember> companyMembers = new ArrayList<>();
        List<EmployeeByActivityNature> empNatures = new ArrayList<>();
        List<EmployeeByProduct> empProducts = new ArrayList<>();
        List<OrganizationalUnitMember> ouMembers = new ArrayList<>();

        Set<String> seenCompanyMember = new HashSet<>();
        Set<String> seenEmpNature = new HashSet<>();
        Set<String> seenEmpProduct = new HashSet<>();
        Set<String> seenOuMember = new HashSet<>();

        System.out.println("📦 Extraction et calcul des liaisons Employés avec dates et matricules (Long)...");

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH), 1024 * 1024)) {
            String line;
            boolean isHeader = true;

            // INDEX EXACTS BASÉS SUR TON ENTÊTE
            final int PRODUCT_COL = 2;
            final int EMP_ID_COL = 12;
            final int COMPANY_COL = 15;
            final int COMPANY_START_DATE_COL = 16;
            final int COMPANY_END_DATE_COL = 17;
            final int REGISTRATION_COL = 18;       // employee_registration_number
            final int OU_COL = 19;
            final int OU_START_DATE_COL = 20;
            final int OU_END_DATE_COL = 21;
            final int NATURE_COL = 23;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) { isHeader = false; continue; }

                String[] fields = parseCsvLine(line);

                String empIdentifier = getString(fields, EMP_ID_COL);
                if (empIdentifier == null || !empMap.containsKey(empIdentifier)) {
                    continue;
                }

                Employee employee = empMap.get(empIdentifier);

                // 1. Liaison CompanyMember (Dates + Matricule Entreprise converti en Long)
                String companyName = getString(fields, COMPANY_COL);
                if (companyName != null && companyMap.containsKey(companyName)) {
                    String key = empIdentifier + "||" + companyName;
                    if (seenCompanyMember.add(key)) {
                        CompanyMember cm = new CompanyMember();
                        cm.setEmployee(employee);
                        cm.setCompany(companyMap.get(companyName));

                        cm.setStartDate(parseDate(getString(fields, COMPANY_START_DATE_COL)));
                        cm.setEndDate(parseDate(getString(fields, COMPANY_END_DATE_COL)));

                        // ✅ Conversion sécurisée du String vers Long
                        String regStr = getString(fields, REGISTRATION_COL);
                        if (regStr != null && !regStr.equalsIgnoreCase("null")) {
                            try {
                                cm.setRegistrationNumber(Long.parseLong(regStr));
                            } catch (NumberFormatException nfe) {
                                // Si la valeur n'est pas un nombre valide, on laisse null
                            }
                        }

                        companyMembers.add(cm);
                    }
                }

                // 2. Liaison EmployeeByActivityNature
                String natureName = getString(fields, NATURE_COL);
                if (natureName != null && natureMap.containsKey(natureName)) {
                    String key = empIdentifier + "||" + natureName;
                    if (seenEmpNature.add(key)) {
                        EmployeeByActivityNature ean = new EmployeeByActivityNature();
                        ean.setEmployee(employee);
                        ean.setActivityNature(natureMap.get(natureName));
                        empNatures.add(ean);
                    }
                }

                // 3. Liaison EmployeeByProduct
                String productName = getString(fields, PRODUCT_COL);
                if (productName != null && productMap.containsKey(productName)) {
                    String key = empIdentifier + "||" + productName;
                    if (seenEmpProduct.add(key)) {
                        EmployeeByProduct ebp = new EmployeeByProduct();
                        ebp.setEmployee(employee);
                        ebp.setProduct(productMap.get(productName));
                        empProducts.add(ebp);
                    }
                }

                // 4. Liaison OrganizationalUnitMember
                String ouName = getString(fields, OU_COL);
                if (ouName != null && ouMap.containsKey(ouName)) {
                    String key = empIdentifier + "||" + ouName;
                    if (seenOuMember.add(key)) {
                        OrganizationalUnitMember oum = new OrganizationalUnitMember();
                        oum.setEmployee(employee);
                        oum.setOrganizationalUnit(ouMap.get(ouName));

                        oum.setStartDate(parseDate(getString(fields, OU_START_DATE_COL)));
                        oum.setEndDate(parseDate(getString(fields, OU_END_DATE_COL)));

                        ouMembers.add(oum);
                    }
                }
            }
        }

        if (!companyMembers.isEmpty()) companyMemberRepository.saveAll(companyMembers);
        if (!empNatures.isEmpty()) empNatureRepository.saveAll(empNatures);
        if (!empProducts.isEmpty()) empProductRepository.saveAll(empProducts);
        if (!ouMembers.isEmpty()) ouMemberRepository.saveAll(ouMembers);

        long endTime = System.currentTimeMillis();
        System.out.println("⚡ [Niveau 1 - Liens Employés] Terminé en " + (endTime - startTime) + " ms !");
        System.out.println("   -> Company Members insérés : " + companyMembers.size());
        System.out.println("   -> Employee Natures insérées : " + empNatures.size());
        System.out.println("   -> Employee Products insérés : " + empProducts.size());
        System.out.println("   -> OU Members insérés : " + ouMembers.size());
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), dateFormatter);
        } catch (Exception e) {
            return null;
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ',' && !inQuotes) { fields.add(sb.toString()); sb.setLength(0); }
            else sb.append(c);
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    private String getString(String[] fields, int col) {
        if (col >= fields.length || col < 0) return null;
        String v = fields[col].trim().replaceAll("^\"|\"$", "").trim();
        return v.isEmpty() ? null : v;
    }
}