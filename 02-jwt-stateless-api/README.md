# Module 2: JWT Stateless API with Persistent Users

## Learning Objectives

By completing this module, you will understand:

- **Authentication vs Authorization** — verifying identity vs. checking permissions
- **User persistence** — storing users in PostgreSQL with JPA/Hibernate
- **BCrypt password hashing** — why plaintext passwords must never be stored
- **UserDetailsService** — Spring Security's bridge to your user database
- **DaoAuthenticationProvider** — the component that orchestrates login verification
- **AuthenticationManager** — the top-level entry point for authentication
- **JWT (JSON Web Tokens)** — stateless tokens for API authentication
- **SecurityContext** — thread-local storage for the authenticated user
- **Stateless APIs** — why modern APIs don't use HTTP sessions
- **Roles** — USER vs ADMIN and how Spring Security checks them
- **Flyway migrations** — database schema version control

---

## Architecture

```text
Client
  |
  +---- POST /api/auth/register
  |          |
  |          v
  |      BCryptPasswordEncoder
  |          |
  |          v
  |      PostgreSQL (users table)
  |
  +---- POST /api/auth/login
  |          |
  |          v
  |  AuthenticationManager
  |          |
  |          v
  |  DaoAuthenticationProvider
  |       /             \
  |      v               v
  | UserDetailsService  PasswordEncoder
  |      |                   |
  |      v                   v
  | UserRepository      BCrypt.matches()
  |      |
  |      v
  | PostgreSQL
  |
  |          authentication success
  |                  |
  |                  v
  |              JwtService
  |                  |
  |                  v
  |               JWT token
  |
  +---- GET /api/users/me
             |
       Authorization: Bearer <JWT>
             |
             v
   JwtAuthenticationFilter
             |
             v
      SecurityContext
             |
             v
        UserController
```

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose

---

## Step-by-Step Walkthrough

### 1. Start PostgreSQL

```bash
cd 02-jwt-stateless-api
docker compose up -d
```

This starts PostgreSQL 17 on port 5432 with:
- Database: `securitydb`
- Username: `security`
- Password: `security`

### 2. Start the Application

```bash
mvn spring-boot:run
```

The application starts on port 8082. Flyway automatically creates the `users` table.

### 3. Register a User

```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
```

Response:
```json
{
  "id": 1,
  "username": "alice",
  "role": "USER"
}
```

**What happened:**
1. The request hit `AuthController.register()`.
2. The username was checked for duplicates.
3. The password was hashed with BCrypt.
4. The user was saved to PostgreSQL with `Role.USER`.
5. The response contains NO password or password hash.

### 4. Login

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

**What happened:**
1. The request hit `AuthController.login()`.
2. `AuthenticationManager.authenticate()` was called.
3. `DaoAuthenticationProvider` loaded the user via `UserDetailsService`.
4. `BCryptPasswordEncoder.matches()` verified the password.
5. On success, `JwtService.generate()` created the JWT.
6. The JWT was returned to the client.

### 5. Access Protected Endpoint

```bash
curl http://localhost:8082/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Response:
```json
{
  "username": "alice",
  "authorities": [{"authority": "ROLE_USER"}]
}
```

**What happened:**
1. `JwtAuthenticationFilter` extracted the JWT from the `Authorization` header.
2. The JWT signature and expiration were validated.
3. The username was extracted from the JWT.
4. `UserDetailsService` loaded the user from PostgreSQL.
5. An authenticated `Authentication` was stored in the `SecurityContext`.
6. The controller returned the user's information.

### 6. Access Without JWT

```bash
curl http://localhost:8082/api/users/me
```

Response: `401 Unauthorized`

### 7. Access with Invalid JWT

```bash
curl http://localhost:8082/api/users/me \
  -H "Authorization: Bearer invalid.token.here"
```

Response: `401 Unauthorized`

---

## AuthenticationManager vs AuthenticationProvider vs UserDetailsService

This is the most important concept in Spring Security authentication.

### AuthenticationManager

The **main authentication entry point**. When you call:

```java
authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(username, password));
```

It delegates to one or more `AuthenticationProvider` implementations.

### AuthenticationProvider

Knows **how to authenticate a particular credential type**. In this module, we use `DaoAuthenticationProvider`, which:

1. Calls `UserDetailsService.loadUserByUsername()` to load the user.
2. Calls `PasswordEncoder.matches()` to verify the password.

### UserDetailsService

**Loads the user's security information** from your data store. In this module:

```
UserRepository -> PostgreSQL
```

It returns a `UserDetails` object that Spring Security uses for authorization checks.

### PasswordEncoder

**Verifies the submitted password against the stored hash.** In this module, we use `BCryptPasswordEncoder`.

### The Mental Model

```
AuthenticationManager
        |
        v
AuthenticationProvider (DaoAuthenticationProvider)
        |
        +--> UserDetailsService --> UserRepository --> PostgreSQL
        |
        +--> PasswordEncoder (BCrypt.matches)
```

**Why this matters:** Each component has a single responsibility. You can swap any of them independently. Want to use LDAP? Replace UserDetailsService. Want Argon2 hashing? Replace PasswordEncoder.

---

## Password Hashing Flow

```
REGISTRATION:
  "password123"
        |
        v
  BCryptPasswordEncoder.encode()
        |
        v
  "$2a$10$N9qo8uLOickgx2ZMRZoMye..." (stored in DB)

LOGIN:
  "password123"
        |
        v
  BCryptPasswordEncoder.matches("password123", "$2a$10$N9qo8uLOickgx2ZMRZoMye...")
        |
        v
  true / false
