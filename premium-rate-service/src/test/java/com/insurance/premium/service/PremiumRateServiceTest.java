package com.insurance.premium.service;

import com.insurance.premium.domain.CentCodesRecord;
import com.insurance.premium.repository.CentCodesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumRateServiceTest {

    @Mock
    private CentCodesRepository repository;

    private PremiumRateService service;

    @BeforeEach
    void setUp() {
        service = new PremiumRateService(repository);
    }

    @Test
    void expireRecordShouldSetDateKey() {
        // T-PRM-003: expireRecord sets dateKey from 99999999 to 20150331
        CentCodesRecord record = new CentCodesRecord();
        record.setDateKey(99999999);
        record.setTableType((short) 150);

        when(repository.save(any(CentCodesRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        CentCodesRecord result = service.expireRecord(record, 20150331);

        assertEquals(20150331, result.getDateKey());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void createNewActiveRecordShouldCopyMetadataAndSetNewValues() {
        // T-PRM-004: createNewActiveRecord copies metadata, sets premium=26667, dateKey=99999999
        CentCodesRecord expired = new CentCodesRecord();
        expired.setTableType((short) 150);
        expired.setDateKey(20150331);
        expired.setCovGrpKey("GRP001");
        expired.setInvType("INV01");
        expired.setCoverCodeSuper("150110AU20150331");

        when(repository.save(any(CentCodesRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        CentCodesRecord result = service.createNewActiveRecord(expired, 26667);

        assertEquals((short) 150, result.getTableType());
        assertEquals(99999999, result.getDateKey());
        assertEquals("GRP001", result.getCovGrpKey());
        assertEquals("INV01", result.getInvType());
        assertEquals(26667, result.getTpPremium1());
        assertEquals(26667, result.getPremium());
        assertTrue(result.getCoverCodeSuper().endsWith("99999999"));
    }
}
