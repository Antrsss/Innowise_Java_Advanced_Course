package by.zgirskaya.advanced_course.controller;

import by.zgirskaya.advanced_course.dto.UserDto;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import by.zgirskaya.advanced_course.mapper.UserMapper;
import by.zgirskaya.advanced_course.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserController userController;

  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Darya");
    user.setSurname("Zgirskaya");
    user.setEmail("darya@example.com");
    user.setActive(true);

    userDto = new UserDto();
    userDto.setId(1L);
    userDto.setName("Darya");
    userDto.setSurname("Zgirskaya");
    userDto.setEmail("darya@example.com");
    userDto.setActive(true);
  }

  @Test
  void createUser_Success() throws UserServiceException {
    when(userMapper.toEntity(userDto)).thenReturn(user);
    when(userService.createUser(user)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.createUser(userDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Darya", response.getBody().getName());
    verify(userService, times(1)).createUser(user);
  }

  @Test
  void createUser_ThrowsException() throws UserServiceException {
    when(userMapper.toEntity(userDto)).thenReturn(user);
    when(userService.createUser(user)).thenThrow(new UserServiceException("Error"));

    assertThrows(UserServiceException.class, () -> userController.createUser(userDto));
  }

  @Test
  void getUserById_Success() throws UserServiceException {
    when(userService.findUserById(1L)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(userDto);

    ResponseEntity<UserDto> response = userController.getUserById(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  void getUserById_NotFound() throws UserServiceException {
    when(userService.findUserById(99L)).thenThrow(new UserServiceException("User not found"));

    assertThrows(UserServiceException.class, () -> userController.getUserById(99L));
  }

  @Test
  void getAllUsers_WithFilters() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(user));

    when(userService.findAll("Darya", "Zgirskaya", pageable)).thenReturn(userPage);
    when(userMapper.toDto(user)).thenReturn(userDto);

    ResponseEntity<Page<UserDto>> response = userController.getAllUsers("Darya", "Zgirskaya", pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getContent().size());
    assertEquals("Darya", response.getBody().getContent().get(0).getName());
  }

  @Test
  void getAllUsers_WithoutFilters() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(user));

    when(userService.findAll(null, null, pageable)).thenReturn(userPage);
    when(userMapper.toDto(user)).thenReturn(userDto);

    ResponseEntity<Page<UserDto>> response = userController.getAllUsers(null, null, pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getContent().size());
  }

  @Test
  void setStatus_Success() {
    doNothing().when(userService).setUserStatus(1L, true);

    ResponseEntity<Void> response = userController.setStatus(1L, true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userService, times(1)).setUserStatus(1L, true);
  }

  @Test
  void deleteUser_Success() {
    doNothing().when(userService).setUserStatus(1L, false);

    ResponseEntity<Void> response = userController.deleteUser(1L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(userService, times(1)).setUserStatus(1L, false);
  }
}