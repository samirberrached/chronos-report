package com.vermeg.classification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportGenerationService reportGenerationService;

    @GetMapping("/reports")
    public ResponseEntity<?> generateReport(@RequestParam("monthPeriod") String monthPeriod) {
        try {
            return ResponseEntity.ok(reportGenerationService.generateReport(monthPeriod));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Report generation failed: " + e.getMessage());
        }
    }

    @GetMapping("/reports/download")
    public ResponseEntity<Resource> downloadReport(
            @RequestParam("monthPeriod") String monthPeriod,
            @RequestParam(value = "type", defaultValue = "report") String type) {
        Path file = reportGenerationService.resolveOutputFile(monthPeriod, type);
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }
}
