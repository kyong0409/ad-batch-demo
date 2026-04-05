package com.demo.enterprisebatch.processor;

import com.demo.enterprisebatch.entity.AdClickLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class ValidationProcessor implements ItemProcessor<AdClickLog, AdClickLog> {

    private static final Logger log = LoggerFactory.getLogger(ValidationProcessor.class);

    @Override
    public AdClickLog process(AdClickLog item) throws Exception {
        if (item.getCampaignId() == null || item.getCampaignId().isBlank()) {
            log.warn("Skipping row id={}: empty campaignId", item.getId());
            return null;
        }
        if (item.getAdId() == null || item.getAdId().isBlank()) {
            log.warn("Skipping row id={}: empty adId", item.getId());
            return null;
        }
        if (item.getEventTime() == null) {
            log.warn("Skipping row id={}: null eventTime", item.getId());
            return null;
        }
        if (item.getEventType() == null || item.getEventType().isBlank()) {
            log.warn("Skipping row id={}: null eventType, campaign={}, ad={}",
                    item.getId(), item.getCampaignId(), item.getAdId());
            return null;
        }
        String eventType = item.getEventType();
        if (!"IMPRESSION".equals(eventType) && !"CLICK".equals(eventType)) {
            log.warn("Invalid eventType '{}' for row id={}, campaign={}, ad={} — will be skipped downstream",
                    eventType, item.getId(), item.getCampaignId(), item.getAdId());
        }
        return item;
    }
}
