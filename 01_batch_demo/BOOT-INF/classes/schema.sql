CREATE TABLE ad_click_log (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    campaign_id VARCHAR(50) NOT NULL,
    ad_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(20) NOT NULL,
    event_time DATETIME NOT NULL,
    user_id VARCHAR(100) NOT NULL
);

CREATE TABLE daily_ad_stats (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    stat_date DATE NOT NULL,
    campaign_id VARCHAR(50) NOT NULL,
    ad_id VARCHAR(50) NOT NULL,
    impression_count BIGINT DEFAULT 0,
    click_count BIGINT DEFAULT 0,
    ctr DECIMAL(5,4) DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT uq_daily_ad_stats UNIQUE (stat_date, campaign_id, ad_id)
);
