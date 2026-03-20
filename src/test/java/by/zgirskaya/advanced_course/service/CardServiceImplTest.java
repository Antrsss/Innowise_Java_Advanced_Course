package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.EntityNotFoundException;
import by.zgirskaya.advanced_course.exception.ResourceConflictException;
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
  void createCard_Success() throws EntityNotFoundException, ResourceConflictException {
    User user = new User();
    user.setId(1L);
    user.setActive(true);
    user.setCards(new ArrayList<>());

    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(userDao.findByIdWithLock(1L)).thenReturn(Optional.of(user));
    when(cardDao.countByUserId(1L)).thenReturn(0L);
    when(cardDao.save(any(PaymentCard.class))).thenReturn(card);

    PaymentCard result = cardService.createCard(card);

    assertNotNull(result);
    verify(cardDao, times(1)).save(card);
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

  @Test
  void createCard_WithNullUser_ThrowsException() {
    PaymentCard card = new PaymentCard();
    card.setUser(null);

    assertThrows(NullPointerException.class,
        () -> cardService.createCard(card));
  }


  @Test
  void findCardsByUserId_ReturnsList() {
    Long userId = 1L;
    List<PaymentCard> expectedCards = List.of(new PaymentCard(), new PaymentCard());
    when(cardDao.findCardsByUserId(userId)).thenReturn(expectedCards);

    List<PaymentCard> result = cardService.findCardsByUserId(userId);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(cardDao, times(1)).findCardsByUserId(userId);
  }

  @Test
  void findCardsByUserId_ReturnsEmptyList_WhenNoCards() {
    Long userId = 1L;
    when(cardDao.findCardsByUserId(userId)).thenReturn(List.of());

    List<PaymentCard> result = cardService.findCardsByUserId(userId);

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(cardDao, times(1)).findCardsByUserId(userId);
  }

  @Test
  void findAll_WithNullNameAndNullSurname_ReturnsAllCards() {
    Pageable pageable = Pageable.unpaged();
    Page<PaymentCard> page = new PageImpl<>(List.of(new PaymentCard()));

    when(cardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<PaymentCard> result = cardService.findAll(null, null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(cardDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithOnlyName_ReturnsFilteredCards() {
    Pageable pageable = Pageable.unpaged();
    Page<PaymentCard> page = new PageImpl<>(List.of(new PaymentCard()));

    when(cardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<PaymentCard> result = cardService.findAll("Ivan", null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(cardDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithOnlySurname_ReturnsFilteredCards() {
    Pageable pageable = Pageable.unpaged();
    Page<PaymentCard> page = new PageImpl<>(List.of(new PaymentCard()));

    when(cardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<PaymentCard> result = cardService.findAll(null, "Ivanov", pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(cardDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithEmptyPageable_ReturnsPage() {
    Pageable pageable = Pageable.unpaged();
    Page<PaymentCard> page = new PageImpl<>(List.of());

    when(cardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<PaymentCard> result = cardService.findAll("Ivan", "Ivanov", pageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
  }
}