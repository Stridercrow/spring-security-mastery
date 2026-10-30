package com.example.demo.model;

/**
 * Application roles used for authorization.
 *
 * Spring Security's {@code hasRole("USER")} internally expects the prefix "ROLE_".
 * When we persist "USER" in the database, the UserDetailsService converts it
 * to "ROLE_USER" when building the GrantedAuthority.
 */
public enum Role {
    USER,
    ADMIN
}
