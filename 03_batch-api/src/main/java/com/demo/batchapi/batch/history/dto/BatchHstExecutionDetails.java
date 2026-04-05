package com.demo.batchapi.batch.history.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class BatchHstExecutionDetails {
    private List<BatchHstStepDetails> batchHstStepList;
    private List<BatchHstJobParamDetails> batchHstJobParamList;
}
