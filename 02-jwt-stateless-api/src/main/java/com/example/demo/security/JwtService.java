package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * JWT generation and validation service.
 *
 * JWT structure (JSON Web Token):
 *   Header:  {"alg": "HS256", "typ": "JWT"}
 *   Payload: {"sub": "alice", "iat": 1700000000, "exp": 1700000900}
 *   Signature: HMAC-SHA256(header + payload, secret)
 *
 * Claims used:
 *   sub  = subject (username) - identifies the user
 *   iat  = issued at - when the token was created
 *   exp  = expiration - when the token expires
 *
 * Security considerations:
 *   - The secret key must be at least 256 bits (32 bytes) for HS256.
 *   - The secret should come from environment variables, not code.
 *   - Tokens have a configurable expiration (default: 900 seconds = 15 minutes).
 *   - The JWT does NOT contain the password or any sensitive data.
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds:900}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    /**
     * Generate a JWT for the given username.
     *
     * The token contains:
     *   - sub: the username
     *   - iat: current time
     *   - exp: current time + expiration seconds
     */
    public String generate(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    /**
     * Extract the username (subject) from a valid JWT.
     *
     * This validates the signature and expiration.
     * Throws an exception if the token is invalid or expired.
     */
    public String subject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
