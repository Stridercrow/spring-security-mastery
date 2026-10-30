package com.example.demo.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication Filter.
 *
 * This filter runs on EVERY request and:
 *   1. Reads the Authorization header.
 *   2. Checks for "Bearer <token>" format.
 *   3. Extracts and validates the JWT (signature + expiration).
 *   4. Loads the user from PostgreSQL via UserDetailsService.
 *   5. Creates an authenticated Authentication object.
 *   6. Stores it in the SecurityContext.
 *
 * This is how a stateless API authenticates requests:
 *
 *   Client sends:  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *                          |
 *                          v
 *   JwtAuthenticationFilter
 *       |
 *       +-- Extract token from header
 *       +-- Validate signature (JwtService.subject())
 *       +-- Check expiration (handled by jjwt parser)
 *       +-- Load UserDetails (UserDetailsServiceImpl -> PostgreSQL)
 *       +-- Set SecurityContext
 *       |
 *       v
 *   SecurityContext holds Authentication
 *       |
 *       v
 *   Authorization check -> Controller
 *
 * Invalid/expired JWTs are silently ignored (no authentication is set).
 * The request then proceeds as unauthenticated, and the authorization
 * layer will return 401 Unauthorized for protected endpoints.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, java.io.IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // This validates both the signature and expiration.
                // Throws if either is invalid.
                String username = jwtService.subject(token);

                // Load the user from PostgreSQL.
                // If the user was deleted after the token was issued,
                // this will throw UsernameNotFoundException.
                var userDetails = userDetailsService.loadUserByUsername(username);

                // Create an authenticated Authentication object.
                // The password is null because we don't need it after authentication.
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // Store the authentication in the SecurityContext.
                // This makes the user "logged in" for this request.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (RuntimeException e) {
                // Invalid token, expired token, or user not found.
                // Clear the context to ensure the request is unauthenticated.
                // The authorization layer will handle returning 401.
                SecurityContextHolder.clearContext();
            }
        }

        // Continue the filter chain regardless of authentication result.
        filterChain.doFilter(request, response);
    }
}
