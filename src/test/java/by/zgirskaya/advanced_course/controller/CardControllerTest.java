package by.zgirskaya.advanced_course.controller;

import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.CardServiceException;
import by.zgirskaya.advanced_course.mapper.CardMapper;
import by.zgirskaya.advanced_course.service.CardService;
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
class CardControllerTest {

  @Mock
  private CardService cardService;

  @Mock
  private CardMapper cardMapper;

  @InjectMocks
  private CardController cardController;

  private PaymentCard paymentCard;
  private PaymentCardDto paymentCardDto;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Darya");

    paymentCard = new PaymentCard();
    paymentCard.setId(10L);
    paymentCard.setNumber("1234567812345678");
    paymentCard.setHolder("DARYA ZGIRSKAYA");
    paymentCard.setExpirationDate("12/28");
    paymentCard.setActive(true);
    paymentCard.setUser(user);

    paymentCardDto = new PaymentCardDto();
    paymentCardDto.setId(10L);
    paymentCardDto.setNumber("1234567812345678");
    paymentCardDto.setHolder("DARYA ZGIRSKAYA");
    paymentCardDto.setExpirationDate("12/28");
    paymentCardDto.setActive(true);
    paymentCardDto.setUserId(1L);
  }

  @Test
  void createCard_Success() throws CardServiceException {
    when(cardMapper.toEntity(paymentCardDto)).thenReturn(paymentCard);
    when(cardService.createCard(paymentCard)).thenReturn(paymentCard);
    when(cardMapper.toDto(paymentCard)).thenReturn(paymentCardDto);

    ResponseEntity<PaymentCardDto> response = cardController.createCard(paymentCardDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(paymentCardDto.getNumber(), response.getBody().getNumber());
    verify(cardService, times(1)).createCard(paymentCard);
  }

  @Test
  void createCard_ThrowsException() throws CardServiceException {
    when(cardMapper.toEntity(paymentCardDto)).thenReturn(paymentCard);
    when(cardService.createCard(paymentCard)).thenThrow(new CardServiceException("Error"));

    assertThrows(CardServiceException.class, () -> cardController.createCard(paymentCardDto));
  }

  @Test
  void getCardById_Success() throws CardServiceException {
    when(cardService.findCardById(10L)).thenReturn(paymentCard);
    when(cardMapper.toDto(paymentCard)).thenReturn(paymentCardDto);

    ResponseEntity<PaymentCardDto> response = cardController.getCardById(10L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(10L, response.getBody().getId());
  }

  @Test
  void getCardById_NotFound() throws CardServiceException {
    when(cardService.findCardById(99L)).thenThrow(new CardServiceException("Card not found"));

    assertThrows(CardServiceException.class, () -> cardController.getCardById(99L));
  }

  @Test
  void getCardsByUserId_Success() {
    List<PaymentCard> cards = List.of(paymentCard);
    when(cardService.findCardsByUserId(1L)).thenReturn(cards);
    when(cardMapper.toDto(paymentCard)).thenReturn(paymentCardDto);

    ResponseEntity<List<PaymentCardDto>> response = cardController.getCardsByUserId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals(10L, response.getBody().get(0).getId());
  }

  @Test
  void getCardsByUserId_EmptyList() {
    when(cardService.findCardsByUserId(1L)).thenReturn(List.of());

    ResponseEntity<List<PaymentCardDto>> response = cardController.getCardsByUserId(1L);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

  @Test
  void getAllCards_WithFilters() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<PaymentCard> cardPage = new PageImpl<>(List.of(paymentCard));

    when(cardService.findAll("Darya", "Zgirskaya", pageable)).thenReturn(cardPage);
    when(cardMapper.toDto(paymentCard)).thenReturn(paymentCardDto);

    ResponseEntity<Page<PaymentCardDto>> response = cardController.getAllCards("Darya", "Zgirskaya", pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getContent().size());
    assertEquals(10L, response.getBody().getContent().get(0).getId());
  }

  @Test
  void getAllCards_WithoutFilters() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<PaymentCard> cardPage = new PageImpl<>(List.of(paymentCard));

    when(cardService.findAll(null, null, pageable)).thenReturn(cardPage);
    when(cardMapper.toDto(paymentCard)).thenReturn(paymentCardDto);

    ResponseEntity<Page<PaymentCardDto>> response = cardController.getAllCards(null, null, pageable);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getContent().size());
  }

  @Test
  void setStatus_Success() throws CardServiceException {
    Long cardId = 10L;
    when(cardService.setCardStatus(cardId, true)).thenReturn(1L);

    ResponseEntity<Void> response = cardController.setStatus(cardId, true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(cardService, times(1)).setCardStatus(cardId, true);
  }

  @Test
  void setStatus_ThrowsException() throws CardServiceException {
    doThrow(new CardServiceException("Error")).when(cardService).setCardStatus(10L, true);

    assertThrows(CardServiceException.class, () -> cardController.setStatus(10L, true));
  }

  @Test
  void deleteCard_Success() throws CardServiceException {
    when(cardService.setCardStatus(10L, false)).thenReturn(1L);

    ResponseEntity<Void> response = cardController.deleteCard(10L);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(cardService, times(1)).setCardStatus(10L, false);
  }
}