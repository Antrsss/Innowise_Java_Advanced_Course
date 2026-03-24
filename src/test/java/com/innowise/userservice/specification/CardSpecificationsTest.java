package com.innowise.userservice.specification;

import com.innowise.userservice.dao.CardDao;
import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.integration.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CardSpecificationsTest extends BaseIntegrationTest {

  @Autowired
  private CardDao cardDao;

  @Autowired
  private UserDao userDao;

  @BeforeEach
  void setUp() {
    cardDao.deleteAll();
    userDao.deleteAll();

    User user = new User();
    user.setName("John");
    user.setSurname("Doe");
    user.setActive(true);
    userDao.save(user);

    PaymentCard card = new PaymentCard();
    card.setNumber("1111222233334444");
    card.setUser(user);
    card.setActive(true);
    cardDao.save(card);
  }

  @Test
  @DisplayName("Should find card by user name through Join")
  void shouldFindCardByUserName() {
    Specification<PaymentCard> spec = CardSpecifications.hasUserName("John");
    List<PaymentCard> results = cardDao.findAll(spec);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getUser().getName()).isEqualTo("John");
  }

  @Test
  @DisplayName("Should return empty list if user name does not match")
  void shouldReturnEmptyIfNameNotFound() {
    Specification<PaymentCard> spec = CardSpecifications.hasUserName("NonExistent");
    List<PaymentCard> results = cardDao.findAll(spec);

    assertThat(results).isEmpty();
  }

  @Test
  @DisplayName("Should return all cards if name is empty (spec returns null)")
  void shouldReturnAllIfNameIsEmpty() {
    Specification<PaymentCard> spec = CardSpecifications.hasUserName("");
    List<PaymentCard> results = cardDao.findAll(spec);

    assertThat(results).hasSize(1);
  }
}