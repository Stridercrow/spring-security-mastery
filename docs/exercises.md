# Hands-on Exercises

After each module, modify the code and prove the behavior with tests.

## Module 01
1. Require ADMIN for `/admin`.
2. Add a custom login failure message.
3. Verify CSRF rejects a state-changing request without a token.
4. Replace in-memory users with JDBC.

## Module 02
1. Add an `ADMIN` claim.
2. Validate expiration.
3. Add refresh-token discussion/design.
4. Replace custom JWT issuance with an OAuth2 authorization server.
5. Add tests for malformed, expired and tampered tokens.

## Module 03
1. Configure Google.
2. Add GitHub.
3. Map provider claims to an internal user.
4. Handle account linking safely.

## Module 04
1. Map a custom `roles` claim.
2. Validate audience.
3. Compare JWT validation with opaque-token introspection.
4. Test scope failures.

## Module 05
1. Add ownership authorization.
2. Compare URL and method-level authorization.
3. Test `@PostAuthorize`.

## Module 06
1. Replace the in-memory rate limiter with Redis.
2. Replace local blacklist with a distributed design.
3. Integrate Vault/KMS.
4. Add structured security audit events.
5. Add actuator exposure controls.

## Module 07
1. Put an OIDC provider in front of the services.
2. Validate issuer and audience in every resource service.
3. Decide whether the gateway should enforce authorization or only route.
4. Add service-to-service client credentials.
5. Document the trust boundaries.
