package com.demo.batchapi.batch.history.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BatchHstJobListRequest {
    private Long jobInstanceId;
    private String jobName;
    private Long jobExecutionId;
    private String startTime;
    private String endTime;
    private String status;
    private String exitCode;
    // Pagination (set by controller)
    private int offset;
    private int pageSize;
}
