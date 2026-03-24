package com.innowise.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwt = authHeader.substring(7);

    try {
      Long userId = jwtService.extractUserId(jwt);
      String role = jwtService.extractRole(jwt);
      String login = jwtService.extractLogin(jwt);

      UserPrincipal principal = UserPrincipal.builder()
          .id(userId)
          .login(login)
          .role(role)
          .build();

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          principal,
          null,
          principal.getAuthorities()
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}