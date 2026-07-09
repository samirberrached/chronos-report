package com.vermeg.controller;

import com.vermeg.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DataImportController {

    @Autowired
    private DataImportService dataImportService;

    @PostMapping("/import")
    public ResponseEntity<String> importData() {
        try {
            dataImportService.importData();
            return ResponseEntity.ok("✅ Import completed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Import failed: " + e.getMessage());
        }
    }
}