package com.innowise.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebS
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
            .requestMatchers("/all-orders", "/all-payments").hasRole("ADMIN")
            .requestMatchers("/my-profile/**", "/my-orders/**").hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated()
        );
    return http.build();
  }
}
