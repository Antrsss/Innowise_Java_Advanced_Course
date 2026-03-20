package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.EntityNotFoundException;
import by.zgirskaya.advanced_course.exception.ResourceConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  User createUser(User user) throws ResourceConflictException;
  User findActiveUserById(Long id) throws EntityNotFoundException;
  Page<User> findAll(String name, String surname, Pageable pageable);
  void setUserStatus(Long id, boolean status) throws EntityNotFoundException;
}
