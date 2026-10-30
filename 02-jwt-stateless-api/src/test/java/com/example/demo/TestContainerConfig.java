package com.example.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

/**
 * Test database configuration using Docker Compose PostgreSQL.
 *
 * For integration tests, we connect to the same PostgreSQL instance
 * used for development (started via docker compose up -d).
 * This ensures tests run against real PostgreSQL SQL, not an in-memory
 * database that might behave differently.
 *
 * The test database name is "testdb" to avoid conflicts with the
 * development database "securitydb". We use a separate schema so
 * tests don't interfere with development data.
 *
 * To run tests:
 *   1. docker compose up -d   (start PostgreSQL)
 *   2. mvn test               (run tests)
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    @Bean
    DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        config.setUsername("test");
        config.setPassword("test");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }
}
