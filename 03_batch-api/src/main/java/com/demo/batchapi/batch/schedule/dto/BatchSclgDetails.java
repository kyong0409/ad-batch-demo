package com.demo.batchapi.batch.schedule.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class BatchSclgDetails {
    private Long rowNum;
    private String jobNm;
    private String jobGroup;
    private String jobDesc;
    private String paramSbst;
    private String perdSbst;
    private String useYn;
    private String useYnNm;
    private String regrId;
    private String regrNm;
    private LocalDateTime regDt;
}
