package com.insurance.reporting.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports/accounts")
public class ReportController {

    private final JobLauncher jobLauncher;
    private final Job accountsReportJob;
    private final JobExplorer jobExplorer;

    public ReportController(JobLauncher jobLauncher, Job accountsReportJob, JobExplorer jobExplorer) {
        this.jobLauncher = jobLauncher;
        this.accountsReportJob = accountsReportJob;
        this.jobExplorer = jobExplorer;
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runReport() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(accountsReportJob, params);
            return ResponseEntity.ok(Map.of(
                    "status", execution.getStatus().toString(),
                    "jobId", execution.getJobId()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "FAILED", "error", e.getMessage()));
        }
    }

    @GetMapping("/{jobId}/status")
    public ResponseEntity<Map<String, String>> getJobStatus(@PathVariable Long jobId) {
        JobExecution execution = jobExplorer.getJobExecution(jobId);
        if (execution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("status", execution.getStatus().toString()));
    }

    @GetMapping("/{jobId}/output")
    public ResponseEntity<Resource> downloadOutput(@PathVariable Long jobId) {
        Resource resource = new FileSystemResource("output/accounts-report.txt");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=accounts-report.txt")
                .body(resource);
    }
}
