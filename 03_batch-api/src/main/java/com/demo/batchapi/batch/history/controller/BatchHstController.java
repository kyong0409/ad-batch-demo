package com.demo.batchapi.batch.history.controller;

import com.demo.batchapi.batch.history.dto.*;
import com.demo.batchapi.batch.history.service.BatchHstService;
import com.demo.batchapi.common.CommonResponse;
import com.demo.batchapi.common.ResponsePage;
import com.demo.batchapi.batch.schedule.dto.BatchJobRestartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/om/batch-history")
@RequiredArgsConstructor
@RestController
public class BatchHstController {
    private final BatchHstService batchHstService;

    @GetMapping("/histories")
    public CommonResponse<ResponsePage<BatchHstJobDetails>> listBatchHst(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            BatchHstJobListRequest request) {
        return CommonResponse.success(batchHstService.listBatchHst(page, size, request));
    }

    @GetMapping("/histories/{jobExecutionId}")
    public CommonResponse<BatchHstExecutionDetails> getBatchHstDetails(@PathVariable Long jobExecutionId) {
        return CommonResponse.success(batchHstService.getBatchHstDetails(jobExecutionId));
    }

    @PutMapping("/histories/trigger")
    public CommonResponse<String> executeTrigger(@RequestBody BatchJobRestartRequest request) {
        // Demo stub -- in production this would call the batch scheduler
        return CommonResponse.success("배치가 재실행되었습니다.");
    }
}
