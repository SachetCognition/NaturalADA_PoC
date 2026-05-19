package com.insurance.premium.service;

import com.insurance.premium.domain.CentCodesRecord;
import com.insurance.premium.repository.CentCodesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PremiumRateService {

    private final CentCodesRepository repository;

    public PremiumRateService(CentCodesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CentCodesRecord expireRecord(CentCodesRecord record, int newDateKey) {
        record.setDateKey(newDateKey);
        record.setUpdatedAt(LocalDateTime.now());
        return repository.save(record);
    }

    @Transactional
    public CentCodesRecord createNewActiveRecord(CentCodesRecord expiredRecord, int newPremium) {
        CentCodesRecord newRecord = new CentCodesRecord();
        newRecord.setTableType(expiredRecord.getTableType());
        newRecord.setDateKey(99999999);
        newRecord.setCovGrpKey(expiredRecord.getCovGrpKey());
        newRecord.setInvType(expiredRecord.getInvType());
        newRecord.setCoverCodeSuper(expiredRecord.getCoverCodeSuper().substring(0, 8) + "99999999");
        newRecord.setTpPremium1(newPremium);
        newRecord.setPremium(newPremium);
        newRecord.setCreatedAt(LocalDateTime.now());
        newRecord.setUpdatedAt(LocalDateTime.now());
        return repository.save(newRecord);
    }

    public List<CentCodesRecord> findByPrefixAndTableType(String prefix, Short tableType) {
        return repository.findByCoverCodeSuperStartingWithAndTableType(prefix, tableType);
    }

    public CentCodesRecord findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
