package com.innowise.userservice.service;

import com.innowise.userservice.dao.CardDao;
import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class UserServiceImplTest {

  @Mock
  private UserDao userDao;

  @Mock
  private CardDao cardDao;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  @DisplayName("Should save user when email is unique")
  void createUser_Success() throws ResourceConflictException {
    User user = new User();
    user.setEmail("test@innowise.com");

    when(userDao.existsByEmailAndActiveTrue(user.getEmail())).thenReturn(false);
    when(userDao.save(any(User.class))).thenReturn(user);

    User result = userService.createUser(user);

    assertThat(result).isNotNull();
    verify(userDao, times(1)).save(user);
  }

  @Test
  @DisplayName("Should throw ResourceConflictException when email already exists")
  void createUser_ThrowsConflict() {
    User user = new User();
    user.setEmail("exists@innowise.com");

    when(userDao.existsByEmailAndActiveTrue(user.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser(user))
        .isInstanceOf(ResourceConflictException.class)
        .hasMessage("User already exists");

    verify(userDao, never()).save(any());
  }

  @Test
  @DisplayName("Should find user by id when user is active")
  void findActiveUserById_Success() throws EntityNotFoundException {
    Long id = 1L;
    User user = new User();
    user.setId(id);

    when(userDao.findActiveUserByIdWithCards(id)).thenReturn(Optional.of(user));

    User result = userService.findActiveUserById(id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when active user not found")
  void findActiveUserById_NotFound() {
    Long id = 1L;
    when(userDao.findActiveUserByIdWithCards(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findActiveUserById(id))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("User not found");
  }

  @Test
  @DisplayName(("Should find user by email when user is active"))
  void findUserById_Success() throws EntityNotFoundException {
    String email = "test@innowise.com";
    User user = new User();
    user.setEmail(email);

    when(userDao.findByEmailWithLock(email)).thenReturn(Optional.of(user));

    User result = userService.findActiveUserByEmail(email);

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(email);
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when active user not found")
  void findActiveUserByEmail_NotFound() {
    String email = "test@innowise.com";
    when(userDao.findByEmailWithLock(email)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.findActiveUserByEmail(email))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("User not found");
  }

  @Test
  @DisplayName("Should throw EntityNotFoundException when user id is null in setUserStatus")
  void setUserStatus_IdIsNull() {
    assertThatThrownBy(() -> userService.setUserStatus(null, true))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("User id is null");

    verify(userDao, never()).setUserStatus(any(), anyBoolean());
    verify(cardDao, never()).setCardsStatusByUserId(any(), anyBoolean());
  }

  @Test
  @DisplayName("Should find all users with pagination and specification")
  void findAll_Success() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(new User()));

    when(userDao.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

    Page<User> result = userService.findAll("John", "Doe", pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    verify(userDao).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("Should disable status for user and their cards")
  void setUserStatus_Disable_Success() throws EntityNotFoundException {
    Long userId = 1L;
    boolean newStatus = false;

    userService.setUserStatus(userId, newStatus);

    verify(userDao).setUserStatus(userId, newStatus);
    verify(cardDao).setCardsStatusByUserId(userId, newStatus);
  }

  @Test
  @DisplayName("Should enable status for user and their cards")
  void setUserStatus_Enable_Success() throws EntityNotFoundException {
    Long userId = 1L;
    boolean newStatus = true;

    userService.setUserStatus(userId, newStatus);

    verify(userDao).setUserStatus(userId, newStatus);
    verify(cardDao).setCardsStatusByUserId(userId, newStatus);
  }
}