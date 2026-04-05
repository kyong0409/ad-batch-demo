package com.demo.batchapi.batch.history.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class BatchHstStepDetails {
    private Long stepExecutionId;
    private String stepName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long commitCount;
    private Long readCount;
    private Long filterCount;
    private Long writeCount;
    private Long readSkipCount;
    private Long writeSkipCount;
    private Long processSkipCount;
    private Long rollbackCount;
    private String exitCode;
    private String exitMessage;
}
