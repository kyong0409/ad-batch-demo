package com.demo.enterprisebatch.job;

import com.demo.enterprisebatch.entity.AdClickLog;
import com.demo.enterprisebatch.entity.DailyAdStats;
import com.demo.enterprisebatch.listener.ChunkProgressListener;
import com.demo.enterprisebatch.listener.JobCompletionListener;
import com.demo.enterprisebatch.listener.SkipLoggingListener;
import com.demo.enterprisebatch.partitioner.CampaignPartitioner;
import com.demo.enterprisebatch.processor.DataCleansingProcessor;
import com.demo.enterprisebatch.processor.EventToStatsProcessor;
import com.demo.enterprisebatch.processor.TimestampEnrichmentProcessor;
import com.demo.enterprisebatch.processor.ValidationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Configuration
public class EntBatchJobConfig {

    private static final Logger log = LoggerFactory.getLogger(EntBatchJobConfig.class);

    @Value("${TARGET_DATE:#{null}}")
    private String targetDateStr;

    @Value("${batch.chunk-size:100}")
    private int chunkSize;

    @Value("${batch.export.output-dir:./output}")
    private String outputDir;

    private LocalDate resolveTargetDate() {
        if (targetDateStr != null && !targetDateStr.isBlank()) {
            return LocalDate.parse(targetDateStr);
        }
        return LocalDate.now().minusDays(1);
    }

    // -----------------------------------------------------------------------
    // Step 1: dataValidationStep
    // -----------------------------------------------------------------------

