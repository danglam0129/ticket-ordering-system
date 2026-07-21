DROP SCHEMA IF EXISTS ticket CASCADE;

CREATE SCHEMA ticket;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS ticket.ticket_status CASCADE;

CREATE TYPE ticket.ticket_status AS ENUM ('AVAILABLE', 'RESERVED', 'SOLD');

DROP TYPE IF EXISTS ticket.outbox_status CASCADE;

CREATE TYPE ticket.outbox_status AS ENUM ('STARTED', 'PROCESSING', 'COMPLETED', 'FAILED');

CREATE TABLE IF NOT EXISTS ticket.tickets
(
    id uuid NOT NULL,
    seat_id uuid NOT NULL,
    price numeric(10,2) NOT NULL,
    status ticket.ticket_status NOT NULL,
    reserved_by_order_id uuid,
    CONSTRAINT tickets_pkey PRIMARY KEY (id),
    CONSTRAINT tickets_seat_id_unique UNIQUE (seat_id)
);

CREATE TABLE IF NOT EXISTS ticket.ticket_outbox
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
    status ticket.outbox_status NOT NULL,
    retry_count integer NOT NULL DEFAULT 0,
    last_error text,
    CONSTRAINT ticket_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_tickets_status ON ticket.tickets (status);
CREATE INDEX IF NOT EXISTS idx_tickets_reserved_by_order_id ON ticket.tickets (reserved_by_order_id);
CREATE INDEX IF NOT EXISTS idx_ticket_outbox_status ON ticket.ticket_outbox (status);
CREATE INDEX IF NOT EXISTS idx_ticket_outbox_status_retry_created_at
    ON ticket.ticket_outbox (status, retry_count, created_at);
