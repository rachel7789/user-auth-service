# User Registration & Authentication Service

## Table of Contents
1. [Overview](#overview)
2. [Development Approach](#development-approach)
3. [Key Technical Decisions](#key-technical-decisions)
4. [Project Setup](#project-setup)
5. [Running the Application](#running-the-application)
6. [API Documentation](#api-documentation)
7. [Database Model](#database-model)
8. [Testing](#testing)
9. [Use of AI Tools](#use-of-ai-tools)
10. [Additional Notes & Assumptions](#additional-notes--assumptions)

---

## Overview

This project was developed independently as a home assignment for a **Java Developer** position at **Super-Pharm**.

The primary goal of this project was not to maximize the number of features, but to design and implement a **clean, structured, and readable authentication service**, similar to how a real production-grade system would be built in a professional environment.

---

## Development Approach

The project was developed in **clear, incremental stages**, where each stage was implemented, tested, and stabilized before moving on to the next one:

- Infrastructure and base configuration
- User registration
- Email verification
- Login flow
- JWT-based authentication
- Security configuration
- Profile management

Throughout development, special attention was given to:

- Clear separation between **Controller / Service / Security** layers
- Consistent and unified error handling
- Readable and maintainable code
- Explicit handling of authentication and authorization flows

---

### Bonus Features (Partial / Foundational)

Some optional features were implemented at a **basic or foundational level**, focusing on clean design and future extensibility rather than full production-scale implementations.

- A **basic refresh token infrastructure** was introduced, laying the groundwork for session renewal and revocation flows.
- Clear separation is maintained between **JWT-based authentication tokens** and **business tokens** (email verification, password reset).
- **Security-related events** (such as password reset requests and verification flows) are logged for visibility and traceability.

More advanced optional features (rate limiting, account lockout policies, Dockerization, Redis, and 2FA) were intentionally left out due to time constraints, as they were not required for the core assignment.

---

## Key Technical Decisions

- JWT-based **stateless authentication** (no server-side sessions)
- **Spring Security** with a dedicated JWT authentication filter
- **BCrypt** for secure password hashing
- **Swagger / OpenAPI** for API documentation and manual testing
- **H2 in-memory database** for simplicity and fast execution
- Unified error response format across the entire API
- JWT refresh mechanism using persistent refresh tokens

---

## Project Setup

### Prerequisites
- **Java 11 or higher (tested with Java 17)**
- **Maven 3.8 or higher**

### Clone the Repository

```bash
git clone https://github.com/rachel7789/user-auth-service
cd user-auth-service
```

## Running the Application

To run the application locally, execute the following command from the project root:

```bash
mvn clean spring-boot:run
```

The application will start on: http://localhost:8080

## API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

From the Swagger UI you can:

- Explore all available API endpoints
- Execute requests directly
- Authenticate using JWT via the **Authorize** button
- View example request and response payloads

---

## Database Model

The application uses an **H2 in-memory database**.

H2 Console: Accessible at http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:userauthdb).

### Main Entity: User

The User entity includes the following fields:

- `uid` (UUID, Primary Key)
- `email` (unique, not null)
- `passwordHash`
- `firstName`
- `lastName`
- `birthDate`
- `phoneNumber`
- `isVerified`
- `isActive`
- `registrationDate`
- `lastLoginDate`
- `verificationToken`
- `verificationTokenExpiry`
- `passwordResetToken`
- `passwordResetTokenExpiry`

The schema is automatically generated using **JPA/Hibernate**.

---

## Testing

### Manual Testing

All API flows were manually tested using **Swagger UI**, including:

- Registration
- Email verification
- Login
- Authenticated profile access
- Profile update
- Password reset flows

All API endpoints, example requests and responses are documented and tested via Swagger UI.

A Postman collection was not provided, as Swagger fully covers the required API documentation and testing flows.

JWT authentication failures return **401 (Unauthorized)**, while business tokens (email verification / password reset) return **400 (Bad Request)**.

### Automated Tests

The project includes **automated unit and integration tests** covering core authentication and account management flows.

**Unit tests** validate:
- User registration logic (including duplicate email handling)
- Email verification flow
- Password reset token generation and expiration handling

**Integration tests** validate:
- End-to-end registration and login flows
- Authentication-protected endpoints

Tests are implemented using **JUnit 5**, **Mockito**, and **Spring Boot Test**, and can be executed using:

```bash
mvn test
```

The current test suite focuses on critical business logic and security-sensitive flows, and is designed to be easily extended with additional coverage if needed.

---

## Use of AI Tools

During development, I used **ChatGPT** as an external working tool.
I deliberately chose this tool over others because maintaining **full control over the code** was important to me.

The tool was used for guidance, validation, refinement, and structured drafting, but **not for automatic code generation**.
I preferred ChatGPT over alternatives such as Gemini because it is already aligned with my working style from previous independent projects, and given the limited timeframe, I chose a familiar and reliable tool.

All architectural, logical, and security-related decisions were reviewed, fully understood, and implemented by me.

---

## Additional Notes & Assumptions

- The project fully implements all mandatory functional and technical requirements of the assignment.
- Bonus features (refresh tokens, rate limiting, Docker, etc.) were intentionally left out, as they were marked optional.
- The system was designed to be easily extended with additional security and scalability features.
