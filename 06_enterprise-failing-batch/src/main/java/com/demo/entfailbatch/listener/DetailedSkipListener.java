package com.demo.entfailbatch.listener;

import com.demo.entfailbatch.entity.OrderSync;
import com.demo.entfailbatch.entity.OrderSyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DetailedSkipListener implements SkipListener<OrderSync, OrderSyncResult> {

    private static final Logger log = LoggerFactory.getLogger(DetailedSkipListener.class);

    private final JdbcTemplate jdbcTemplate;

    public DetailedSkipListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("[SKIP-READ] Exception during read: type={}, message={}",
                t.getClass().getSimpleName(), t.getMessage());
    }

    @Override
    public void onSkipInProcess(OrderSync item, Throwable t) {
        log.warn("[SKIP-PROCESS] Skipped item: orderId={}, exception={}, message={}",
                item.getOrderId(), t.getClass().getSimpleName(), t.getMessage());
        updateSyncStatus(item.getOrderId(), "FAILED", t.getMessage());
    }

    @Override
    public void onSkipInWrite(OrderSyncResult item, Throwable t) {
        log.warn("[SKIP-WRITE] Skipped item on write: orderId={}, exception={}, message={}",
                item.getOrderId(), t.getClass().getSimpleName(), t.getMessage());
        updateSyncStatus(item.getOrderId(), "FAILED", t.getMessage());
    }

    private void updateSyncStatus(String orderId, String syncStatus, String errorMessage) {
        try {
            String truncatedError = errorMessage != null && errorMessage.length() > 500
                    ? errorMessage.substring(0, 500)
                    : errorMessage;
            jdbcTemplate.update(
                    "UPDATE ent_order_sync SET sync_status = ?, error_message = ? WHERE order_id = ?",
                    syncStatus, truncatedError, orderId);
            log.debug("[DetailedSkipListener] Updated sync_status={} for orderId={}", syncStatus, orderId);
        } catch (Exception e) {
            log.error("[DetailedSkipListener] Failed to update sync_status for orderId={}: {}", orderId, e.getMessage());
        }
    }
}
