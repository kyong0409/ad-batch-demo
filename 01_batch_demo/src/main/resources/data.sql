-- Seed data: clear and re-insert on every startup
DELETE FROM ad_click_log;

-- CAMP_001 / AD_001 (14 impressions, 6 clicks = ~30%)
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE,  10, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0001');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE,  25, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0002');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'CLICK',      DATEADD(MINUTE,  30, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0002');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE,  55, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0003');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE,  90, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0004');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'CLICK',      DATEADD(MINUTE,  95, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0004');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 130, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0005');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 200, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0006');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'CLICK',      DATEADD(MINUTE, 205, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0006');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 270, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0007');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 330, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0008');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'CLICK',      DATEADD(MINUTE, 335, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0008');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 400, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0009');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 480, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0010');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'CLICK',      DATEADD(MINUTE, 485, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0010');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 540, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0011');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_001', 'IMPRESSION', DATEADD(MINUTE, 600, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0012');

-- CAMP_001 / AD_002 (12 impressions, 4 clicks = ~25%)
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE,  15, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0013');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE,  45, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0014');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'CLICK',      DATEADD(MINUTE,  50, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0014');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 110, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0015');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 175, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0016');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'CLICK',      DATEADD(MINUTE, 180, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0016');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 240, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0017');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 310, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0018');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 370, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0019');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'CLICK',      DATEADD(MINUTE, 375, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0019');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 430, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0020');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 510, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0021');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 570, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0022');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'CLICK',      DATEADD(MINUTE, 575, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0022');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 630, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0023');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_001', 'AD_002', 'IMPRESSION', DATEADD(MINUTE, 700, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0024');

-- CAMP_002 / AD_003 (11 impressions, 4 clicks = ~27%)
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE,  20, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0025');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'CLICK',      DATEADD(MINUTE,  22, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0025');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE,  80, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0026');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 150, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0027');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'CLICK',      DATEADD(MINUTE, 155, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0027');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 220, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0028');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 290, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0029');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 360, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0030');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'CLICK',      DATEADD(MINUTE, 362, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0030');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 420, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0031');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 490, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0032');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'CLICK',      DATEADD(MINUTE, 495, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0032');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 560, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0033');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 620, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0034');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_003', 'IMPRESSION', DATEADD(MINUTE, 680, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0035');

-- CAMP_002 / AD_004 (13 impressions, 4 clicks = ~24%)
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE,  35, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0036');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE,  70, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0037');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'CLICK',      DATEADD(MINUTE,  73, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0037');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 140, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0038');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 210, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0039');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'CLICK',      DATEADD(MINUTE, 215, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0039');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 280, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0040');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 345, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0041');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 410, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0042');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'CLICK',      DATEADD(MINUTE, 413, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0042');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 470, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0043');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 530, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0044');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'CLICK',      DATEADD(MINUTE, 534, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0044');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 590, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0045');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 650, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0046');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_002', 'AD_004', 'IMPRESSION', DATEADD(MINUTE, 720, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0047');

-- CAMP_003 / AD_005 (12 impressions, 4 clicks = ~25%)
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE,   5, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0048');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'CLICK',      DATEADD(MINUTE,   8, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0048');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE,  60, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0049');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 120, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0050');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'CLICK',      DATEADD(MINUTE, 124, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0050');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 185, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0051');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 255, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0052');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 320, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0053');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'CLICK',      DATEADD(MINUTE, 323, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0053');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 385, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0054');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 445, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0055');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 505, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0056');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'CLICK',      DATEADD(MINUTE, 508, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0056');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 565, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0057');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 640, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0058');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_005', 'IMPRESSION', DATEADD(MINUTE, 710, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0059');

-- CAMP_003 / AD_006 (11 impressions, 5 clicks = ~31%)
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE,  40, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0060');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'CLICK',      DATEADD(MINUTE,  43, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0060');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 100, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0061');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 165, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0062');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'CLICK',      DATEADD(MINUTE, 168, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0062');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 230, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0063');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 300, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0064');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'CLICK',      DATEADD(MINUTE, 303, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0064');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 365, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0065');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 435, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0066');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 500, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0067');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'CLICK',      DATEADD(MINUTE, 503, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0067');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 555, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0068');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 615, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0069');
INSERT INTO ad_click_log (campaign_id, ad_id, event_type, event_time, user_id) VALUES ('CAMP_003', 'AD_006', 'IMPRESSION', DATEADD(MINUTE, 690, CAST(CAST(DATEADD(DAY, -1, GETDATE()) AS DATE) AS DATETIME)), 'user_0070');
