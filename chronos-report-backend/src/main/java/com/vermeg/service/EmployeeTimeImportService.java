package com.vermeg.service;

import com.vermeg.model.*;
import com.vermeg.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmployeeTimeImportService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ActivityRepository activityRepository;
    @Autowired private OrganizationalUnitRepository ouRepository;

    private static final String FILE_PATH =
            "C:\\Users\\berra\\OneDrive\\Bureau\\chronos-report\\data-cleaning\\employee_time_clean.csv";

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Transactional
    public void importEmployeeTime() throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("🗑️ [Niveau 3] Vidage de la table employee_time...");
        jdbcTemplate.execute("TRUNCATE TABLE employee_time CASCADE;");

        System.out.println("🧠 Chargement des référentiels en mémoire...");

        // 1. Map Employés (indexé par employee_identifier)
        Map<String, Employee> employeeMap = new HashMap<>();
        employeeRepository.findAll().forEach(e -> {
            if (e.getIdentifier() != null) employeeMap.put(e.getIdentifier().trim(), e);
        });

        // 2. Map Unités Organisationnelles (indexé par organizational_unit_name)
        Map<String, OrganizationalUnit> ouMap = new HashMap<>();
        ouRepository.findAll().forEach(ou -> {
            if (ou.getName() != null) ouMap.put(ou.getName().trim(), ou);
        });

        // 3. Map Activités (Clé composite Phase + Nom)
        Map<String, Activity> activityMap = new HashMap<>();
        activityRepository.findAll().forEach(act -> {
            if (act.getPhase() != null && act.getPhase().getName() != null && act.getName() != null) {
                String key = act.getPhase().getName().trim() + "||" + act.getName().trim();
                activityMap.put(key, act);
            }
        });

        List<EmployeeTime> batchList = new ArrayList<>();
        System.out.println("📦 Traitement du fichier CSV avec les index réalignés...");

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH), 1024 * 1024)) {
            String line;
            boolean isHeader = true;

            // 🎯 INDEX 100% ALIGNÉS SUR TON HEADER CSV
            final int TIME_ID_COL = 0;
            final int OU_NAME_COL = 1;
            final int PHASE_NAME_COL = 9;
            final int ACTIVITY_NAME_COL = 11;
            final int MATRICULE_COL = 12; // employee_identifier
            final int WORK_DATE_COL = 26; // day
            final int ELAPSED_TIME_COL = 27;
            final int MAN_DAY_COL = 28;
            final int SITE_COL = 29;
            final int PRICE_INCREASE_REASON_COL = 30;
            final int COMMENT_COL = 31;
            final int CREATION_DATE_COL = 32;
            final int CREATOR_USER_ID_COL = 33;
            final int UPDATE_DATE_COL = 34;
            final int UPDATOR_USER_ID_COL = 35;
            final int STATUS_COL = 36;    // employee_time_status
            final int VALIDATOR_ID_COL = 37;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) { isHeader = false; continue; }

                String[] fields = parseCsvLine(line);

                String idStr = getString(fields, TIME_ID_COL);
                if (idStr == null) continue;

                EmployeeTime et = new EmployeeTime();
                et.setId(Long.parseLong(idStr));

                // Mappings direct avec les nouveaux index vérifiés
                et.setDate(parseDate(getString(fields, WORK_DATE_COL)));
                et.setElapsedTime(parseDouble(getString(fields, ELAPSED_TIME_COL)));
                et.setManDay(parseDouble(getString(fields, MAN_DAY_COL)));
                et.setSite(getString(fields, SITE_COL));
                et.setStatus(getString(fields, STATUS_COL));
                et.setValidatorId(getString(fields, VALIDATOR_ID_COL));
                et.setComment(getString(fields, COMMENT_COL));
                et.setPriceIncreaseReason(getString(fields, PRICE_INCREASE_REASON_COL));
                et.setCreationDate(parseDateTime(getString(fields, CREATION_DATE_COL)));
                et.setCreatorUserId(getString(fields, CREATOR_USER_ID_COL));
                et.setUpdateDate(parseDateTime(getString(fields, UPDATE_DATE_COL)));
                et.setUpdatorUserId(getString(fields, UPDATOR_USER_ID_COL));

                // Liaisons Clés Étrangères
                String matricule = getString(fields, MATRICULE_COL);
                if (matricule != null && employeeMap.containsKey(matricule)) {
                    et.setEmployee(employeeMap.get(matricule));
                }

                String ouName = getString(fields, OU_NAME_COL);
                if (ouName != null && ouMap.containsKey(ouName)) {
                    et.setOrganizationalUnit(ouMap.get(ouName));
                }

                String phaseName = getString(fields, PHASE_NAME_COL);
                String activityName = getString(fields, ACTIVITY_NAME_COL);
                if (phaseName != null && activityName != null) {
                    String actKey = phaseName + "||" + activityName;
                    if (activityMap.containsKey(actKey)) {
                        et.setActivity(activityMap.get(actKey));
                    }
                }

                batchList.add(et);

                if (batchList.size() >= 2000) {
                    executeJdbcBatch(batchList);
                    batchList.clear();
                }
            }

            if (!batchList.isEmpty()) {
                executeJdbcBatch(batchList);
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("⚡ [Niveau 3] Importation finie sans décalage en " + (endTime - startTime) + " ms !");
    }

    private void executeJdbcBatch(List<EmployeeTime> entries) {
        String sql = "INSERT INTO employee_time (id, date, elapsed_time, man_day, site, status, validator_id, comment, price_increase_reason, creation_date, creator_user_id, update_date, updator_user_id, employee_id, organizational_unit_id, activity_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new org.springframework.jdbc.core.BatchPreparedStatementSetter() {
            @Override
            public void setValues(java.sql.PreparedStatement ps, int i) throws java.sql.SQLException {
                EmployeeTime et = entries.get(i);
                ps.setLong(1, et.getId());
                ps.setDate(2, et.getDate() != null ? java.sql.Date.valueOf(et.getDate()) : null);

                if (et.getElapsedTime() != null) ps.setDouble(3, et.getElapsedTime()); else ps.setNull(3, java.sql.Types.DOUBLE);
                if (et.getManDay() != null) ps.setDouble(4, et.getManDay()); else ps.setNull(4, java.sql.Types.DOUBLE);

                ps.setString(5, et.getSite());
                ps.setString(6, et.getStatus());
                ps.setString(7, et.getValidatorId());
                ps.setString(8, et.getComment());
                ps.setString(9, et.getPriceIncreaseReason());

                ps.setTimestamp(10, et.getCreationDate() != null ? java.sql.Timestamp.valueOf(et.getCreationDate()) : null);
                ps.setString(11, et.getCreatorUserId());
                ps.setTimestamp(12, et.getUpdateDate() != null ? java.sql.Timestamp.valueOf(et.getUpdateDate()) : null);
                ps.setString(13, et.getUpdatorUserId());

                if (et.getEmployee() != null) ps.setLong(14, et.getEmployee().getId()); else ps.setNull(14, java.sql.Types.BIGINT);
                if (et.getOrganizationalUnit() != null) ps.setLong(15, et.getOrganizationalUnit().getId()); else ps.setNull(15, java.sql.Types.BIGINT);
                if (et.getActivity() != null) ps.setLong(16, et.getActivity().getId()); else ps.setNull(16, java.sql.Types.BIGINT);
            }

            @Override
            public int getBatchSize() { return entries.size(); }
        });
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equalsIgnoreCase("null")) return null;
        try { return LocalDate.parse(dateStr.trim(), dateFormatter); } catch (Exception e) { return null; }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty() || dateTimeStr.equalsIgnoreCase("null")) return null;
        try {
            if (dateTimeStr.trim().length() <= 10) {
                return LocalDate.parse(dateTimeStr.trim(), dateFormatter).atStartOfDay();
            }
            return LocalDateTime.parse(dateTimeStr.trim(), dateTimeFormatter);
        } catch (Exception e) { return null; }
    }

    private Double parseDouble(String doubleStr) {
        if (doubleStr == null || doubleStr.trim().isEmpty() || doubleStr.equalsIgnoreCase("null")) return null;
        try { return Double.parseDouble(doubleStr.trim()); } catch (Exception e) { return null; }
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