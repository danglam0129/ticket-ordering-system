# Ticket Ordering System

A backend-focused ticket ordering platform built as a Spring Boot microservices system. The project demonstrates domain-driven design, clean architecture, asynchronous workflows with Kafka, Saga orchestration, Transactional Outbox, Debezium CDC, Redis-based temporary ticket holds, and local container orchestration with Docker Compose.

> This repository is intentionally designed as a portfolio-grade backend system: the focus is distributed transaction handling, service boundaries, messaging reliability, and maintainable modular architecture.

## Table of Contents

- [System Overview](#system-overview)
- [Core Capabilities](#core-capabilities)
- [Technology Stack](#technology-stack)
- [High-Level Architecture](#high-level-architecture)
- [Microservices](#microservices)
- [Order Processing Flow](#order-processing-flow)
- [Low-Level Design](#low-level-design)
- [Data Model](#data-model)
- [Messaging Design](#messaging-design)
- [Transactional Outbox and Debezium CDC](#transactional-outbox-and-debezium-cdc)
- [Redis Ticket Holds](#redis-ticket-holds)
- [REST API](#rest-api)
- [Local Development](#local-development)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Design Trade-offs](#design-trade-offs)
- [Future Improvements](#future-improvements)

## System Overview

The system models a ticket purchase workflow across four business domains:

- **Customer**: registration, authentication, refresh tokens, and customer lookup.
- **Order**: order creation, tracking, state transitions, idempotency, and Saga coordination.
- **Ticket**: ticket reservation, approval, rejection, and temporary Redis holds.
- **Payment**: payment completion, cancellation, credit entry, and credit history.

The main challenge is that a single order requires coordinated work across multiple services:

1. An order is created.
2. Requested tickets are reserved.
3. Payment is processed.
4. Tickets are approved/sold.
5. The order reaches a final approved or cancelled state.

Because each service owns its own schema and data, the project avoids a distributed database transaction. Instead, it uses Kafka-based Saga workflows and outbox-backed event publishing.

## Core Capabilities

- Multi-module Maven monorepo with 4 Spring Boot services.
- DDD-style domain modules for order, customer, payment, and ticket domains.
- Clean architecture separation between domain logic, application use cases, REST adapters, messaging adapters, and persistence adapters.
- Kafka-based asynchronous service communication.
- Saga-based distributed order processing.
- Transactional Outbox tables for reliable event persistence.
- Debezium PostgreSQL CDC connector for publishing outbox events to Kafka.
- Redis-backed temporary ticket holds with TTL.
- JWT access tokens and refresh token rotation.
- Idempotent order creation using an idempotency key.
- Docker Compose stack for PostgreSQL, Redis, Kafka KRaft, Debezium Kafka Connect, and all services.
- `ResponseEntity`-based REST API responses with explicit HTTP status codes.

## Technology Stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 |
| Build | Maven Wrapper, multi-module Maven |
| Database | PostgreSQL |
| Messaging | Apache Kafka |
| CDC | Debezium PostgreSQL Connector, Kafka Connect |
| Cache / Temporary Holds | Redis |
| Persistence | Spring Data JPA, Hibernate |
| API | Spring Web, Jakarta Validation |
| Auth | JWT access tokens, refresh token rotation |
| Containers | Docker, Docker Compose |
| Testing | JUnit, Mockito, Maven Surefire, JaCoCo |

## High-Level Architecture

```text
                        +----------------------+
                        |      API Client      |
                        +----------+-----------+
                                   |
                 +-----------------+-----------------+
                 |                                   |
                 v                                   v
        +----------------+                  +-------------------+
        | customer-svc   |                  | order-service     |
        | REST API       |                  | REST API + Saga   |
        +-------+--------+                  +---------+---------+
                |                                     |
                v                                     v
        +----------------+                  +-------------------+
        | customer schema|                  | order schema      |
        | PostgreSQL     |                  | outbox table      |
        +----------------+                  +---------+---------+
                                                        |
                                                        | CDC
                                                        v
                                            +-------------------+
                                            | Debezium Connect  |
                                            +---------+---------+
                                                      |
                                                      v
                                            +-------------------+
                                            | Kafka Topics      |
                                            +----+---------+----+
                                                 |         |
                    +----------------------------+         +----------------------------+
                    v                                                                   v
        +------------------------+                                      +------------------------+
        | ticket-service         |                                      | payment-service        |
        | Kafka consumers        |                                      | Kafka consumers        |
        | Redis ticket holds     |                                      | payment processing     |
        +-----------+------------+                                      +-----------+------------+
                    |                                                               |
                    v                                                               v
        +------------------------+                                      +------------------------+
        | ticket schema          |                                      | payment schema         |
        | outbox table           |                                      | outbox table           |
        +------------------------+                                      +------------------------+
```

## Microservices

### Customer Service

**Port:** `8184`

Responsibilities:

- Register customers.
- Authenticate customers.
- Generate JWT access tokens.
- Generate, store, rotate, and revoke refresh tokens.
- Return customer profile data.

Main modules:

- `customer-domain-core`
- `customer-application-serivce`
- `customer-application`
- `customer-dataaccess`
- `customer-container`

### Order Service

**Port:** `8181`

Responsibilities:

- Create orders.
- Track orders by tracking ID.
- Validate customer existence.
- Validate order total and duplicate ticket IDs.
- Maintain order state.
- Coordinate reservation, payment, and approval Sagas.
- Write outgoing messages to `order_outbox`.

Main modules:

- `order-domain-core`
- `order-application-service`
- `order-application`
- `order-dataaccess`
- `order-messaging`
- `order-container`

### Ticket Service

**Port:** `8183`

Responsibilities:

- Consume ticket reservation requests.
- Hold requested tickets in Redis with TTL before PostgreSQL reservation.
- Reserve available tickets.
- Approve paid tickets.
- Reject reservation or approval requests when validation fails.
- Write outgoing messages to `ticket_outbox`.

Main modules:

- `ticket-domain-core`
- `ticket-application-service`
- `ticket-application`
- `ticket-dataaccess`
- `ticket-messaging`
- `ticket-container`

### Payment Service

**Port:** `8182`

Responsibilities:

- Consume payment requests.
- Complete payment when credit is sufficient.
- Cancel payment when order rollback is required.
- Maintain payment, credit entry, and credit history data.
- Write outgoing messages to `payment_outbox`.

Main modules:

- `payment-domain-core`
- `payment-application-service`
- `payment-dataaccess`
- `payment-messaging`
- `payment-container`

## Order Processing Flow

### Happy Path

```text
1. Client -> order-service
   POST /api/orders

2. order-service
   - validates customer
   - validates order amount
   - creates order with PENDING status
   - writes TicketReservationRequest to order_outbox

3. Debezium
   - captures order_outbox insert from PostgreSQL WAL
   - routes event to ticket-reservation-request Kafka topic

4. ticket-service
   - consumes ticket-reservation-request
   - attempts Redis temporary holds for ticket IDs
   - validates ticket availability
   - updates tickets to RESERVED
   - writes TicketReservationResponse to ticket_outbox

5. Debezium
   - publishes ticket reservation response to Kafka

6. order-service
   - consumes ticket-reservation-response
   - updates order to RESERVED
   - writes PaymentRequest to order_outbox

7. payment-service
   - consumes payment-request
   - validates customer credit
   - creates payment record
   - writes PaymentResponse to payment_outbox

8. order-service
   - consumes payment-response
   - updates order to PAID
   - writes TicketApprovalRequest to order_outbox

9. ticket-service
   - consumes ticket-approval-request
   - marks reserved tickets as SOLD
   - writes TicketApprovalResponse to ticket_outbox

10. order-service
    - consumes ticket-approval-response
    - updates order to APPROVED
```

### Rejection / Rollback Paths

| Failure | Service detecting it | Result |
| --- | --- | --- |
| Customer does not exist | order-service | order creation fails |
| Duplicate ticket IDs | order-service | order creation fails |
| Ticket not found | ticket-service | reservation rejected |
| Ticket already reserved/sold | ticket-service | reservation rejected |
| Redis hold already owned by another order | ticket-service | reservation rejected |
| Insufficient credit | payment-service | payment failed response |
| Ticket approval fails | ticket-service | approval rejected and compensation starts |

## Low-Level Design

Each business service follows the same broad module split:

```text
*-domain-core
  Pure domain entities, value objects, domain services, and domain events.

*-application-service
  Use cases, command handlers, Saga handlers, ports, DTOs, and mappers.

*-application
  REST controllers and service-specific exception handlers.

*-dataaccess
  JPA entities, Spring Data repositories, persistence adapters, and data mappers.

*-messaging
  Kafka listeners, Kafka publishers, outbox schedulers, messaging mappers.

*-container
  Spring Boot application entry point, bean configuration, application.yml, schema/data scripts.
```

The direction of dependency follows clean architecture:

```text
REST / Messaging / Data Access adapters
             |
             v
Application Service Ports and Use Cases
             |
             v
Domain Core
```

Domain code does not depend on Spring Web, Kafka, JPA, or Redis. External systems are accessed through output ports implemented by adapter modules.

## Data Model

PostgreSQL is split by schema:

| Schema | Owner |
| --- | --- |
| `customer` | customer-service |
| `"order"` | order-service |
| `payment` | payment-service |
| `ticket` | ticket-service |

### Customer Schema

Main tables:

- `customers`
- `refresh_tokens`

Used for:

- Customer identity.
- Password hash storage.
- JWT claim source data.
- Refresh token lifecycle.

### Order Schema

Main tables:

- `orders`
- `order_items`
- `order_idempotency`
- `order_outbox`

Important order statuses:

- `PENDING`
- `RESERVED`
- `PAID`
- `APPROVED`
- `CANCELLING`
- `CANCELLED`

### Ticket Schema

Main tables:

- `tickets`
- `ticket_outbox`

Important ticket statuses:

- `AVAILABLE`
- `RESERVED`
- `SOLD`

### Payment Schema

Main tables:

- `payments`
- `credit_entry`
- `credit_history`
- `payment_outbox`

Important payment statuses:

- `COMPLETED`
- `CANCELLED`
- `FAILED`

## Messaging Design

Kafka topics:

| Topic | Producer | Consumer | Purpose |
| --- | --- | --- | --- |
| `ticket-reservation-request` | order-service | ticket-service | Ask ticket-service to reserve tickets |
| `ticket-reservation-response` | ticket-service | order-service | Report reservation approved/rejected |
| `payment-request` | order-service | payment-service | Ask payment-service to complete/cancel payment |
| `payment-response` | payment-service | order-service | Report payment completed/failed/cancelled |
| `ticket-approval-request` | order-service | ticket-service | Ask ticket-service to sell/approve reserved tickets |
| `ticket-approval-response` | ticket-service | order-service | Report final ticket approval result |

Messages are represented by model classes in `infrastructure/kafka/kafka-model`.

## Transactional Outbox and Debezium CDC

The project uses the Transactional Outbox pattern to avoid losing messages between a database commit and Kafka publish.

Instead of directly publishing to Kafka inside a business transaction, services write an outbox row in the same transaction as the domain state change.

```text
Business data update + outbox insert
             |
             v
       PostgreSQL commit
             |
             v
      Debezium reads WAL
             |
             v
      Kafka Connect emits event
             |
             v
        Kafka topic
```

Outbox tables:

- `"order".order_outbox`
- `payment.payment_outbox`
- `ticket.ticket_outbox`

Each outbox row contains:

- `id`
- `saga_id`
- `aggregate_id`
- `event_type`
- `topic_name`
- `message_key`
- `payload_type`
- `payload`
- `created_at`
- `updated_at`
- `status`
- `retry_count`
- `last_error`

Debezium connector config:

```text
infrastructure/docker-compose/debezium/postgres-outbox-connector.json
```

The connector uses Debezium's Outbox Event Router to:

- watch the service outbox tables,
- route each event to the Kafka topic stored in `topic_name`,
- use `message_key` as Kafka message key,
- expand the JSON payload,
- propagate `payload_type` as Spring Kafka's `__TypeId__` header.

The in-app outbox scheduler still exists as a fallback mechanism, but Docker Compose disables it with:

```text
OUTBOX_SCHEDULER_ENABLED=false
```

## Redis Ticket Holds

The ticket service uses Redis for temporary ticket holds during reservation processing.

Why this exists:

- PostgreSQL remains the source of truth.
- Redis provides a short-lived guard against concurrent reservation attempts.
- A ticket can be temporarily held by one order while reservation validation runs.

Implementation behavior:

- Key format: `ticket:reservation:hold:{ticketId}`
- Value: `orderId`
- Operation: Redis `SET NX` with TTL
- Default TTL: `600` seconds
- If a ticket is already held by another order, the reservation is rejected.
- Holds are released after the reservation transaction completes.
- If Redis is unavailable, the service logs a warning and continues without temporary holds.

Docker Compose enables this feature only for `ticket-service`:

```text
TICKET_SERVICE_REDIS_RESERVATION_HOLDS_ENABLED=true
TICKET_SERVICE_RESERVATION_HOLD_TTL_SECONDS=600
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379
```

## REST API

REST endpoints return `ResponseEntity` with explicit HTTP status codes.

### Customer API

Base URL:

```text
http://localhost:8184/api/customers
```

#### Register Customer

```http
POST /api/customers/register
```

Status:

```text
201 Created
```

Request:

```json
{
  "username": "john.doe",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Login

```http
POST /api/customers/login
```

Status:

```text
200 OK
```

Request:

```json
{
  "username": "john.doe",
  "password": "password123"
}
```

#### Refresh Token

```http
POST /api/customers/token/refresh
```

Status:

```text
200 OK
```

Request:

```json
{
  "refreshToken": "refresh-token"
}
```

#### Logout

```http
POST /api/customers/logout
```

Status:

```text
200 OK
```

Request:

```json
{
  "refreshToken": "refresh-token"
}
```

#### Get Customer

```http
GET /api/customers/{customerId}
```

Status:

```text
200 OK
```

### Order API

Base URL:

```text
http://localhost:8181/api/orders
```

#### Create Order

```http
POST /api/orders
```

Status:

```text
201 Created
```

Request:

```json
{
  "idempotencyKey": "create-order-001",
  "customerId": "d215b5f8-0249-4dc5-89a3-51fd148cfb41",
  "price": 200.00,
  "items": [
    {
      "ticketId": "d215b5f8-0249-4dc5-89a3-51fd148cfb48",
      "price": 100.00
    },
    {
      "ticketId": "d215b5f8-0249-4dc5-89a3-51fd148cfb49",
      "price": 100.00
    }
  ]
}
```

#### Track Order

```http
GET /api/orders/{trackingId}
```

Status:

```text
200 OK
```

## Local Development

### Prerequisites

- Java 17
- Docker
- Docker Compose
- Bash-compatible shell

The project uses Maven Wrapper, so installing Maven globally is not required.

### Build and Test

```bash
./mvnw test
```

### Start Full Local Stack

```bash
docker compose -f infrastructure/docker-compose/docker-compose.yml up --build
```

Services:

| Component | Host Port |
| --- | --- |
| order-service | `8181` |
| payment-service | `8182` |
| ticket-service | `8183` |
| customer-service | `8184` |
| PostgreSQL | `55432` |
| Redis | `56379` |
| Kafka broker 1 | `19092` |
| Kafka broker 2 | `29092` |
| Kafka broker 3 | `39092` |
| Debezium Kafka Connect | `8083` |

### Stop Stack

```bash
docker compose -f infrastructure/docker-compose/docker-compose.yml down
```

### Stop and Remove Volumes

```bash
docker compose -f infrastructure/docker-compose/docker-compose.yml down -v
```

Use `down -v` when you want a clean PostgreSQL/Kafka state.

### Check Debezium Connector Status

```bash
curl http://localhost:8083/connectors/ticket-ordering-outbox-connector/status
```

### Check Kafka Topics from a Broker Container

```bash
docker exec -it ticket-kafka-1 kafka-topics --bootstrap-server kafka-1:9092 --list
```

## Testing

Run all tests:

```bash
./mvnw test
```

The current test coverage focuses on core application service behavior for:

- customer registration,
- login,
- refresh token rotation,
- logout,
- customer lookup,
- order creation,
- idempotent order creation,
- reservation Saga behavior,
- payment Saga behavior,
- approval Saga behavior.

JaCoCo is configured in the root Maven build and generates coverage data during test runs.

## Project Structure

```text
ticket-ordering-system/
  common/
    common-domain/
    common-application/
    common-dataaccess/

  infrastructure/
    docker-compose/
      docker-compose.yml
      debezium/
        postgres-outbox-connector.json
    kafka/
      kafka-config-data/
      kafka-consumer/
      kafka-model/
      kafka-producer/
    saga/

  customer-service/
    customer-domain/
    customer-application/
    customer-dataaccess/
    customer-messaging/
    customer-container/

  order-service/
    order-domain/
    order-application/
    order-dataaccess/
    order-messaging/
    order-container/

  payment-service/
    payment-domain/
    payment-dataaccess/
    payment-messaging/
    payment-container/

  ticket-service/
    ticket-domain/
    ticket-application/
    ticket-dataaccess/
    ticket-messaging/
    ticket-container/
```

## Design Trade-offs

### Why Saga Instead of Distributed Transactions?

The system uses service-owned schemas. A single order touches order, ticket, and payment data. A distributed database transaction would tightly couple services and infrastructure. Saga keeps each service autonomous and uses events to coordinate state transitions.

### Why Transactional Outbox?

Publishing directly to Kafka after a database update can lose events if the service crashes after commit but before publish. The outbox pattern stores the event in the same transaction as the state change.

### Why Debezium CDC?

Debezium removes the need for services to poll their own outbox tables. It reads committed outbox inserts from PostgreSQL WAL and publishes them to Kafka through Kafka Connect.

### Why Redis for Ticket Holds?

Redis is used as a temporary concurrency guard. It reduces the chance of competing reservation attempts working on the same tickets at the same time. PostgreSQL remains the durable source of truth.

### Why Keep the Outbox Scheduler?

The scheduler can act as a fallback path outside the Debezium-enabled Docker Compose stack. In the Docker Compose environment, it is disabled to avoid double publishing.

## Future Improvements

- Add integration tests with Testcontainers for PostgreSQL, Kafka, Redis, and Debezium.
- Add OpenAPI documentation.
- Add a gateway service for routing and authentication enforcement.
- Add role-based authorization for customer and admin operations.
- Add observability with structured logs, tracing, and metrics.
- Add CI pipeline for build, tests, SCA, SAST, and container image checks.
- Add end-to-end order flow tests that verify Kafka messages and database state.
- Add database migration tooling such as Flyway or Liquibase.
- Add production-oriented secrets management.
