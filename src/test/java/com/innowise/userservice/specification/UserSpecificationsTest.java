package com.innowise.userservice.specification;

import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserSpecificationsTest extends BaseIntegrationTest {

  @Autowired
  private UserDao userDao;

  @BeforeEach
  void setUp() {
    userDao.deleteAll();

    User activeUser = new User();
    activeUser.setName("Ivan");
    activeUser.setSurname("Ivanov");
    activeUser.setActive(true);
    activeUser.setEmail("ivan@test.com");

    User inactiveUser = new User();
    inactiveUser.setName("Petr");
    inactiveUser.setSurname("Petrov");
    inactiveUser.setActive(false);
    inactiveUser.setEmail("petr@test.com");

    userDao.saveAll(List.of(activeUser, inactiveUser));
  }

  @Test
  @DisplayName("Should find user by name")
  void shouldFindByName() {
    Specification<User> spec = UserSpecifications.hasName("Ivan");
    List<User> results = userDao.findAll(spec);

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().getName()).isEqualTo("Ivan");
  }

  @Test
  @DisplayName("Should find active users only")
  void shouldFindOnlyActive() {
    Specification<User> spec = UserSpecifications.isActive();
    List<User> results = userDao.findAll(spec);

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().isActive()).isTrue();
    assertThat(results.getFirst().getName()).isEqualTo("Ivan");
  }

  @Test
  @DisplayName("Should return all if name is null")
  void shouldReturnAllIfNameIsNull() {
    Specification<User> spec = UserSpecifications.hasName(null);
    List<User> results = userDao.findAll(spec);

    assertThat(results).hasSize(2);
  }
}