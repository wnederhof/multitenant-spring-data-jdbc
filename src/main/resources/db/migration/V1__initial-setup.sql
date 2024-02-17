CREATE TABLE customer
(
    id        BIGSERIAL PRIMARY KEY,
    tenant_id INT          NOT NULL DEFAULT current_setting('app.tenant_id')::INT,
    name      VARCHAR(255) NOT NULL,
    address   TEXT         NOT NULL
);

CREATE POLICY customer_tenant_isolation_policy ON customer
    USING (tenant_id = current_setting('app.tenant_id')::INT);

ALTER TABLE customer
    ENABLE ROW LEVEL SECURITY;
