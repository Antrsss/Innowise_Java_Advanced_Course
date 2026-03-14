package by.zgirskaya.advanced_course.integration;

import by.zgirskaya.advanced_course.dto.UserDto;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.dao.UserDao;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

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
        .andExpect(jsonPath("$.name", is("Darya")))
        .andReturn();

    UserDto createdUser = objectMapper.readValue(createResult.getResponse().getContentAsString(), UserDto.class);
    Long userId = createdUser.getId();

    mockMvc.perform(get("/api/users/{id}", userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(userId.intValue())));

    mockMvc.perform(patch("/api/users/{id}/status", userId)
            .param("active", "false"))
        .andExpect(status().isOk());

    User userInDb = userDao.findById(userId).orElseThrow();
    assertThat(userInDb.isActive()).isFalse();

    mockMvc.perform(get("/api/users")
            .param("name", "Darya")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name", is("Darya")));

    mockMvc.perform(delete("/api/users/{id}", userId))
        .andExpect(status().isNoContent());

    assertThat(userDao.findById(userId)).isEmpty();
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
}