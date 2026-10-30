package com.example.demo.service;
import org.springframework.security.core.Authentication; import org.springframework.stereotype.Service;
@Service public class SecurityService {
 public boolean isOwner(Authentication a,String username){return a!=null && a.getName().equals(username);}
}
