package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

  private final CardService cardService;
  private final CardMapper cardMapper;

  @PostMapping
  public ResponseEntity<PaymentCardDto> createCard(@Valid @RequestBody PaymentCardDto cardDto)
      throws EntityNotFoundException, ResourceConflictException {
    PaymentCard card = cardMapper.toEntity(cardDto);
    PaymentCard savedCard = cardService.createCard(card);
    return new ResponseEntity<>(cardMapper.toDto(savedCard), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardDto> getCardById(@PathVariable Long id)
      throws EntityNotFoundException {
    PaymentCard card = cardService.findCardById(id);
    return ResponseEntity.ok(cardMapper.toDto(card));
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
  public ResponseEntity<Void> setStatus(
      @PathVariable Long id,
      @RequestParam boolean active) throws EntityNotFoundException {
    cardService.setCardStatus(id, active);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCard(@PathVariable Long id)
      throws EntityNotFoundException {
    cardService.setCardStatus(id, false);
    return ResponseEntity.noContent().build();
  }
}
