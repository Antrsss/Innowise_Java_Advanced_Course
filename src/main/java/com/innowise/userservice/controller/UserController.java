package com.innowise.userservice.controller;

import com.innowise.userservice.dto.PaymentCardDto;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import com.innowise.userservice.mapper.CardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  private final CardService cardService;
  private final CardMapper cardMapper;

  @PostMapping
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto)
      throws ResourceConflictException {
    User user = userMapper.toEntity(userDto);
    User savedUser = userService.createUser(user);
    return new ResponseEntity<>(userMapper.toDto(savedUser), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id)
      throws EntityNotFoundException {
    User user = userService.findActiveUserById(id);
    return ResponseEntity.ok(userMapper.toDto(user));
  }

  @GetMapping
  public ResponseEntity<Page<UserDto>> getAllUsers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String surname,
      Pageable pageable) {

    Page<User> userPage = userService.findAll(name, surname, pageable);
    Page<UserDto> dtoPage = userPage.map(userMapper::toDto);

    return ResponseEntity.ok(dtoPage);
  }

  @GetMapping("/{id}/cards")
  public ResponseEntity<List<PaymentCardDto>> getCardsByUserId(@PathVariable Long id) {
    List<PaymentCard> cards = cardService.findCardsByUserId(id);
    return ResponseEntity.ok(cards.stream().map(cardMapper::toDto).toList());
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> setStatus(@PathVariable Long id, @RequestParam boolean active)
      throws EntityNotFoundException {
    userService.setUserStatus(id, active);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id)
      throws EntityNotFoundException {
    userService.setUserStatus(id, false);
    return ResponseEntity.noContent().build();
  }
}