    @Bean
    public Step dataValidationStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   DataSource dataSource) {
        LocalDate targetDate = resolveTargetDate();
        Tasklet tasklet = (contribution, chunkContext) -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM ent_ad_click_log WHERE CAST(event_time AS DATE) = ?",
                    Integer.class, targetDate);
            if (count == null || count == 0) {
                log.warn("No source data found for targetDate={}. Failing job.", targetDate);
                contribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            }
            log.info("Data validation passed: {} rows found for targetDate={}", count, targetDate);
            chunkContext.getStepContext().getStepExecution()
                    .getJobExecution().getExecutionContext()
                    .putLong("sourceRowCount", count);
            return RepeatStatus.FINISHED;
        };
        return new StepBuilder("dataValidationStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    // -----------------------------------------------------------------------
    // Step 1.5: cleanupStep — delete stale aggregation results for re-run safety
    // -----------------------------------------------------------------------

    @Bean
    public Step cleanupStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            DataSource dataSource) {
        LocalDate targetDate = resolveTargetDate();
        Tasklet tasklet = (contribution, chunkContext) -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            int deleted = jdbc.update("DELETE FROM ent_daily_ad_stats WHERE stat_date = ?", targetDate);
            if (deleted > 0) {
                log.info("Cleanup: deleted {} stale rows from ent_daily_ad_stats for targetDate={}", deleted, targetDate);
            }
            return RepeatStatus.FINISHED;
        };
        return new StepBuilder("cleanupStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    // -----------------------------------------------------------------------
    // Step 2: aggregationStep (partitioned chunk)
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Bean
    public ItemProcessor<AdClickLog, DailyAdStats> compositeProcessor() {
        CompositeItemProcessor<AdClickLog, DailyAdStats> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(
                new DataCleansingProcessor(),
                new ValidationProcessor(),
                new EventToStatsProcessor(),
                new TimestampEnrichmentProcessor()
        ));
        return processor;
    }

    @Bean
    @ConditionalOnMissingBean(name = "aggregationWriter")
    public JdbcBatchItemWriter<DailyAdStats> aggregationWriter(DataSource dataSource) {
        String sql = """
                MERGE ent_daily_ad_stats WITH (ROWLOCK) AS target
                USING (VALUES (:statDate, :campaignId, :adId, :impressionCount, :clickCount, :ctr, :createdAt))
                  AS source (stat_date, campaign_id, ad_id, impression_count, click_count, ctr, created_at)
                ON target.stat_date = source.stat_date
                   AND target.campaign_id = source.campaign_id
                   AND target.ad_id = source.ad_id
                WHEN MATCHED THEN UPDATE SET
                    impression_count = target.impression_count + source.impression_count,
                    click_count = target.click_count + source.click_count,
                    created_at = source.created_at
                WHEN NOT MATCHED THEN INSERT
                    (stat_date, campaign_id, ad_id, impression_count, click_count, ctr, created_at)
                    VALUES (source.stat_date, source.campaign_id, source.ad_id,
                            source.impression_count, source.click_count, source.ctr, source.created_at);
                """;
        return new JdbcBatchItemWriterBuilder<DailyAdStats>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<AdClickLog> aggregationWorkerReader(
            DataSource dataSource,
            @Value("#{stepExecutionContext['campaignId']}") String campaignId) {
        LocalDate targetDate = resolveTargetDate();
        return new JdbcCursorItemReaderBuilder<AdClickLog>()
                .name("aggregationWorkerReader")
                .dataSource(dataSource)
                .sql("""
                        SELECT id, campaign_id, ad_id, event_type, event_time, user_id
                        FROM ent_ad_click_log
                        WHERE CAST(event_time AS DATE) = ? AND campaign_id = ?
                        ORDER BY id
                        """)
                .preparedStatementSetter(ps -> {
                    ps.setObject(1, targetDate);
                    ps.setString(2, campaignId);
                })
                .saveState(false)
                .rowMapper((rs, rowNum) -> {
                    AdClickLog log = new AdClickLog();
                    log.setId(rs.getLong("id"));
                    log.setCampaignId(rs.getString("campaign_id"));
                    log.setAdId(rs.getString("ad_id"));
                    log.setEventType(rs.getString("event_type"));
                    log.setEventTime(rs.getObject("event_time", java.time.LocalDateTime.class));
                    log.setUserId(rs.getString("user_id"));
                    return log;
                })
                .build();
    }

    @Bean
    public Step aggregationWorkerStep(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      JdbcCursorItemReader<AdClickLog> aggregationWorkerReader,
                                      JdbcBatchItemWriter<DailyAdStats> aggregationWriter) {
        org.springframework.retry.backoff.ExponentialRandomBackOffPolicy backOff = new org.springframework.retry.backoff.ExponentialRandomBackOffPolicy();
        backOff.setInitialInterval(500);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5000);

        return new StepBuilder("aggregationWorkerStep", jobRepository)
                .<AdClickLog, DailyAdStats>chunk(500, transactionManager)
                .reader(aggregationWorkerReader)
                .processor(compositeProcessor())
                .writer(aggregationWriter)
                .faultTolerant()
                .retry(org.springframework.dao.CannotAcquireLockException.class)
                .retryLimit(10)
                .backOffPolicy(backOff)
                .listener(new ChunkProgressListener())
                .build();
    }

    @Bean
    public Step aggregationStep(JobRepository jobRepository,
                                DataSource dataSource,
                                Step aggregationWorkerStep) {
        LocalDate targetDate = resolveTargetDate();
        CampaignPartitioner partitioner = new CampaignPartitioner(dataSource, targetDate);

        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(3);

        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(aggregationWorkerStep);
        partitionHandler.setTaskExecutor(taskExecutor);
        partitionHandler.setGridSize(10);

        return new StepBuilder("aggregationStep", jobRepository)
                .partitioner("aggregationWorkerStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    // -----------------------------------------------------------------------
    // Step 3: ctrUpdateStep
    // -----------------------------------------------------------------------

    @Bean
    public Step ctrUpdateStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               DataSource dataSource) {
        LocalDate targetDate = resolveTargetDate();
        Tasklet tasklet = (contribution, chunkContext) -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            int updated = jdbc.update("""
                    UPDATE ent_daily_ad_stats
                    SET ctr = CASE
                        WHEN impression_count > 0
                        THEN CAST(click_count AS DECIMAL(10,6)) / CAST(impression_count AS DECIMAL(10,6))
                        ELSE 0
                    END
                    WHERE stat_date = ?
                    """, targetDate);
            log.info("CTR updated for {} rows, targetDate={}", updated, targetDate);
            return RepeatStatus.FINISHED;
        };
        return new StepBuilder("ctrUpdateStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    // -----------------------------------------------------------------------
    // Step 4: verificationStep
    // -----------------------------------------------------------------------

    @Bean
    public Step verificationStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  DataSource dataSource) {
        LocalDate targetDate = resolveTargetDate();
        Tasklet tasklet = (contribution, chunkContext) -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);

            Integer sourceCount = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM ent_ad_click_log WHERE CAST(event_time AS DATE) = ?",
                    Integer.class, targetDate);
            Integer aggCount = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM ent_daily_ad_stats WHERE stat_date = ?",
                    Integer.class, targetDate);

            log.info("Verification: sourceRows={}, aggregatedRows={} for targetDate={}",
                    sourceCount, aggCount, targetDate);

            // Check per-partition StepExecution status
            Collection<StepExecution> stepExecutions = chunkContext.getStepContext()
                    .getStepExecution().getJobExecution().getStepExecutions();
            long failedPartitions = stepExecutions.stream()
                    .filter(se -> se.getStepName().startsWith("aggregationWorkerStep"))
                    .filter(se -> se.getStatus() == BatchStatus.FAILED)
                    .count();
            if (failedPartitions > 0) {
                log.warn("Verification: {} partition(s) failed during aggregation.", failedPartitions);
            } else {
                log.info("Verification: all partitions completed successfully.");
            }

            return RepeatStatus.FINISHED;
        };
        return new StepBuilder("verificationStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    // -----------------------------------------------------------------------
    // Step 4: csvExportStep
    // -----------------------------------------------------------------------

    @Bean
    public Step csvExportStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               DataSource dataSource) {
        LocalDate targetDate = resolveTargetDate();
        String csvPath = outputDir + "/daily_ad_stats_" + targetDate + ".csv";

        JdbcCursorItemReader<DailyAdStats> csvReader = new JdbcCursorItemReaderBuilder<DailyAdStats>()
                .name("csvExportReader")
                .dataSource(dataSource)
                .sql("SELECT stat_date, campaign_id, ad_id, impression_count, click_count, ctr, created_at FROM ent_daily_ad_stats WHERE stat_date = ?")
                .preparedStatementSetter(ps -> ps.setObject(1, targetDate))
                .rowMapper((rs, rowNum) -> {
                    DailyAdStats stats = new DailyAdStats();
                    stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                    stats.setCampaignId(rs.getString("campaign_id"));
                    stats.setAdId(rs.getString("ad_id"));
                    stats.setImpressionCount(rs.getLong("impression_count"));
                    stats.setClickCount(rs.getLong("click_count"));
                    stats.setCtr(rs.getBigDecimal("ctr"));
                    stats.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
                    return stats;
                })
                .build();

        FlatFileItemWriter<DailyAdStats> csvWriter = new FlatFileItemWriterBuilder<DailyAdStats>()
                .name("csvExportWriter")
                .resource(new FileSystemResource(csvPath))
                .headerCallback(writer -> writer.write("stat_date,campaign_id,ad_id,impression_count,click_count,ctr,created_at"))
                .delimited()
                .delimiter(",")
                .names("statDate", "campaignId", "adId", "impressionCount", "clickCount", "ctr", "createdAt")
                .build();

        return new StepBuilder("csvExportStep", jobRepository)
                .<DailyAdStats, DailyAdStats>chunk(chunkSize, transactionManager)
                .reader(csvReader)
                .writer(csvWriter)
                .build();
    }

    // -----------------------------------------------------------------------
    // Step 5: completionStep
    // -----------------------------------------------------------------------

    @Bean
    public Step completionStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                DataSource dataSource) {
        LocalDate targetDate = resolveTargetDate();
        String csvPath = outputDir + "/daily_ad_stats_" + targetDate + ".csv";
        Tasklet tasklet = (contribution, chunkContext) -> {
            JdbcTemplate jdbc = new JdbcTemplate(dataSource);
            long sourceRowCount = chunkContext.getStepContext().getStepExecution()
                    .getJobExecution().getExecutionContext().containsKey("sourceRowCount")
                    ? chunkContext.getStepContext().getStepExecution()
                            .getJobExecution().getExecutionContext().getLong("sourceRowCount")
                    : -1L;
            Integer aggCount = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM ent_daily_ad_stats WHERE stat_date = ?",
                    Integer.class, targetDate);
            log.info("Job summary: targetDate={}, sourceRows={}, aggregatedRows={}, csvFile={}",
                    targetDate, sourceRowCount, aggCount, csvPath);
            return RepeatStatus.FINISHED;
        };
        return new StepBuilder("completionStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    // -----------------------------------------------------------------------
    // Job
    // -----------------------------------------------------------------------

    @Bean
    public Job enterpriseAdStatsJob(JobRepository jobRepository,
                                     Step dataValidationStep,
                                     Step cleanupStep,
                                     Step aggregationStep,
                                     Step ctrUpdateStep,
                                     Step verificationStep,
                                     Step csvExportStep,
                                     Step completionStep) {
        return new JobBuilder("enterpriseAdStatsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobCompletionListener())
                .start(dataValidationStep)
                    .on("FAILED").end()
                .from(dataValidationStep)
                    .on("*").to(cleanupStep)
                .from(cleanupStep)
                    .next(aggregationStep)
                .from(aggregationStep)
                    .next(ctrUpdateStep)
                .from(ctrUpdateStep)
                    .next(verificationStep)
                .from(verificationStep)
                    .next(csvExportStep)
                .from(csvExportStep)
                    .next(completionStep)
                .end()
                .build();
    }
}
