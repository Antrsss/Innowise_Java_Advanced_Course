package com.innowise.userservice.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Builder
public class UserPrinciple implements UserDetails {
  private final Long id;
  private final String login;
  private final Collection<? extends GrantedAuthority> authorities;

  @Override
  public String getPassword() { return null; }
  @Override
  public String getUsername() { return login; }
}
