package com.example.demo.model;

import jakarta.persistence.*;

/**
 * Persistent user entity stored in PostgreSQL.
 *
 * Passwords are NEVER stored in plaintext. During registration, the
 * raw password is hashed with BCrypt before being persisted here.
 *
 * The 'role' column stores the role as a string (e.g., "USER", "ADMIN").
 * This is mapped using {@link EnumType#STRING} so the column value is
 * human-readable in the database.
 *
 * Flow:
 *   Register -> BCryptPasswordEncoder.encode(plaintext) -> hash stored in 'password'
 *   Login    -> BCrypt.matches(plaintext, stored_hash)   -> true/false
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {
        // Required by JPA
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
