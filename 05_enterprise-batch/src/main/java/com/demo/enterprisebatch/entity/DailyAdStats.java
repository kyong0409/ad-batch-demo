package com.demo.enterprisebatch.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyAdStats {

    private Long id;
    private LocalDate statDate;
    private String campaignId;
    private String adId;
    private Long impressionCount;
    private Long clickCount;
    private BigDecimal ctr;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStatDate() {
        return statDate;
    }

    public void setStatDate(LocalDate statDate) {
        this.statDate = statDate;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public Long getImpressionCount() {
        return impressionCount;
    }

    public void setImpressionCount(Long impressionCount) {
        this.impressionCount = impressionCount;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyAdStats that = (DailyAdStats) o;
        return java.util.Objects.equals(statDate, that.statDate)
                && java.util.Objects.equals(campaignId, that.campaignId)
                && java.util.Objects.equals(adId, that.adId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(statDate, campaignId, adId);
    }
}
