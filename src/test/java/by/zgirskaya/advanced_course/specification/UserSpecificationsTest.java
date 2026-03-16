package by.zgirskaya.advanced_course.specification;

import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSpecificationsTest extends BaseIntegrationTest {

  @Autowired
  private UserDao userDao;

  private User user1;
  private User user2;
  private User user3;

  @BeforeEach
  void setUp() {
    userDao.deleteAll();

    user1 = new User();
    user1.setName("Darya");
    user1.setSurname("Zgirskaya");
    user1.setEmail("darya@example.com");
    user1.setBirthDate(LocalDate.of(1990, 1, 1));
    user1.setActive(true);
    userDao.save(user1);

    user2 = new User();
    user2.setName("Ivan");
    user2.setSurname("Petrov");
    user2.setEmail("ivan@example.com");
    user2.setBirthDate(LocalDate.of(1985, 5, 15));
    user2.setActive(true);
    userDao.save(user2);

    user3 = new User();
    user3.setName("Darya");
    user3.setSurname("Smirnova");
    user3.setEmail("darya.s@example.com");
    user3.setBirthDate(LocalDate.of(1992, 3, 10));
    user3.setActive(false);
    userDao.save(user3);
  }

  @Test
  void hasName_WithValidName_ReturnsUsers() {
    Specification<User> spec = UserSpecifications.hasName("Darya");
    List<User> results = userDao.findAll(spec);

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(user -> user.getName().equals("Darya")));
  }

  @Test
  void hasName_WithNonExistentName_ReturnsEmptyList() {
    Specification<User> spec = UserSpecifications.hasName("NonExistent");
    List<User> results = userDao.findAll(spec);

    assertTrue(results.isEmpty());
  }

  @Test
  void hasName_WithNullName_ReturnsAllUsers() {
    Specification<User> spec = UserSpecifications.hasName(null);
    List<User> results = userDao.findAll(spec);

    assertEquals(3, results.size());
  }

  @Test
  void hasSurname_WithValidSurname_ReturnsUsers() {
    Specification<User> spec = UserSpecifications.hasSurname("Zgirskaya");
    List<User> results = userDao.findAll(spec);

    assertEquals(1, results.size());
    assertEquals("Zgirskaya", results.get(0).getSurname());
  }

  @Test
  void hasSurname_WithNonExistentSurname_ReturnsEmptyList() {
    Specification<User> spec = UserSpecifications.hasSurname("NonExistent");
    List<User> results = userDao.findAll(spec);

    assertTrue(results.isEmpty());
  }

  @Test
  void hasSurname_WithNullSurname_ReturnsAllUsers() {
    Specification<User> spec = UserSpecifications.hasSurname(null);
    List<User> results = userDao.findAll(spec);

    assertEquals(3, results.size());
  }

  @Test
  void isActive_ReturnsOnlyActiveUsers() {
    Specification<User> spec = UserSpecifications.isActive();
    List<User> results = userDao.findAll(spec);

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(User::isActive));
    assertTrue(results.stream().noneMatch(user -> !user.isActive()));
  }

  @Test
  void combinedSpecifications_WithNameAndSurname_ReturnsFilteredUsers() {
    Specification<User> spec = UserSpecifications.hasName("Darya")
        .and(UserSpecifications.hasSurname("Zgirskaya"))
        .and(UserSpecifications.isActive());

    List<User> results = userDao.findAll(spec);

    assertEquals(1, results.size());
    assertEquals("Darya", results.get(0).getName());
    assertEquals("Zgirskaya", results.get(0).getSurname());
    assertTrue(results.get(0).isActive());
  }

  @Test
  void combinedSpecifications_WithNameAndInactive_ReturnsEmptyList() {
    Specification<User> spec = UserSpecifications.hasName("Darya")
        .and(UserSpecifications.hasSurname("Smirnova"))
        .and(UserSpecifications.isActive());

    List<User> results = userDao.findAll(spec);

    assertTrue(results.isEmpty());
  }

  @Test
  void combinedSpecifications_WithNameAndSurname_ReturnsCorrectUser() {
    Specification<User> spec = UserSpecifications.hasName("Ivan")
        .and(UserSpecifications.hasSurname("Petrov"));

    List<User> results = userDao.findAll(spec);

    assertEquals(1, results.size());
    assertEquals("Ivan", results.get(0).getName());
    assertEquals("Petrov", results.get(0).getSurname());
  }

  @Test
  void chainedSpecifications_UsingOr_ReturnsUnion() {
    Specification<User> spec = UserSpecifications.hasName("Darya")
        .or(UserSpecifications.hasSurname("Petrov"));

    List<User> results = userDao.findAll(spec);

    assertEquals(3, results.size());
  }

  @Test
  void allSpecifications_CombinedWithAnd_ReturnsIntersection() {
    Specification<User> spec = UserSpecifications.hasName("Darya")
        .and(UserSpecifications.hasSurname("Zgirskaya"));

    List<User> results = userDao.findAll(spec);

    assertEquals(1, results.size());
    assertEquals("Darya", results.get(0).getName());
    assertEquals("Zgirskaya", results.get(0).getSurname());
  }

  @Test
  void complexQuery_WithMultipleConditions_ReturnsExpectedResults() {
    Specification<User> spec = Specification
        .where(UserSpecifications.hasName("Darya"))
        .and(UserSpecifications.isActive());

    List<User> results = userDao.findAll(spec);

    assertEquals(1, results.size());
    assertEquals("Darya", results.get(0).getName());
    assertEquals("Zgirskaya", results.get(0).getSurname());
    assertTrue(results.get(0).isActive());
  }
}