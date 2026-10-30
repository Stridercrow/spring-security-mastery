package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for user persistence in PostgreSQL.
 *
 * Spring Data JPA auto-generates the implementation from method names:
 *   findByUsername(String)  -> SELECT * FROM users WHERE username = ?
 *   existsByUsername(String) -> SELECT COUNT(*) > 0 FROM users WHERE username = ?
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
