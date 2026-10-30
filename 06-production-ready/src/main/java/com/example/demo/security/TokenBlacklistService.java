package com.example.demo.security;
import org.springframework.stereotype.Service; import java.time.Instant; import java.util.concurrent.ConcurrentHashMap;
@Service public class TokenBlacklistService {
 private final ConcurrentHashMap<String,Instant> revoked=new ConcurrentHashMap<>();
 public void revoke(String token,Instant expiresAt){revoked.put(token,expiresAt);}
 public boolean isRevoked(String token){var e=revoked.get(token); if(e==null)return false; if(e.isBefore(Instant.now())){revoked.remove(token);return false;} return true;}
}
