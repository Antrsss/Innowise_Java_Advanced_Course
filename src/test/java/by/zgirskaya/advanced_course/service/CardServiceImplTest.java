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
  void createCard_ThrowsException_WhenLimitReached() {
    User user = new User();
    user.setId(1L);
    user.setActive(true);

    List<PaymentCard> cards = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      PaymentCard c = new PaymentCard();
      c.setActive(true);
      cards.add(c);
    }
    user.setCards(cards);

    PaymentCard newCard = new PaymentCard();
    newCard.setUser(user);

    when(userDao.findByIdWithLock(1L)).thenReturn(Optional.of(user));
    when(cardDao.countByUserId(1L)).thenReturn(5L);

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

  @Test
  void createCard_UserNotFound_ThrowsException() {
    User user = new User();
    user.setId(99L);

    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(userDao.findByIdWithLock(99L)).thenReturn(Optional.empty());

    CardServiceException exception = assertThrows(CardServiceException.class,
        () -> cardService.createCard(card));

    assertEquals("User not found or inactive!", exception.getMessage());
    verify(cardDao, never()).save(any());
  }

  @Test
  void createCard_WithNullUser_ThrowsException() {
    PaymentCard card = new PaymentCard();
    card.setUser(null);

    assertThrows(NullPointerException.class,
        () -> cardService.createCard(card));
  }

  @Test
  void findCardById_NotFound_ThrowsException() {
    Long cardId = 99L;
    when(cardDao.findById(cardId)).thenReturn(Optional.empty());

    CardServiceException exception = assertThrows(CardServiceException.class,
        () -> cardService.findCardById(cardId));

    assertEquals("Card not found with id: " + cardId, exception.getMessage());
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

  @Test
  void setCardStatus_Success() throws CardServiceException {
    Long cardId = 10L;
    boolean newStatus = false;

    User user = new User();
    user.setId(1L);

    PaymentCard card = new PaymentCard();
    card.setId(cardId);
    card.setUser(user);
    card.setActive(true);

    when(cardDao.findById(cardId)).thenReturn(Optional.of(card));
    doNothing().when(cardDao).setCardStatus(cardId, newStatus);

    Long userId = cardService.setCardStatus(cardId, newStatus);

    assertEquals(1L, userId);
    verify(cardDao, times(1)).setCardStatus(cardId, newStatus);
  }

  @Test
  void setCardStatus_CardNotFound_ThrowsException() {
    Long cardId = 99L;
    when(cardDao.findById(cardId)).thenReturn(Optional.empty());

    CardServiceException exception = assertThrows(CardServiceException.class,
        () -> cardService.setCardStatus(cardId, true));

    assertEquals("Card not found", exception.getMessage());
    verify(cardDao, never()).setCardStatus(any(), anyBoolean());
  }

  @Test
  void setCardStatus_WithNullId_ThrowsException() {
    Long cardId = null;
    assertThrows(CardServiceException.class,
        () -> cardService.setCardStatus(cardId, true));
  }

  @Test
  void setCardStatus_WithCacheEvict_ShouldEvictCache() throws CardServiceException {
    Long cardId = 10L;
    User user = new User();
    user.setId(1L);

    PaymentCard card = new PaymentCard();
    card.setId(cardId);
    card.setUser(user);

    when(cardDao.findById(cardId)).thenReturn(Optional.of(card));
    doNothing().when(cardDao).setCardStatus(cardId, false);

    Long userId = cardService.setCardStatus(cardId, false);

    assertEquals(1L, userId);
    verify(cardDao, times(1)).setCardStatus(cardId, false);
  }

  @Test
  void setCardStatus_WithSameStatus_ShouldStillUpdate() throws CardServiceException {
    Long cardId = 10L;
    boolean currentStatus = true;
    boolean newStatus = true;

    User user = new User();
    user.setId(1L);

    PaymentCard card = new PaymentCard();
    card.setId(cardId);
    card.setUser(user);
    card.setActive(currentStatus);

    when(cardDao.findById(cardId)).thenReturn(Optional.of(card));
    doNothing().when(cardDao).setCardStatus(cardId, newStatus);

    Long userId = cardService.setCardStatus(cardId, newStatus);

    assertEquals(1L, userId);
    verify(cardDao, times(1)).setCardStatus(cardId, newStatus);
  }

  @Test
  void createCard_WithUserHavingExistingCards_ShouldSucceedIfUnderLimit() throws CardServiceException {
    User user = new User();
    user.setId(1L);
    user.setActive(true);
    user.setCards(new ArrayList<>());

    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(userDao.findByIdWithLock(1L)).thenReturn(Optional.of(user));
    when(cardDao.countByUserId(1L)).thenReturn(4L);
    when(cardDao.save(any(PaymentCard.class))).thenReturn(card);

    PaymentCard result = cardService.createCard(card);

    assertNotNull(result);
    verify(cardDao, times(1)).save(card);
  }

  @Test
  void createCard_WithUserHavingExactly5Cards_ShouldThrowException() {
    User user = new User();
    user.setId(1L);
    user.setActive(true);

    PaymentCard newCard = new PaymentCard();
    newCard.setUser(user);

    when(userDao.findByIdWithLock(1L)).thenReturn(Optional.of(user));
    when(cardDao.countByUserId(1L)).thenReturn(5L);

    CardServiceException exception = assertThrows(CardServiceException.class,
        () -> cardService.createCard(newCard));

    assertEquals("User already has 5 cards. Limit reached!", exception.getMessage());
    verify(cardDao, never()).save(any());
  }
}