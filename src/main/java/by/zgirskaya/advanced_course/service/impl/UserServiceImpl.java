package by.zgirskaya.advanced_course.service.impl;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.service.UserService;
import by.zgirskaya.advanced_course.specification.UserSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final CardDao cardDao;

  @Override
  @Transactional
  public User createUser(User user) throws UserServiceException {
    if (user.getCards() != null && user.getCards().size() > 5) {
      throw new UserServiceException("User cannot have more than 5 cards!");
    }
    return userDao.save(user);
  }

  @Override
  @Transactional
  @Cacheable(value = "users", key = "#id")
  public User findUserById(Long id) throws UserServiceException {
    return userDao.findByIdWithCards(id).orElseThrow(() -> new UserServiceException("User not found!"));
  }

  @Override
  public Page<User> findAll(String name, String surname, Pageable pageable) {
    Specification<User> spec = Specification.where(UserSpecifications.hasName(name))
        .and(UserSpecifications.hasSurname(surname))
        .and(UserSpecifications.isActive());
    return userDao.findAll(spec, pageable);
  }

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#id")
  public void setUserStatus(Long id, boolean status) {
    userDao.setUserStatus(id, status);
    cardDao.setCardsStatusByUserId(id, status);
  }
}
