package com.innowise.userservice.config;

import com.innowise.userservice.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String ADMIN_ROLE = "ADMIN";
  private static final String USER_ROLE = "USER";
  public static final String USERS_API = "/api/users";
  public static final String CARDS_API = "/api/cards";

  private final JwtAuthenticationFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, USERS_API, CARDS_API).hasRole(ADMIN_ROLE)
            .requestMatchers(HttpMethod.PATCH, USERS_API + "/*/status", CARDS_API + "/*/status").hasRole(ADMIN_ROLE)
            .requestMatchers(HttpMethod.DELETE, USERS_API + "/*", CARDS_API + "/*").hasRole(ADMIN_ROLE)
            .requestMatchers(USERS_API + "/**", CARDS_API + "/**").hasAnyRole(USER_ROLE, ADMIN_ROLE)

            .anyRequest().authenticated()
        )
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
