package com.demo.enterprisebatch.partitioner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.infrastructure.item.ExecutionContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CampaignPartitioner implements Partitioner {

    private static final Logger log = LoggerFactory.getLogger(CampaignPartitioner.class);

    private final DataSource dataSource;
    private final LocalDate targetDate;

    public CampaignPartitioner(DataSource dataSource, LocalDate targetDate) {
        this.dataSource = dataSource;
        this.targetDate = targetDate;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitions = new HashMap<>();
        String sql = "SELECT DISTINCT campaign_id FROM ent_ad_click_log WHERE CAST(event_time AS DATE) = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, targetDate);
            try (ResultSet rs = ps.executeQuery()) {
                int index = 0;
                while (rs.next()) {
                    String campaignId = rs.getString("campaign_id");
                    ExecutionContext context = new ExecutionContext();
                    context.putString("campaignId", campaignId);
                    context.putString("targetDate", targetDate.toString());
                    partitions.put("partition_" + index, context);
                    log.info("Created partition_{} for campaignId={}", index, campaignId);
                    index++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create partitions", e);
        }

        if (partitions.isEmpty()) {
            log.warn("No campaigns found for targetDate={}. Creating empty partition.", targetDate);
            ExecutionContext context = new ExecutionContext();
            context.putString("campaignId", "NONE");
            context.putString("targetDate", targetDate.toString());
            partitions.put("partition_0", context);
        }

        return partitions;
    }
}
