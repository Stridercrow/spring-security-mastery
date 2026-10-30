package com.example.demo.security;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
@Component public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
 public void onAuthenticationSuccess(HttpServletRequest r,HttpServletResponse s,Authentication a)throws IOException,ServletException{
   s.sendRedirect("/profile");
 }
}
