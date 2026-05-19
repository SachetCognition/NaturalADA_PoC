package com.insurance.premium.batch;

import com.insurance.premium.domain.CentCodesRecord;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

@Configuration
public class PremiumRateBatchConfig {

    private final EntityManagerFactory entityManagerFactory;

    public PremiumRateBatchConfig(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    // --- Step 1: R1 - Audit Before ---

    @Bean
    public JpaPagingItemReader<CentCodesRecord> auditBeforeReader() {
        return new JpaPagingItemReaderBuilder<CentCodesRecord>()
                .name("auditBeforeReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM CentCodesRecord c WHERE c.tableType = :tableType AND c.coverCodeSuper LIKE :prefix ORDER BY c.coverCodeSuper")
                .parameterValues(Map.of("tableType", (short) 150, "prefix", "150110%"))
                .pageSize(100)
                .build();
    }

    @Bean
    public FlatFileItemWriter<CentCodesRecord> auditBeforeWriter() {
        return new FlatFileItemWriterBuilder<CentCodesRecord>()
                .name("auditBeforeWriter")
                .resource(new FileSystemResource("output/audit-before.csv"))
                .delimited()
                .delimiter(",")
                .names("coverCodeSuper", "tpPremium1", "premium", "invType", "covGrpKey")
                .build();
    }

    @Bean
    public Step auditBeforeStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("auditBefore", jobRepository)
                .<CentCodesRecord, CentCodesRecord>chunk(100, transactionManager)
                .reader(auditBeforeReader())
                .writer(auditBeforeWriter())
                .build();
    }

    // --- Step 2: R2 - Expire Active Records ---

    @Bean
    public JpaCursorItemReader<CentCodesRecord> expireActiveReader() {
        return new JpaCursorItemReaderBuilder<CentCodesRecord>()
                .name("expireActiveReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM CentCodesRecord c WHERE c.tableType = :tableType AND c.coverCodeSuper LIKE :prefix AND c.dateKey = :dateKey ORDER BY c.coverCodeSuper")
                .parameterValues(Map.of("tableType", (short) 150, "prefix", "150110%", "dateKey", 99999999))
                .build();
    }

    @Bean
    public ItemProcessor<CentCodesRecord, CentCodesRecord> expireProcessor() {
        return record -> {
            record.setDateKey(20150331);
            record.setUpdatedAt(LocalDateTime.now());
            return record;
        };
    }

    @Bean
    public JpaItemWriter<CentCodesRecord> jpaItemWriter() {
        JpaItemWriter<CentCodesRecord> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step expireActiveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("expireActive", jobRepository)
                .<CentCodesRecord, CentCodesRecord>chunk(1, transactionManager)
                .reader(expireActiveReader())
                .processor(expireProcessor())
                .writer(jpaItemWriter())
                .build();
    }

    // --- Step 3: R3 - Insert New Active Records ---

    @Bean
    public JpaPagingItemReader<CentCodesRecord> insertNewActiveReader() {
        return new JpaPagingItemReaderBuilder<CentCodesRecord>()
                .name("insertNewActiveReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM CentCodesRecord c WHERE c.tableType = :tableType AND c.coverCodeSuper LIKE :prefix AND c.dateKey = :dateKey ORDER BY c.coverCodeSuper")
                .parameterValues(Map.of("tableType", (short) 150, "prefix", "150110%", "dateKey", 20150331))
                .pageSize(1)
                .build();
    }

    @Bean
    public ItemProcessor<CentCodesRecord, CentCodesRecord> newRecordProcessor() {
        return expiredRecord -> {
            CentCodesRecord newRecord = new CentCodesRecord();
            newRecord.setTableType(expiredRecord.getTableType());
            newRecord.setDateKey(99999999);
            newRecord.setCovGrpKey(expiredRecord.getCovGrpKey());
            newRecord.setInvType(expiredRecord.getInvType());
            newRecord.setCoverCodeSuper(expiredRecord.getCoverCodeSuper().substring(0, 8) + "99999999");
            newRecord.setTpPremium1(26667);
            newRecord.setPremium(26667);
            newRecord.setCreatedAt(LocalDateTime.now());
            newRecord.setUpdatedAt(LocalDateTime.now());
            return newRecord;
        };
    }

    @Bean
    public JpaItemWriter<CentCodesRecord> insertWriter() {
        JpaItemWriter<CentCodesRecord> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Step insertNewActiveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("insertNewActive", jobRepository)
                .<CentCodesRecord, CentCodesRecord>chunk(1, transactionManager)
                .reader(insertNewActiveReader())
                .processor(newRecordProcessor())
                .writer(insertWriter())
                .build();
    }

    // --- Step 4: R4 - Audit After ---

    @Bean
    public JpaPagingItemReader<CentCodesRecord> auditAfterReader() {
        return new JpaPagingItemReaderBuilder<CentCodesRecord>()
                .name("auditAfterReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM CentCodesRecord c WHERE c.tableType = :tableType AND c.coverCodeSuper LIKE :prefix ORDER BY c.coverCodeSuper")
                .parameterValues(Map.of("tableType", (short) 150, "prefix", "150110%"))
                .pageSize(100)
                .build();
    }

    @Bean
    public FlatFileItemWriter<CentCodesRecord> auditAfterWriter() {
        return new FlatFileItemWriterBuilder<CentCodesRecord>()
                .name("auditAfterWriter")
                .resource(new FileSystemResource("output/audit-after.csv"))
                .delimited()
                .delimiter(",")
                .names("coverCodeSuper", "tpPremium1", "premium", "invType", "covGrpKey")
                .build();
    }

    @Bean
    public Step auditAfterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("auditAfter", jobRepository)
                .<CentCodesRecord, CentCodesRecord>chunk(100, transactionManager)
                .reader(auditAfterReader())
                .writer(auditAfterWriter())
                .build();
    }

    // --- Job: R1 → R2 → R3 → R4 ---

    @Bean
    public Job premiumRateUpdateJob(JobRepository jobRepository,
                                     Step auditBeforeStep,
                                     Step expireActiveStep,
                                     Step insertNewActiveStep,
                                     Step auditAfterStep) {
        return new JobBuilder("premiumRateUpdateJob", jobRepository)
                .start(auditBeforeStep)
                .next(expireActiveStep)
                .next(insertNewActiveStep)
                .next(auditAfterStep)
                .build();
    }
}
