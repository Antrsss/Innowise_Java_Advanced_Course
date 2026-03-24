package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CardService cardService;

  @Mock
  private CardMapper cardMapper;

  @InjectMocks
  private CardController cardController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(cardController)
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();
  }

  @Test
  @DisplayName("Should create card and return 201")
  void createCard_Success() throws Exception {
    PaymentCardDto dto = new PaymentCardDto();
    dto.setNumber("1234567812345678");
    dto.setHolder("IVAN IVANOV");
    dto.setExpirationDate("12/26");

    PaymentCard entity = new PaymentCard();

    when(cardMapper.toEntity(any(PaymentCardDto.class))).thenReturn(entity);
    when(cardService.createCard(any(PaymentCard.class))).thenReturn(entity);
    when(cardMapper.toDto(any(PaymentCard.class))).thenReturn(dto);

    mockMvc.perform(post("/api/cards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.number").value("1234567812345678"));
  }

  @Test
  @DisplayName("Should get card by id")
  void getCardById_Success() throws Exception {
    PaymentCard card = new PaymentCard();
    PaymentCardDto dto = new PaymentCardDto();
    dto.setId(1L);

    when(cardService.findCardById(1L)).thenReturn(card);
    when(cardMapper.toDto(card)).thenReturn(dto);

    mockMvc.perform(get("/api/cards/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  @DisplayName("Should get all cards with pagination")
  void getAllCards_Success() throws Exception {
    PaymentCard card = new PaymentCard();
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
    PageImpl<PaymentCard> page = new PageImpl<>(List.of(card), pageable, 1);

    when(cardService.findAll(
        any(),
        any(),
        any(org.springframework.data.domain.Pageable.class)
    )).thenReturn(page);

    when(cardMapper.toDto(any())).thenReturn(new PaymentCardDto());

    mockMvc.perform(get("/api/cards")
            .param("name", "John")
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("Should set card status")
  void setStatus_Success() throws Exception {
    mockMvc.perform(patch("/api/cards/1/status")
            .param("active", "true"))
        .andExpect(status().isOk());

    verify(cardService).setCardStatus(1L, true);
  }

  @Test
  @DisplayName("Should delete card (deactivate)")
  void deleteCard_Success() throws Exception {
    mockMvc.perform(delete("/api/cards/1"))
        .andExpect(status().isNoContent());

    verify(cardService).setCardStatus(1L, false);
  }
}