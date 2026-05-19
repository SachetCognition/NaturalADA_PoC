package com.insurance.reporting.dto;

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
        String type2
) {
}
