package by.zgirskaya.advanced_course.controller;

import by.zgirskaya.advanced_course.dto.UserDto;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.UserServiceException;
import by.zgirskaya.advanced_course.mapper.UserMapper;
import by.zgirskaya.advanced_course.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @PostMapping
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) throws UserServiceException {
    User user = userMapper.toEntity(userDto);
    User savedUser = userService.createUser(user);
    return new ResponseEntity<>(userMapper.toDto(savedUser), HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long id) throws UserServiceException {
    User user = userService.findUserById(id);
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

  @PatchMapping("/{id}/status")
  public ResponseEntity<Void> setStatus(@PathVariable Long id, @RequestParam boolean active) {
    userService.setUserStatus(id, active);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.setUserStatus(id, false);
    return ResponseEntity.noContent().build();
  }
}
