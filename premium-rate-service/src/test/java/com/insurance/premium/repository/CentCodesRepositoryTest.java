package com.insurance.premium.repository;

import com.insurance.premium.domain.CentCodesRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CentCodesRepositoryTest {

    @Autowired
    private CentCodesRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // Insert records in non-sorted order to verify query ordering
        saveRecord("150110US99999999", (short) 150, 99999999, 30000);
        saveRecord("150110AU99999999", (short) 150, 99999999, 25000);
        saveRecord("150110AU20150331", (short) 150, 20150331, 25000);
        saveRecord("200110AU99999999", (short) 200, 99999999, 40000);
    }

    private void saveRecord(String coverCodeSuper, short tableType, int dateKey, int premium) {
        CentCodesRecord record = new CentCodesRecord();
        record.setCoverCodeSuper(coverCodeSuper);
        record.setTableType(tableType);
        record.setDateKey(dateKey);
        record.setPremium(premium);
        repository.save(record);
    }

    @Test
    void shouldOrderByCoverCodeSuper() {
        List<CentCodesRecord> results = repository
                .findByCoverCodeSuperStartingWithAndTableType("150110", (short) 150);

        assertEquals(3, results.size());
        assertTrue(results.get(0).getCoverCodeSuper().compareTo(results.get(1).getCoverCodeSuper()) <= 0,
                "Results should be ordered by cover_code_super");
    }

    @Test
    void shouldFilterByTableType() {
        List<CentCodesRecord> results = repository
                .findByCoverCodeSuperStartingWithAndTableType("150110", (short) 150);

        for (CentCodesRecord record : results) {
            assertEquals((short) 150, record.getTableType());
        }
    }

    @Test
    void shouldFilterByDateKey() {
        List<CentCodesRecord> results = repository
                .findByCoverCodeSuperStartingWithAndTableTypeAndDateKey("150110", (short) 150, 99999999);

        assertEquals(2, results.size());
        for (CentCodesRecord record : results) {
            assertEquals(99999999, record.getDateKey());
        }
    }
}
