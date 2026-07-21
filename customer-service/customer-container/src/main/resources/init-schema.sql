CREATE SCHEMA IF NOT EXISTS customer;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS customer.customers
(
    id uuid NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    first_name character varying NOT NULL,
    last_name character varying NOT NULL,
    role character varying NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id),
    CONSTRAINT customers_username_unique UNIQUE (username)
);

ALTER TABLE customer.customers
    ADD COLUMN IF NOT EXISTS password character varying;

ALTER TABLE customer.customers
    ADD COLUMN IF NOT EXISTS role character varying;

UPDATE customer.customers
SET password = '$2a$10$XIrEyY6Hu9AhSkfvRa7C2.Pg1Z2yoyRETpNd0Fy7jeG1Jj4WCOarm'
WHERE password IS NULL;

UPDATE customer.customers
SET role = 'CUSTOMER'
WHERE role IS NULL;

ALTER TABLE customer.customers
    ALTER COLUMN password SET NOT NULL;

ALTER TABLE customer.customers
    ALTER COLUMN role SET NOT NULL;

CREATE TABLE IF NOT EXISTS customer.refresh_tokens
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    token_hash character varying NOT NULL,
    created_at timestamp with time zone NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    revoked boolean NOT NULL,
    CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id),
    CONSTRAINT refresh_tokens_token_hash_unique UNIQUE (token_hash),
    CONSTRAINT fk_refresh_tokens_customer_id FOREIGN KEY (customer_id)
        REFERENCES customer.customers (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_customer_id ON customer.refresh_tokens (customer_id);

DROP MATERIALIZED VIEW IF EXISTS customer.order_customer_m_view;

CREATE MATERIALIZED VIEW customer.order_customer_m_view
AS
SELECT id,
       username,
       first_name,
       last_name
FROM customer.customers
WITH DATA;

REFRESH MATERIALIZED VIEW customer.order_customer_m_view;

CREATE OR REPLACE FUNCTION customer.refresh_order_customer_m_view()
RETURNS trigger
AS '
BEGIN
    REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
    RETURN NULL;
END;
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS refresh_order_customer_m_view ON customer.customers;

CREATE TRIGGER refresh_order_customer_m_view
AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
ON customer.customers
FOR EACH STATEMENT
EXECUTE PROCEDURE customer.refresh_order_customer_m_view();
