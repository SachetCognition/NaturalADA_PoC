package com.insurance.reporting.batch;

import com.insurance.reporting.domain.AccountRecord;
import com.insurance.reporting.dto.ReportLine;
import com.insurance.reporting.service.AgentLookupService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class AccountsReportBatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final AgentLookupService agentLookupService;

    public AccountsReportBatchConfig(EntityManagerFactory entityManagerFactory,
                                      AgentLookupService agentLookupService) {
        this.entityManagerFactory = entityManagerFactory;
        this.agentLookupService = agentLookupService;
    }

    @Bean
    public JpaPagingItemReader<AccountRecord> accountItemReader() {
        return new JpaPagingItemReaderBuilder<AccountRecord>()
                .name("accountItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM AccountRecord a ORDER BY a.accKey")
                .pageSize(1000)
                .build();
    }

    @Bean
    public AccountFilterProcessor accountFilterProcessor() {
        return new AccountFilterProcessor(agentLookupService);
    }

    @Bean
    public AccountTransformProcessor accountTransformProcessor() {
        return new AccountTransformProcessor(agentLookupService);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public CompositeItemProcessor<AccountRecord, ReportLine> compositeProcessor() {
        CompositeItemProcessor<AccountRecord, ReportLine> composite = new CompositeItemProcessor<>();
        composite.setDelegates(List.of(accountFilterProcessor(), accountTransformProcessor()));
        return composite;
    }

    @Bean
    public TildeDelimitedWriter tildeDelimitedWriter() {
        return new TildeDelimitedWriter("output/accounts-report.txt");
    }

    @Bean
    public ReportTotalsListener reportTotalsListener() {
        return new ReportTotalsListener(tildeDelimitedWriter());
    }

    @Bean
    public Step accountsReportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("accountsReportStep", jobRepository)
                .<AccountRecord, ReportLine>chunk(1000, transactionManager)
                .reader(accountItemReader())
                .processor(compositeProcessor())
                .writer(tildeDelimitedWriter())
                .build();
    }

    @Bean
    public Job accountsReportJob(JobRepository jobRepository, Step accountsReportStep) {
        return new JobBuilder("accountsReportJob", jobRepository)
                .listener(reportTotalsListener())
                .start(accountsReportStep)
                .build();
    }
}
