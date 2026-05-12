package com.innowise.userservice.service.impl;

import com.innowise.userservice.dao.CardDao;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.specification.UserSpecifications;
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
    return userDao.findActiveUserByIdWithCards(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  @Override
  public User findActiveUserByEmail(String email) throws EntityNotFoundException {
    return userDao.findByEmailWithLock(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
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
