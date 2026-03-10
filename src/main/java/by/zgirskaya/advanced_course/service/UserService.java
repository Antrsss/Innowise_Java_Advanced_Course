package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;

public interface UserService {
  User createUser(User user) throws UserServiceException;
  User findUserById(Long id) throws UserServiceException;
  void setUserStatus(Long id, boolean status);
  void deleteUser(Long id);
}
