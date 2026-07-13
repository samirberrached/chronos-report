package com.vermeg.dataimport;

import com.vermeg.entity.accountingcode.AccountingCode;
import com.vermeg.entity.accountingcode.AccountingCodeRepository;
import com.vermeg.entity.activity.Activity;
import com.vermeg.entity.activity.ActivityRepository;
import com.vermeg.entity.activitynature.ActivityNature;
import com.vermeg.entity.activitynature.ActivityNatureRepository;
import com.vermeg.entity.iteration.Iteration;
import com.vermeg.entity.iteration.IterationRepository;
import com.vermeg.entity.lot.Lot;
import com.vermeg.entity.lot.LotRepository;
import com.vermeg.entity.organizationalunit.OrganizationalUnit;
import com.vermeg.entity.organizationalunit.OrganizationalUnitRepository;
import com.vermeg.entity.phase.Phase;
import com.vermeg.entity.phase.PhaseRepository;
import com.vermeg.entity.product.Product;
import com.vermeg.entity.product.ProductRepository;
import com.vermeg.entity.project.Project;
import com.vermeg.entity.project.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProjectCascadeImportService {

    @Autowired private JdbcTemplate jdbcTemplate;

    @Autowired private ProjectRepository projectRepository;
    @Autowired private OrganizationalUnitRepository ouRepository;
    @Autowired private ActivityNatureRepository activityNatureRepository;
    @Autowired private ProductRepository productRepository;

    @Autowired private LotRepository lotRepository;
    @Autowired private IterationRepository iterationRepository;
    @Autowired private PhaseRepository phaseRepository;
    @Autowired private AccountingCodeRepository accountingCodeRepository;
    @Autowired private ActivityRepository activityRepository;

    private static final String FILE_PATH =
            "C:\\Users\\berra\\OneDrive\\Bureau\\chronos-report\\data-cleaning\\employee_time_clean.csv";

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional
    public void importProjectCascade() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("🗑️ [Niveau 2 - Cascade Projet] Nettoyage et remise à zéro des IDs...");

        jdbcTemplate.execute("TRUNCATE TABLE activity, phase, accounting_code, iteration, lot CASCADE;");

        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS activity_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS phase_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS accounting_code_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS iteration_id_seq RESTART WITH 1;");
        jdbcTemplate.execute("ALTER SEQUENCE IF EXISTS lot_id_seq RESTART WITH 1;");

        System.out.println("🧠 Chargement des dépendances Niveau 0 en mémoire...");

        Map<String, Project> projectMap = new HashMap<>();
        projectRepository.findAll().forEach(p -> { if (p.getName() != null) projectMap.put(p.getName().trim(), p); });

        Map<String, OrganizationalUnit> ouMap = new HashMap<>();
        ouRepository.findAll().forEach(ou -> { if (ou.getName() != null) ouMap.put(ou.getName().trim(), ou); });

        Map<String, ActivityNature> natureMap = new HashMap<>();
        activityNatureRepository.findAll().forEach(n -> { if (n.getName() != null) natureMap.put(n.getName().trim(), n); });

        Map<String, Product> productMap = new HashMap<>();
        productRepository.findAll().forEach(p -> { if (p.getName() != null) productMap.put(p.getName().trim(), p); });

        Map<String, Lot> lotMap = new LinkedHashMap<>();
        Map<String, Iteration> iterationMap = new LinkedHashMap<>();
        Map<String, AccountingCode> accCodeMap = new LinkedHashMap<>();
        Map<String, Phase> phaseMap = new LinkedHashMap<>();
        Map<String, Activity> activityMap = new LinkedHashMap<>();

        System.out.println("📦 Extraction et création de l'arbre du Projet...");

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH), 1024 * 1024)) {
            String line;
            boolean isHeader = true;

            final int PRODUCT_NAME_COL = 2;
            final int OU_NAME_COL = 1;
            final int PROJECT_NAME_COL = 5;
            final int LOT_NAME_COL = 7;
            final int ITERATION_NAME_COL = 8;
            final int PHASE_NAME_COL = 9;
            final int ACTIVITY_NAME_COL = 11;
            final int ACC_OP_ID_COL = 22;
            final int ACC_NATURE_COL = 23;
            final int ACC_BILLING_MODE_COL = 24;
            final int BILLABLE_COL = 25;
            final int DELIVERABLE_COL = 38;
            final int IS_CAPITALIZABLE_COL = 39;
            final int CAPITALIZABLE_DATE_COL = 40;
            final int IS_CAPITALIZABLE_BY_COL = 41;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) { isHeader = false; continue; }

                String[] fields = parseCsvLine(line);

                // 1. LOT
                String projectName = getString(fields, PROJECT_NAME_COL);
                String lotName = getString(fields, LOT_NAME_COL);
                Lot currentLot = null;

                if (lotName != null && projectName != null && projectMap.containsKey(projectName)) {
                    String lotKey = projectName + "||" + lotName;
                    if (!lotMap.containsKey(lotKey)) {
                        Lot lot = new Lot();
                        lot.setName(lotName);
                        lot.setProject(projectMap.get(projectName));
                        lotMap.put(lotKey, lot);
                    }
                    currentLot = lotMap.get(lotKey);
                }

                // 2. ITERATION
                String iterationName = getString(fields, ITERATION_NAME_COL);
                Iteration currentIteration = null;

                if (iterationName != null && currentLot != null) {
                    String iterKey = currentLot.getProject().getName() + "||" + currentLot.getName() + "||" + iterationName;
                    if (!iterationMap.containsKey(iterKey)) {
                        Iteration iter = new Iteration();
                        iter.setName(iterationName);
                        iter.setLot(currentLot);
                        iterationMap.put(iterKey, iter);
                    }
                    currentIteration = iterationMap.get(iterKey);
                }

                // 3. ACCOUNTING CODE
                String accOpIdentifier = getString(fields, ACC_OP_ID_COL);
                String ouName = getString(fields, OU_NAME_COL);
                String natureName = getString(fields, ACC_NATURE_COL);
                String productName = getString(fields, PRODUCT_NAME_COL);
                AccountingCode currentAccCode = null;

                if (accOpIdentifier != null) {
                    if (!accCodeMap.containsKey(accOpIdentifier)) {
                        AccountingCode ac = new AccountingCode();
                        ac.setOperationalIdentifier(accOpIdentifier);
                        ac.setBillingMode(getString(fields, ACC_BILLING_MODE_COL));

                        String billStr = getString(fields, BILLABLE_COL);
                        ac.setBillable(billStr != null && (billStr.equalsIgnoreCase("true") || billStr.equals("1")));

                        if (ouName != null && ouMap.containsKey(ouName)) ac.setOrganizationalUnit(ouMap.get(ouName));
                        if (natureName != null && natureMap.containsKey(natureName)) ac.setActivityNature(natureMap.get(natureName));

                        accCodeMap.put(accOpIdentifier, ac);
                    }

                    currentAccCode = accCodeMap.get(accOpIdentifier);

                    // ✅ RATTRAPAGE PRODUCT
                    if (currentAccCode.getProduct() == null && productName != null && productMap.containsKey(productName)) {
                        currentAccCode.setProduct(productMap.get(productName));
                    }
                }

                // 4. PHASE
                String phaseName = getString(fields, PHASE_NAME_COL);
                Phase currentPhase = null;

                if (phaseName != null && currentIteration != null) {
                    String phaseKey = currentIteration.getLot().getProject().getName() + "||" + phaseName;
                    if (!phaseMap.containsKey(phaseKey)) {
                        Phase phase = new Phase();
                        phase.setName(phaseName);
                        phase.setIteration(currentIteration);
                        phase.setDelivrableName(getString(fields, DELIVERABLE_COL));

                        String isCapStr = getString(fields, IS_CAPITALIZABLE_COL);
                        phase.setIsCapitalizable(isCapStr != null && (isCapStr.equalsIgnoreCase("true") || isCapStr.equals("1")));
                        phase.setCapitalizableDate(parseDate(getString(fields, CAPITALIZABLE_DATE_COL)));
                        phase.setIsCapitalizableBy(getString(fields, IS_CAPITALIZABLE_BY_COL));

                        if (currentAccCode != null) {
                            phase.setAccountingCode(currentAccCode);
                        }

                        phaseMap.put(phaseKey, phase);
                    }
                    currentPhase = phaseMap.get(phaseKey);
                }

                // 5. ACTIVITY
                String activityName = getString(fields, ACTIVITY_NAME_COL);
                if (activityName != null && currentPhase != null) {
                    String actKey = currentPhase.getName() + "||" + activityName;
                    if (!activityMap.containsKey(actKey)) {
                        Activity act = new Activity();
                        act.setName(activityName);
                        act.setPhase(currentPhase);
                        activityMap.put(actKey, act);
                    }
                }
            }
        }

        System.out.println("💾 Sauvegarde ordonnée des cascades dans PostgreSQL...");

        // ✅ Sécurisation de toutes les collections en ArrayList explicites pour lever toute ambiguïté de type (Generics)
        if (!lotMap.isEmpty()) lotRepository.saveAll(new ArrayList<>(lotMap.values()));
        if (!iterationMap.isEmpty()) iterationRepository.saveAll(new ArrayList<>(iterationMap.values()));
        if (!accCodeMap.isEmpty()) accountingCodeRepository.saveAll(new ArrayList<>(accCodeMap.values()));
        if (!phaseMap.isEmpty()) phaseRepository.saveAll(new ArrayList<>(phaseMap.values()));
        if (!activityMap.isEmpty()) activityRepository.saveAll(new ArrayList<>(activityMap.values()));

        long endTime = System.currentTimeMillis();
        System.out.println("⚡ [Niveau 2] Terminé avec succès en " + (endTime - startTime) + " ms !");
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equalsIgnoreCase("null")) return null;
        try { return LocalDate.parse(dateStr.trim(), dateFormatter); } catch (Exception e) { return null; }
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