package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import by.zgirskaya.advanced_course.service.impl.UserServiceImpl;
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
class UserServiceImplTest {

  @Mock
  private UserDao userDao;

  @Mock
  private CardDao cardDao;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void createUser_Success() throws UserServiceException {
    User user = new User();
    user.setCards(List.of(new PaymentCard()));

    when(userDao.save(user)).thenReturn(user);

    User result = userService.createUser(user);

    assertNotNull(result);
    verify(userDao).save(user);
  }

  @Test
  void createUser_ThrowsException_TooManyCards() {
    User user = new User();

    user.setCards(List.of(new PaymentCard(), new PaymentCard(), new PaymentCard(),
        new PaymentCard(), new PaymentCard(), new PaymentCard()));

    assertThrows(UserServiceException.class, () -> userService.createUser(user));
    verify(userDao, never()).save(any());
  }

  @Test
  void createUser_WithNullCards_ShouldSucceed() throws UserServiceException {
    User user = new User();
    user.setCards(null);

    when(userDao.save(user)).thenReturn(user);

    User result = userService.createUser(user);

    assertNotNull(result);
    verify(userDao).save(user);
  }

  @Test
  void createUser_WithEmptyCards_ShouldSucceed() throws UserServiceException {
    User user = new User();
    user.setCards(new ArrayList<>());

    when(userDao.save(user)).thenReturn(user);

    User result = userService.createUser(user);

    assertNotNull(result);
    verify(userDao).save(user);
  }

  @Test
  void createUser_WithExactlyFiveCards_ShouldSucceed() throws UserServiceException {
    User user = new User();
    List<PaymentCard> cards = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      cards.add(new PaymentCard());
    }
    user.setCards(cards);

    when(userDao.save(user)).thenReturn(user);

    User result = userService.createUser(user);

    assertNotNull(result);
    verify(userDao).save(user);
  }

  @Test
  void findUserById_Success() throws UserServiceException {
    User user = new User();
    user.setId(1L);
    when(userDao.findByIdWithCards(1L)).thenReturn(Optional.of(user));

    User result = userService.findUserById(1L);

    assertEquals(1L, result.getId());
  }

  @Test
  void findUserById_NotFound() {
    when(userDao.findByIdWithCards(1L)).thenReturn(Optional.empty());

    assertThrows(UserServiceException.class, () -> userService.findUserById(1L));
  }

  @Test
  void findAll_WithNameAndSurname_ReturnsPage() {
    Pageable pageable = Pageable.unpaged();
    Page<User> page = new PageImpl<>(List.of(new User()));

    when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<User> result = userService.findAll("John", "Doe", pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(userDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithOnlyName_ReturnsPage() {
    Pageable pageable = Pageable.unpaged();
    Page<User> page = new PageImpl<>(List.of(new User()));

    when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<User> result = userService.findAll("John", null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(userDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithOnlySurname_ReturnsPage() {
    Pageable pageable = Pageable.unpaged();
    Page<User> page = new PageImpl<>(List.of(new User()));

    when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<User> result = userService.findAll(null, "Doe", pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(userDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithNullParameters_ReturnsActiveUsers() {
    Pageable pageable = Pageable.unpaged();
    Page<User> page = new PageImpl<>(List.of(new User()));

    when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<User> result = userService.findAll(null, null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    verify(userDao, times(1)).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void findAll_WithEmptyPageable_ReturnsEmptyPage() {
    Pageable pageable = Pageable.unpaged();
    Page<User> page = new PageImpl<>(new ArrayList<>());

    when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<User> result = userService.findAll("John", "Doe", pageable);

    assertNotNull(result);
    assertTrue(result.getContent().isEmpty());
  }

  @Test
  void setUserStatus_CallsDao() throws UserServiceException {
    doNothing().when(userDao).setUserStatus(1L, true);
    doNothing().when(cardDao).setCardsStatusByUserId(1L, true);

    userService.setUserStatus(1L, true);

    verify(userDao).setUserStatus(1L, true);
    verify(cardDao).setCardsStatusByUserId(1L, true);
  }

  @Test
  void setUserStatus_WithFalseStatus_CallsDao() throws UserServiceException {
    doNothing().when(userDao).setUserStatus(1L, false);
    doNothing().when(cardDao).setCardsStatusByUserId(1L, false);

    userService.setUserStatus(1L, false);

    verify(userDao).setUserStatus(1L, false);
    verify(cardDao).setCardsStatusByUserId(1L, false);
  }

  @Test
  void setUserStatus_WithCacheEvict_ShouldWorkCorrectly() throws UserServiceException {
    doNothing().when(userDao).setUserStatus(1L, true);
    doNothing().when(cardDao).setCardsStatusByUserId(1L, true);

    userService.setUserStatus(1L, true);

    verify(userDao).setUserStatus(1L, true);
    verify(cardDao).setCardsStatusByUserId(1L, true);
  }

  @Test
  void setUserStatus_WithNullId_ShouldThrowException() {
    assertThrows(UserServiceException.class, () -> userService.setUserStatus(null, true));
  }

  @Test
  void findUserById_WithNullId_ShouldThrowException() {
    assertThrows(UserServiceException.class, () -> userService.findUserById(null));
  }

  @Test
  void createUser_WithNullUser_ShouldThrowException() {
    assertThrows(NullPointerException.class, () -> userService.createUser(null));
  }
}