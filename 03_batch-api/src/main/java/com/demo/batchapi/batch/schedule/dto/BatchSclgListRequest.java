package com.demo.batchapi.batch.schedule.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BatchSclgListRequest {
    private String jobGroup;
    private String useYn;
    private int offset;
    private int pageSize;
}
