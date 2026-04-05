package com.demo.batchapi.batch.history.dto;

import lombok.*;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class BatchHstJobParamDetails {
    private Long jobExecutionId;
    private String parameterName;
    private String parameterType;
    private String parameterValue;
}
