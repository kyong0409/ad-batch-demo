package com.demo.entfailbatch.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderSyncResult {

    private Long id;
    private String orderId;
    private String customerName;
    private BigDecimal amount;
    private String status;
    private LocalDateTime syncedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }

    @Override
    public String toString() {
        return "OrderSyncResult{orderId='" + orderId + "', customerName='" + customerName +
               "', amount=" + amount + ", status='" + status + "', syncedAt=" + syncedAt + "}";
    }
}