```

BCrypt automatically includes a random salt, so two identical passwords produce different hashes.

---

## JWT Structure

```json
{
  "header": {"alg": "HS256", "typ": "JWT"},
  "payload": {
    "sub": "alice",
    "iat": 1700000000,
    "exp": 1700000900
  }
}
```

- **sub** — Subject (the username)
- **iat** — Issued At (when the token was created)
- **exp** — Expiration (when the token expires, default: 900 seconds = 15 minutes)

The signature is `HMAC-SHA256(header + payload, secret_key)`.

---

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/securitydb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `security` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `security` |
| `SECURITY_JWT_SECRET` | HMAC signing secret (min 32 chars) | development placeholder |
| `SECURITY_JWT_EXPIRATION_SECONDS` | Token lifetime | `900` |

### .env.example

See `.env.example` for a template. Copy it to `.env` and customize:

```bash
cp .env.example .env
```

---

## Endpoint Summary

| Method | Path | Auth Required | Description |
|--------|------|---------------|-------------|
| POST | `/api/auth/register` | No | Register a new user (always USER role) |
| POST | `/api/auth/login` | No | Login and receive a JWT |
| GET | `/api/users/me` | Yes | Get current user info |
| GET | `/api/users/admin` | Yes (ADMIN) | Admin-only endpoint |

---

## Error Responses

### Invalid Login
```http
HTTP/1.1 401 Unauthorized
```
```json
{"error": "Invalid username or password"}
```

### Duplicate Username
```http
HTTP/1.1 400 Bad Request
```
```json
{"error": "Username already exists"}
```

### Missing/Invalid JWT
```http
HTTP/1.1 401 Unauthorized
```

### Insufficient Role
```http
HTTP/1.1 403 Forbidden
```

### Validation Error
```http
HTTP/1.1 400 Bad Request
```
```json
{"error": "username: Username is required"}
```

---

## Security Considerations

1. **Never store plaintext passwords.** Always use BCrypt or similar.
2. **Never put passwords in JWTs.** JWTs contain only what's needed for authorization.
3. **Never commit JWT secrets.** Use environment variables.
4. **JWTs must have expiration.** Long-lived tokens are a security risk.
5. **HTTPS is required in production.** JWTs in HTTP headers are visible to network observers.
6. **Stateless ≠ No security.** Every request is independently verified.
7. **JWT validation must verify the signature.** Otherwise tokens can be forged.
8. **Public registration must not allow privilege escalation.** The server always assigns USER role.
9. **CORS should be configured deliberately.** See `CorsConfig.java`.
10. **CSRF is disabled for bearer-token APIs** because browsers don't auto-attach JWTs. CSRF matters for cookie-based auth.

---

## Running Tests

Tests run against a real PostgreSQL database (same Docker Compose container used for development).

```bash
cd 02-jwt-stateless-api
docker compose up -d     # Start PostgreSQL
mvn test                 # Run tests against testdb
```

The tests use a separate `testdb` database so they don't interfere with development data. The `TestContainerConfig` creates a `DataSource` bean pointing to `localhost:5432/testdb`.

If Testcontainers is available and compatible with your Docker setup, it will be used automatically (spinning up an isolated PostgreSQL container per test class).

### Test Coverage

| Test | Description |
|------|-------------|
| Register | POST /api/auth/register creates a user |
| Password Hashing | Stored password is BCrypt, not plaintext |
| Duplicate Username | Returns 400 Bad Request |
| Successful Login | Returns a valid JWT |
| Wrong Password | Returns 401 Unauthorized |
| No JWT | Protected endpoint returns 401 |
| Valid JWT | Returns user info |
| Invalid JWT | Returns 401 |
| Expired JWT | Returns 401 |
| Bean Configuration | All security beans are wired correctly |

---

## Project Structure

```
02-jwt-stateless-api/
├── docker-compose.yml          # PostgreSQL container
├── .env.example                # Environment variable template
├── pom.xml                     # Dependencies
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java      # AuthManager + PasswordEncoder + filter chain
│   │   │   │   ├── AuthManagerConfig.java    # Exposes AuthenticationManager bean
│   │   │   │   ├── CorsConfig.java           # CORS configuration
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       # /register, /login
│   │   │   │   └── UserController.java       # /me, /admin
│   │   │   ├── dto/
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── RegisterResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── model/
│   │   │   │   ├── User.java                 # JPA entity
│   │   │   │   └── Role.java                 # USER, ADMIN
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java       # JPA repository
│   │   │   └── security/
│   │   │       ├── JwtService.java           # JWT generation & validation
│   │   │       ├── JwtAuthenticationFilter.java  # Extracts & validates JWTs
│   │   │       └── UserDetailsServiceImpl.java   # Loads users from PostgreSQL
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           └── V1__create_users_table.sql
│   └── test/
│       └── java/com/example/demo/
│           ├── JwtIntegrationTest.java       # 10 comprehensive tests
│           ├── SecurityConfigTest.java
│           └── TestContainerConfig.java      # Docker Compose PostgreSQL for tests
└── README.md
```

---

## Common Errors

| Error | Cause | Fix |
|-------|-------|-----|
| `Connection refused` | PostgreSQL not running | `docker compose up -d` |
| `401 Unauthorized` | Missing/invalid JWT | Include `Authorization: Bearer <token>` |
| `403 Forbidden` | Insufficient role | User needs ROLE_ADMIN |
| `400 Bad Request` | Validation failed | Check username/password format |
| `Username already exists` | Duplicate registration | Choose a different username |
| `FlywayException` | Migration mismatch | Check `db/migration/` files |

---

## Next Steps

After completing this module:
- Module 3: OAuth2 Social Login
- Module 4: OAuth2 Resource Server
- Module 5: Method-Level Security
