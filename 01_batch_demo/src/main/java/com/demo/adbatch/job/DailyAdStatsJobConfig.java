package com.demo.adbatch.job;

import com.demo.adbatch.entity.DailyAdStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DailyAdStatsJobConfig {

    private static final Logger log = LoggerFactory.getLogger(DailyAdStatsJobConfig.class);

    @Bean
    public JdbcCursorItemReader<DailyAdStats> dailyAdStatsReader(DataSource dataSource) {
        String sql = """
            SELECT
                CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS stat_date,
                campaign_id,
                ad_id,
                SUM(CASE WHEN event_type = 'IMPRESSION' THEN 1 ELSE 0 END) AS impression_count,
                SUM(CASE WHEN event_type = 'CLICK' THEN 1 ELSE 0 END) AS click_count,
                CASE
                    WHEN SUM(CASE WHEN event_type = 'IMPRESSION' THEN 1 ELSE 0 END) > 0
                    THEN CAST(SUM(CASE WHEN event_type = 'CLICK' THEN 1 ELSE 0 END) AS DECIMAL(5,4))
                         / SUM(CASE WHEN event_type = 'IMPRESSION' THEN 1 ELSE 0 END)
                    ELSE 0
                END AS ctr
            FROM ad_click_log
            WHERE CAST(event_time AS DATE) = CAST(DATEADD(DAY, -1, GETDATE()) AS DATE)
            GROUP BY campaign_id, ad_id
            """;

        return new JdbcCursorItemReaderBuilder<DailyAdStats>()
                .name("dailyAdStatsReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper((rs, rowNum) -> {
                    DailyAdStats stats = new DailyAdStats();
                    stats.setStatDate(rs.getObject("stat_date", LocalDate.class));
                    stats.setCampaignId(rs.getString("campaign_id"));
                    stats.setAdId(rs.getString("ad_id"));
                    stats.setImpressionCount(rs.getLong("impression_count"));
                    stats.setClickCount(rs.getLong("click_count"));
                    stats.setCtr(rs.getBigDecimal("ctr"));
                    return stats;
                })
                .build();
    }

    @Bean
    public ItemProcessor<DailyAdStats, DailyAdStats> dailyAdStatsProcessor() {
        return item -> {
            item.setCreatedAt(LocalDateTime.now());
            log.info("Processing: campaign={}, ad={}, impressions={}, clicks={}, ctr={}",
                    item.getCampaignId(), item.getAdId(),
                    item.getImpressionCount(), item.getClickCount(), item.getCtr());
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<DailyAdStats> dailyAdStatsWriter(DataSource dataSource) {
        String sql = """
            MERGE daily_ad_stats AS target
            USING (VALUES (:statDate, :campaignId, :adId, :impressionCount, :clickCount, :ctr, :createdAt))
              AS source (stat_date, campaign_id, ad_id, impression_count, click_count, ctr, created_at)
            ON target.stat_date = source.stat_date
               AND target.campaign_id = source.campaign_id
               AND target.ad_id = source.ad_id
            WHEN MATCHED THEN UPDATE SET
                impression_count = source.impression_count,
                click_count = source.click_count,
                ctr = source.ctr,
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
    public Step dailyAdStatsStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  JdbcCursorItemReader<DailyAdStats> reader,
                                  ItemProcessor<DailyAdStats, DailyAdStats> processor,
                                  JdbcBatchItemWriter<DailyAdStats> writer) {
        return new StepBuilder("dailyAdStatsStep", jobRepository)
                .<DailyAdStats, DailyAdStats>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job dailyAdStatsJob(JobRepository jobRepository, Step dailyAdStatsStep) {
        return new JobBuilder("dailyAdStatsJob", jobRepository)
                .incrementer(new org.springframework.batch.core.job.parameters.RunIdIncrementer())
                .start(dailyAdStatsStep)
                .build();
    }
}
