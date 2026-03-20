package com.innowise.userservice.integration;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserFlowIntegrationTest extends BaseIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserDao userDao;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    userDao.deleteAll();
  }

  @Test
  @DisplayName("Full User Flow: Create -> Cache Check -> Evict Check -> Delete")
  void fullUserFlowTest() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("Darya");
    userDto.setSurname("Tester");
    userDto.setEmail("darya@example.com");
    userDto.setActive(true);

    MvcResult createResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Darya"))
        .andReturn();

    UserDto createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDto.class);
    Long userId = createdUser.getId();

    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId));

    mockMvc.perform(patch("/api/users/{id}/status", userId)
            .param("active", "false"))
        .andExpect(status().isOk());

    assertThat(userDao.findById(userId).get().isActive()).isFalse();

    mockMvc.perform(get("/api/users")
            .param("name", "Darya")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk());

    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNoContent());

    User finalUser = userDao.findById(userId)
        .orElseThrow(() -> new AssertionError("Запись должна остаться в БД при Soft Delete"));

    assertThat(finalUser.isActive()).isFalse();
  }

  @Test
  @DisplayName("Validation Test: Should return 400 when name is too short")
  void shouldReturnBadRequestWhenValidationFails() throws Exception {
    UserDto invalidUser = new UserDto();
    invalidUser.setName("D");
    invalidUser.setSurname("ValidSurname");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUser)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should get user by id")
  void shouldGetUserById() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("Ivan");
    userDto.setSurname("Petrov");
    userDto.setEmail("ivan@example.com");
    userDto.setActive(true);

    MvcResult createResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andReturn();

    UserDto createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDto.class);
    Long userId = createdUser.getId();

    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId))
        .andExpect(jsonPath("$.name").value("Ivan"))
        .andExpect(jsonPath("$.surname").value("Petrov"))
        .andExpect(jsonPath("$.email").value("ivan@example.com"));
  }

  @Test
  @DisplayName("Should return 404 when user not found")
  void shouldReturn404WhenUserNotFound() throws Exception {
    mockMvc.perform(get("/api/users/9999"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should get all users with pagination")
  void shouldGetAllUsersWithPagination() throws Exception {
    for (int i = 1; i <= 5; i++) {
      UserDto userDto = new UserDto();
      userDto.setName("User" + i);
      userDto.setSurname("Surname" + i);
      userDto.setEmail("user" + i + "@example.com");
      userDto.setActive(true);

      mockMvc.perform(post("/api/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(userDto)))
          .andExpect(status().isCreated());
    }

    mockMvc.perform(get("/api/users")
            .param("page", "0")
            .param("size", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements").value(5))
        .andExpect(jsonPath("$.totalPages").value(3));
  }

  @Test
  @DisplayName("Should filter users by name and surname")
  void shouldFilterUsersByNameAndSurname() throws Exception {
    UserDto userDto1 = new UserDto();
    userDto1.setName("John");
    userDto1.setSurname("Doe");
    userDto1.setEmail("john@example.com");
    userDto1.setActive(true);

    UserDto userDto2 = new UserDto();
    userDto2.setName("Jane");
    userDto2.setSurname("Smith");
    userDto2.setEmail("jane@example.com");
    userDto2.setActive(true);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto2)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users")
            .param("name", "John")
            .param("surname", "Doe")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name").value("John"))
        .andExpect(jsonPath("$.content[0].surname").value("Doe"));
  }

  @Test
  @DisplayName("Should filter users by name only")
  void shouldFilterUsersByNameOnly() throws Exception {
    UserDto userDto1 = new UserDto();
    userDto1.setName("John");
    userDto1.setSurname("Doe");
    userDto1.setEmail("john@example.com");
    userDto1.setActive(true);

    UserDto userDto2 = new UserDto();
    userDto2.setName("John");
    userDto2.setSurname("Smith");
    userDto2.setEmail("john.smith@example.com");
    userDto2.setActive(true);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto2)))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/users")
            .param("name", "John")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)));
  }

  @Test
  @DisplayName("Should update user status")
  void shouldUpdateUserStatus() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("Peter");
    userDto.setSurname("Parker");
    userDto.setEmail("peter@example.com");
    userDto.setActive(true);

    MvcResult createResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andReturn();

    UserDto createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDto.class);
    Long userId = createdUser.getId();

    mockMvc.perform(patch("/api/users/{id}/status", userId)
            .param("active", "false"))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should delete user (soft delete)")
  void shouldDeleteUser() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("Mary");
    userDto.setSurname("Jane");
    userDto.setEmail("mary@example.com");
    userDto.setActive(true);

    MvcResult createResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andReturn();

    UserDto createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDto.class);
    Long userId = createdUser.getId();

    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should return 400 when creating user with invalid email")
  void shouldReturn400WhenCreatingUserWithInvalidEmail() throws Exception {
    UserDto invalidUser = new UserDto();
    invalidUser.setName("John");
    invalidUser.setSurname("Doe");
    invalidUser.setEmail("invalid-email");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUser)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when creating user with blank surname")
  void shouldReturn400WhenCreatingUserWithBlankSurname() throws Exception {
    UserDto invalidUser = new UserDto();
    invalidUser.setName("John");
    invalidUser.setSurname("");
    invalidUser.setEmail("john@example.com");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUser)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should return 400 when updating status with null id")
  void shouldReturn400WhenUpdatingStatusWithNullId() throws Exception {
    mockMvc.perform(patch("/api/users/null/status")
            .param("active", "false"))
        .andExpect(status().isBadRequest());
  }
}