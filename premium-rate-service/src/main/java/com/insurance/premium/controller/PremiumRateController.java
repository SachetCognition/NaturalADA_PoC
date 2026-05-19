package com.insurance.premium.controller;

import com.insurance.premium.domain.CentCodesRecord;
import com.insurance.premium.service.PremiumRateService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/premium-rates")
public class PremiumRateController {

    private final PremiumRateService premiumRateService;
    private final JobLauncher jobLauncher;
    private final Job premiumRateUpdateJob;

    public PremiumRateController(PremiumRateService premiumRateService,
                                  JobLauncher jobLauncher,
                                  Job premiumRateUpdateJob) {
        this.premiumRateService = premiumRateService;
        this.jobLauncher = jobLauncher;
        this.premiumRateUpdateJob = premiumRateUpdateJob;
    }

    @PostMapping("/batch/run")
    public ResponseEntity<Map<String, String>> runBatch() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(premiumRateUpdateJob, params);
            return ResponseEntity.ok(Map.of("status", "STARTED"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "FAILED", "error", e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }

    @GetMapping
    public ResponseEntity<List<CentCodesRecord>> queryRates(
            @RequestParam String coverCodeSuper,
            @RequestParam Short tableType) {
        return ResponseEntity.ok(
                premiumRateService.findByPrefixAndTableType(coverCodeSuper, tableType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CentCodesRecord> getRate(@PathVariable Long id) {
        CentCodesRecord record = premiumRateService.findById(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }
}
