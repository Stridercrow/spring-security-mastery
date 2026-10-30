# Security Best Practices

- Prefer standards: OAuth2/OIDC for distributed identity.
- Use adaptive password hashing such as bcrypt/Argon2.
- Validate JWT issuer, signature, expiration and audience where applicable.
- Use least privilege.
- Keep CSRF protection for cookie/session browser applications.
- Do not disable CORS/CSRF as a reflex; understand the client architecture first.
- Store secrets in a secret manager.
- Log security events without passwords, tokens or sensitive claims.
