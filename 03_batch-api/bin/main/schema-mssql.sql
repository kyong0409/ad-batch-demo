-- Batch Schedule Management Table (from dscore.fw-be)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'om_batch_sclg_bas')
CREATE TABLE om_batch_sclg_bas (
    job_nm      VARCHAR(30) NOT NULL,
    job_group   VARCHAR(30) NOT NULL,
    job_desc    VARCHAR(100) NULL,
    param_sbst  VARCHAR(300) NULL,
    perd_sbst   VARCHAR(100) NULL,
    use_yn      VARCHAR(1) NULL,
    regr_id     VARCHAR(64) NOT NULL,
    reg_dt      DATETIME2(6) NOT NULL,
    reg_pgm     VARCHAR(64) NOT NULL,
    amdr_id     VARCHAR(64) NULL,
    amd_dt      DATETIME2(6) NULL,
    amd_pgm     VARCHAR(64) NULL,
    CONSTRAINT pk_om_batch_sclg_bas PRIMARY KEY (job_nm)
);

-- Sample data
IF NOT EXISTS (SELECT 1 FROM om_batch_sclg_bas WHERE job_nm = 'dailyAdStats')
INSERT INTO om_batch_sclg_bas (job_nm, job_group, job_desc, perd_sbst, use_yn, regr_id, reg_dt, reg_pgm)
VALUES ('dailyAdStats', 'DAILY', N'일별 광고 성과 집계 배치', '0 0 2 * * ?', 'Y', 'system', GETDATE(), 'BatchSclgService');

IF NOT EXISTS (SELECT 1 FROM om_batch_sclg_bas WHERE job_nm = 'dataSyncJob')
INSERT INTO om_batch_sclg_bas (job_nm, job_group, job_desc, perd_sbst, use_yn, regr_id, reg_dt, reg_pgm)
VALUES ('dataSyncJob', 'DAILY', N'데이터 동기화 배치 (ERP 연동)', '0 0 3 * * ?', 'Y', 'system', GETDATE(), 'BatchSclgService');

IF NOT EXISTS (SELECT 1 FROM om_batch_sclg_bas WHERE job_nm = 'enterpriseAdStats')
INSERT INTO om_batch_sclg_bas (job_nm, job_group, job_desc, perd_sbst, use_yn, regr_id, reg_dt, reg_pgm)
VALUES ('enterpriseAdStats', 'DAILY', N'엔터프라이즈 광고 성과 집계 (파티셔닝/CSV)', '0 0 4 * * ?', 'Y', 'system', GETDATE(), 'BatchSclgService');

IF NOT EXISTS (SELECT 1 FROM om_batch_sclg_bas WHERE job_nm = 'orderSyncJob')
INSERT INTO om_batch_sclg_bas (job_nm, job_group, job_desc, perd_sbst, use_yn, regr_id, reg_dt, reg_pgm)
VALUES ('orderSyncJob', 'DAILY', N'주문 동기화 배치 (skip 정책 적용)', '0 0 5 * * ?', 'Y', 'system', GETDATE(), 'BatchSclgService');
