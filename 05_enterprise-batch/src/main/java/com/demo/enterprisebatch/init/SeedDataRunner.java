package com.demo.enterprisebatch.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Profile("!test")
@Order(-1)
public class SeedDataRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public SeedDataRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private LocalDate resolveTargetDate() {
        String env = System.getenv("TARGET_DATE");
        if (env != null && !env.isBlank()) {
            return LocalDate.parse(env);
        }
        return LocalDate.now().minusDays(1);
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDate targetDate = resolveTargetDate();

        int expectedRows = 50000 + 20; // good + bad rows
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ent_ad_click_log WHERE CAST(event_time AS DATE) = ?",
                Integer.class, targetDate);
        if (count != null && count >= expectedRows) {
            log.info("Seed data already exists ({} rows) for targetDate={}. Skipping.", count, targetDate);
            return;
        }
        if (count != null && count > 0) {
            log.warn("Incomplete seed data found ({}/{} rows) for targetDate={}. Re-seeding...", count, expectedRows, targetDate);
            jdbcTemplate.update("DELETE FROM ent_ad_click_log WHERE CAST(event_time AS DATE) = ?", targetDate);
            jdbcTemplate.update("DELETE FROM ent_daily_ad_stats WHERE stat_date = ?", targetDate);
        }

        log.info("Seeding 50,000 rows into ent_ad_click_log for targetDate={}...", targetDate);
        long startMs = System.currentTimeMillis();

        String sql = "INSERT INTO ent_ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES (?, ?, ?, ?, ?)";
        String[] campaigns = {"CAMP_001", "CAMP_002", "CAMP_003", "CAMP_004", "CAMP_005"};
        String[] eventTypes = {"IMPRESSION", "IMPRESSION", "IMPRESSION", "IMPRESSION", "IMPRESSION",
                               "IMPRESSION", "IMPRESSION", "CLICK", "CLICK", "CLICK"};
        Random random = new Random(42);

        int totalRows = 50000;
        int batchSize = 1000;
        int userIndex = 1;

        List<Object[]> batch = new ArrayList<>(batchSize);
        for (int i = 0; i < totalRows; i++) {
            int campIdx = i / (totalRows / campaigns.length);
            if (campIdx >= campaigns.length) campIdx = campaigns.length - 1;
            String campaignId = campaigns[campIdx];

            int adNum = (i % 10) + 1;
            String adId = String.format("AD_%03d", (campIdx * 10) + adNum);

            String eventType = eventTypes[random.nextInt(eventTypes.length)];
            int hour = random.nextInt(24);
            int minute = random.nextInt(60);
            LocalDateTime eventTime = targetDate.atTime(hour, minute);
            String userId = String.format("user_%05d", userIndex++);

            batch.add(new Object[]{campaignId, adId, eventType, eventTime, userId});

            if (batch.size() == batchSize) {
                jdbcTemplate.batchUpdate(sql, batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batch);
        }

        // Add bad data rows for skip/retry demo
        // DB has NOT NULL constraints, so use values that pass DB but fail in processors
        int badRows = 0;
        List<Object[]> badBatch = new ArrayList<>();
        // 8 rows with invalid event_type → fails in EventToStatsProcessor
        for (int i = 0; i < 8; i++) {
            badBatch.add(new Object[]{"CAMP_001", "AD_001", "INVALID",
                    targetDate.atTime(10, 0), String.format("bad_user_%03d", i + 1)});
            badRows++;
        }
        // 6 rows with unknown event_type → fails in EventToStatsProcessor
        for (int i = 0; i < 6; i++) {
            badBatch.add(new Object[]{"CAMP_002", "AD_012", "BOUNCE",
                    targetDate.atTime(11, 0), String.format("bad_user_%03d", badRows + i + 1)});
            badRows++;
        }
        // 6 rows with whitespace campaign_id → filtered by ValidationProcessor after trim
        for (int i = 0; i < 6; i++) {
            badBatch.add(new Object[]{" ", "AD_005", "CLICK",
                    targetDate.atTime(12, 0), String.format("bad_user_%03d", badRows + i + 1)});
            badRows++;
        }
        jdbcTemplate.batchUpdate(sql, badBatch);
        log.info("Seeded {} bad rows for skip/retry demo", badRows);

        long elapsed = System.currentTimeMillis() - startMs;
        log.info("Seed data complete in {}ms ({} good + {} bad rows)", elapsed, totalRows, badRows);
    }
}
