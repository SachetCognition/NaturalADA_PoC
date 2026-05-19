package com.insurance.premium.repository;

import com.insurance.premium.domain.CentCodesRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentCodesRepository extends JpaRepository<CentCodesRecord, Long> {

    @Query("SELECT c FROM CentCodesRecord c WHERE c.coverCodeSuper LIKE :prefix% AND c.tableType = :tableType ORDER BY c.coverCodeSuper")
    List<CentCodesRecord> findByCoverCodeSuperStartingWithAndTableType(
            @Param("prefix") String prefix,
            @Param("tableType") Short tableType);

    @Query("SELECT c FROM CentCodesRecord c WHERE c.coverCodeSuper LIKE :prefix% AND c.tableType = :tableType AND c.dateKey = :dateKey ORDER BY c.coverCodeSuper")
    List<CentCodesRecord> findByCoverCodeSuperStartingWithAndTableTypeAndDateKey(
            @Param("prefix") String prefix,
            @Param("tableType") Short tableType,
            @Param("dateKey") Integer dateKey);
}
