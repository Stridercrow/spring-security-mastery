package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        // DelegatingPasswordEncoder selects a safe, versioned password-hashing algorithm.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    InMemoryUserDetailsManager users(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("student")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Requests are authorized after authentication has been established.
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        // Enables the generated login page and session-backed browser authentication.
        http.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll());

        // CSRF is enabled by default and is appropriate for browser/session applications.
        http.csrf(csrf -> {});

        // Limits concurrent sessions and invalidates a session after logout.
        http.sessionManagement(session -> session.maximumSessions(1));

        http.logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
