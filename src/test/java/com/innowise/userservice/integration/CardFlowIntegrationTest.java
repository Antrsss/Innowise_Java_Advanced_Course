package com.innowise.userservice.integration;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CardFlowIntegrationTest extends BaseIntegrationTest {

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
  void shouldCreateCardForUser() throws Exception {
    User user = new User();
    user.setName("Card");
    user.setSurname("Holder");
    user.setEmail("holder@example.com");
    user.setBirthDate(LocalDate.now());
    user.setActive(true);
    user = userDao.save(user);

    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("1111222233334444");
    cardDto.setHolder("CARD HOLDER");
    cardDto.setExpirationDate("12/28");
    cardDto.setUserId(user.getId());
    cardDto.setActive(true);

    mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.number").value("1111222233334444"))
        .andExpect(jsonPath("$.userId").value(user.getId()));
  }

  @Test
  void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
    PaymentCardDto cardDto = new PaymentCardDto();
    cardDto.setNumber("9999888877776666");
    cardDto.setHolder("GHOST");
    cardDto.setExpirationDate("01/29");
    cardDto.setUserId(999L);

    mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(cardDto)))
        .andExpect(status().isNotFound());
  }
}