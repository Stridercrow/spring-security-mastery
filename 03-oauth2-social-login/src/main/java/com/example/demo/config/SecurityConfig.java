package com.example.demo.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
   http.authorizeHttpRequests(a -> a.requestMatchers("/","/error").permitAll().anyRequest().authenticated())
       .oauth2Login(o -> o.loginPage("/oauth2/authorization/google"));
   return http.build();
 }
}
