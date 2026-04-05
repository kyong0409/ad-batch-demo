package com.demo.batchapi.batch.schedule.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BatchJobRestartRequest {
    private String jobNm;
    private String jobGroup;
}
