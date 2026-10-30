package com.example.demo.health;
import org.springframework.boot.actuate.health.*; import org.springframework.stereotype.Component;
@Component public class SecurityHealthIndicator implements HealthIndicator {
 public Health health(){return Health.up().withDetail("securityConfiguration","loaded").build();}
}
