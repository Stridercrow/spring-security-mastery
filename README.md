# Spring Security Mastery

A progressive, hands-on Spring Security learning repository for experienced Java/Spring developers.

> **Goal:** build muscle memory for explaining and implementing Spring Security in interviews and production systems.

## Learning path

| Module | Topic | Level |
|---|---|---|
| 01 | Form login, sessions, CSRF | Beginner |
| 02 | JWT stateless REST API | Intermediate |
| 03 | OAuth2 social login | Intermediate |
| 04 | OAuth2 Resource Server | Advanced |
| 05 | Method-level security | Advanced |
| 06 | Production security concerns | Expert |
| 07 | Microservices security | Expert |

## Requirements

- Java 17+
- Maven 3.9+
- Docker (for selected modules)
- Optional: Google/GitHub OAuth application for module 03
- Optional: an OIDC issuer such as Keycloak for module 04/07

## Build

Each standalone module contains its own Maven project:

```bash
cd 01-basic-web-security
mvn test
mvn spring-boot:run
```

For all modules:

```bash
find . -name pom.xml -not -path './target/*' -print0 |
  xargs -0 -n1 sh -c 'cd "$(dirname "$0")" && mvn test'
```

## Important security notes

This repository intentionally demonstrates both recommended Spring Security patterns and a few educational implementations that should **not** be copied blindly into production.

In particular:

- Module 02 shows JWT creation so you understand the mechanics. Production systems normally use a dedicated authorization server/identity provider.
- Module 06 uses local in-memory examples for blacklist/rate-limit concepts. Distributed production systems need shared infrastructure and operational controls.
- Never commit real secrets, signing keys, OAuth client secrets, or Vault tokens.
- Use HTTPS/TLS in deployed environments.

## Suggested practice loop

1. Read the module README.
2. Run the tests.
3. Read `SecurityConfig`.
4. Break one security rule intentionally.
5. Add a test proving the failure.
6. Restore the rule.
7. Explain the request flow out loud as if in an interview.

## Documentation

See [`docs/`](docs/) for architecture, pitfalls, production guidance, troubleshooting, and interview-oriented references.
