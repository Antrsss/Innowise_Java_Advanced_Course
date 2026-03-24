package com.innowise.userservice.integration;

import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserFlowIntegrationTest extends BaseIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserDao userDao;

  @BeforeEach
  void setUp() {
    userDao.deleteAll();
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }

  @Test
  void shouldCreateAndRetrieveUser() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("John");
    userDto.setSurname("Doe");
    userDto.setEmail("john.doe@example.com");
    userDto.setBirthDate(LocalDate.of(1990, 1, 1));
    userDto.setActive(true);

    String response = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andReturn().getResponse().getContentAsString();

    UserDto createdUser = objectMapper.readValue(response, UserDto.class);
    Long userId = createdUser.getId();

    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John"));
  }

  @Test
  void shouldReturnConflictWhenEmailExists() throws Exception {
    UserDto userDto = new UserDto();
    userDto.setName("Jane");
    userDto.setSurname("Smith");
    userDto.setEmail("jane@example.com");
    userDto.setBirthDate(LocalDate.of(1995, 5, 5));
    userDto.setActive(true);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isConflict());
  }
}