package com.innowise.userservice.service;

import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  User createUser(User user) throws ResourceConflictException;
  User findActiveUserById(Long id) throws EntityNotFoundException;
  User findActiveUserByEmail(String email) throws EntityNotFoundException;
  Page<User> findAll(String name, String surname, Pageable pageable);
  void setUserStatus(Long id, boolean status) throws EntityNotFoundException;
}
