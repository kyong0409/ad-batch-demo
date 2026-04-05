package com.demo.enterprisebatch.job;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@Import(H2WriterTestConfig.class)
class EnterpriseAdStatsJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job enterpriseAdStatsJob;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private JobParameters defaultParams() {
        return new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
    }

    @Test
    void testJobCompletesSuccessfully() throws Exception {
        jobLauncherTestUtils.setJob(enterpriseAdStatsJob);
        JobExecution execution = jobLauncherTestUtils.launchJob(defaultParams());
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void testJobWithTargetDateParameter() throws Exception {
        jobLauncherTestUtils.setJob(enterpriseAdStatsJob);
        String yesterday = LocalDate.now().minusDays(1).toString();
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("TARGET_DATE", yesterday)
                .toJobParameters();
        JobExecution execution = jobLauncherTestUtils.launchJob(params);
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    void testCsvFileGenerated() throws Exception {
        jobLauncherTestUtils.setJob(enterpriseAdStatsJob);
        JobExecution execution = jobLauncherTestUtils.launchJob(defaultParams());
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        String yesterday = LocalDate.now().minusDays(1).toString();
        File csvFile = new File("./build/test-output/daily_ad_stats_" + yesterday + ".csv");
        assertThat(csvFile).exists();
    }

    @Test
    void testAggregationResults() throws Exception {
        jobLauncherTestUtils.setJob(enterpriseAdStatsJob);
        JobExecution execution = jobLauncherTestUtils.launchJob(defaultParams());
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Integer aggCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ent_daily_ad_stats WHERE stat_date = ?",
                Integer.class, LocalDate.now().minusDays(1));
        assertThat(aggCount).isGreaterThan(0);
    }

    @Test
    void testPartitioningByCampaign() throws Exception {
        jobLauncherTestUtils.setJob(enterpriseAdStatsJob);
        JobExecution execution = jobLauncherTestUtils.launchJob(defaultParams());
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        long workerStepCount = execution.getStepExecutions().stream()
                .filter(se -> se.getStepName().startsWith("aggregationWorkerStep"))
                .count();
        assertThat(workerStepCount).isGreaterThanOrEqualTo(1);
    }
}
