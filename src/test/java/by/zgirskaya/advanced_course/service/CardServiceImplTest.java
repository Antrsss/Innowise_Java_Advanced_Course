package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.CardServiceException;
import by.zgirskaya.advanced_course.service.impl.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

  @Mock
  private CardDao cardDao;

  @Mock
  private UserDao userDao;

  @InjectMocks
  private CardServiceImpl cardService;

  @Test
  void createCard_Success() throws CardServiceException {
    User user = new User();
    user.setId(1L);
    user.setCards(new ArrayList<>());

    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(userDao.findById(1L)).thenReturn(Optional.of(user));
    when(cardDao.save(any(PaymentCard.class))).thenReturn(card);

    PaymentCard result = cardService.createCard(card);

    assertNotNull(result);
    verify(cardDao, times(1)).save(card);
  }

  @Test
  void createCard_ThrowsException_WhenLimitReached() {
    User user = new User();
    user.setId(1L);

    List<PaymentCard> cards = new ArrayList<>();
    for (int i = 0; i < 5; i++) cards.add(new PaymentCard());
    user.setCards(cards);

    PaymentCard newCard = new PaymentCard();
    newCard.setUser(user);

    when(userDao.findById(1L)).thenReturn(Optional.of(user));

    CardServiceException exception = assertThrows(CardServiceException.class,
        () -> cardService.createCard(newCard));

    assertEquals("User already has 5 cards. Limit reached!", exception.getMessage());
    verify(cardDao, never()).save(any());
  }

  @Test
  void findCardById_Success() throws CardServiceException {
    PaymentCard card = new PaymentCard();
    card.setId(10L);
    when(cardDao.findById(10L)).thenReturn(Optional.of(card));

    PaymentCard result = cardService.findCardById(10L);

    assertEquals(10L, result.getId());
  }

  @Test
  void findAll_ReturnsPage() {
    Pageable pageable = Pageable.unpaged();
    Page<PaymentCard> page = new PageImpl<>(List.of(new PaymentCard()));

    when(cardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<PaymentCard> result = cardService.findAll("Ivan", "Ivanov", pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
  }
}