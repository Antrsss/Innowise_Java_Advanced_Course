package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  User createUser(User user) throws UserServiceException;
  User findUserById(Long id) throws UserServiceException;
  Page<User> findAll(String name, String surname, Pageable pageable);
  void setUserStatus(Long id, boolean status);
  void deleteUser(Long id);
}
