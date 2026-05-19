package com.insurance.reporting.batch;

import com.insurance.reporting.dto.ReportLine;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.core.io.FileSystemResource;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Produces tilde-delimited output matching line 138-140 of Reporting Mode program.
 * Format: BCH~AGT~POL~INSPECT_NO~AG_NAME~EDATE~CASH_DATE~TYPE~EM_PREM~EM_COMM~EM_CASH~TYPE2
 */
public class TildeDelimitedWriter extends FlatFileItemWriter<ReportLine> {

    private final AtomicReference<BigDecimal> totalDeb = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> totalComm = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> totalCash = new AtomicReference<>(BigDecimal.ZERO);

    public TildeDelimitedWriter(String outputPath) {
        setName("tildeDelimitedWriter");
        setResource(new FileSystemResource(outputPath));
        setLineAggregator(createLineAggregator());
    }

    private LineAggregator<ReportLine> createLineAggregator() {
        return item -> String.join("~",
                nullSafe(item.branch()),
                nullSafe(item.agent()),
                nullSafe(item.policy()),
                item.inspectNo() != null ? String.valueOf(item.inspectNo()) : "",
                nullSafe(item.agentName()),
                String.valueOf(item.entryDate()),
                String.valueOf(item.cashDate()),
                nullSafe(item.type()),
                nullSafe(item.emPrem()),
                nullSafe(item.emComm()),
                nullSafe(item.emCash()),
                nullSafe(item.type2())
        );
    }

    @Override
    public void write(Chunk<? extends ReportLine> items) throws Exception {
        for (ReportLine item : items) {
            accumulateTotals(item);
        }
        super.write(items);
    }

    private void accumulateTotals(ReportLine item) {
        totalDeb.updateAndGet(v -> v.add(item.rawDeb() != null ? item.rawDeb() : BigDecimal.ZERO));
        totalComm.updateAndGet(v -> v.add(item.rawComm() != null ? item.rawComm() : BigDecimal.ZERO));
        totalCash.updateAndGet(v -> v.add(item.rawCash() != null ? item.rawCash() : BigDecimal.ZERO));
    }

    public BigDecimal getTotalDeb() { return totalDeb.get(); }
    public BigDecimal getTotalComm() { return totalComm.get(); }
    public BigDecimal getTotalCash() { return totalCash.get(); }

    private static String nullSafe(String value) {
        return value != null ? value : "";
    }
}
