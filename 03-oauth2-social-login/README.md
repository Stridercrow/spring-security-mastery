# Module 3: OAuth2 Social Login

## 🎯 Learning Objectives
- OAuth2 authorization-code login
- Client registration
- OAuth2 user mapping
- Success handlers
- Secure external identity integration

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
cd 03-oauth2-social-login
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
