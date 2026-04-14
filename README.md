# 🚀 Core Banking API (Spring Boot)

A **production-style banking backend system** handling secure user authentication and financial transactions, built using **Spring Boot, JWT, and MySQL**.

---

## 📌 Overview

This project simulates a **real-world banking system** with support for:

* User registration & authentication
* Account management
* Money transactions (deposit, withdraw, transfer)
* Transaction history tracking
* Notification system

Designed using **clean architecture and backend best practices**, focusing on **security, data integrity, and scalability**.

---

## 🚀 Key Highlights

* Designed complete banking workflow (**User → Account → Transaction**)
* Implemented secure authentication using **JWT and BCrypt hashing**
* Ensured **data consistency and integrity** during financial operations
* Built REST APIs following **layered architecture principles**
* Developed system with focus on **transaction traceability and reliability**

---

## ⚙️ Tech Stack

* **Java 17**
* **Spring Boot 3**
* **Spring Security (JWT)**
* **Spring Data JPA (Hibernate)**
* **MySQL**
* **Maven**
* **Swagger (OpenAPI)**

---

## 🔐 Key Features

### 👤 User Management

* Register new users
* Secure password storage using **BCrypt hashing**
* Authenticate users using **JWT tokens**

### 🏦 Account Management

* Create bank accounts linked to users
* Maintain account balance and status

### 💸 Transactions

* Deposit money
* Withdraw money with validation
* Transfer funds between accounts
* Maintain complete transaction history

### 🔔 Notifications

* Track user notifications
* Support read/unread status

---

## 🧠 System Design Highlights

* **Layered Architecture**
  Controller → Service → Repository → Database

* **Security**

  * JWT-based authentication
  * Stateless API design
  * Secure password hashing using BCrypt

* **Data Integrity**

  * Transactions act as **source of truth**
  * Balance maintained as **optimized state**
  * Validations prevent invalid operations

---

## 📡 API Endpoints (Key)

### 🔐 Authentication

```text
POST   /api/v1/users              → Register new user
POST   /api/v1/auth/login        → Authenticate user and generate JWT
```

### 🏦 Account

```text
POST   /api/v1/users/{userId}/accounts   → Create account
GET    /api/v1/users/{userId}/accounts   → Fetch user accounts
```

### 💸 Transactions

```text
POST   /api/v1/accounts/{id}/deposit      → Deposit money
POST   /api/v1/accounts/{id}/withdraw     → Withdraw money
POST   /api/v1/accounts/{id}/transfer     → Transfer funds
GET    /api/v1/accounts/{id}/transactions → View transaction history
```

### 🔔 Notifications

```text
GET    /api/v1/users/{userId}/notifications
PATCH  /api/v1/users/{userId}/notifications/{id}/read
```

👉 Interactive API documentation available via Swagger UI

---

## 🚀 How to Run

### 1. Clone Repository

```bash
git clone https://github.com/your-username/CoreBanking-API.git
cd CoreBanking-API
```

---

### 2. Configure Database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/corebankingdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

---

### 3. Run Application

```bash
./mvnw spring-boot:run
```

---

### 4. Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 API Flow Example

1. Register User → `/api/v1/users`
2. Login → `/api/v1/auth/login`
3. Create Account → `/api/v1/users/{userId}/accounts`
4. Perform Transactions (Deposit / Withdraw / Transfer)
5. View Transaction History

---

## ⚠️ Important Concepts Implemented

* Secure password hashing (BCrypt)
* JWT-based authentication
* RESTful API design
* Exception handling
* Data consistency & validation
* Transaction traceability

---

## 📈 Future Improvements

* Role-based access control (Admin/User)
* Improved transaction atomicity handling
* Rate limiting & security hardening
* Migration to microservices architecture

---

## 👨‍💻 Author

**Ravi Raju Chintalapudi**
Java Backend Developer

---

## ⭐ Final Note

This project emphasizes **backend system design, security, and real-world implementation practices**, making it a strong foundation for scalable production systems.
