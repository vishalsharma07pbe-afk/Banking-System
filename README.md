# Banking System API

A Spring Boot backend project for managing core banking operations:
- Accounts (create, update, close/block/activate)
- Customers
- Employees
- Branches
- Transactions (deposit, withdraw, transfer, history)

## Tech Stack
- Java 17
- Spring Boot 4
- Spring Data JPA
- PostgreSQL (runtime)
- H2 (test)
- Maven Wrapper (`./mvnw`)
- SpringDoc OpenAPI (Swagger UI)

## Key Features
- Global API error handling with custom exceptions:
  - `ResourceNotFoundException`
  - `ConflictException`
  - `BadRequestException`
- Soft-delete / lifecycle-based status handling:
  - Employee termination and rehire support (record reuse)
  - Customer inactive/reactivation support
  - Branch close/reopen support
- Transaction audit records for:
  - Deposit
  - Withdrawal
  - Transfer

## Project Structure
- `account` - account lifecycle + balance operations
- `customer` - customer onboarding and lifecycle
- `employee` - employee onboarding and lifecycle
- `branch` - branch management and lifecycle
- `transaction` - transaction model/repository/dto
- `exception` - global exception mapping and API error model

## Prerequisites
- JDK 17+
- PostgreSQL running locally or remotely
- Maven (optional, wrapper included)

## Configuration
Main configuration file:
- `src/main/resources/application.properties`

Environment variables supported:
- `DB_HOST` (default: `localhost`)
- `DB_PORT` (default: `5432`)
- `DB_NAME` (default: `banking_system`)
- `DB_USERNAME` (default: `postgres`)
- `DB_PASSWORD` (default: `postgres`)
- `JWT_SECRET` (required, use a long random value and never commit it)
- `app.security.password-expiry-months` (default: `6`)
- `app.security.max-failed-login-attempts` (default: `5`)
- `app.security.lock-duration-minutes` (default: `30`)

## Run Locally
1. Create database:
```sql
CREATE DATABASE banking_system;
```

2. Start application:
```bash
export JWT_SECRET='replace-with-a-long-random-secret'
./mvnw spring-boot:run
```

3. Run tests:
```bash
./mvnw test
```

Application default port: `9090`

## API Base Paths
- Account: `/api/account`
- Customer: `/api/customers`
- Employee: `/api/employees`
- Branch: `/api/branches`

## Endpoint Quick Reference
### Account
- `GET /api/account/id/{id}`
- `POST /api/account`
- `PATCH /api/account/number/{accountNumber}`
- `DELETE /api/account/number/{accountNumber}`
- `GET /api/account/number/{accountNumber}`
- `GET /api/account/status/{status}`
- `GET /api/account/balance?min={min}&max={max}`
- `GET /api/account/email/{email}`
- `POST /api/account/number/{accountNumber}/close`
- `POST /api/account/number/{accountNumber}/block`
- `POST /api/account/number/{accountNumber}/activate`
- `POST /api/account/number/{accountNumber}/deposit?amount={amount}`
- `POST /api/account/number/{accountNumber}/withdraw?amount={amount}`
- `POST /api/account/transfer`
- `POST /api/account/number/{accountNumber}/transactionhistory`

### Customer
- `POST /api/customers`
- `GET /api/customers/{customerNumber}`
- `GET /api/customers`
- `DELETE /api/customers/{customerNumber}`
- `GET /api/customers/email/{email}`

### Employee
- `POST /api/employees`
- `GET /api/employees/{employeeCode}`
- `GET /api/employees`
- `PUT /api/employees/{employeeCode}`
- `DELETE /api/employees/{employeeCode}`
- `GET /api/employees/branch/{branchCode}`
- `GET /api/employees/role/{role}`
- `GET /api/employees/branch/{branchCode}/role/{role}`

### Branch
- `POST /api/branches`
- `GET /api/branches/{id}`
- `GET /api/branches`
- `PUT /api/branches/{id}`
- `DELETE /api/branches/{id}`
- `GET /api/branches/code/{branchCode}`
- `GET /api/branches/ifsc/{ifscCode}`

## Swagger / OpenAPI
After app startup, check:
- `/swagger-ui.html`
- `/v3/api-docs`

## Notes
- JWT signing is configured from the `JWT_SECRET` environment variable rather than a hardcoded source value.
- Password expiry and temporary lockout are maintained automatically from the authentication flow.
- `transaction/controller/TransactionController` is currently a placeholder.
- Current tests are basic (`contextLoads`); more feature-level tests are recommended.

## Author
Vishal Sharma
