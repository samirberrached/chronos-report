-- ============================================================
-- Chronos: staging + clean schemas (raw landing + cleansed)
-- ============================================================

CREATE TABLE staging.raw_employee_time (
    id          BIGSERIAL PRIMARY KEY,
    source_row  JSONB,
    loaded_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE clean.employee_time (
    id          BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(50),
    product_code  VARCHAR(50),
    activity_code VARCHAR(50),
    period        VARCHAR(20),
    hours         NUMERIC(19, 4),
    cleansed_at   TIMESTAMP NOT NULL DEFAULT now()
);