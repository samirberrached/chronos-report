package com.vermeg.dataimport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataImportService {

    @Autowired private DictionaryImportService dictionaryImportService;
    @Autowired private ProjectImportService projectImportService;
    @Autowired private EmployeeStructureImportService employeeStructureImportService;

    // ✅ Correction ici (camelCase)
    @Autowired private ProjectCascadeImportService projectCascadeImportService;
    @Autowired private EmployeeTimeImportService employeeTimeImportService;
    public void importData() {
        System.out.println("🏁 Début de l'importation séquentielle globale...");
        try {

            // 📦 NIVEAU 0 : Les dictionnaires indépendants
            // dictionaryImportService.cleanAndImportLevel0();

            // 📦 NIVEAU 1 : Les tables dépendantes
            //projectImportService.cleanAndImportLevel1();

            //employeeStructureImportService.importEmployeeStructures();

            System.out.println("🎉 [Fin du Niveau 1] Tout est OK !");

            // ✅ Appel correct
            //projectCascadeImportService.importProjectCascade();
                employeeTimeImportService.importEmployeeTime();
        } catch (Exception e) {
            System.err.println("❌ Erreur générale pendant l'import : " + e.getMessage());
            e.printStackTrace();
        }
    }
}