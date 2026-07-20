CREATE SCHEMA IF NOT EXISTS customer;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS customer.customers
(
    id uuid NOT NULL,
    username character varying NOT NULL,
    first_name character varying NOT NULL,
    last_name character varying NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id),
    CONSTRAINT customers_username_unique UNIQUE (username)
);

DO $$
BEGIN
    IF to_regclass('customer.order_customer_m_view') IS NULL THEN
        EXECUTE '
            CREATE MATERIALIZED VIEW customer.order_customer_m_view
            AS
            SELECT id,
                   username,
                   first_name,
                   last_name
            FROM customer.customers
            WITH DATA
        ';
    END IF;
END $$;

REFRESH MATERIALIZED VIEW customer.order_customer_m_view;

CREATE OR REPLACE FUNCTION customer.refresh_order_customer_m_view()
RETURNS trigger
AS $$
BEGIN
    REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS refresh_order_customer_m_view ON customer.customers;

CREATE TRIGGER refresh_order_customer_m_view
AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
ON customer.customers
FOR EACH STATEMENT
EXECUTE PROCEDURE customer.refresh_order_customer_m_view();
