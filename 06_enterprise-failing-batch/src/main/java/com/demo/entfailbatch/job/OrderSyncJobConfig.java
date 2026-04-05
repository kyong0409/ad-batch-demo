package com.demo.entfailbatch.job;

import com.demo.entfailbatch.entity.OrderSync;
import com.demo.entfailbatch.entity.OrderSyncResult;
import com.demo.entfailbatch.listener.DetailedSkipListener;
import com.demo.entfailbatch.listener.FailureNotificationListener;
import com.demo.entfailbatch.processor.OrderSyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class OrderSyncJobConfig {

    private static final Logger log = LoggerFactory.getLogger(OrderSyncJobConfig.class);

    @Value("${batch.chunk-size}")
    private int chunkSize;

    @Bean
    public JdbcPagingItemReader<OrderSync> orderSyncReader(DataSource dataSource) throws Exception {
        return new JdbcPagingItemReaderBuilder<OrderSync>()
                .name("orderSyncReader")
                .dataSource(dataSource)
                .selectClause("SELECT id, order_id, customer_name, amount, status, sync_status, error_message, created_at, updated_at")
                .fromClause("FROM ent_order_sync")
                .whereClause("WHERE sync_status = 'PENDING'")
                .sortKeys(Map.of("id", Order.ASCENDING))
                .pageSize(chunkSize)
                .rowMapper(new BeanPropertyRowMapper<>(OrderSync.class))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<OrderSyncResult> orderSyncWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<OrderSyncResult>()
                .dataSource(dataSource)
                .sql("MERGE INTO ent_order_sync_result AS target " +
                     "USING (VALUES (:orderId, :customerName, :amount, :status, :syncedAt)) " +
                     "AS source (order_id, customer_name, amount, status, synced_at) " +
                     "ON target.order_id = source.order_id " +
                     "WHEN MATCHED THEN UPDATE SET " +
                     "  customer_name = source.customer_name, " +
                     "  amount = source.amount, " +
                     "  status = source.status, " +
                     "  synced_at = source.synced_at " +
                     "WHEN NOT MATCHED THEN INSERT (order_id, customer_name, amount, status, synced_at) " +
                     "VALUES (source.order_id, source.customer_name, source.amount, source.status, source.synced_at);")
                .beanMapped()
                .build();
    }

    @Bean
    public Step orderSyncStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               JdbcPagingItemReader<OrderSync> orderSyncReader,
                               JdbcBatchItemWriter<OrderSyncResult> orderSyncWriter,
                               OrderSyncProcessor orderSyncProcessor,
                               DetailedSkipListener detailedSkipListener) {
        return new StepBuilder("orderSyncStep", jobRepository)
                .<OrderSync, OrderSyncResult>chunk(chunkSize, transactionManager)
                .reader(orderSyncReader)
                .processor(orderSyncProcessor)
                .writer(orderSyncWriter)
                .faultTolerant()
                .skip(DataIntegrityViolationException.class)
                .skip(IllegalArgumentException.class)
                .skipLimit(3)
                .listener(detailedSkipListener)
                .build();
    }

    @Bean
    public Job orderSyncJob(JobRepository jobRepository,
                             Step orderSyncStep,
                             FailureNotificationListener failureNotificationListener) {
        return new JobBuilder("orderSyncJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(failureNotificationListener)
                .start(orderSyncStep)
                .build();
    }
}
