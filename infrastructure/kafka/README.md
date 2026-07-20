# Kafka KRaft Runtime

This project uses a local three-node Kafka cluster in KRaft mode, without ZooKeeper.

Start Kafka:

```bash
docker compose -f infrastructure/kafka/docker-compose.kraft.yml up -d
```

Stop Kafka:

```bash
docker compose -f infrastructure/kafka/docker-compose.kraft.yml down
```

Host bootstrap servers:

```text
localhost:19092,localhost:29092,localhost:39092
```

The Spring Kafka config creates these topics with three partitions and replication factor three:

```text
payment-request
payment-response
ticket-reservation-request
ticket-reservation-response
ticket-approval-request
ticket-approval-response
```
