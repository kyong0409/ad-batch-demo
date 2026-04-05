package com.demo.enterprisebatch.processor;

import com.demo.enterprisebatch.entity.DailyAdStats;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CtrCalculationProcessor implements ItemProcessor<DailyAdStats, DailyAdStats> {

    @Override
    public DailyAdStats process(DailyAdStats item) throws Exception {
        if (item.getImpressionCount() == null || item.getImpressionCount() == 0) {
            item.setCtr(BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP));
        } else {
            BigDecimal clicks = BigDecimal.valueOf(item.getClickCount());
            BigDecimal impressions = BigDecimal.valueOf(item.getImpressionCount());
            BigDecimal ctr = clicks.divide(impressions, 6, RoundingMode.HALF_UP);
            item.setCtr(ctr);
        }
        return item;
    }
}
