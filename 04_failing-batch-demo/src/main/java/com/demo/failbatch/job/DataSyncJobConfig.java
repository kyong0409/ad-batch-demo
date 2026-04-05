package com.demo.failbatch.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

@Configuration
public class DataSyncJobConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSyncJobConfig.class);

    @Bean
    public ListItemReader<Map<String, String>> dataSyncReader() {
        return new ListItemReader<>(List.of(
            Map.of("id", "SYNC-001", "source", "CRM", "target", "DW"),
            Map.of("id", "SYNC-002", "source", "CRM", "target", "DW"),
            Map.of("id", "SYNC-003", "source", "ERP", "target", "DW"),
            Map.of("id", "SYNC-004", "source", "ERP", "target", "DW"),
            Map.of("id", "SYNC-005", "source", "HR",  "target", "DW")
        ));
    }

    @Bean
    public ItemProcessor<Map<String, String>, Map<String, String>> dataSyncProcessor() {
        return item -> {
            String id = item.get("id");
            log.info("DataSync processing: id={}, source={} -> target={}", id, item.get("source"), item.get("target"));

            if ("SYNC-004".equals(id)) {
                throw new RuntimeException(
                    "외부 시스템 연동 실패: ERP API 응답 타임아웃 (30s)\n" +
                    "  - endpoint: https://erp-api.internal/sync/incremental\n" +
                    "  - request_id: req-8f3a-4b2c\n" +
                    "  - 처리 완료: 3/5건, 실패 시점: SYNC-004"
                );
            }
            return item;
        };
    }

    @Bean
    public ItemWriter<Map<String, String>> dataSyncWriter() {
        return items -> {
            for (Map<String, String> item : items) {
                log.info("DataSync write complete: id={} synced to {}", item.get("id"), item.get("target"));
            }
        };
    }

    @Bean
    public Step dataSyncStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("dataSyncStep", jobRepository)
                .<Map<String, String>, Map<String, String>>chunk(10, transactionManager)
                .reader(dataSyncReader())
                .processor(dataSyncProcessor())
                .writer(dataSyncWriter())
                .build();
    }

    @Bean
    public Job dataSyncJob(JobRepository jobRepository, Step dataSyncStep) {
        return new JobBuilder("dataSyncJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(dataSyncStep)
                .build();
    }
}
