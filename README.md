# PaydayBank Microservices

PaydayBank is a backend banking application built with a Microservices architecture using Java and Spring Boot. It typically consists of decoupled services handling different domains of the banking application.

## Architecture

The system is composed of the following microservices:

1.  **Auth Service** (`:8085`)
    *   Handles User Signup and Login.
    *   Generates and Validates JWT Tokens.
    *   Stores authentication specific data 
    *   Communicates with User Service to create user profiles.
2.  **User Service** (`:8086`)
    *   Manages user profile information
    *   Stores detailed user records.
    *   Provides user data via internal APIs.
3.  **PostgreSQL** (`:5432`)
    *   Relational database used by both services.
4.  **Notification Service** (`:8087`)
    *   Listens to Kafka events and sends emails.
5.  **Email Confirmation Service** (`:8088`)
    *   Handles email verification links.
6.  **Infrastructure**
    *   Kafka, Zookeeper, MailHog

## Tech Stack

*   **Java 17**
*   **Spring Boot 3.2+**
*   **Spring Data JPA** (Hibernate)
*   **Spring Cloud OpenFeign** (Inter-service communication)
*   **PostgreSQL** (Database)
*   **Docker & Docker Compose** (Containerization & Orchestration)
*   **Lombok** (Boilerplate code reduction)
*   **MapStruct** (Object mapping)
*   **Apache Kafka** (Event Streaming)
*   **MailHog** (Email Testing)
*   **JWT** (JSON Web Tokens for Security)

## Getting Started

### Prerequisites

*   Docker Desktop installed and running.
*   Java 17+ installed (for local builds).
*   Maven installed.

### Installation & Running

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd paydaybank
    ```

2.  **Build the project:**
    You need to build the JAR files for the services first.
    ```bash
    mvn clean package -DskipTests
    ```

3.  **Run with Docker Compose:**
    This will start PostgreSQL, Auth Service, and User Service.
    ```bash
    docker compose up -d --build
    ```
    *   **Note:** If you made schema changes, make sure to remove old volumes:
        ```bash
        docker compose down -v
        docker compose up -d --build
        ```

## API Endpoints

### Auth Service (`http://localhost:8085`)

| Method | Endpoint | Description | Payload |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/signup` | Registers a new user. | `{ "firstName": "...", "lastName": "...", "email": "...", "password": "..." }` |
| `POST` | `/auth/login` | Logs in and returns JWT. | `{ "username": "email@example.com", "password": "..." }` |

### User Service (`http://localhost:8086`)

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/users` | Internal endpoint to create a user (Used by Auth Service). |
| `GET` | `/users/{id}` | Get user details by UUID. |

## Event Flow

1.  **Signup:** Client -> `Auth Service` -> (Feign) -> `User Service` (DB Save) -> `Auth Service` (DB Save) -> Client.
2.  **Login:** Client -> `Auth Service` (Verify & Update Last Login) -> Return JWT -> Client.

## Updates

*   **UUID Refactoring:** All IDs for Users and Authorizations have been migrated from `Long` to `UUID` for better scalability and security.

## Test Scenarios (Postman / cURL)

You can use the following commands to test the system manually.

### 1. Signup (Create New User)
```bash
curl -X POST http://localhost:8085/auth/signup \
-H "Content-Type: application/json" \
-d '{
  "firstName": "Ali",
  "lastName": "Veli",
  "email": "ali.veli@example.com",
  "password": "SecurePassword123",
  "phoneNumber": "5551234567",
  "gender": "MALE",
  "dateOfBirth": "1990-01-01"
}'
```

### 2. Login (Get Token)
```bash
curl -X POST http://localhost:8085/auth/login \
-H "Content-Type: application/json" \
-d '{
  "username": "ali.veli@example.com",
  "password": "SecurePassword123"
}'
```
*Response should return a JWT Token.*

### 3. Get User Profile (Using Token)
*(Note: Currently `/users/{id}` is public for testing, but typically requires token)*
```bash
# Gets user details using the ID identified from DB or response
curl -X GET http://localhost:8086/users/{USER_UUID_HERE}
```

### 4. Manual Notification Trigger (Test)
```bash
curl -X POST http://localhost:8087/notifications/email-confirmation \
-H "Content-Type: application/json" \
-d '{
    "userId": "11111111-1111-1111-1111-111111111111",
    "email": "test@example.com",
    "type": "REGISTRATION"
}'
```

### 5. Confirm Email (Test)
*(Replace UUID with the one received in MailHog at http://localhost:8025)*
```bash
curl "http://localhost:8088/confirm?id=aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"
```