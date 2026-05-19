package com.insurance.reporting.dto;

import java.math.BigDecimal;

public record ReportLine(
        String branch,
        String agent,
        String policy,
        Short inspectNo,
        String agentName,
        int entryDate,
        int cashDate,
        String type,
        String emPrem,
        String emComm,
        String emCash,
        String type2,
        BigDecimal rawDeb,
        BigDecimal rawComm,
        BigDecimal rawCash
) {
}
