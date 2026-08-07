# Ticket Ordering Docker Compose

This compose stack runs the local infrastructure and all application services:

- PostgreSQL
- Redis
- Kafka KRaft cluster with three brokers
- Kafka Connect with Debezium PostgreSQL CDC
- order-service
- payment-service
- ticket-service
- customer-service

Start everything from the project root:

```bash
docker compose -f infrastructure/docker-compose/docker-compose.yml up --build
```

Stop everything:

```bash
docker compose -f infrastructure/docker-compose/docker-compose.yml down
```

Remove persisted PostgreSQL and Kafka data:

```bash
docker compose -f infrastructure/docker-compose/docker-compose.yml down -v
```

Host ports:

- order-service: `8181`
- payment-service: `8182`
- ticket-service: `8183`
- customer-service: `8184`
- PostgreSQL: `55432`
- Redis: `56379`
- Kafka brokers: `19092`, `29092`, `39092`
- Debezium Kafka Connect: `8083`

CDC outbox flow:

```text
service transaction -> service outbox table -> Debezium PostgreSQL connector -> Kafka topic
```

The compose stack disables the in-app outbox schedulers with
`OUTBOX_SCHEDULER_ENABLED=false` for order, payment, and ticket services. Debezium
captures inserts from these tables instead:

- `"order".order_outbox`
- `payment.payment_outbox`
- `ticket.ticket_outbox`

The connector routes each row to the Kafka topic stored in its `topic_name`
column and copies `payload_type` into the Spring Kafka `__TypeId__` header so
the existing consumers can deserialize the JSON payload.

Redis ticket holds:

```text
ticket reservation request -> Redis temporary hold with TTL -> PostgreSQL reservation
```

The compose stack enables Redis-backed ticket holds in `ticket-service` with
`TICKET_SERVICE_REDIS_RESERVATION_HOLDS_ENABLED=true`. Holds expire after
`TICKET_SERVICE_RESERVATION_HOLD_TTL_SECONDS` seconds and are released when the
reservation transaction completes.

Connector status:

```bash
curl http://localhost:8083/connectors/ticket-ordering-outbox-connector/status
```
