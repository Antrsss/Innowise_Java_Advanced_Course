package com.innowise.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  public Claims extractAllClaims(String token) {
    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public Long extractUserId(String token) {
    return extractAllClaims(token).get("userId", Long.class);
  }

  public String extractRole(String token) {
    return extractAllClaims(token).get("role", String.class);
  }

  public String extractLogin(String token) {
    return extractAllClaims(token).get("login", String.class);
  }
}
