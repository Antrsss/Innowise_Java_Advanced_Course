package com.innowise.userservice.service;

import com.innowise.userservice.dao.CardDao;
import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import com.innowise.userservice.service.impl.CardServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

  @Mock
  private UserDao userDao;

  @Mock
  private CardDao cardDao;

  @Mock
  private CacheManager cacheManager;

  @InjectMocks
  private CardServiceImpl cardService;

  @Test
  @DisplayName("Should create card when limit is not reached and number is unique")
  void createCard_Success() throws EntityNotFoundException, ResourceConflictException {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);

    PaymentCard card = new PaymentCard();
    card.setUser(user);
    card.setNumber("1234567890123456");

    when(userDao.findByIdWithLock(userId)).thenReturn(Optional.of(user));
    when(cardDao.existsByNumberAndActiveTrue(card.getNumber())).thenReturn(false);
    when(cardDao.countByUserId(userId)).thenReturn(2L);
    when(cardDao.save(any(PaymentCard.class))).thenReturn(card);

    PaymentCard result = cardService.createCard(card);

    assertThat(result).isNotNull();
    verify(cardDao).save(card);
  }

  @Test
  @DisplayName("Should throw ResourceConflictException when card number already exists")
  void createCard_DuplicateNumber() {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    PaymentCard card = new PaymentCard();
    card.setUser(user);
    card.setNumber("1111222233334444");

    when(userDao.findByIdWithLock(userId)).thenReturn(Optional.of(user));
    when(cardDao.existsByNumberAndActiveTrue("1111222233334444")).thenReturn(true);

    assertThatThrownBy(() -> cardService.createCard(card))
        .isInstanceOf(ResourceConflictException.class)
        .hasMessage("Card with that number already exists");
  }

  @Test
  @DisplayName("Should throw ResourceConflictException when user has 5 cards")
  void createCard_LimitReached() {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);
    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(userDao.findByIdWithLock(userId)).thenReturn(Optional.of(user));
    when(cardDao.existsByNumberAndActiveTrue(any())).thenReturn(false);
    when(cardDao.countByUserId(userId)).thenReturn(5L);

    assertThatThrownBy(() -> cardService.createCard(card))
        .isInstanceOf(ResourceConflictException.class)
        .hasMessage("User already has 5 cards. Limit reached!");
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when user does not exist")
  void createCard_UserNotFound() {
    Long userId = 99L;
    User user = new User();
    user.setId(userId);
    PaymentCard card = new PaymentCard();
    card.setUser(user);

    when(userDao.findByIdWithLock(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cardService.createCard(card))
        .isInstanceOf(EntityNotFoundException.class);
  }

  @Test
  @DisplayName("Should find card by id")
  void findCardById_Success() throws EntityNotFoundException {
    Long cardId = 1L;
    PaymentCard card = new PaymentCard();
    card.setId(cardId);

    when(cardDao.findById(cardId)).thenReturn(Optional.of(card));

    PaymentCard result = cardService.findCardById(cardId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(cardId);
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when card by id not found")
  void findCardById_NotFound() {
    Long cardId = 1L;
    when(cardDao.findById(cardId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cardService.findCardById(cardId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("Card not found");
  }

  @Test
  @DisplayName("Should find cards by user id")
  void findCardsByUserId_Success() {
    Long userId = 1L;
    List<PaymentCard> cards = List.of(new PaymentCard(), new PaymentCard());

    when(cardDao.findCardsByUserId(userId)).thenReturn(cards);

    List<PaymentCard> result = cardService.findCardsByUserId(userId);

    assertThat(result).hasSize(2);
    verify(cardDao).findCardsByUserId(userId);
  }

  @Test
  @DisplayName("Should find all cards with pagination and specification")
  void findAll_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<PaymentCard> page = new PageImpl<>(List.of(new PaymentCard()));

    when(cardDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<PaymentCard> result = cardService.findAll("John", "Doe", pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  @DisplayName("Should set card status and evict cache")
  void setCardStatus_Success() throws EntityNotFoundException {
    Long cardId = 1L;
    Long userId = 10L;
    User user = new User();
    user.setId(userId);
    PaymentCard card = new PaymentCard();
    card.setId(cardId);
    card.setUser(user);

    Cache mockCache = mock(Cache.class);

    when(cardDao.findById(cardId)).thenReturn(Optional.of(card));
    when(cacheManager.getCache("users")).thenReturn(mockCache);

    cardService.setCardStatus(cardId, true);

    verify(cardDao).setCardStatus(cardId, true);
    verify(mockCache).evict(userId);
  }

  @Test
  @DisplayName("Should update card status even if cache manager returns null cache")
  void setCardStatus_CacheIsNull_Success() throws EntityNotFoundException {
    Long cardId = 1L;
    Long userId = 10L;
    User user = new User();
    user.setId(userId);

    PaymentCard card = new PaymentCard();
    card.setId(cardId);
    card.setUser(user);

    when(cardDao.findById(cardId)).thenReturn(Optional.of(card));
    when(cacheManager.getCache("users")).thenReturn(null);

    cardService.setCardStatus(cardId, true);

    verify(cardDao).setCardStatus(cardId, true);
    verify(cacheManager).getCache("users");
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException during status update if card missing")
  void setCardStatus_CardNotFound() {
    Long cardId = 1L;
    when(cardDao.findById(cardId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> cardService.setCardStatus(cardId, true))
        .isInstanceOf(EntityNotFoundException.class);
  }
}