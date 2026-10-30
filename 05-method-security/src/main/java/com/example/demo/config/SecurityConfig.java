package com.example.demo.config;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity h)throws Exception{
  h.csrf(c->c.disable()).authorizeHttpRequests(a->a.anyRequest().authenticated()).httpBasic(b->{});
  return h.build();
 }
}
