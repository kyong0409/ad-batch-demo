package com.demo.batchapi.batch.schedule.controller;

import com.demo.batchapi.batch.schedule.dto.*;
import com.demo.batchapi.batch.schedule.service.BatchSclgService;
import com.demo.batchapi.common.CommonResponse;
import com.demo.batchapi.common.ResponsePage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/om/batch-schedule")
@RequiredArgsConstructor
@RestController
public class BatchSclgController {
    private final BatchSclgService batchSclgService;

    @GetMapping("/schedules")
    public CommonResponse<ResponsePage<BatchSclgDetails>> listBatchSclg(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            BatchSclgListRequest request) {
        return CommonResponse.success(batchSclgService.listBatchSclg(page, size, request));
    }

    @GetMapping("/schedules/{jobGroup}/{jobNm}")
    public CommonResponse<BatchSclgDetails> getBatchSclg(
            @PathVariable String jobGroup, @PathVariable String jobNm) {
        return CommonResponse.success(batchSclgService.getBatchSclg(jobGroup, jobNm));
    }

    @PostMapping("/schedules")
    public CommonResponse<BatchSclgDetails> addBatchSclg(@RequestBody BatchSclgAddModifyRequest request) {
        return CommonResponse.success(batchSclgService.addBatchSclg(request));
    }

    @PutMapping("/schedules/{jobGroup}/{jobNm}")
    public CommonResponse<BatchSclgDetails> modifyBatchSclg(
            @PathVariable String jobGroup, @PathVariable String jobNm,
            @RequestBody BatchSclgAddModifyRequest request) {
        return CommonResponse.success(batchSclgService.modifyBatchSclg(jobGroup, jobNm, request));
    }
}
