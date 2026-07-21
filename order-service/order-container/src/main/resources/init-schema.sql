DROP SCHEMA IF EXISTS "order" CASCADE;

CREATE SCHEMA "order";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS "order".order_status CASCADE;

CREATE TYPE "order".order_status AS ENUM ('PENDING', 'RESERVED', 'PAID', 'APPROVED', 'CANCELLING', 'CANCELLED');

DROP TYPE IF EXISTS "order".outbox_status CASCADE;

CREATE TYPE "order".outbox_status AS ENUM ('STARTED', 'PROCESSING', 'COMPLETED', 'FAILED');

CREATE TABLE IF NOT EXISTS "order".orders
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    tracking_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    order_status "order".order_status NOT NULL,
    failure_messages character varying,
    CONSTRAINT orders_pkey PRIMARY KEY (id),
    CONSTRAINT orders_tracking_id_unique UNIQUE (tracking_id)
);

CREATE TABLE IF NOT EXISTS "order".order_items
(
    id bigint NOT NULL,
    order_id uuid NOT NULL,
    ticket_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    sub_total numeric(10,2) NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id, order_id),
    CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id)
        REFERENCES "order".orders (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "order".order_idempotency
(
    idempotency_key character varying NOT NULL,
    order_id uuid NOT NULL,
    CONSTRAINT order_idempotency_pkey PRIMARY KEY (idempotency_key),
    CONSTRAINT order_idempotency_order_id_unique UNIQUE (order_id),
    CONSTRAINT fk_order_idempotency_order_id FOREIGN KEY (order_id)
        REFERENCES "order".orders (id)
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "order".order_outbox
(
    id uuid NOT NULL,
    saga_id character varying NOT NULL,
    aggregate_id character varying NOT NULL,
    event_type character varying NOT NULL,
    topic_name character varying NOT NULL,
    message_key character varying NOT NULL,
    payload_type character varying NOT NULL,
    payload text NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    status "order".outbox_status NOT NULL,
    retry_count integer NOT NULL DEFAULT 0,
    last_error text,
    CONSTRAINT order_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON "order".orders (customer_id);
CREATE INDEX IF NOT EXISTS idx_order_items_ticket_id ON "order".order_items (ticket_id);
CREATE INDEX IF NOT EXISTS idx_order_outbox_status ON "order".order_outbox (status);
CREATE INDEX IF NOT EXISTS idx_order_outbox_status_retry_created_at
    ON "order".order_outbox (status, retry_count, created_at);
