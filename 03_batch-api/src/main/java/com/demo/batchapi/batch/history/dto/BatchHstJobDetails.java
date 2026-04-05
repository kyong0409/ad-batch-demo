package com.demo.batchapi.batch.history.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class BatchHstJobDetails {
    private Long rowNum;
    private Long jobInstanceId;
    private String jobName;
    private Long jobExecutionId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String exitCode;
    private String exitMessage;
}
