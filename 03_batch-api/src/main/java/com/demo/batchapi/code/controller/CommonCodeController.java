package com.demo.batchapi.code.controller;

import com.demo.batchapi.common.CommonResponse;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RequestMapping("/api/om/code-mgt")
@RestController
public class CommonCodeController {

    private static final Map<String, List<Map<String, Object>>> CODE_MAP = new LinkedHashMap<>();

    static {
        CODE_MAP.put("BATCH_STATUS", List.of(
            Map.of("cdId", "COMPLETED", "cdNm", "완료", "cdGroupId", "BATCH_STATUS", "cdOrd", 1, "useYn", "Y"),
            Map.of("cdId", "FAILED", "cdNm", "실패", "cdGroupId", "BATCH_STATUS", "cdOrd", 2, "useYn", "Y"),
            Map.of("cdId", "STARTED", "cdNm", "실행중", "cdGroupId", "BATCH_STATUS", "cdOrd", 3, "useYn", "Y"),
            Map.of("cdId", "STARTING", "cdNm", "시작", "cdGroupId", "BATCH_STATUS", "cdOrd", 4, "useYn", "Y"),
            Map.of("cdId", "STOPPED", "cdNm", "중단", "cdGroupId", "BATCH_STATUS", "cdOrd", 5, "useYn", "Y"),
            Map.of("cdId", "UNKNOWN", "cdNm", "알수없음", "cdGroupId", "BATCH_STATUS", "cdOrd", 6, "useYn", "Y")
        ));
        CODE_MAP.put("BATCH_GROUP_NAME", List.of(
            Map.of("cdId", "DAILY", "cdNm", "일별", "cdGroupId", "BATCH_GROUP_NAME", "cdOrd", 1, "useYn", "Y"),
            Map.of("cdId", "WEEKLY", "cdNm", "주간", "cdGroupId", "BATCH_GROUP_NAME", "cdOrd", 2, "useYn", "Y"),
            Map.of("cdId", "MONTHLY", "cdNm", "월간", "cdGroupId", "BATCH_GROUP_NAME", "cdOrd", 3, "useYn", "Y")
        ));
        CODE_MAP.put("USE_YN", List.of(
            Map.of("cdId", "Y", "cdNm", "사용", "cdGroupId", "USE_YN", "cdOrd", 1, "useYn", "Y"),
            Map.of("cdId", "N", "cdNm", "미사용", "cdGroupId", "USE_YN", "cdOrd", 2, "useYn", "Y")
        ));
    }

    @GetMapping("/groups/{cdGroupId}/codes")
    public CommonResponse<Map<String, Object>> getCodesByGroup(@PathVariable String cdGroupId) {
        List<Map<String, Object>> result = CODE_MAP.getOrDefault(cdGroupId, List.of());
        return CommonResponse.success(Map.of("result", result));
    }
}
