package com.vermeg.service;

import com.vermeg.model.*;
import com.vermeg.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

@Service
public class DictionaryImportService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private ClientRepository clientRepository;
    @Autowired private BillingEntityRepository billingEntityRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ActivityNatureRepository activityNatureRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private OrganizationalUnitRepository organizationalUnitRepository;
    @Autowired private EmployeeRepository employeeRepository;

    private static final String FILE_PATH =
            "C:\\Users\\berra\\OneDrive\\Bureau\\chronos-report\\data-cleaning\\employee_time_clean.csv";

    @Transactional
    public void cleanAndImportLevel0() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("🗑️ [Niveau 0] Nettoyage complet des 7 dictionnaires...");

        // Vidage de force en cascade
        jdbcTemplate.execute("TRUNCATE TABLE client CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE billing_entity CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE product CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE activity_nature CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE company CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE organizational_unit CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE employee CASCADE;");

        // Reset des séquences
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS client_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS billing_entity_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS product_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS activity_nature_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS company_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS organizational_unit_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS employee_id_seq RESTART WITH 1;");

        System.out.println("📦 [Niveau 0] Extraction optimisée en une seule passe du CSV...");

        // Structures temporaires à haute performance (HashSets & HashMaps)
        Set<String> clientNames = new HashSet<>();
        Set<String> billingNames = new HashSet<>();
        Set<String> productNames = new HashSet<>();
        Set<String> natureNames = new HashSet<>();
        Set<String> companyNames = new HashSet<>();

        // Pour les unités organisationnelles
        Set<String> rawOrgUnitNames = new HashSet<>();

        // Pour les employés
        Map<String, Employee> employeeMap = new HashMap<>();

        // 1. LECTURE DU CSV (Passe unique)
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH), 1024 * 1024)) { // Buffer de 1Mo pour booster les I/O
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }

                String[] fields = parseCsvLine(line);

                String productName = getString(fields, 2);
                String clientName = getString(fields, 3);
                String billingName = getString(fields, 4);
                String empId = getString(fields, 12);
                String empFirstName = getString(fields, 13);
                String empLastName = getString(fields, 14);
                String companyName = getString(fields, 15);
                String orgUnitLine = getString(fields, 1);
                String orgUnitEmp = getString(fields, 19);
                String actNatureName = getString(fields, 23);

                if (productName != null) productNames.add(productName);
                if (clientName != null) clientNames.add(clientName);
                if (billingName != null) billingNames.add(billingName);
                if (companyName != null) companyNames.add(companyName);
                if (actNatureName != null) natureNames.add(actNatureName);

                // On collecte toutes les unités brutes (Ligne + Contrat)
                if (orgUnitLine != null) rawOrgUnitNames.add(orgUnitLine);
                if (orgUnitEmp != null) rawOrgUnitNames.add(orgUnitEmp);

                // Employés
                if (empId != null && !employeeMap.containsKey(empId)) {
                    Employee e = new Employee();
                    e.setIdentifier(empId);
                    e.setFirstName(empFirstName);
                    e.setLastName(empLastName);
                    employeeMap.put(empId, e);
                }
            }
        }

        // 2. INSERTIONS DIRECTES EN MASSE (Dictionnaires simples)
        System.out.println("💾 Insertions massives des dictionnaires indépendants...");

        List<Client> clients = clientNames.stream().map(n -> { Client c = new Client(); c.setName(n); return c; }).toList();
        clientRepository.saveAll(clients);

        List<BillingEntity> billings = billingNames.stream().map(n -> { BillingEntity b = new BillingEntity(); b.setName(n); return b; }).toList();
        billingEntityRepository.saveAll(billings);

        List<Product> products = productNames.stream().map(n -> { Product p = new Product(); p.setName(n); return p; }).toList();
        productRepository.saveAll(products);

        List<ActivityNature> natures = natureNames.stream().map(n -> { ActivityNature an = new ActivityNature(); an.setName(n); return an; }).toList();
        activityNatureRepository.saveAll(natures);

        List<Company> companies = companyNames.stream().map(n -> { Company c = new Company(); c.setName(n); return c; }).toList();
        companyRepository.saveAll(companies);

        employeeRepository.saveAll(employeeMap.values());

        // 3. LOGIQUE ALGORITHMIQUE OPTIMISÉE POUR ORGANIZATIONAL UNIT (Parent-Enfant)
        System.out.println("🌿 Traitement de l'auto-référence pour Organizational Units...");

        Map<String, OrganizationalUnit> insertedParents = new HashMap<>();
        List<OrganizationalUnit> childrenToInsert = new ArrayList<>();

        for (String rawName : rawOrgUnitNames) {
            // Extraction du premier mot comme étant le parent (ex: "MEA" depuis "MEA EUROPE")
            String[] parts = rawName.split("\\s+", 2);
            String parentName = parts[0];

            // Étape A : Créer ou obtenir l'entité parent (Racine)
            if (!insertedParents.containsKey(parentName)) {
                OrganizationalUnit parentUnit = new OrganizationalUnit();
                parentUnit.setName(parentName);
                parentUnit.setParent(null); // Les racines n'ont pas de parent

                // On sauvegarde le parent immédiatement pour générer son ID en BDD
                parentUnit = organizationalUnitRepository.save(parentUnit);
                insertedParents.put(parentName, parentUnit);
            }

            // Étape B : Si le nom brut possède un deuxième mot, c'est un fils (ex: "MEA EUROPE")
            if (parts.length > 1) {
                OrganizationalUnit childUnit = new OrganizationalUnit();
                childUnit.setName(rawName);
                childUnit.setParent(insertedParents.get(parentName)); // Liaison directe en mémoire !
                childrenToInsert.add(childUnit);
            }
        }

        // Étape C : Insertion de tous les fils en une seule fois
        if (!childrenToInsert.isEmpty()) {
            organizationalUnitRepository.saveAll(childrenToInsert);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("✅ [Niveau 0] Importation globale effectuée avec succès en " + (endTime - startTime) + " ms !");
        System.out.println("   -> Clients: " + clients.size());
        System.out.println("   -> Billing Entities: " + billings.size());
        System.out.println("   -> Products: " + products.size());
        System.out.println("   -> Natures: " + natures.size());
        System.out.println("   -> Companies: " + companies.size());
        System.out.println("   -> Employees: " + employeeMap.size());
        System.out.println("   -> Org Units (Racines): " + insertedParents.size() + " | (Fils): " + childrenToInsert.size());
    }

    // UTILS PARSER
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
        if (col >= fields.length) return null;
        String v = fields[col].trim().replaceAll("^\"|\"$", "");
        return v.isEmpty() ? null : v;
    }
}