CREATE SCHEMA IF NOT EXISTS payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DO $$
BEGIN
    CREATE TYPE payment.payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

DO $$
BEGIN
    CREATE TYPE payment.transaction_type AS ENUM ('DEBIT', 'CREDIT');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

CREATE TABLE IF NOT EXISTS payment.payments
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    order_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    status payment.payment_status NOT NULL,
    created_at timestamp with time zone NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id),
    CONSTRAINT payments_order_id_unique UNIQUE (order_id)
);

CREATE TABLE IF NOT EXISTS payment.credit_entry
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    total_credit_amount numeric(10,2) NOT NULL,
    CONSTRAINT credit_entry_pkey PRIMARY KEY (id),
    CONSTRAINT credit_entry_customer_id_unique UNIQUE (customer_id)
);

CREATE TABLE IF NOT EXISTS payment.credit_history
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    amount numeric(10,2) NOT NULL,
    type payment.transaction_type NOT NULL,
    CONSTRAINT credit_history_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_payments_customer_id ON payment.payments (customer_id);
CREATE INDEX IF NOT EXISTS idx_credit_history_customer_id ON payment.credit_history (customer_id);
