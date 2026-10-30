package com.example.demo.config;
import org.springframework.context.annotation.*; import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain security(HttpSecurity h)throws Exception{
  h.authorizeHttpRequests(a->a.requestMatchers("/actuator/health").permitAll().anyRequest().authenticated())
   .httpBasic(b->{})
   .headers(x->x.frameOptions(f->f.deny()).contentTypeOptions(c->{}).httpStrictTransportSecurity(hsts->hsts.includeSubDomains(true).maxAgeInSeconds(31536000)));
  return h.build();
 }
}
