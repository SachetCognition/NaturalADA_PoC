package com.insurance.reporting.batch;

import com.insurance.reporting.dto.ReportLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TildeDelimitedWriterTest {

    private TildeDelimitedWriter writer;
    private final Path outputPath = Path.of("output/test-report.txt");

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(outputPath.getParent());
        writer = new TildeDelimitedWriter(outputPath.toString());
        writer.open(new ExecutionContext());
    }

    @AfterEach
    void tearDown() throws IOException {
        writer.close();
        Files.deleteIfExists(outputPath);
    }

    @Test
    void shouldWriteTildeDelimitedOutput() throws Exception {
        // T-RPT-009: Verify output format matches BCH~AGT~POL~...~TYPE2
        ReportLine line = new ReportLine(
                "001", "ABC123", "POL000001",
                (short) 5, "Test Agent",
                20130601, 20130715,
                "ADDL", "   1234.56", "   -500.00", "      0.00",
                "UNMATCHED CASH"
        );

        writer.write(new Chunk<>(List.of(line)));
        writer.close();

        List<String> lines = Files.readAllLines(outputPath);
        assertFalse(lines.isEmpty());
        String output = lines.get(0);
        String[] fields = output.split("~");
        assertEquals(12, fields.length);
        assertEquals("001", fields[0]);
        assertEquals("ABC123", fields[1]);
        assertEquals("POL000001", fields[2]);
        assertEquals("5", fields[3]);
        assertEquals("Test Agent", fields[4]);
        assertEquals("UNMATCHED CASH", fields[11]);
    }
}
