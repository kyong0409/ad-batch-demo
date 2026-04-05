package com.demo.batchapi.batch.history.mapper;

import com.demo.batchapi.batch.history.dto.BatchHstJobDetails;
import com.demo.batchapi.batch.history.dto.BatchHstJobListRequest;
import com.demo.batchapi.batch.history.dto.BatchHstStepDetails;
import com.demo.batchapi.batch.history.dto.BatchHstJobParamDetails;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BatchHstMapper {
    int count(BatchHstJobListRequest request);
    List<BatchHstJobDetails> select(BatchHstJobListRequest request);
    List<BatchHstStepDetails> selectBatchHstStep(Long jobExecutionId);
    List<BatchHstJobParamDetails> selectBatchHstParam(Long jobExecutionId);
}
