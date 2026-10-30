package com.example.demo.security;
import org.springframework.core.env.Environment; import org.springframework.stereotype.Service;
@Service public class SecretResolutionService {
 private final Environment env; public SecretResolutionService(Environment env){this.env=env;}
 public String resolve(String name){String value=env.getProperty(name); if(value==null||value.isBlank()) throw new IllegalStateException("Missing secret: "+name); return value;}
}
