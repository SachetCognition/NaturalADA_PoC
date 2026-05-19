package com.insurance.reporting.repository;

import com.insurance.reporting.domain.AccountRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountRecord, Long> {

    Page<AccountRecord> findAllByOrderByAccKey(Pageable pageable);
}
