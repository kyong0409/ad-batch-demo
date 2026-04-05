package com.demo.enterprisebatch.processor;

import com.demo.enterprisebatch.entity.DailyAdStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.time.LocalDateTime;

public class TimestampEnrichmentProcessor implements ItemProcessor<DailyAdStats, DailyAdStats> {

    private static final Logger log = LoggerFactory.getLogger(TimestampEnrichmentProcessor.class);

    @Override
    public DailyAdStats process(DailyAdStats item) throws Exception {
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }
}
