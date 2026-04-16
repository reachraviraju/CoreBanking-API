# Core Banking API

A production-style banking backend built with Spring Boot, JWT security, and MySQL.

This project models a real-world core banking flow:

`User -> Account -> Transaction -> Notification`

## Why This Project

This codebase is designed to demonstrate how financial systems are built with:

- clean layered architecture
- secure authentication and authorization
- data integrity during money movement
- traceable transaction records

## Key Features

- User registration with validation and BCrypt password hashing
- JWT-based login with stateless API security
- Account creation and account listing per user
- Money operations:
  - deposit
  - withdraw
  - transfer between accounts
- Notification center with unread count and mark-as-read flow
- Global exception handling with consistent API error responses

## Architecture

Layered architecture is used throughout the project:

`Controller -> Service -> Repository -> Entity`

Highlights:

- DTO-based request/response contracts
- JPA repositories for persistence
- Service-layer business rules for banking operations
- Access guard checks to prevent cross-user data access
- Pessimistic locking for safe concurrent transfers

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- JWT (`jjwt`)
- Spring Data JPA / Hibernate
- MySQL
- Maven
- SpringDoc OpenAPI (Swagger)

## API Snapshot

### Auth and Users

```text
POST   /api/v1/users                  Register user
POST   /api/v1/auth/login             Login and get JWT
```

### Accounts

```text
POST   /api/v1/users/{userId}/accounts    Create account
GET    /api/v1/users/{userId}/accounts    List user accounts
```

### Transactions

```text
POST   /api/v1/accounts/{accountId}/deposit       Deposit money
POST   /api/v1/accounts/{accountId}/withdraw      Withdraw money
POST   /api/v1/accounts/{accountId}/transfer      Transfer money
GET    /api/v1/accounts/{accountId}/transactions  Account transaction history
```

### Notifications

```text
GET    /api/v1/users/{userId}/notifications
GET    /api/v1/users/{userId}/notifications/unread-count
PATCH  /api/v1/users/{userId}/notifications/{notificationId}/read
```

## Security Model

- Public endpoints:
  - `POST /api/v1/users`
  - `POST /api/v1/auth/login`
- All other endpoints require `Authorization: Bearer <token>`
- Access is scoped to the authenticated user via guard checks
- Disabled users and invalid/expired tokens are blocked

## Getting Started

### 1. Clone

```bash
git clone https://github.com/reachraviraju/CoreBanking-API.git
cd CoreBanking-API
```

### 2. Configure Environment

The app reads credentials from environment variables with safe defaults for local dev:

- `MYSQL_USER` (default: `root`)
- `MYSQL_PASSWORD` (default: `admin`)
- `JWT_SECRET` (must be at least 32 bytes in production)
- `JWT_EXPIRATION_MS` (default: `86400000`)

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 4. Open API Docs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Local Testing

Run tests:

```bash
./mvnw test
```

The context test is configured with in-memory H2 so tests do not depend on local MySQL availability.

## Example Flow

1. Register a user
2. Login to receive JWT
3. Create an account for that user
4. Deposit funds
5. Withdraw or transfer funds
6. Read notifications and mark them as read

## Future Improvements

- Role-based authorization (admin/user roles)
- Audit trail enrichment for compliance-style reporting
- Rate limiting and abuse protection
- Idempotency support for payment-like operations

## Author

Ravi Raju Chintalapudi
