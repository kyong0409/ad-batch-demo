package com.demo.entfailbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FailureNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(FailureNotificationListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[FailureNotificationListener] Job started: name={}, params={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String status = jobExecution.getStatus().toString();
        String jobName = jobExecution.getJobInstance().getJobName();

        if ("FAILED".equals(status)) {
            log.error("==================================================");
            log.error("[FAILURE NOTIFICATION] Job failed - alerting team");
            log.error("==================================================");
            log.error("  Job Name   : {}", jobName);
            log.error("  Parameters : {}", jobExecution.getJobParameters());
            log.error("  Status     : {}", status);
            log.error("  Timestamp  : {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            log.error("  Exit Code  : {}", jobExecution.getExitStatus().getExitCode());
            log.error("  Exit Msg   : {}", jobExecution.getExitStatus().getExitDescription());

            for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                log.error("  --- Step: {}", stepExecution.getStepName());
                log.error("      Status       : {}", stepExecution.getStatus());
                log.error("      Read Count   : {}", stepExecution.getReadCount());
                log.error("      Write Count  : {}", stepExecution.getWriteCount());
                log.error("      Skip Count   : {}", stepExecution.getSkipCount());
                log.error("      Rollback     : {}", stepExecution.getRollbackCount());
                log.error("      Exit Code    : {}", stepExecution.getExitStatus().getExitCode());
                log.error("      Exit Message : {}", stepExecution.getExitStatus().getExitDescription());
            }

            log.error("==================================================");
            log.error("[FAILURE NOTIFICATION] Simulated alert sent to Slack #batch-alerts and ops@example.com");
            log.error("==================================================");
        } else {
            log.info("[FailureNotificationListener] Job completed with status: {}", status);
        }
    }
}
