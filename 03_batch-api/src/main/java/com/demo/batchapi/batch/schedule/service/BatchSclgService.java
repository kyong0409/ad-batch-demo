package com.demo.batchapi.batch.schedule.service;

import com.demo.batchapi.batch.schedule.dto.*;
import com.demo.batchapi.batch.schedule.mapper.BatchSclgMapper;
import com.demo.batchapi.common.ResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BatchSclgService {
    private final BatchSclgMapper batchSclgMapper;

    @Transactional(readOnly = true)
    public ResponsePage<BatchSclgDetails> listBatchSclg(int page, int size, BatchSclgListRequest request) {
        request.setOffset(page * size);
        request.setPageSize(size);
        int count = batchSclgMapper.count(request);
        List<BatchSclgDetails> list = batchSclgMapper.select(request);
        return ResponsePage.of(list, page, size, count);
    }

    @Transactional(readOnly = true)
    public BatchSclgDetails getBatchSclg(String jobGroup, String jobNm) {
        return batchSclgMapper.selectOne(jobGroup, jobNm)
                .orElseThrow(() -> new RuntimeException("배치 스케줄을 찾을 수 없습니다."));
    }

    @Transactional
    public BatchSclgDetails addBatchSclg(BatchSclgAddModifyRequest request) {
        batchSclgMapper.insert(request);
        return batchSclgMapper.selectOne(request.getJobGroup(), request.getJobNm())
                .orElseThrow(() -> new RuntimeException("등록 실패"));
    }

    @Transactional
    public BatchSclgDetails modifyBatchSclg(String jobGroup, String jobNm, BatchSclgAddModifyRequest request) {
        int result = batchSclgMapper.update(jobGroup, jobNm, request);
        if (result < 1) throw new RuntimeException("수정 실패");
        return batchSclgMapper.selectOne(request.getJobGroup(), request.getJobNm())
                .orElseThrow(() -> new RuntimeException("조회 실패"));
    }
}
