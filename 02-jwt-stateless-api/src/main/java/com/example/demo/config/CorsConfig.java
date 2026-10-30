package com.example.demo.config;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
@Configuration public class CorsConfig {
 @Bean UrlBasedCorsConfigurationSource corsConfigurationSource() {
   var c = new CorsConfiguration(); c.setAllowedOrigins(List.of("http://localhost:3000"));
   c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
   c.setAllowedHeaders(List.of("Authorization","Content-Type"));
   var s = new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**",c); return s;
 }
}
