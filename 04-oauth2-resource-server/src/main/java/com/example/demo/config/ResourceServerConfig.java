package com.example.demo.config;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class ResourceServerConfig {
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
   http.authorizeHttpRequests(a -> a.requestMatchers("/public").permitAll()
     .requestMatchers("/api/read").hasAuthority("SCOPE_read")
     .requestMatchers("/api/write").hasAuthority("SCOPE_write").anyRequest().authenticated())
     .oauth2ResourceServer(o -> o.jwt(j -> {}));
   return http.build();
 }
}
