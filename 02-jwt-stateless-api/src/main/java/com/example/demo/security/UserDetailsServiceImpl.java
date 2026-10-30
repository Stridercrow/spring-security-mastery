package com.example.demo.security;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads user security information from PostgreSQL.
 *
 * This is the bridge between Spring Security and our database.
 * When DaoAuthenticationProvider needs to verify a user, it calls:
 *
 *   userDetailsService.loadUserByUsername(username)
 *
 * This method:
 *   1. Queries PostgreSQL via UserRepository.
 *   2. Converts our User entity into Spring Security's UserDetails.
 *   3. Maps the role correctly: "USER" -> "ROLE_USER"
 *
 * IMPORTANT: Spring Security's hasRole("USER") internally expects
 * the "ROLE_" prefix. So we prepend it when building GrantedAuthority.
 * If we stored "ROLE_USER" in the database, we wouldn't need the prefix.
 * But storing just "USER" is cleaner in the database.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        // Map database role to Spring Security authority.
        // "USER" -> "ROLE_USER" because hasRole("USER") checks for "ROLE_USER" internally.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
