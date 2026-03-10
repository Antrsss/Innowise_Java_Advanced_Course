package by.zgirskaya.advanced_course.service.impl;

import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;

  @Override
  public User createUser(User user) throws UserServiceException {
    if (user.getCards() != null && user.getCards().size() > 5) {
      throw new UserServiceException("User cannot have more than 5 cards!");
    }
    return userDao.save(user);
  }

  @Override
  public User findUserById(Long id) throws UserServiceException {
    return userDao.findById(id).orElseThrow(() -> new UserServiceException("User not found!"));
  }

  @Override
  public void setUserStatus(Long id, boolean status) {
    userDao.setUserStatus(id, status);
  }

  @Override
  public void deleteUser(Long id) {
    userDao.deleteById(id);
  }
}
