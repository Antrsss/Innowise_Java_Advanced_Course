package by.zgirskaya.advanced_course.specification;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardSpecificationsTest extends BaseIntegrationTest {

  @Autowired
  private CardDao cardDao;

  @Autowired
  private UserDao userDao;

  private User user1;
  private User user2;
  private PaymentCard card1;
  private PaymentCard card2;
  private PaymentCard card3;

  @BeforeEach
  void setUp() {
    userDao.deleteAll();
    cardDao.deleteAll();

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

    card1 = new PaymentCard();
    card1.setNumber("1111222233334444");
    card1.setHolder("DARYA ZGIRSKAYA");
    card1.setExpirationDate("12/25");
    card1.setActive(true);
    card1.setUser(user1);
    cardDao.save(card1);

    card2 = new PaymentCard();
    card2.setNumber("5555666677778888");
    card2.setHolder("DARYA ZGIRSKAYA");
    card2.setExpirationDate("12/26");
    card2.setActive(true);
    card2.setUser(user1);
    cardDao.save(card2);

    card3 = new PaymentCard();
    card3.setNumber("9999000011112222");
    card3.setHolder("IVAN PETROV");
    card3.setExpirationDate("12/27");
    card3.setActive(true);
    card3.setUser(user2);
    cardDao.save(card3);
  }

  @Test
  void hasUserName_WithValidName_ReturnsCards() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserName("Darya"));

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(card -> card.getUser().getName().equals("Darya")));
  }

  @Test
  void hasUserName_WithNonExistentName_ReturnsEmptyList() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserName("NonExistent"));

    assertTrue(results.isEmpty());
  }

  @Test
  void hasUserName_WithNullName_ReturnsAllCards() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserName(null));

    assertEquals(3, results.size());
  }

  @Test
  void hasUserName_WithEmptyName_ReturnsAllCards() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserName(""));

    assertEquals(3, results.size());
  }

  @Test
  void hasUserSurname_WithValidSurname_ReturnsCards() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserSurname("Zgirskaya"));

    assertEquals(2, results.size());
    assertTrue(results.stream().allMatch(card -> card.getUser().getSurname().equals("Zgirskaya")));
  }

  @Test
  void hasUserSurname_WithNonExistentSurname_ReturnsEmptyList() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserSurname("NonExistent"));

    assertTrue(results.isEmpty());
  }

  @Test
  void hasUserSurname_WithNullSurname_ReturnsAllCards() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserSurname(null));

    assertEquals(3, results.size());
  }

  @Test
  void hasUserSurname_WithEmptySurname_ReturnsAllCards() {
    List<PaymentCard> results = cardDao.findAll(CardSpecifications.hasUserSurname(""));

    assertEquals(3, results.size());
  }

  @Test
  void combinedSpecifications_WithNameAndSurname_ReturnsFilteredCards() {
    List<PaymentCard> results = cardDao.findAll(
        CardSpecifications.hasUserName("Darya")
            .and(CardSpecifications.hasUserSurname("Zgirskaya"))
    );

    assertEquals(2, results.size());
  }

  @Test
  void combinedSpecifications_WithNameAndWrongSurname_ReturnsEmptyList() {
    List<PaymentCard> results = cardDao.findAll(
        CardSpecifications.hasUserName("Darya")
            .and(CardSpecifications.hasUserSurname("Petrov"))
    );

    assertTrue(results.isEmpty());
  }

  @Test
  void chainedSpecifications_UsingOr_ReturnsUnion() {
    List<PaymentCard> results = cardDao.findAll(
        CardSpecifications.hasUserName("Darya")
            .or(CardSpecifications.hasUserSurname("Petrov"))
    );

    assertEquals(3, results.size());
  }
}