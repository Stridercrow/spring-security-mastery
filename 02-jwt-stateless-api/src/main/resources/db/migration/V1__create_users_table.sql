-- V1: Create the users table for JWT authentication module.
--
-- This table stores registered users with BCrypt-hashed passwords.
-- The 'role' column uses VARCHAR to support JPA @Enumerated(EnumType.STRING).
--
-- Password hashing flow:
--   Registration: plaintext -> BCrypt encoder -> hash stored here
--   Login:        plaintext -> BCrypt.matches(stored hash) -> true/false

CREATE TABLE users (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(100) UNIQUE NOT NULL,
    password  VARCHAR(255)        NOT NULL,
    role      VARCHAR(30)         NOT NULL
);

-- Index for fast username lookups during authentication.
CREATE INDEX idx_users_username ON users (username);
