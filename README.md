# PaydayBank Microservices

PaydayBank is a centralized, secure backend banking system built with **Java 17**, **Spring Boot 3.4.2**, and **Spring Cloud 2024.0.0**.

## Architecture Overview

The system uses a **Reactive API Gateway** as a single entry point. Microservices are isolated and verified through cryptographical identity injection from the Gateway.

- **Gateway Port**: `8080` (All traffic must flow here)
- **Infrastructure**: PostgreSQL, Apache Kafka, MailHog.

---

## Tech Stack

*   **Java 17** & **Spring Boot 3.4.2**
*   **Spring Cloud Gateway** (Reactive/WebFlux)
*   **Spring Data JPA** (Hibernate)
*   **Spring Cloud OpenFeign** (Inter-service communication)
*   **Apache Kafka** (Event Discovery/Streaming)
*   **PostgreSQL** (Relational Database)
*   **Docker & Docker Compose** (Containerization)
*   **Lombok** (Boilerplate reduction)
*   **MapStruct** (Object mapping)
*   **JWT** (Stateless authentication)
*   **MailHog** (Email testing tool)
*   **Maven** (Build automation)

---

## API Documentation (Gateway: http://localhost:8080)

### 1. Authentication Service
Handles user onboarding and security tokens.

| Method | Endpoint | Description | Payload (JSON) |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/signup` | Register a new user | `{"email": "user@example.com", "password": "password123", "firstName": "John", "lastName": "Doe", "phoneNumber": "5550001122", "gender": "MALE", "dateOfBirth": "1990-01-01"}` |
| `POST` | `/auth/login` | Obtain a JWT token | `{"username": "user@example.com", "password": "password123"}` |

### 2. Account Service
Manages bank accounts and audit history.
> [!IMPORTANT]
> Requires `Authorization: Bearer <token>`

| Method | Endpoint | Description | Payload (JSON) / Notes |
| :--- | :--- | :--- | :--- |
| `POST` | `/accounts` | Create a new bank account | `{"accountType": "DEBIT"}` (Options: DEBIT, DEPOSIT) |
| `GET` | `/accounts` | List all accounts of the user | *Identified automatically via JWT* |
| `GET` | `/accounts/{id}/transactions`| View account transaction history | Fetches from Transaction Service |

### 3. Transaction Service
Stores immutable financial audit logs.

| Method | Endpoint | Description | Notes |
| :--- | :--- | :--- | :--- |
| `GET` | `/transactions/accounts/{accountId}` | List transactions by account | External path via Gateway |

### 4. User Service
User profile management.

| Method | Endpoint | Description | Notes |
| :--- | :--- | :--- | :--- |
| `GET` | `/users/{id}` | Get specific user profile | Uses UUID |

### 5. Notification & Confirmation
Handles email flows and verification.

| Method | Endpoint | Description | Payload (JSON) |
| :--- | :--- | :--- | :--- |
| `POST` | `/notifications/email-confirmation` | Trigger manual email | `{"userId": "UUID", "email": "user@example.com", "type": "REGISTRATION"}` |
| `GET` | `/email-confirmations?id=...` | Confirm email via link | Re-routes to Confirmation Service |

---

## Getting Started

### Prerequisites
- Docker Desktop
- Java 17+ (for local builds)

### Build & Run
```bash
# 1. Build all JARs
mvn clean package -DskipTests

# 2. Start the cluster
docker compose up -d --build
```

## Security Model
- **Network Isolation**: Only the Gateway (8080) is exposed. Downstream services are unreachable from outside.
- **Identity Injected**: The Gateway verifies the JWT and injects `X-User-Id` and `X-User-Email` headers into internal requests.
- **Stateless**: All sessions are managed via JWT.

## Manual Data Insertion (Optional)

Since the database starts empty, you can manually insert multiple test transactions for your account by running these SQL commands. This allows you to test the chronological listing:

```sql
-- Replace '{ACCOUNT_UUID}' with a real account ID from your 'accounts' table

-- 1. Initial Deposit (2 days ago)
INSERT INTO transactions (id, account_id, amount, description, type, created_at)
VALUES (gen_random_uuid(), '{ACCOUNT_UUID}', 1000.00, 'Opening Balance', 'OPENING', CURRENT_TIMESTAMP - INTERVAL '2 days');

-- 2. Grocery Shopping (1 day ago)
INSERT INTO transactions (id, account_id, amount, description, type, created_at)
VALUES (gen_random_uuid(), '{ACCOUNT_UUID}', 50.75, 'Market Shopping', 'DEBIT', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- 3. Salary Payment (Today)
INSERT INTO transactions (id, account_id, amount, description, type, created_at)
VALUES (gen_random_uuid(), '{ACCOUNT_UUID}', 2500.00, 'Monthly Salary', 'CREDIT', CURRENT_TIMESTAMP);
```

### How to Run via Terminal

You can execute these commands directly from your terminal using the following template:

```bash
# Template:
docker exec -it paydaybank-postgres psql -U postgres -d paydaybank -c "INSERT INTO ..."

# Full example for the first transaction:
docker exec -it paydaybank-postgres psql -U postgres -d paydaybank -c "INSERT INTO transactions (id, account_id, amount, description, type, created_at) VALUES (gen_random_uuid(), '{ACCOUNT_UUID}', 1000.00, 'Opening Balance', 'OPENING', CURRENT_TIMESTAMP - INTERVAL '2 days');"
```

## Email Testing (MailHog)

All emails sent by the system (Registration, Account Confirmation, etc.) are captured by **MailHog** for easy development and testing.

- **SMTP Server**: `localhost:1025` (Internal usage)

You can view the content of sent emails, check headers, and verify that the system's notification flow is working correctly without needing a real SMTP server.

---
*Visit [MailHog](http://localhost:8025) to view outbound emails during testing.*
