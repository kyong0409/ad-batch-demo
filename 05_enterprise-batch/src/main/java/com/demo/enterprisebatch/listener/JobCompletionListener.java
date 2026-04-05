package com.demo.enterprisebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;

public class JobCompletionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job starting: name={}, parameters={}, startTime={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters(),
                jobExecution.getCreateTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long durationMs = 0;
        if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
            durationMs = java.time.Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis();
        }

        long totalRead = 0;
        long totalWrite = 0;
        long totalSkip = 0;
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            totalRead += stepExecution.getReadCount();
            totalWrite += stepExecution.getWriteCount();
            totalSkip += stepExecution.getSkipCount();
        }

        log.info("Job completed: name={}, status={}, durationMs={}, totalRead={}, totalWrite={}, totalSkip={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                durationMs,
                totalRead,
                totalWrite,
                totalSkip);
    }
}
