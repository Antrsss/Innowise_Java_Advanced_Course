package com.innowise.userservice.controller;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @Mock
  private UserMapper userMapper;

  @Mock
  private CardService cardService;

  @Mock
  private CardMapper cardMapper;

  @InjectMocks
  private UserController userController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  @Test
  @DisplayName("Should create user and return 201")
  void createUser_Success() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setEmail("test@example.com");
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setBirthDate(LocalDate.now());

    User user = new User();

    when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
    when(userService.createUser(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(userDto);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }

  @Test
  @DisplayName("Should get user by id")
  void getUserById_Success() throws Exception {
    User user = new User();
    UserDto dto = new UserDto();
    dto.setId(1L);

    when(userService.findActiveUserById(1L)).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(dto);

    mockMvc.perform(get("/api/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("Should get all users with filters")
  void getAllUsers_Success() throws Exception {
    User user = new User();
    UserDto userDto = new UserDto();

    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

    when(userService.findAll(any(), any(), any(Pageable.class))).thenReturn(userPage);
    when(userMapper.toDto(any(User.class))).thenReturn(userDto);

    mockMvc.perform(get("/api/users")
            .param("surname", "Doe")
            .param("page", "0")
            .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0]").exists());
  }

  @Test
  @DisplayName("Should get cards by user id")
  void getCardsByUserId_Success() throws Exception {
    when(cardService.findCardsByUserId(1L)).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/users/1/cards"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should set user status")
  void setStatus_Success() throws Exception {
    mockMvc.perform(patch("/api/users/1/status")
            .param("active", "false"))
        .andExpect(status().isOk());

    verify(userService).setUserStatus(1L, false);
  }

  @Test
  @DisplayName("Should delete user")
  void deleteUser_Success() throws Exception {
    mockMvc.perform(delete("/api/users/1"))
        .andExpect(status().isNoContent());

    verify(userService).setUserStatus(1L, false);
  }
}