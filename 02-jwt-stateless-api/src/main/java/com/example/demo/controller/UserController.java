package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Protected user endpoints.
 *
 * These endpoints require a valid JWT in the Authorization header.
 * The JwtAuthenticationFilter extracts the JWT, loads the user,
 * and sets the Authentication in the SecurityContext before
 * these controllers are reached.
 *
 * /api/users/me    -> Any authenticated user (USER or ADMIN)
 * /api/users/admin -> Only users with ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Returns the currently authenticated user's information.
     *
     * GET /api/users/me
     * Authorization: Bearer <JWT>
     *
     * The Authentication object is automatically populated by
     * JwtAuthenticationFilter from the SecurityContext.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        ));
    }

    /**
     * Admin-only endpoint.
     *
     * GET /api/users/admin
     * Authorization: Bearer <JWT with ROLE_ADMIN>
     *
     * Returns 403 Forbidden if the JWT belongs to a non-admin user.
     */
    @GetMapping("/admin")
    public ResponseEntity<Map<String, String>> admin() {
        return ResponseEntity.ok(Map.of(
                "message", "Hello, Admin! You have elevated privileges."
        ));
    }
}
