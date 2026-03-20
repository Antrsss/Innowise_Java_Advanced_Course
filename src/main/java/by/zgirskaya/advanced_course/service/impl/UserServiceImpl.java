package by.zgirskaya.advanced_course.service.impl;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.EntityNotFoundException;
import by.zgirskaya.advanced_course.exception.ResourceConflictException;
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
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final CardDao cardDao;

  @Override
  public User createUser(User user) throws ResourceConflictException {
    if (userDao.existsByEmailAndActiveTrue(user.getEmail())) {
      throw new ResourceConflictException("User already exists");
    }
    return userDao.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "users", key = "#id")
  public User findActiveUserById(Long id) throws EntityNotFoundException {
    return userDao.findActiveUserByIdWithCards(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> findAll(String name, String surname, Pageable pageable) {
    Specification<User> spec = Specification.where(UserSpecifications.hasName(name))
        .and(UserSpecifications.hasSurname(surname))
        .and(UserSpecifications.isActive());
    return userDao.findAll(spec, pageable);
  }

  @Override
  @CacheEvict(value = "users", key = "#id")
  public void setUserStatus(Long id, boolean status) throws EntityNotFoundException {
    if (id == null) {
      throw new EntityNotFoundException("User id is null");
    }
    userDao.setUserStatus(id, status);
    cardDao.setCardsStatusByUserId(id, status);
  }
}
