package com.example.demo.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.concurrent.ConcurrentHashMap; import java.util.concurrent.atomic.AtomicInteger;
@Component public class RateLimitFilter extends OncePerRequestFilter {
 private final ConcurrentHashMap<String,AtomicInteger> counters=new ConcurrentHashMap<>();
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain c)throws ServletException,IOException{
   String key=r.getRemoteAddr(); int n=counters.computeIfAbsent(key,k->new AtomicInteger()).incrementAndGet();
   if(n>100){s.setStatus(429);return;} c.doFilter(r,s);
 }
}
