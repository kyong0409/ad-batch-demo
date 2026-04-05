package com.demo.enterprisebatch.job;

import com.demo.enterprisebatch.entity.DailyAdStats;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@TestConfiguration
public class H2WriterTestConfig {

    @Bean
    @Primary
    public JdbcBatchItemWriter<DailyAdStats> aggregationWriter(DataSource dataSource) {
        // H2 MERGE syntax: MERGE INTO ... KEY (...) VALUES (...)
        String sql = """
                MERGE INTO ent_daily_ad_stats
                    (stat_date, campaign_id, ad_id, impression_count, click_count, ctr, created_at)
                KEY (stat_date, campaign_id, ad_id)
                VALUES (:statDate, :campaignId, :adId, :impressionCount, :clickCount, :ctr, :createdAt)
                """;
        return new JdbcBatchItemWriterBuilder<DailyAdStats>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }
}
