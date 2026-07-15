-- ============================================================
-- Chronos: reporting schema tables (dimensions + facts)
-- ============================================================

-- ---------- DIMENSIONS ----------

CREATE TABLE reporting.company (
    id              BIGSERIAL PRIMARY KEY,
    company_code    VARCHAR(50)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_company_code UNIQUE (company_code)
);

CREATE TABLE reporting.organization (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL,
    parent_id       BIGINT,
    organization_code VARCHAR(50) NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_organization_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT fk_organization_parent
        FOREIGN KEY (parent_id) REFERENCES reporting.organization (id),
    CONSTRAINT uk_organization_code UNIQUE (organization_code)
);

CREATE TABLE reporting.product (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL,
    product_code    VARCHAR(50)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_product_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT uk_product_code UNIQUE (product_code)
);

CREATE TABLE reporting.activity (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL,
    activity_code   VARCHAR(50)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_activity_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT uk_activity_code UNIQUE (activity_code)
);

CREATE TABLE reporting.accounting_code (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL,
    code            VARCHAR(50)  NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_accounting_code_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT uk_accounting_code UNIQUE (code)
);

CREATE TABLE reporting.calendar (
    id              BIGSERIAL PRIMARY KEY,
    company_id      BIGINT NOT NULL,
    period          VARCHAR(20)  NOT NULL,
    day            DATE NOT NULL,
    is_working_day BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_calendar_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT uk_calendar_period_day UNIQUE (period, day)
);

CREATE TABLE reporting.employee (
    id                  BIGSERIAL PRIMARY KEY,
    company_id          BIGINT NOT NULL,
    organization_id     BIGINT NOT NULL,
    employee_code       VARCHAR(50)  NOT NULL,
    full_name           VARCHAR(200) NOT NULL,
    email               VARCHAR(255),
    monthly_cost        NUMERIC(19, 2) NOT NULL DEFAULT 0,
    standard_monthly_hours INTEGER NOT NULL DEFAULT 151,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at    TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_employee_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT fk_employee_organization
        FOREIGN KEY (organization_id) REFERENCES reporting.organization (id),
    CONSTRAINT uk_employee_company_code
        UNIQUE (company_id, employee_code)
);

-- ---------- FACTS ----------

CREATE TABLE reporting.employee_time (
    id              BIGSERIAL PRIMARY KEY,
    employee_id     BIGINT NOT NULL,
    product_id      BIGINT NOT NULL,
    activity_id     BIGINT NOT NULL,
    calendar_id     BIGINT NOT NULL,
    month           VARCHAR(20)  NOT NULL,
    hours           NUMERIC(19, 4) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_employee_time_employee
        FOREIGN KEY (employee_id) REFERENCES reporting.employee (id),
    CONSTRAINT fk_employee_time_product
        FOREIGN KEY (product_id) REFERENCES reporting.product (id),
    CONSTRAINT fk_employee_time_activity
        FOREIGN KEY (activity_id) REFERENCES reporting.activity (id),
    CONSTRAINT fk_employee_time_calendar
        FOREIGN KEY (calendar_id) REFERENCES reporting.calendar (id),
    CONSTRAINT uk_employee_time_unique
        UNIQUE (employee_id, product_id, activity_id, calendar_id)
);

CREATE TABLE reporting.employee_allocation (
    id                  BIGSERIAL PRIMARY KEY,
    employee_id         BIGINT NOT NULL,
    product_id          BIGINT NOT NULL,
    activity_id         BIGINT NOT NULL,
    accounting_code_id  BIGINT NOT NULL,
    company_id          BIGINT NOT NULL,
    month               VARCHAR(20)  NOT NULL,
    allocated_cost      NUMERIC(19, 2) NOT NULL DEFAULT 0,
    allocation_percentage NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at    TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_allocation_employee
        FOREIGN KEY (employee_id) REFERENCES reporting.employee (id),
    CONSTRAINT fk_allocation_product
        FOREIGN KEY (product_id) REFERENCES reporting.product (id),
    CONSTRAINT fk_allocation_activity
        FOREIGN KEY (activity_id) REFERENCES reporting.activity (id),
    CONSTRAINT fk_allocation_accounting_code
        FOREIGN KEY (accounting_code_id) REFERENCES reporting.accounting_code (id),
    CONSTRAINT fk_allocation_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT idx_allocation_company_month
        UNIQUE (company_id, month, employee_id, product_id, activity_id)
);

CREATE TABLE reporting.employee_report (
    id                  BIGSERIAL PRIMARY KEY,
    company_id          BIGINT NOT NULL,
    month               VARCHAR(20)  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    total_allocated_cost NUMERIC(19, 2),
    employee_count      INTEGER,
    note                TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at    TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_report_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id),
    CONSTRAINT uk_report_company_month UNIQUE (company_id, month)
);

CREATE TABLE reporting.employee_anomaly (
    id              BIGSERIAL PRIMARY KEY,
    employee_id     BIGINT NOT NULL,
    report_id       BIGINT NOT NULL,
    company_id      BIGINT NOT NULL,
    month           VARCHAR(20)  NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    severity        VARCHAR(20)  NOT NULL DEFAULT 'INFO',
    description     TEXT,
    resolved        BOOLEAN NOT NULL DEFAULT false,
    resolution_note TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_anomaly_employee
        FOREIGN KEY (employee_id) REFERENCES reporting.employee (id),
    CONSTRAINT fk_anomaly_report
        FOREIGN KEY (report_id) REFERENCES reporting.employee_report (id),
    CONSTRAINT fk_anomaly_company
        FOREIGN KEY (company_id) REFERENCES reporting.company (id)
);

-- ---------- APPLICATION USER ----------

CREATE TABLE reporting.app_user (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(200) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_app_user_email UNIQUE (email)
);