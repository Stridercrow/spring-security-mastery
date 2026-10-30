package com.example.demo.security;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
@Service public class CustomOAuth2UserService extends DefaultOAuth2UserService {
 @Override public OAuth2User loadUser(org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest request) {
   OAuth2User user=super.loadUser(request);
   // Production code should validate/map provider-specific claims explicitly.
   return user;
 }
}
