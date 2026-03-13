package by.zgirskaya.advanced_course.controller;

import by.zgirskaya.advanced_course.dto.PaymentCardDto;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.exception.CardServiceException;
import by.zgirskaya.advanced_course.mapper.CardMapper;
import by.zgirskaya.advanced_course.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

  private final CardService cardService;
  private final CardMapper cardMapper;

  @PostMapping
  public ResponseEntity<PaymentCardDto> createCard(@Valid @RequestBody PaymentCardDto cardDto) throws CardServiceException {
    PaymentCard card = cardMapper.toEntity(cardDto);
    PaymentCard savedCard = cardService.createCard(card);
    return new ResponseEntity<>(cardMapper.toDto(savedCard), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardDto> getCardById(@PathVariable Long id) throws CardServiceException {
    PaymentCard card = cardService.findCardById(id);
    return ResponseEntity.ok(cardMapper.toDto(card));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentCardDto>> getCardsByUserId(@PathVariable Long userId) {
    List<PaymentCard> cards = cardService.findCardsByUserId(userId);
    return ResponseEntity.ok(cards.stream().map(cardMapper::toDto).toList());
  }

  @GetMapping
  public ResponseEntity<Page<PaymentCardDto>> getAllCards(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String surname,
      Pageable pageable) {

    Page<PaymentCard> cardPage = cardService.findAll(name, surname, pageable);
    Page<PaymentCardDto> dtoPage = cardPage.map(cardMapper::toDto);

    return ResponseEntity.ok(dtoPage);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> setStatus(@PathVariable Long id, @RequestParam boolean active) {
    cardService.setCardStatus(id, active);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
    cardService.deleteCard(id);
    return ResponseEntity.noContent().build();
  }
}
