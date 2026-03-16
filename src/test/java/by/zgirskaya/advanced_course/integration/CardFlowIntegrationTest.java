package by.zgirskaya.advanced_course.integration;

import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CardFlowIntegrationTest extends BaseIntegrationTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private UserDao userDao;

  @Autowired
  private ObjectMapper objectMapper;

  private Long savedUserId;

  @BeforeEach
  void setUp() {
    userDao.deleteAll();
    User user = new User();
    user.setName("Darya");
    user.setSurname("Zgirskaya");
    user.setEmail("test@mail.com");
    user.setBirthDate(LocalDate.of(1990, 1, 1));
    user.setActive(true);
    savedUserId = userDao.save(user).getId();

    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
  }

  @Test
  void shouldCreateCardAndThenFindItUsingSpecification() throws Exception {
    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("1234567812345678");
    cardDto.setHolder("DARYA ZGIRSKAYA");
    cardDto.setExpirationDate("12/28");
    cardDto.setActive(true);
    cardDto.setUserId(savedUserId);

    mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.number").value("1234567812345678"));

    mockMvc.perform(get("/api/cards")
            .param("name", "Darya")
            .param("surname", "Zgirskaya"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].holder").value("DARYA ZGIRSKAYA"));
  }

  @Test
  @DisplayName("Should fail when user already has 5 cards")
  void shouldFailCreationWhenLimitReached() throws Exception {
    for (int i = 1; i <= 5; i++) {
      PaymentCardDto setupCard = new PaymentCardDto();
      setupCard.setNumber("111122223333444" + i);
      setupCard.setHolder("DARYA ZGIRSKAYA");
      setupCard.setExpirationDate("12/28");
      setupCard.setActive(true);
      setupCard.setUserId(savedUserId);

      mockMvc.perform(post("/api/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(setupCard)))
          .andExpect(status().isCreated());
    }

    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("0000000000000000");
    cardDto.setUserId(savedUserId);
    cardDto.setHolder("Test");
    cardDto.setExpirationDate("01/30");

    mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should delete card and return 204 No Content")
  void shouldDeleteCardAndCheckStatus() throws Exception {
    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("8888777766665555");
    cardDto.setHolder("TO BE DELETED");
    cardDto.setExpirationDate("10/25");
    cardDto.setUserId(savedUserId);

    String response = mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    Long cardId = objectMapper.readTree(response).get("id").asLong();

    mockMvc.perform(delete("/api/cards/{id}", cardId))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/cards")
            .param("name", "Darya"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].active").value(false));
  }
}
