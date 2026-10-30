package com.example.demo.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.slf4j.*; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component public class AuditLogFilter extends OncePerRequestFilter {
 private static final Logger log=LoggerFactory.getLogger(AuditLogFilter.class);
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain c)throws ServletException,IOException{
  long start=System.nanoTime(); try{c.doFilter(r,s);}finally{log.info("security-audit method={} path={} status={} durationMs={}",r.getMethod(),r.getRequestURI(),s.getStatus(),(System.nanoTime()-start)/1_000_000);}
 }
}
