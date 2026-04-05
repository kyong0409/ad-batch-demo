package com.demo.entfailbatch.job;

import com.demo.entfailbatch.entity.OrderSyncResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
class OrderSyncJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @TestConfiguration
    static class H2WriterConfig {
        @Bean
        @Primary
        public JdbcBatchItemWriter<OrderSyncResult> orderSyncWriter(DataSource dataSource) {
            return new JdbcBatchItemWriterBuilder<OrderSyncResult>()
                    .dataSource(dataSource)
                    .sql("INSERT INTO ent_order_sync_result (order_id, customer_name, amount, status, synced_at) " +
                         "VALUES (:orderId, :customerName, :amount, :status, :syncedAt)")
                    .beanMapped()
                    .build();
        }
    }

    @BeforeEach
    void resetData() {
        // Reset tables before each test so each test starts fresh
        jdbcTemplate.update("DELETE FROM ent_order_sync_result WHERE 1=1");
        jdbcTemplate.update("DELETE FROM ent_order_sync WHERE 1=1");

        // Re-insert the 12 test orders
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-001', 'Alice Kim', 15000.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-002', 'Bob Park', 23500.50, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-003', 'Charlie Lee', -500.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-004', 'Diana Choi', 8900.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-005', 'Eve Jung', 42000.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-006', '', 12000.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-007', 'Grace Han', 67800.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-008', 'Henry Yoon', 5600.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-009', 'Iris Shin', 31200.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-010', 'Jack Oh', 9999999.99, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-011', 'Kate Ryu', 18700.00, 'CONFIRMED')");
        jdbcTemplate.update("INSERT INTO ent_order_sync (order_id, customer_name, amount, status) VALUES ('ORD-012', 'Leo Moon', 24300.00, 'CONFIRMED')");

        // Pre-insert ORD-008 into result to cause UNIQUE violation on write
        jdbcTemplate.update("INSERT INTO ent_order_sync_result (order_id, customer_name, amount, status) VALUES ('ORD-008', 'Existing Record', 1000.00, 'SYNCED')");
    }

    @Test
    void testJobFailsDueToExceededSkipLimit() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    }

    @Test
    void testPartialCommitBehavior() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);

        List<String> writtenOrderIds = jdbcTemplate.queryForList(
                "SELECT order_id FROM ent_order_sync_result ORDER BY order_id",
                String.class);

        // ORD-001, ORD-002, ORD-004, ORD-005, ORD-007 should be committed
        // ORD-008 was pre-inserted (exists from data.sql seed)
        assertThat(writtenOrderIds).contains("ORD-001", "ORD-002", "ORD-004", "ORD-005", "ORD-007");
    }

    @Test
    void testSkipCountRecorded() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);

        long totalSkipCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getSkipCount)
                .sum();

        // 3 skips before hitting the limit (ORD-003, ORD-006, ORD-008)
        assertThat(totalSkipCount).isEqualTo(3L);
    }

    @Test
    void testFailedStatusInMetaTables() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        Long jobExecutionId = jobExecution.getId();
        String status = jdbcTemplate.queryForObject(
                "SELECT STATUS FROM BATCH_JOB_EXECUTION WHERE JOB_EXECUTION_ID = ?",
                String.class, jobExecutionId);

        assertThat(status).isEqualTo("FAILED");
    }
}
