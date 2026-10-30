# Module 4: OAuth2 Resource Server

## 🎯 Learning Objectives
- JWT bearer-token validation
- Issuer metadata
- Scopes and authorities
- Custom JWT claim conversion
- Resource-server exception handling

## 🏗️ Architecture
```text
Client -> Spring Security Filter Chain -> Authentication -> Authorization -> Controller
```

## 🔧 Prerequisites
- Java 17+
- Maven 3.9+
- Docker where noted

## 🚀 Quick Start
```bash
cd 04-oauth2-resource-server
mvn test
mvn spring-boot:run
```

## 🔍 What to inspect
- Read `SecurityConfig` first.
- Follow the request through the Spring Security filter chain.
- Identify authentication, authorization, exception handling and persistence/session behavior.
- Read the tests before changing the configuration.

## ⚠️ Common errors
- **401 Unauthorized:** authentication is missing/invalid.
- **403 Forbidden:** authentication exists but authorization failed, or CSRF rejected a state-changing request.
- **CORS failure:** browser policy is blocking the cross-origin request; configure CORS deliberately.
- **Invalid token:** verify issuer, signature, expiration and claims.

## 🔐 Security considerations
Use least privilege, strong password hashing, TLS, short-lived credentials, secure secret storage and automated security tests.

## ➡️ Next
Continue to the next module after completing the exercises in this module.
