package com.demo.enterprisebatch.processor;

import com.demo.enterprisebatch.entity.AdClickLog;
import com.demo.enterprisebatch.entity.DailyAdStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.math.BigDecimal;

public class EventToStatsProcessor implements ItemProcessor<AdClickLog, DailyAdStats> {

    private static final Logger log = LoggerFactory.getLogger(EventToStatsProcessor.class);

    @Override
    public DailyAdStats process(AdClickLog item) throws Exception {
        DailyAdStats stats = new DailyAdStats();
        stats.setStatDate(item.getEventTime().toLocalDate());
        stats.setCampaignId(item.getCampaignId());
        stats.setAdId(item.getAdId());

        switch (item.getEventType()) {
            case "IMPRESSION" -> {
                stats.setImpressionCount(1L);
                stats.setClickCount(0L);
            }
            case "CLICK" -> {
                stats.setImpressionCount(0L);
                stats.setClickCount(1L);
            }
            default -> {
                log.warn("SKIP unknown eventType '{}' for id={}, campaign={}, ad={}",
                        item.getEventType(), item.getId(), item.getCampaignId(), item.getAdId());
                return null;
            }
        }
        stats.setCtr(BigDecimal.ZERO);
        return stats;
    }
}
