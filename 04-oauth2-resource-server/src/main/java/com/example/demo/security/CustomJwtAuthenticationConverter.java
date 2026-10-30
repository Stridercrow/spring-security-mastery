package com.example.demo.security;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
 private final JwtAuthenticationConverter delegate = new JwtAuthenticationConverter();
 @Override public AbstractAuthenticationToken convert(Jwt source) { return delegate.convert(source); }
}
