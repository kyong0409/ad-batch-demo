package com.demo.entfailbatch.processor;

import com.demo.entfailbatch.entity.OrderSync;
import com.demo.entfailbatch.entity.OrderSyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class OrderSyncProcessor implements ItemProcessor<OrderSync, OrderSyncResult> {

    private static final Logger log = LoggerFactory.getLogger(OrderSyncProcessor.class);
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000");

    @Override
    public OrderSyncResult process(OrderSync item) throws Exception {
        log.debug("[OrderSyncProcessor] Processing: orderId={}, amount={}, customerName='{}'",
                item.getOrderId(), item.getAmount(), item.getCustomerName());

        // Validation: zero or negative amount
        if (item.getAmount() == null || item.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DataIntegrityViolationException(
                    "Amount must be positive: " + item.getOrderId());
        }

        // Validation: amount exceeds maximum limit
        if (item.getAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new DataIntegrityViolationException(
                    "Amount exceeds maximum limit: " + item.getOrderId());
        }

        // Validation: blank customer name
        if (item.getCustomerName() == null || item.getCustomerName().isBlank()) {
            throw new IllegalArgumentException(
                    "Customer name is required: " + item.getOrderId());
        }

        // Map to result
        OrderSyncResult result = new OrderSyncResult();
        result.setOrderId(item.getOrderId());
        result.setCustomerName(item.getCustomerName());
        result.setAmount(item.getAmount());
        result.setStatus("SYNCED");
        result.setSyncedAt(LocalDateTime.now());

        log.debug("[OrderSyncProcessor] Processed successfully: orderId={}", item.getOrderId());
        return result;
    }
}
