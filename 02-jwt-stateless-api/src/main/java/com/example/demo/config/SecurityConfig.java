package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

/**
 * Spring Security configuration for the JWT Stateless API.
 *
 * Key architectural decisions:
 *
 * 1. STATELESS sessions: The server does NOT maintain HTTP sessions.
 *    Every request carries a JWT in the Authorization header.
 *    This is critical for scalable APIs.
 *
 * 2. Explicit DaoAuthenticationProvider: Instead of relying on
 *    Spring Boot auto-configuration, we wire the provider manually
 *    so the architecture is visible and educational.
 *
 * 3. CSRF disabled: Stateless APIs using Bearer tokens don't need CSRF
 *    protection because the browser won't auto-attach JWTs.
 *    (CSRF matters for cookie-based auth where browsers auto-attach cookies.)
 *
 * 4. 401 for unauthenticated: We configure the exception handling to return
 *    401 Unauthorized (not 403 Forbidden) when a request lacks valid
 *    authentication. This is the correct behavior for bearer-token APIs.
 *
 * Authentication flow:
 *   Client -> FilterChain -> JwtAuthenticationFilter
 *     -> SecurityContext.setAuthentication(auth)
 *     -> Authorization check -> Controller
 *
 * Login flow:
 *   Client -> POST /api/auth/login
 *     -> AuthenticationManager.authenticate()
 *     -> DaoAuthenticationProvider
 *       -> UserDetailsService.loadUserByUsername()
 *       -> PasswordEncoder.matches()
 *     -> Authentication success
 *     -> JwtService.generate()
 *     -> JWT returned to client
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    public SecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * The main security filter chain.
     *
     * - CSRF is disabled because we use Bearer tokens, not cookies.
     * - Sessions are STATELESS because JWTs carry authentication state.
     * - Public endpoints: /api/auth/register, /api/auth/login
     * - Protected endpoints: everything else (requires valid JWT)
     * - JwtAuthenticationFilter runs before UsernamePasswordAuthenticationFilter
     *   to extract and validate JWTs from incoming requests.
     *
     * When an unauthenticated request hits a protected endpoint:
     *   Spring Security 6 returns 403 by default.
     *   We override this to return 401 with a JSON body, which is
     *   the standard for bearer-token APIs.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                             JwtAuthenticationFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers("/api/users/admin").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(
                            objectMapper.writeValueAsString(
                                    Map.of("error", "Unauthorized")));
                }))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Explicitly configure DaoAuthenticationProvider.
     *
     * This is the heart of Spring Security's authentication mechanism:
     *
     *   AuthenticationManager
     *           |
     *           v
     *   DaoAuthenticationProvider
     *       /             \
     *      v               v
     * UserDetailsService  PasswordEncoder
     *      |
     *      v
     * UserRepository -> PostgreSQL
     *
     * When authenticationManager.authenticate() is called:
     *   1. DaoAuthenticationProvider receives the authentication token.
     *   2. It calls userDetailsService.loadUserByUsername() to load the user.
     *   3. It calls passwordEncoder.matches() to verify the password.
     *   4. If both succeed, an authenticated Authentication object is returned.
     *   5. If either fails, a BadCredentialsException is thrown.
     */
    @Bean
    DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * BCrypt password encoder.
     *
     * Registration flow:
     *   plaintext password -> BCryptPasswordEncoder.encode() -> hash -> stored in DB
     *
     * Login flow:
     *   plaintext password -> BCryptPasswordEncoder.matches(plaintext, storedHash) -> true/false
     *
     * BCrypt is a slow hashing algorithm designed for password storage.
     * It automatically includes a random salt in the hash, so two
     * identical passwords produce different hashes.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
