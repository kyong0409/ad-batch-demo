package com.demo.enterprisebatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepExecution;

public class ChunkProgressListener implements ChunkListener {

    private static final Logger log = LoggerFactory.getLogger(ChunkProgressListener.class);

    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution stepExecution = context.getStepContext().getStepExecution();
        String stepName = stepExecution.getStepName();
        long readCount = stepExecution.getReadCount();
        long writeCount = stepExecution.getWriteCount();
        long skipCount = stepExecution.getSkipCount();
        long commitCount = stepExecution.getCommitCount();

        log.info("[{}] Chunk #{}: read={}, write={}, skip={}",
                stepName, commitCount, readCount, writeCount, skipCount);
    }
}
