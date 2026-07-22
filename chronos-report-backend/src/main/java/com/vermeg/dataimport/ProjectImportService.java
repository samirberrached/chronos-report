package com.vermeg.dataimport;

import com.vermeg.entity.billingentity.BillingEntity;
import com.vermeg.entity.billingentity.BillingEntityRepository;
import com.vermeg.entity.client.Client;
import com.vermeg.entity.client.ClientRepository;
import com.vermeg.entity.project.Project;
import com.vermeg.entity.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;

@Service
public class ProjectImportService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private BillingEntityRepository billingEntityRepository;

    private static final String FILE_PATH =
            "C:\\Users\\berra\\OneDrive\\Bureau\\chronos-report\\data-cleaning\\employee_time_clean_cleaned.csv";

    @Transactional
    public void cleanAndImportLevel1() throws IOException {
        System.out.println("🗑️ [Niveau 1] Nettoyage de la table Project...");
        jdbcTemplate.execute("TRUNCATE TABLE project CASCADE;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS project_id_seq RESTART WITH 1;");

        System.out.println("🧠 Chargement des dictionnaires (Clients & Billing Entities) en mémoire...");

        // Indexation des clients par nom
        Map<String, Client> clientMap = new HashMap<>();
        clientRepository.findAll().forEach(c -> {
            if (c.getName() != null) clientMap.put(c.getName().trim(), c);
        });

        // Indexation des entités de facturation par nom
        Map<String, BillingEntity> billingMap = new HashMap<>();
        billingEntityRepository.findAll().forEach(b -> {
            if (b.getName() != null) billingMap.put(b.getName().trim(), b);
        });

        System.out.println("📦 [Niveau 1] Extraction et liaison des Projets avec Manager (String)...");

        Map<String, Project> uniqueProjects = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH), 1024 * 1024)) {
            String line;
            boolean isHeader = true;

            // Ajuste ces index selon la structure exacte de ton CSV
            final int CLIENT_NAME_COL = 3;
            final int BILLING_ENTITY_NAME_COL = 4;
            final int PROJECT_NAME_COL = 5;
            final int PROJECT_MANAGER_COL = 6; // ⚠️ Remplace par l'index réel du manager dans ton CSV si ce n'est pas 6 !

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) { isHeader = false; continue; }

                String[] fields = parseCsvLine(line);

                String projectName = getString(fields, PROJECT_NAME_COL);
                String clientName = getString(fields, CLIENT_NAME_COL);
                String billingName = getString(fields, BILLING_ENTITY_NAME_COL);
                String managerName = getString(fields, PROJECT_MANAGER_COL); // Récupération en String

                if (projectName != null && !uniqueProjects.containsKey(projectName)) {
                    Project project = new Project();
                    project.setName(projectName);

                    // 🌟 Assignation du manager en tant que simple String
                    if (managerName != null) {
                        project.setProjectManager(managerName); // Assure-toi que le setter accepte un String dans Project.java
                    }

                    // Liaison 1 : Le Client
                    if (clientName != null && clientMap.containsKey(clientName)) {
                        project.setClient(clientMap.get(clientName));
                    }

                    // Liaison 2 : La Billing Entity
                    if (billingName != null && billingMap.containsKey(billingName)) {
                        project.setBillingEntity(billingMap.get(billingName));
                    }

                    uniqueProjects.put(projectName, project);
                }
            }
        }

        if (!uniqueProjects.isEmpty()) {
            projectRepository.saveAll(uniqueProjects.values());
            System.out.println("✅ [Niveau 1] " + uniqueProjects.size() + " Projets insérés (avec leur Project Manager) !");
        }
    }

    // UTILS
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