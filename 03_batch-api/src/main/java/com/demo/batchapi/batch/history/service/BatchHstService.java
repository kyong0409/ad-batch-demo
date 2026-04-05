package com.demo.batchapi.batch.history.service;

import com.demo.batchapi.batch.history.dto.*;
import com.demo.batchapi.batch.history.mapper.BatchHstMapper;
import com.demo.batchapi.common.ResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BatchHstService {
    private final BatchHstMapper batchHstMapper;

    @Transactional(readOnly = true)
    public ResponsePage<BatchHstJobDetails> listBatchHst(int page, int size, BatchHstJobListRequest request) {
        request.setOffset(page * size);
        request.setPageSize(size);
        int count = batchHstMapper.count(request);
        List<BatchHstJobDetails> list = batchHstMapper.select(request);
        return ResponsePage.of(list, page, size, count);
    }

    @Transactional(readOnly = true)
    public BatchHstExecutionDetails getBatchHstDetails(Long jobExecutionId) {
        List<BatchHstStepDetails> steps = batchHstMapper.selectBatchHstStep(jobExecutionId);
        List<BatchHstJobParamDetails> params = batchHstMapper.selectBatchHstParam(jobExecutionId);
        return BatchHstExecutionDetails.builder()
                .batchHstStepList(steps)
                .batchHstJobParamList(params)
                .build();
    }
}
