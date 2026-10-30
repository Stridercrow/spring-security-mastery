package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Authentication controller handling registration and login.
 *
 * Registration flow:
 *   1. Validate the request (username/password constraints).
 *   2. Check for duplicate username -> 400 if exists.
 *   3. Hash password with BCrypt.
 *   4. Persist user with Role.USER (never accept role from client).
 *   5. Return user info (no password).
 *
 * Login flow:
 *   1. Delegate to AuthenticationManager.authenticate().
 *   2. The AuthenticationManager delegates to DaoAuthenticationProvider.
 *   3. DaoAuthenticationProvider uses UserDetailsService to load the user
 *      and PasswordEncoder to verify the password.
 *   4. On success, generate a JWT via JwtService.
 *   5. Return the JWT.
 *
 * IMPORTANT: The login endpoint does NOT manually query the database or
 * compare passwords. It relies entirely on the Spring Security
 * AuthenticationManager chain. This is the correct architecture.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user.
     *
     * POST /api/auth/register
     *
     * The role is always USER. Clients cannot register as ADMIN.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Username already exists");
        }

        // Hash the password before persisting. The database will NEVER contain plaintext passwords.
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(request.username(), hashedPassword, Role.USER);
        userRepository.save(user);

        RegisterResponse response = new RegisterResponse(
                user.getId(), user.getUsername(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate and receive a JWT.
     *
     * POST /api/auth/login
     *
     * This delegates to AuthenticationManager -> DaoAuthenticationProvider
     * -> UserDetailsService (loads user from PostgreSQL)
     *                    -> PasswordEncoder (BCrypt verifies password).
     *
     * Do NOT replace this with manual DB queries and password comparison.
     * The AuthenticationManager chain handles:
     *   - User loading
     *   - Password verification
     *   - Authentication event publishing
     *   - Proper exception translation
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()));

            String token = jwtService.generate(authentication.getName());
            return ResponseEntity.ok(new AuthResponse(token, "Bearer"));

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }
}
