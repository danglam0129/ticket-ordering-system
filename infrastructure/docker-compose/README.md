# Ticket Ordering Docker Compose

This compose stack runs the local infrastructure and all application services:

- PostgreSQL
- Kafka KRaft cluster with three brokers
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
- Kafka brokers: `19092`, `29092`, `39092`
