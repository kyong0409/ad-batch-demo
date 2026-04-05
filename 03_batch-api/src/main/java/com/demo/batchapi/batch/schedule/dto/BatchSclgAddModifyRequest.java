package com.demo.batchapi.batch.schedule.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BatchSclgAddModifyRequest {
    private String jobNm;
    private String jobGroup;
    private String jobDesc;
    private String paramSbst;
    private String perdSbst;
    private String useYn;
}
