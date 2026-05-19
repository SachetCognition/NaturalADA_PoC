package com.insurance.reporting.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * After job completes, logs totals matching lines 148-151 of Reporting Mode.
 */
public class ReportTotalsListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(ReportTotalsListener.class);

    private final TildeDelimitedWriter writer;

    public ReportTotalsListener(TildeDelimitedWriter writer) {
        this.writer = writer;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("========================================");
        log.info("TOTALS");
        log.info("TOTAL DEB-CRED-AMT = {}", writer.getTotalDeb());
        log.info("TOTAL COMM-AMOUNT  = {}", writer.getTotalComm());
        log.info("TOTAL CASH-AMOUNT  = {}", writer.getTotalCash());
        log.info("========================================");

        // Store totals in execution context for downstream access
        jobExecution.getExecutionContext().putString("totalDeb", writer.getTotalDeb().toPlainString());
        jobExecution.getExecutionContext().putString("totalComm", writer.getTotalComm().toPlainString());
        jobExecution.getExecutionContext().putString("totalCash", writer.getTotalCash().toPlainString());
    }
}
