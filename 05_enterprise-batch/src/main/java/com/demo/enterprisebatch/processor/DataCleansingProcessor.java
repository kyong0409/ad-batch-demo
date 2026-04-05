package com.demo.enterprisebatch.processor;

import com.demo.enterprisebatch.entity.AdClickLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class DataCleansingProcessor implements ItemProcessor<AdClickLog, AdClickLog> {

    private static final Logger log = LoggerFactory.getLogger(DataCleansingProcessor.class);

    @Override
    public AdClickLog process(AdClickLog item) throws Exception {
        if (item.getCampaignId() != null) {
            item.setCampaignId(item.getCampaignId().trim().toUpperCase());
        }
        if (item.getAdId() != null) {
            item.setAdId(item.getAdId().trim().toUpperCase());
        }
        if (item.getEventType() != null) {
            item.setEventType(item.getEventType().trim().toUpperCase());
        }
        if (item.getUserId() != null) {
            item.setUserId(item.getUserId().trim());
        }
        return item;
    }
}
