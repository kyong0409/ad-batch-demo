package com.demo.enterprisebatch.listener;

import com.demo.enterprisebatch.entity.AdClickLog;
import com.demo.enterprisebatch.entity.DailyAdStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;

public class SkipLoggingListener implements SkipListener<AdClickLog, DailyAdStats> {

    private static final Logger log = LoggerFactory.getLogger(SkipLoggingListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("SKIP in read: exception={}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(AdClickLog item, Throwable t) {
        log.warn("SKIP in process: id={}, campaign={}, ad={}, eventType={}, exception={}",
                item.getId(), item.getCampaignId(), item.getAdId(),
                item.getEventType(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(DailyAdStats item, Throwable t) {
        log.warn("SKIP in write: campaign={}, ad={}, exception={}",
                item.getCampaignId(), item.getAdId(), t.getMessage());
    }
}
