package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.User;
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
  void createUser_WithNullUser_ShouldThrowException() {
    assertThrows(NullPointerException.class, () -> userService.createUser(null));
  }
}