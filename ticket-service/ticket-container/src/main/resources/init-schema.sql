CREATE SCHEMA IF NOT EXISTS ticket;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DO $$
BEGIN
    CREATE TYPE ticket.ticket_status AS ENUM ('AVAILABLE', 'RESERVED', 'SOLD');
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

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

CREATE INDEX IF NOT EXISTS idx_tickets_status ON ticket.tickets (status);
CREATE INDEX IF NOT EXISTS idx_tickets_reserved_by_order_id ON ticket.tickets (reserved_by_order_id);
