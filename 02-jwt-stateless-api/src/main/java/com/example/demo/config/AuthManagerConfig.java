package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * Exposes the AuthenticationManager bean.
 *
 * The AuthenticationManager is the top-level entry point for authentication.
 * When a controller calls:
 *
 *   authenticationManager.authenticate(
 *       new UsernamePasswordAuthenticationToken(username, password));
 *
 * The flow is:
 *   AuthenticationManager
 *           |
 *           v
 *   DaoAuthenticationProvider  (configured in SecurityConfig)
 *       /             \
 *      v               v
 * UserDetailsService  PasswordEncoder
 *
 * This class simply extracts the AuthenticationManager from Spring Security's
 * AuthenticationConfiguration, which automatically includes all registered
 * AuthenticationProvider beans (including our DaoAuthenticationProvider).
 */
@Configuration
public class AuthManagerConfig {

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
