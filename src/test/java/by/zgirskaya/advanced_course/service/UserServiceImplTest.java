package by.zgirskaya.advanced_course.service;

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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserDao userDao;

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
  void setUserStatus_CallsDao() {
    userService.setUserStatus(1L, true);
    verify(userDao).setUserStatus(1L, true);
  }

  @Test
  void deleteUser_CallsDao() {
    userService.deleteUser(1L);
    verify(userDao).deleteById(1L);
  }
}