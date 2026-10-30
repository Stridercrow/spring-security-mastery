package com.example.demo.controller;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController public class AuthController {
 @GetMapping("/") Object home(){return java.util.Map.of("login","/oauth2/authorization/google");}
 @GetMapping("/profile") Object profile(Authentication a){return java.util.Map.of("name",a.getName(),"authorities",a.getAuthorities());}
}
